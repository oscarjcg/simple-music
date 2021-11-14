package com.example.simplemusic.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplemusic.R
import com.example.simplemusic.adapters.ArtistAlbumsAdapter
import com.example.simplemusic.databinding.FragmentArtistAlbumsBinding
import com.example.simplemusic.utils.UIEvent
import com.example.simplemusic.models.multimediacontent.ArtistAlbum
import com.example.simplemusic.utils.Connectivity
import com.example.simplemusic.viewmodels.AlbumViewModel
import com.example.simplemusic.viewmodels.SongViewModel

/**
 * Shows albums from an artist.
 */
class ArtistAlbumsFragment : Fragment(), ArtistAlbumsAdapter.ActionInterface {

    private lateinit var albumsAdapter: ArtistAlbumsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var binding: FragmentArtistAlbumsBinding
    private val args: ArtistAlbumsFragmentArgs by navArgs()
    private val albumViewModel: AlbumViewModel by activityViewModels()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        albumViewModel.resetPagination()

        // Inflate the layout for this fragment
        binding = FragmentArtistAlbumsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = albumViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setToolbar()

        observeAlbums()

        observeUIEvents()

        initEmptyList()

        // Listener. At end of list request more albums data
        albumListOnEndListener()

        // Go to music videos
        binding.musicVideos.setOnClickListener {
            val action = ArtistAlbumsFragmentDirections
                .actionArtistAlbumsFragmentToArtistMusicVideosFragment(args.artistName)
            navController.navigate(action)
        }

        requestAlbums(args.artistId)
    }

    private fun initEmptyList() {
        albumsAdapter = ArtistAlbumsAdapter(ArrayList(), this)
        linearLayoutManager = LinearLayoutManager(activity)
        binding.albums.layoutManager = linearLayoutManager
        binding.albums.adapter = albumsAdapter

        albumViewModel.albums.value = ArrayList()
    }

    private fun setToolbar() {
        // Toolbar
        val navHostFragment = NavHostFragment.findNavController(this)
        binding.toolbar.setupWithNavController(navHostFragment)
        binding.toolbar.title = args.artistName

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
    }

    private fun observeAlbums() {
        albumViewModel.albums.observe(viewLifecycleOwner, { albums ->
            albumsAdapter.setAlbums(albums)

            // Restore list scroll
            binding.albums.layoutManager?.onRestoreInstanceState(albumViewModel.recyclerViewState)
        })
    }

    private fun observeUIEvents() {
        albumViewModel.uiState.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { uiEvent ->
                handleUIEvent(uiEvent)
            }
        })
    }

    private fun handleUIEvent(event: UIEvent<Nothing>) {
        when (event) {
            is UIEvent.CheckInternet -> {
                isInternetAvailable()
            }
            is UIEvent.EmptyList -> {
                albumViewModel.setStateInfo(true, getText(R.string.no_results) as String)
            }
        }
    }

    private fun isInternetAvailable(): Boolean {
        if (context?.let { Connectivity.isOnline(it) } == false) {
            Toast.makeText(activity, R.string.no_internet, Toast.LENGTH_SHORT)
                .show()
            return false
        }
        return true
    }

    private fun albumListOnEndListener() {
        binding.albums.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                // If end of list and there is data to continue
                if (!recyclerView.canScrollVertically(1) && albumViewModel.canGetMoreData()) {
                    if (!albumViewModel.loading.value!!) {
                        // Save list scroll data
                        albumViewModel.recyclerViewState =
                            (binding.albums.layoutManager as LinearLayoutManager).onSaveInstanceState()

                        requestAlbums(args.artistId)
                    }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> {
                albumViewModel.waitShare = true
                Toast.makeText(activity, R.string.select_album, Toast.LENGTH_SHORT).show()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Start request to get albums.
     */
    private fun requestAlbums(artistId: Int) {
        albumViewModel.searchArtistAlbum(artistId)
    }

    /**
     * Click album. Goes to album songs or share it.
     */
    override fun onClickAlbum(album: ArtistAlbum) {
        if (albumViewModel.waitShare) {
            albumViewModel.waitShare = false
            // Share artist and album
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, args.artistName + " - " + album.collectionName)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, args.artistName + " - " + album.collectionName)
            startActivity(shareIntent)
        } else {
            val action =
                ArtistAlbumsFragmentDirections
                    .actionArtistAlbumsFragmentToAlbumSongsFragment(album.collectionId!!, album.collectionName!!, args.artistName)
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
