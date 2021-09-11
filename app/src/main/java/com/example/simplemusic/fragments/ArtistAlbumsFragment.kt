package com.example.simplemusic.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplemusic.R
import com.example.simplemusic.adapters.ArtistAlbumsAdapter
import com.example.simplemusic.models.multimediacontent.ArtistAlbum
import com.example.simplemusic.utils.Connectivity
import com.example.simplemusic.viewmodels.AlbumViewModel
import com.example.simplemusic.viewmodels.SongViewModel
import kotlinx.android.synthetic.main.fragment_artist_albums.*
import kotlinx.coroutines.launch

private const val SEARCH_PAGINATION = 20

/**
 * Shows albums from an artist.
 */
class ArtistAlbumsFragment : Fragment(), ArtistAlbumsAdapter.ActionInterface {

    private lateinit var albumsAdapter: ArtistAlbumsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private val args: ArtistAlbumsFragmentArgs by navArgs()
    private val albumViewModel: AlbumViewModel by activityViewModels()
    private val songViewModel: SongViewModel by activityViewModels()
    private lateinit var navController: NavController
    // List scroll
    private var recyclerViewState: Parcelable? = null
    private var pagination = SEARCH_PAGINATION

    private var waitShare = false


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
        return inflater.inflate(R.layout.fragment_artist_albums, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

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

            //Log.println(Log.ERROR, "DEBUG", "request $pagination")//
            if (albums.isEmpty()) {
                stateTv.visibility = View.VISIBLE
                stateTv.text = getText(R.string.no_results)

                // Check if it is because internet
                if (context?.let { Connectivity.isOnline(it) } == false) {
                    Toast.makeText(activity, R.string.no_internet, Toast.LENGTH_SHORT).show()
                }

            } else {
                stateTv.visibility = View.GONE
            }
        })

        // Init album list empty
        initEmptyList()

        // Listener. At end of list request more albums data
        albumRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val size = albumViewModel.albums.value?.size ?: Int.MAX_VALUE
                // If end of list and there is data to continue
                if (!recyclerView.canScrollVertically(1) && pagination <= size) {
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

        // Go to music videos
        musicVideosBtn.setOnClickListener {
            val action = ArtistAlbumsFragmentDirections.actionArtistAlbumsFragmentToArtistMusicVideosFragment()
            navController.navigate(action)
        }

        // Start fetching albums
        requestAlbums(args.artitstId, pagination)
    }

    private fun initView() {
        progressBar.visibility = View.GONE
        stateTv.visibility = View.GONE
    }

    private fun initEmptyList() {
        // Init album list empty
        albumsAdapter = ArtistAlbumsAdapter(ArrayList(), this)
        linearLayoutManager = LinearLayoutManager(activity)
        albumRv.layoutManager = linearLayoutManager
        albumRv.adapter = albumsAdapter
    }

    private fun setToolbar() {
        // Toolbar
        val navHostFragment = NavHostFragment.findNavController(this)
        toolbar.setupWithNavController( navHostFragment)
        // Title
        toolbar.title = albumViewModel.searchedArtist

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> {
                waitShare = true
                Toast.makeText(activity, R.string.select_album, Toast.LENGTH_SHORT).show()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Start request to get albums.
     */
    private fun requestAlbums(artistId: Int, pagination: Int) {
        progressBar.visibility = View.VISIBLE

        // Start request
        lifecycleScope.launch {
            albumViewModel.searchArtistAlbum(artistId,pagination)
        }
    }

    /**
     * Click album. Goes to album songs or share it.
     */
    override fun onClickAlbum(album: ArtistAlbum) {
        if (waitShare) {
            waitShare = false
            // Share artist and album
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, albumViewModel.searchedArtist + " - " + album.collectionName)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, albumViewModel.searchedArtist + " - " + album.collectionName)
            startActivity(shareIntent)
        } else {
            songViewModel.selectedAlbum = album.collectionName
            val action =
                ArtistAlbumsFragmentDirections.actionArtistAlbumsFragmentToAlbumSongsFragment(album.collectionId!!)
            navController.navigate(action)
        }
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