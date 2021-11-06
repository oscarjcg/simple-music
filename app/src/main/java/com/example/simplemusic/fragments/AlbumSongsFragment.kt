package com.example.simplemusic.fragments

import android.content.Intent
import android.content.res.Configuration
import android.media.AudioAttributes
import android.media.MediaPlayer
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplemusic.R
import com.example.simplemusic.activities.MainActivity
import com.example.simplemusic.adapters.AlbumSongsAdapter
import com.example.simplemusic.databinding.FragmentAlbumSongsBinding
import com.example.simplemusic.models.UIEvent
import com.example.simplemusic.models.multimediacontent.AlbumSong
import com.example.simplemusic.utils.Connectivity
import com.example.simplemusic.viewmodels.AlbumViewModel
import com.example.simplemusic.viewmodels.SongViewModel
import com.example.simplemusic.viewmodels.UserViewModel

private const val GRID_SIZE_PORTRAIT = 2
private const val GRID_SIZE_LANDSCAPE = 3

/**
 * Shows songs from an album.
 */
class AlbumSongsFragment : Fragment(), AlbumSongsAdapter.ActionInterface {

    private lateinit var songsAdapter: AlbumSongsAdapter
    private lateinit var gridLayoutManager: GridLayoutManager

    private lateinit var binding: FragmentAlbumSongsBinding
    private val args: AlbumSongsFragmentArgs by navArgs()
    private lateinit var navController: NavController
    private val songViewModel: SongViewModel by activityViewModels()
    private val albumViewModel: AlbumViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()


    // Audio player
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        songViewModel.resetPagination()

        // Inflate the layout for this fragment
        binding = FragmentAlbumSongsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = songViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setToolbar()

        observeSongs()

        observeUIEvents()

        initListEmpty()

        // Listener. At end of list request more albums data
        songListOnEndListener()

        loadLikes()

        // Click pause button. Stop playing song
        binding.pauseBtn.setOnClickListener {
            releasePlayer()
        }

        requestSongs(args.albumId)
    }

    private fun initListEmpty() {
        // Init songs list empty
        songsAdapter = AlbumSongsAdapter(ArrayList(), activity as MainActivity, this, ArrayList())
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            gridLayoutManager = GridLayoutManager(activity, GRID_SIZE_PORTRAIT)
        else
            gridLayoutManager = GridLayoutManager(activity, GRID_SIZE_LANDSCAPE)
        binding.songRv.layoutManager = gridLayoutManager
        binding.songRv.adapter = songsAdapter

        songViewModel.songs.value = ArrayList()
    }

    private fun setToolbar() {
        // Toolbar
        val navHostFragment = NavHostFragment.findNavController(this)
        binding.toolbar.setupWithNavController( navHostFragment)
        // Title
        binding.toolbar.title = songViewModel.selectedAlbum

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> {
                songViewModel.waitShare = true
                Toast.makeText(activity, R.string.select_song, Toast.LENGTH_SHORT).show()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun requestSongs(albumId: Long) {
        songViewModel.searchAlbumSongs(albumId)
    }

    /**
     * Retrieves liked song ids for the adapter list.
     */
    private fun loadLikes() {
        userViewModel.user.value?.let { userViewModel.getUserLikesTrack(it.userId) }
    }

    private fun observeSongs() {
        songViewModel.songs.observe(viewLifecycleOwner, { songs ->
            // Update songs data
            songsAdapter.setSongs(songs)

            // Restore list scroll
            binding.songRv.layoutManager?.onRestoreInstanceState(songViewModel.recyclerViewState)

            //Log.println(Log.ERROR, "DEBUG", "request $pagination")//
        })

        userViewModel.likedTracksId.observe(viewLifecycleOwner, { liked ->
            songsAdapter.setLikesSongId(liked)
        })
    }

    private fun observeUIEvents() {
        songViewModel.uiState.observe(viewLifecycleOwner, {
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
                songViewModel.setStateInfo(true, getText(R.string.no_results) as String)
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

    private fun songListOnEndListener() {
        binding.songRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                // If end of list and there is data to continue
                if (!recyclerView.canScrollVertically(1) && songViewModel.canGetMoreData()) {
                    if (!songViewModel.loading.value!!) {
                        // Save list scroll data
                        songViewModel.recyclerViewState =
                            (binding.songRv.layoutManager as GridLayoutManager).onSaveInstanceState()

                        // Request more songs data
                        requestSongs(args.albumId)
                    }
                }
            }
        })
    }

    /**
     * Click like button. Either save or delete. Updates adapter list.
     */
    override fun onClickLike(song: AlbumSong) {
        userViewModel.userLikesTrack(song)
    }

    /**
     * Click play button. Play a song.
     */
    override fun onClickPlay(song: AlbumSong) {
        if (!isInternetAvailable()) {
            return
        }

        // If already playing something don't do anything, reset before
        if (mediaPlayer?.isPlaying == true) {
            releasePlayer()
        }

        startMediaPlayer(song)
    }

    private fun startMediaPlayer(song: AlbumSong) {
        songViewModel.setLoading(true)

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(song.previewUrl)
            prepare()
            setOnPreparedListener {
                songViewModel.setLoading(false)
            }
            start()

            song.trackName?.let {
                songViewModel.setPlayingSong(true, it)
            } ?: run {
                songViewModel.setPlayingSong(true)
            }
        }.also { mp ->
            mp.setOnCompletionListener {
                releasePlayer()
            }
        }
    }

    /**
     * Click a song to share it.
     */
    override fun onClickSong(song: AlbumSong) {
        if (songViewModel.waitShare) {
            songViewModel.waitShare = false
            // Share artist and song
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, albumViewModel.searchedArtist + " - " + song.trackName)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, albumViewModel.searchedArtist + " - " + song.trackName)
            startActivity(shareIntent)
        }
    }

    /**
     * Stops media player by releasing it.
     */
    private fun releasePlayer() {
        // Release audio player
        mediaPlayer?.release()
        mediaPlayer = null
        songViewModel.setPlayingSong(false)
    }

    override fun onPause() {
        // Release audio player
        releasePlayer()

        super.onPause()
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            AlbumSongsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
