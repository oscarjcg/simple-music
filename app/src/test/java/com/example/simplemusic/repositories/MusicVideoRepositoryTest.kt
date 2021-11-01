package com.example.simplemusic.repositories

import com.example.simplemusic.database.dao.ApiCacheDao
import com.example.simplemusic.database.dao.SearchDao
import com.example.simplemusic.models.SearchResponse
import com.example.simplemusic.models.multimediacontent.MusicVideo
import com.example.simplemusic.webservices.MusicVideoWebService
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class MusicVideoRepositoryTest {

    @Mock
    private lateinit var apiCacheDao: ApiCacheDao
    @Mock
    private lateinit var musicVideoWebService: MusicVideoWebService
    @InjectMocks
    private lateinit var musicVideoRepository: MusicVideoRepository

    private val limit = 20
    private val nMusicVideos = 20
    private val artistName = "Eminem"
    private val cacheId: Long = 1
    private val webServiceId: Long = 2

    @Before
    fun init() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun request20MusicVideos_1TimeWithoutCache_get20MusicVideosFromWebService() = runBlockingTest {
        val response = createFakeResponseMusicVideos(nMusicVideos, webServiceId)
        val cacheSongs = ArrayList<MusicVideo>()

        `when`(musicVideoWebService.getArtistMusicVideos(artistName, limit)).thenReturn(response)
        `when`(apiCacheDao.getArtistOwnerMusicVideos(artistName, 1)).thenReturn(cacheSongs)
        val musicVideos = musicVideoRepository.getArtistMusicVideos(artistName)

        assertThat(musicVideos).isEqualTo(response.results)
        assertThat(musicVideos.size).isEqualTo(nMusicVideos)
    }

    @Test
    fun request20MusicVideos_2Times_get40MusicVideos() = runBlockingTest {
        val response20Songs = createFakeResponseMusicVideos(nMusicVideos, webServiceId)
        val response40Songs = createFakeResponseMusicVideos(40, webServiceId)
        val cacheSongs = ArrayList<MusicVideo>()

        `when`(musicVideoWebService.getArtistMusicVideos(artistName, limit)).thenReturn(response20Songs)
        `when`(musicVideoWebService.getArtistMusicVideos(artistName, limit*2)).thenReturn(response40Songs)
        `when`(apiCacheDao.getArtistOwnerMusicVideos(artistName, 1)).thenReturn(cacheSongs)
        musicVideoRepository.getArtistMusicVideos(artistName)
        val musicVideos = musicVideoRepository.getArtistMusicVideos(artistName)

        assertThat(musicVideos).isEqualTo(response40Songs.results)
        assertThat(musicVideos.size).isEqualTo(nMusicVideos*2)

    }

    @Test
    fun request20MusicVideos_2TimesResettingPaginationInBetween_get20MusicVideos() = runBlockingTest {
        val response = createFakeResponseMusicVideos(nMusicVideos, webServiceId)
        val cacheSongs = ArrayList<MusicVideo>()

        `when`(musicVideoWebService.getArtistMusicVideos(artistName, limit)).thenReturn(response)
        `when`(apiCacheDao.getArtistOwnerMusicVideos(artistName, 1)).thenReturn(cacheSongs)
        musicVideoRepository.getArtistMusicVideos(artistName)
        musicVideoRepository.resetPagination()
        val musicVideos = musicVideoRepository.getArtistMusicVideos(artistName)

        verify(musicVideoWebService, times(2)).getArtistMusicVideos(artistName, limit)
        assertThat(musicVideos).isEqualTo(response.results)
        assertThat(musicVideos.size).isEqualTo(nMusicVideos)
    }

    @Test
    fun request20MusicVideos_with20CachedMusicVideos_get20MusicVideosFromCache() = runBlockingTest {
        val response = createFakeResponseMusicVideos(nMusicVideos, webServiceId)
        val cacheSongs = createFakeMusicVideos(nMusicVideos, cacheId)

        `when`(musicVideoWebService.getArtistMusicVideos(artistName, limit)).thenReturn(response)
        `when`(apiCacheDao.getArtistOwnerMusicVideos(artistName, 1)).thenReturn(cacheSongs)
        `when`(apiCacheDao.getArtistOwnerMusicVideos(artistName, limit)).thenReturn(cacheSongs)
        val musicVideos = musicVideoRepository.getArtistMusicVideos(artistName)

        verify(musicVideoWebService, never()).getArtistMusicVideos(artistName, limit)
        assertThat(musicVideos).isEqualTo(cacheSongs)
        assertThat(musicVideos.size).isEqualTo(nMusicVideos)
    }

    private fun createFakeResponseMusicVideos(n: Int, id: Long): SearchResponse {
        val response = SearchResponse()
        response.resultCount = n
        response.results = createFakeMusicVideos(n, id)
        return response
    }

    private fun createFakeMusicVideos(n: Int, id: Long): ArrayList<MusicVideo> {
        val musicVideos = ArrayList<MusicVideo>()
        for (i in 1..n) {
            val musicVideo = MusicVideo()
            musicVideo.trackId = id
            musicVideo.limit = n
            musicVideos.add(musicVideo)
        }
        return musicVideos
    }
}
