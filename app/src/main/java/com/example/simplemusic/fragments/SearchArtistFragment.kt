package com.example.simplemusic.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplemusic.R
import com.example.simplemusic.activities.MainActivity
import com.example.simplemusic.adapters.ArtistAdapter
import com.example.simplemusic.viewmodels.ArtistViewModel
import kotlinx.coroutines.launch
import android.os.Parcelable
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.simplemusic.models.multimediacontent.Artist
import com.example.simplemusic.utils.Connectivity
import com.example.simplemusic.viewmodels.AlbumViewModel


private const val SEARCH_DEFAULT = "a"
private const val SEARCH_PAGINATION = 20

class SearchArtistFragment : Fragment(), SearchView.OnQueryTextListener, ArtistAdapter.ActionInterface {

    private lateinit var artistRv: RecyclerView
    private lateinit var artistAdapter: ArtistAdapter
    private lateinit var toolbar: Toolbar
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var progressBar: ProgressBar
    private lateinit var stateTv: TextView

    private val artistViewModel: ArtistViewModel by activityViewModels()
    private val albumViewModel: AlbumViewModel by activityViewModels()
    private lateinit var navController: NavController
    // List scroll
    private var recyclerViewState: Parcelable? = null
    private var pagination = SEARCH_PAGINATION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search_artist, container, false)

        initView(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigation
        navController = findNavController()

        // Toolbar
        setToolbar()

        // Observe artists
        artistViewModel.artists.observe(viewLifecycleOwner, { artists ->
            // Request indicators off
            progressBar.visibility = View.GONE
            artistViewModel.searchingArtist = false
            stateTv.visibility = View.GONE

            // Update artists data
            artistAdapter.setArtists(artists)

            // Restore list scroll
            artistRv.layoutManager?.onRestoreInstanceState(recyclerViewState);

            Log.println(Log.ERROR, "DEBUG", "request $pagination")//
        })

        // Init artist list empty
        artistAdapter = ArtistAdapter(ArrayList(), this)
        linearLayoutManager = LinearLayoutManager(context)
        artistRv.layoutManager = linearLayoutManager
        artistRv.adapter = artistAdapter

        // Listener. At end of list request more artist data
        artistRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    if (!artistViewModel.searchingArtist) {
                        // Save list scroll data
                        recyclerViewState = (artistRv.layoutManager as LinearLayoutManager).onSaveInstanceState()

                        // Request more artists data
                        pagination += SEARCH_PAGINATION
                        artistViewModel.searchedArtist?.let { requestSearch(it, pagination) }
                    }
                }
            }
        })

        // Default search when no text
        if (artistViewModel.searchedArtist == null)
            requestSearch(SEARCH_DEFAULT, pagination)
    }

    private fun initView(view: View) {
        artistRv = view.findViewById(R.id.artistRv)
        toolbar = view.findViewById(R.id.toolbar)
        progressBar = view.findViewById(R.id.progressBar)
        stateTv = view.findViewById(R.id.stateTv)

        progressBar.visibility = View.GONE
        stateTv.visibility = View.GONE
    }

    private fun setToolbar() {
        // Toolbar
        val navHostFragment = NavHostFragment.findNavController(this)
        val config = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController( navHostFragment, config)

        // Menu which contains a search view
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        // Hide title
        (requireActivity() as MainActivity).title = ""
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Search as icon that deploys an input
        inflater.inflate(R.menu.menu, menu)
        val menuItem = menu.findItem(R.id.action_search)
        val searchView = menuItem?.actionView as SearchView

        searchView.isIconified = true
        searchView.setOnQueryTextListener(this)

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun requestSearch(search: String, pagination: Int) {
        if (context?.let { Connectivity.isOnline(it) } == true) {
            progressBar.visibility = View.VISIBLE

            lifecycleScope.launch {
                artistViewModel.searchArtist(search, pagination)
            }
        } else {
            // If no internet and no artist data, show at least info
            if (artistViewModel.artists.value == null) {
                stateTv.text = getText(R.string.no_results)
                stateTv.visibility = View.VISIBLE
            }

            Toast.makeText(activity, R.string.no_internet, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            pagination = SEARCH_PAGINATION
            if (newText.isEmpty()) {
                // Default search when no text
                requestSearch(SEARCH_DEFAULT, pagination)
            } else {
                requestSearch(newText, pagination)
            }
        }

        return true
    }

    override fun onClickArtist(artist: Artist) {
        albumViewModel.searchedArtist = artist.artistName
        val action =  SearchArtistFragmentDirections.actionSearchArtistFragmentToArtistAlbumsFragment(artist.artistId!!)
        navController.navigate(action)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            SearchArtistFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}