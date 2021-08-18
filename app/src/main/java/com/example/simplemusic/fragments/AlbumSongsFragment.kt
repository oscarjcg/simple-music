package com.example.simplemusic.fragments

import android.content.res.Configuration
import android.media.AudioAttributes
import android.media.MediaPlayer
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
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
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
import com.example.simplemusic.models.multimediacontent.AlbumSong
import com.example.simplemusic.utils.Connectivity
import com.example.simplemusic.viewmodels.SongViewModel
import com.example.simplemusic.viewmodels.UserViewModel
import kotlinx.coroutines.launch



private const val SEARCH_PAGINATION = 20
private const val GRID_SIZE_PORTRAIT = 2
private const val GRID_SIZE_LANDSCAPE = 3

/**
 * Shows songs from an album.
 */
class AlbumSongsFragment : Fragment(), AlbumSongsAdapter.ActionInterface {

    private lateinit var songRv: RecyclerView
    private lateinit var songsAdapter: AlbumSongsAdapter
    private lateinit var toolbar: Toolbar
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var progressBar: ProgressBar
    private lateinit var stateTv: TextView

    private val args: AlbumSongsFragmentArgs by navArgs()
    private lateinit var navController: NavController
    private val songViewModel: SongViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    // List scroll
    private var recyclerViewState: Parcelable? = null
    private var pagination = SEARCH_PAGINATION

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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_album_songs, container, false)
        initView(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigation
        navController = findNavController()

        // Toolbar
        setToolbar()

        // Observe when songs are ready
        songViewModel.songs.observe(viewLifecycleOwner, { songs ->
            // Request indicators off
            progressBar.visibility = View.GONE
            songViewModel.searchingSongs = false
            stateTv.visibility = View.GONE

            // Update songs data
            songsAdapter.setSongs(songs)

            // Restore list scroll
            songRv.layoutManager?.onRestoreInstanceState(recyclerViewState);

            //Log.println(Log.ERROR, "DEBUG", "request $pagination")//
        })

        // Init songs list empty
        initListEmpty()

        // Listener. At end of list request more albums data
        songRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val size = songViewModel.songs.value?.size ?: Int.MAX_VALUE
                // If end of list and there is data to continue
                if (!recyclerView.canScrollVertically(1) && pagination <= size) {
                    if (!songViewModel.searchingSongs) {
                        // Save list scroll data
                        recyclerViewState = (songRv.layoutManager as GridLayoutManager).onSaveInstanceState()

                        // Request more albums data
                        pagination += SEARCH_PAGINATION
                        requestSongs(args.albumId, pagination)
                    }
                }
            }
        })

        // Load liked songs
        loadLikes()

        // Start fetching songs
        requestSongs(args.albumId, pagination)
    }

    private fun initView(view: View) {
        songRv = view.findViewById(R.id.songRv)
        toolbar = view.findViewById(R.id.toolbar)
        progressBar = view.findViewById(R.id.progressBar)
        stateTv = view.findViewById(R.id.stateTv)

        progressBar.visibility = View.GONE
        stateTv.visibility = View.GONE
    }

    private fun initListEmpty() {
        // Init songs list empty
        songsAdapter = AlbumSongsAdapter(ArrayList(), activity as MainActivity, this, ArrayList())
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            gridLayoutManager = GridLayoutManager(activity, GRID_SIZE_PORTRAIT)
        else
            gridLayoutManager = GridLayoutManager(activity, GRID_SIZE_LANDSCAPE)
        songRv.layoutManager = gridLayoutManager
        songRv.adapter = songsAdapter
    }

    private fun setToolbar() {
        // Toolbar
        val navHostFragment = NavHostFragment.findNavController(this)
        toolbar.setupWithNavController( navHostFragment)
        // Title
        toolbar.title = songViewModel.selectedAlbum
    }

    private fun requestSongs(albumId: Int, pagination: Int) {
        if (context?.let { Connectivity.isOnline(it) } == true) {
            progressBar.visibility = View.VISIBLE

            // Start request
            lifecycleScope.launch {
                songViewModel.searchAlbumSongs(albumId, pagination)
            }
        } else {
            // If no internet and no artist data, show at least info
            if (songViewModel.songs.value == null) {
                stateTv.text = getText(R.string.no_results)
                stateTv.visibility = View.VISIBLE
            }

            Toast.makeText(activity, R.string.no_internet, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Retrieves liked song ids for the adapter list.
     */
    private fun loadLikes() {
        lifecycleScope.launch {
            userViewModel.user.value?.let {
                val likedTracksId = userViewModel.getUserLikesTrack(it.userId)
                songsAdapter.setLikesSongId(likedTracksId)
            }
        }
    }

    /**
     * Click like button. Either save or delete. Updates adapter list.
     */
    override fun onClickLike(song: AlbumSong) {
        lifecycleScope.launch {
            userViewModel.user.value?.let {
                // Switch like . Set or remove like
                if (song.like!!)
                    userViewModel.deleteUserLikesTrack(it.userId, song.trackId!!)
                else
                    userViewModel.addUserLikesTrack(it.userId, song.trackId!!)
                song.like = !song.like!!

                // Update liked songs id list
                val likedTracksId = userViewModel.getUserLikesTrack(it.userId)
                songsAdapter.setLikesSongId(likedTracksId)
            }
        }
    }

    /**
     * Click play button. Play a song.
     */
    override fun onClickPlay(song: AlbumSong) {
        // If already playing something don't do anything, we have a button pause
        if (mediaPlayer?.isPlaying == true) {
            return
        }

        // Start a playing and release on completion
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(song.previewUrl)
            prepare()
            start()
        }.also { mp ->
            mp.setOnCompletionListener { releasePlayer() }
        }
    }

    /**
     * Click pause button. Stop playing song.
     */
    override fun onClickPause(song: AlbumSong) {
        releasePlayer()
    }

    /**
     * Stops media player by releasing it.
     */
    private fun releasePlayer() {
        // Release audio player
        mediaPlayer?.release()
        mediaPlayer = null
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