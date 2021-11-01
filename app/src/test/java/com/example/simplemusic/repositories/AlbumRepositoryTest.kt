package com.example.simplemusic.repositories

import com.example.simplemusic.database.dao.ApiCacheDao
import com.example.simplemusic.models.SearchResponse
import com.example.simplemusic.models.multimediacontent.ArtistAlbum
import com.example.simplemusic.webservices.AlbumWebService
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
class AlbumRepositoryTest {

    @Mock
    private lateinit var albumWebService: AlbumWebService
    @Mock
    private lateinit var apiCacheDao: ApiCacheDao
    @InjectMocks
    private lateinit var albumRepository: AlbumRepository

    private val limit = 20
    private val nAlbums = 20
    private val artistId = 1
    private val cacheId: Long = 1
    private val webServiceId: Long = 2

    @Before
    fun init() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun request20Albums_1TimeWithoutCache_get20AlbumsFromWebService() = runBlockingTest {
        val response = createFakeResponseAlbums(nAlbums, webServiceId)
        val cacheAlbums = ArrayList<ArtistAlbum>()

        `when`(albumWebService.getArtistAlbums(artistId, limit)).thenReturn(response)
        `when`(apiCacheDao.getArtistOwnerAlbums(artistId, 1)).thenReturn(cacheAlbums)
        val albums = albumRepository.getArtistAlbums(artistId)

        verify(albumWebService).getArtistAlbums(artistId, limit)
        assertThat(albums).isEqualTo(response.results)
        assertThat(albums.size).isEqualTo(nAlbums)
    }

    @Test
    fun request20Albums_2Times_get40Albums() = runBlockingTest {
        val response20Albums = createFakeResponseAlbums(nAlbums, webServiceId)
        val response40Albums = createFakeResponseAlbums(nAlbums*2, webServiceId)
        val cacheAlbums = ArrayList<ArtistAlbum>()

        `when`(albumWebService.getArtistAlbums(artistId, limit)).thenReturn(response20Albums)
        `when`(albumWebService.getArtistAlbums(artistId, limit*2)).thenReturn(response40Albums)
        `when`(apiCacheDao.getArtistOwnerAlbums(artistId, 1)).thenReturn(cacheAlbums)
        albumRepository.getArtistAlbums(artistId)
        val albums = albumRepository.getArtistAlbums(artistId)

        verify(albumWebService).getArtistAlbums(artistId, limit)
        verify(albumWebService).getArtistAlbums(artistId, limit*2)
        assertThat(albums).isEqualTo(response40Albums.results)
        assertThat(albums.size).isEqualTo(nAlbums*2)
    }

    @Test
    fun request20Albums_2TimesResettingPaginationInBetween_get20Albums() = runBlockingTest {
        val response = createFakeResponseAlbums(nAlbums, webServiceId)
        val cacheAlbums = ArrayList<ArtistAlbum>()

        `when`(albumWebService.getArtistAlbums(artistId, limit)).thenReturn(response)
        `when`(apiCacheDao.getArtistOwnerAlbums(artistId, 1)).thenReturn(cacheAlbums)
        albumRepository.getArtistAlbums(artistId)
        albumRepository.resetPagination()
        val albums = albumRepository.getArtistAlbums(artistId)

        verify(albumWebService, times(2)).getArtistAlbums(artistId, limit)
        assertThat(albums).isEqualTo(response.results)
        assertThat(albums.size).isEqualTo(nAlbums)
    }

    @Test
    fun request20Albums_with20CachedAlbums_get20AlbumsFromCache() = runBlockingTest {
        val response = createFakeResponseAlbums(nAlbums, webServiceId)
        val cacheAlbums = createFakeAlbums(nAlbums, cacheId)

        `when`(albumWebService.getArtistAlbums(artistId, limit)).thenReturn(response)
        `when`(apiCacheDao.getArtistOwnerAlbums(artistId, 1)).thenReturn(cacheAlbums)
        `when`(apiCacheDao.getArtistOwnerAlbums(artistId, limit)).thenReturn(cacheAlbums)
        val albums = albumRepository.getArtistAlbums(artistId)

        verify(albumWebService, never()).getArtistAlbums(artistId, limit)
        assertThat(albums).isEqualTo(cacheAlbums)
        assertThat(albums.size).isEqualTo(nAlbums)
    }

    private fun createFakeResponseAlbums(n: Int, albumId: Long): SearchResponse {
        val response = SearchResponse()
        response.resultCount = n
        response.results = createFakeAlbums(n, albumId)
        return response
    }

    private fun createFakeAlbums(n: Int, albumId: Long): ArrayList<ArtistAlbum> {
        val albums = ArrayList<ArtistAlbum>()
        for (i in 1..n) {
            val album = ArtistAlbum()
            album.collectionId = albumId
            album.limit = n
            albums.add(album)
        }
        return albums
    }
}
