package com.example.simplemusic.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.example.simplemusic.databinding.FragmentArtistAlbumsBinding
import com.example.simplemusic.models.UIEvent
import com.example.simplemusic.models.multimediacontent.ArtistAlbum
import com.example.simplemusic.utils.Connectivity
import com.example.simplemusic.viewmodels.AlbumViewModel
import com.example.simplemusic.viewmodels.SongViewModel
//import kotlinx.android.synthetic.main.fragment_artist_albums.*
import kotlinx.coroutines.launch

/**
 * Shows albums from an artist.
 */
class ArtistAlbumsFragment : Fragment(), ArtistAlbumsAdapter.ActionInterface {

    private lateinit var albumsAdapter: ArtistAlbumsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var binding: FragmentArtistAlbumsBinding
    private val args: ArtistAlbumsFragmentArgs by navArgs()
    private val albumViewModel: AlbumViewModel by activityViewModels()
    private val songViewModel: SongViewModel by activityViewModels()
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
        binding.musicVideosBtn.setOnClickListener {
            val action = ArtistAlbumsFragmentDirections.actionArtistAlbumsFragmentToArtistMusicVideosFragment()
            navController.navigate(action)
        }

        requestAlbums(args.artitstId)
    }

    private fun initEmptyList() {
        albumsAdapter = ArtistAlbumsAdapter(ArrayList(), this)
        linearLayoutManager = LinearLayoutManager(activity)
        binding.albumRv.layoutManager = linearLayoutManager
        binding.albumRv.adapter = albumsAdapter

        albumViewModel.albums.value = ArrayList()
    }

    private fun setToolbar() {
        // Toolbar
        val navHostFragment = NavHostFragment.findNavController(this)
        binding.toolbar.setupWithNavController(navHostFragment)
        // Title
        binding.toolbar.title = albumViewModel.searchedArtist

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
    }

    private fun observeAlbums() {
        albumViewModel.albums.observe(viewLifecycleOwner, { albums ->
            // Update artists data
            albumsAdapter.setAlbums(albums)

            // Restore list scroll
            binding.albumRv.layoutManager?.onRestoreInstanceState(albumViewModel.recyclerViewState)
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
        binding.albumRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                // If end of list and there is data to continue
                if (!recyclerView.canScrollVertically(1) && albumViewModel.canGetMoreData()) {
                    if (!albumViewModel.loading.value!!) {
                        // Save list scroll data
                        albumViewModel.recyclerViewState =
                            (binding.albumRv.layoutManager as LinearLayoutManager).onSaveInstanceState()

                        // Request more albums data
                        requestAlbums(args.artitstId)
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
        binding.progressBar.visibility = View.VISIBLE

        // Start request
        lifecycleScope.launch {
            albumViewModel.searchArtistAlbum(artistId)
        }
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
