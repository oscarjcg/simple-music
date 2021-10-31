package com.example.simplemusic.repositories

import com.example.simplemusic.database.dao.ApiCacheDao
import com.example.simplemusic.models.SearchResponse
import com.example.simplemusic.models.multimediacontent.AlbumSong
import com.example.simplemusic.models.multimediacontent.MultimediaContent
import com.example.simplemusic.webservices.SongWebService
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.junit.Before
import org.mockito.InjectMocks


@ExperimentalCoroutinesApi
class SongRepositoryTest {

    @Mock
    private lateinit var songWebService: SongWebService
    @Mock
    private lateinit var apiCacheDao: ApiCacheDao
    @InjectMocks
    private lateinit var songRepository: SongRepository

    private val limit = 20
    private val nSongs = 20
    private val albumId: Long = 1
    private val cacheId: Long = 1
    private val webServiceId: Long = 2

    @Before
    fun init() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun request20Songs_1TimeWithoutCache_get20SongsFromWebService() = runBlockingTest {
        val response = createFakeResponseSongs(nSongs, webServiceId)
        val cacheSongs = ArrayList<AlbumSong>()

        `when`(songWebService.getAlbumSongs(albumId, limit)).thenReturn(response)
        `when`(apiCacheDao.getAlbumOwnerSongs(albumId, 1)).thenReturn(cacheSongs)
        val songs = songRepository.getAlbumSongs(albumId)

        verify(songWebService).getAlbumSongs(albumId, limit)
        assertThat(songs).isEqualTo(response.results)
        assertThat(songs.size).isEqualTo(nSongs)
    }

    @Test
    fun request20Songs_2Times_get40Songs() = runBlockingTest {
        val response20Songs = createFakeResponseSongs(nSongs, webServiceId)
        val response40Songs = createFakeResponseSongs(40, webServiceId)
        val cacheSongs = ArrayList<AlbumSong>()

        `when`(songWebService.getAlbumSongs(albumId, limit)).thenReturn(response20Songs)
        `when`(songWebService.getAlbumSongs(albumId, limit*2)).thenReturn(response40Songs)
        `when`(apiCacheDao.getAlbumOwnerSongs(albumId, 1)).thenReturn(cacheSongs)
        songRepository.getAlbumSongs(albumId)
        val songs = songRepository.getAlbumSongs(albumId)

        verify(songWebService).getAlbumSongs(albumId, limit)
        verify(songWebService).getAlbumSongs(albumId, limit*2)
        assertThat(songs).isEqualTo(response40Songs.results)
        assertThat(songs.size).isEqualTo(nSongs*2)
    }

    @Test
    fun request20Songs_2TimesResettingPaginationInBetween_get20Songs() = runBlockingTest {
        val response = createFakeResponseSongs(nSongs, webServiceId)
        val cacheSongs = ArrayList<AlbumSong>()

        `when`(songWebService.getAlbumSongs(albumId, limit)).thenReturn(response)
        `when`(apiCacheDao.getAlbumOwnerSongs(albumId, 1)).thenReturn(cacheSongs)
        songRepository.getAlbumSongs(albumId)
        songRepository.resetPagination()
        val songs = songRepository.getAlbumSongs(albumId)

        verify(songWebService, times(2)).getAlbumSongs(albumId, limit)
        assertThat(songs).isEqualTo(response.results)
        assertThat(songs.size).isEqualTo(nSongs)
    }

    @Test
    fun request20Songs_with20CachedSongs_get20SongsFromCache() = runBlockingTest {
        val response = createFakeResponseSongs(nSongs, webServiceId)
        val cacheSongs = createFakeSongs(nSongs, cacheId)

        `when`(songWebService.getAlbumSongs(albumId, limit)).thenReturn(response)
        `when`(apiCacheDao.getAlbumOwnerSongs(albumId, 1)).thenReturn(cacheSongs)
        `when`(apiCacheDao.getAlbumOwnerSongs(albumId, limit)).thenReturn(cacheSongs)
        val songs = songRepository.getAlbumSongs(albumId)

        verify(songWebService, never()).getAlbumSongs(albumId, limit)
        assertThat(songs).isEqualTo(cacheSongs)
        assertThat(songs.size).isEqualTo(nSongs)
    }

    private fun createFakeResponseSongs(nSongs: Int, songId: Long):SearchResponse {
        val response = SearchResponse()
        response.resultCount = nSongs
        response.results = createFakeSongs(nSongs, songId)
        return response
    }

    private fun createFakeSongs(nSongs: Int, songId: Long): ArrayList<AlbumSong> {
        val songs = ArrayList<AlbumSong>()
        for (i in 1..nSongs) {
            val song = AlbumSong()
            song.trackId = songId
            song.limit = nSongs
            songs.add(song)
        }
        return songs
    }

}
