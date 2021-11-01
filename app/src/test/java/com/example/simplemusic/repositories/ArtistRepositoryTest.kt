package com.example.simplemusic.repositories

import com.example.simplemusic.database.dao.ApiCacheDao
import com.example.simplemusic.database.dao.SearchDao
import com.example.simplemusic.models.SearchResponse
import com.example.simplemusic.models.multimediacontent.Artist
import com.example.simplemusic.models.stored.search.Search
import com.example.simplemusic.webservices.SearchWebService
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
class ArtistRepositoryTest {

    @Mock
    private lateinit var apiCacheDao: ApiCacheDao
    @Mock
    private lateinit var searchDao: SearchDao
    @Mock
    private lateinit var searchWebService: SearchWebService
    @InjectMocks
    private lateinit var artistRepository: ArtistRepository

    private val limit = 20
    private val nArtist = 20
    private val artistName = "Eminem"
    private val cacheId: Int = 1
    private val webServiceId: Int = 2

    @Before
    fun init() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun request20Artist_1TimeWithoutCache_get20ArtistFromWebService() = runBlockingTest {
        val response = createFakeResponseArtist(nArtist, webServiceId)
        val search = Search(artistName, limit)

        `when`(searchWebService.getArtists(artistName, limit)).thenReturn(response)
        `when`(searchDao.addSearch(search)).thenReturn(1L)
        val artists = artistRepository.getArtists(artistName)

        assertThat(artists).isEqualTo(response.results)
        assertThat(artists.size).isEqualTo(nArtist)
    }

    @Test
    fun request20Artist_2Times_get40Artists() = runBlockingTest {
        val response20 = createFakeResponseArtist(nArtist, webServiceId)
        val response40 = createFakeResponseArtist(nArtist*2, webServiceId)
        val search20 = Search(artistName, limit)
        val search40 = Search(artistName, limit*2)

        `when`(searchWebService.getArtists(artistName, limit)).thenReturn(response20)
        `when`(searchWebService.getArtists(artistName, limit*2)).thenReturn(response40)
        `when`(searchDao.addSearch(search20)).thenReturn(1L)
        `when`(searchDao.addSearch(search40)).thenReturn(2L)
        artistRepository.getArtists(artistName)
        val artists = artistRepository.getArtists(artistName)

        assertThat(artists).isEqualTo(response40.results)
        assertThat(artists.size).isEqualTo(nArtist*2)
    }

    @Test
    fun request20Artists_2TimesResettingPaginationInBetween_get20Artists() = runBlockingTest {
        val response = createFakeResponseArtist(nArtist, webServiceId)
        val search = Search(artistName, limit)

        `when`(searchWebService.getArtists(artistName, limit)).thenReturn(response)
        `when`(searchDao.addSearch(search)).thenReturn(1L)
        artistRepository.getArtists(artistName)
        artistRepository.resetPagination()
        val artists = artistRepository.getArtists(artistName)

        verify(searchWebService, times(2)).getArtists(artistName, limit)
        assertThat(artists).isEqualTo(response.results)
        assertThat(artists.size).isEqualTo(nArtist)
    }

    @Test
    fun request20Artists_with20CachedArtists_get20ArtistsFromCache() = runBlockingTest {
        val response = createFakeResponseArtist(nArtist, webServiceId)
        val cacheArtists = createFakeArtists(nArtist, cacheId)
        val cacheArtistsId = getArtistsId(cacheArtists)
        val search = Search(artistName, limit)

        `when`(searchWebService.getArtists(artistName, limit)).thenReturn(response)
        `when`(searchDao.getSearch(artistName)).thenReturn(search)
        `when`(searchDao.getSearchResultsArtistId(search.searchId, search.limit)).thenReturn(cacheArtistsId)
        `when`(apiCacheDao.getArtist(cacheId)).thenReturn(cacheArtists[0])
        val artists = artistRepository.getArtists(artistName)

        verify(searchWebService, never()).getArtists(artistName, limit)
        assertThat(artists).isEqualTo(cacheArtists)
        assertThat(artists.size).isEqualTo(nArtist)
    }

    private fun createFakeResponseArtist(n: Int, id: Int): SearchResponse {
        val response = SearchResponse()
        response.resultCount = n
        response.results = createFakeArtists(n, id)
        return response
    }

    private fun createFakeArtists(n: Int, id: Int): ArrayList<Artist> {
        val artists = ArrayList<Artist>()
        for (i in 1..n) {
            val artist = Artist()
            artist.artistId = id
            artists.add(artist)
        }
        return artists
    }

    private fun getArtistsId(artists: ArrayList<Artist>): ArrayList<Int> {
        val ids = ArrayList<Int>()
        for (artist in artists)
            ids.add(artist.artistId!!)
        return ids
    }

}
