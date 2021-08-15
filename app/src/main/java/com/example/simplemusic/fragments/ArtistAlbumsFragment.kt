package com.example.simplemusic.fragments

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplemusic.R
import com.example.simplemusic.activities.MainActivity
import com.example.simplemusic.adapters.ArtistAlbumsAdapter
import com.example.simplemusic.models.multimediacontent.ArtistAlbum
import com.example.simplemusic.utils.Connectivity
import com.example.simplemusic.viewmodels.AlbumViewModel
import com.example.simplemusic.viewmodels.SongViewModel
import kotlinx.coroutines.launch

private const val SEARCH_PAGINATION = 20

class ArtistAlbumsFragment : Fragment(), ArtistAlbumsAdapter.ActionInterface {

    private lateinit var albumRv: RecyclerView
    private lateinit var albumsAdapter: ArtistAlbumsAdapter
    private lateinit var toolbar: Toolbar
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var progressBar: ProgressBar
    private lateinit var stateTv: TextView

    private val args: ArtistAlbumsFragmentArgs by navArgs()
    private val albumViewModel: AlbumViewModel by activityViewModels()
    private val songViewModel: SongViewModel by activityViewModels()
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
        val view = inflater.inflate(R.layout.fragment_artist_albums, container, false)
        initView(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigation
        navController = findNavController()

        // Toolbar
        setToolbar()

        // Observe when albums ready
        albumViewModel.albums.observe(viewLifecycleOwner, { albums ->
            // Request indicators off
            progressBar.visibility = View.GONE
            albumViewModel.searchingAlbums = false
            stateTv.visibility = View.GONE

            // Update artists data
            albumsAdapter.setAlbums(albums)

            // Restore list scroll
            albumRv.layoutManager?.onRestoreInstanceState(recyclerViewState);

            Log.println(Log.ERROR, "DEBUG", "request $pagination")//
        })

        // Init album list empty
        albumsAdapter = ArtistAlbumsAdapter(ArrayList(), this)
        linearLayoutManager = LinearLayoutManager(activity)
        albumRv.layoutManager = linearLayoutManager
        albumRv.adapter = albumsAdapter

        // Listener. At end of list request more albums data
        albumRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    if (!albumViewModel.searchingAlbums) {
                        // Save list scroll data
                        recyclerViewState = (albumRv.layoutManager as LinearLayoutManager).onSaveInstanceState()

                        // Request more albums data
                        pagination += SEARCH_PAGINATION
                        requestAlbums(args.artitstId, pagination)
                    }
                }
            }
        })

        // Start fetching albums
        requestAlbums(args.artitstId, pagination)
    }

    private fun initView(view: View) {
        albumRv = view.findViewById(R.id.albumRv)
        toolbar = view.findViewById(R.id.toolbar)
        progressBar = view.findViewById(R.id.progressBar)
        stateTv = view.findViewById(R.id.stateTv)

        progressBar.visibility = View.GONE
        stateTv.visibility = View.GONE
    }

    private fun setToolbar() {
        // Toolbar
        val navHostFragment = NavHostFragment.findNavController(this)
        toolbar.setupWithNavController( navHostFragment)
        // Title
        toolbar.title = albumViewModel.searchedArtist
    }

    private fun requestAlbums(artistId: Int, pagination: Int) {
        if (context?.let { Connectivity.isOnline(it) } == true) {
            progressBar.visibility = View.VISIBLE

            lifecycleScope.launch {
                albumViewModel.searchArtistAlbum(artistId,pagination)
            }
        } else {
            // If no internet and no artist data, show at least info
            if (albumViewModel.albums.value == null) {
                stateTv.text = getText(R.string.no_results)
                stateTv.visibility = View.VISIBLE
            }

            Toast.makeText(activity, R.string.no_internet, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClickAlbum(album: ArtistAlbum) {
        songViewModel.selectedAlbum = album.collectionName
        val action =  ArtistAlbumsFragmentDirections.actionArtistAlbumsFragmentToAlbumSongsFragment(album.collectionId!!)
        navController.navigate(action)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            ArtistAlbumsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}