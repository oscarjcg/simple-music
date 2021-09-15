package com.example.simplemusic.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplemusic.R
import com.example.simplemusic.adapters.ArtistAlbumsAdapter
import com.example.simplemusic.adapters.ArtistMusicVideosAdapter
import com.example.simplemusic.models.multimediacontent.MusicVideo
import com.example.simplemusic.utils.Connectivity
import com.example.simplemusic.viewmodels.AlbumViewModel
import com.example.simplemusic.viewmodels.MusicVideoViewModel
import kotlinx.android.synthetic.main.fragment_artist_music_videos.*
import kotlinx.coroutines.launch

/**
 * Shows a music video list. It can play videos.
 */
class ArtistMusicVideosFragment : Fragment(), ArtistMusicVideosAdapter.ActionInterface {

    private lateinit var linearLayoutManager: LinearLayoutManager

    private val albumViewModel: AlbumViewModel by activityViewModels()
    private val musicVideoViewModel: MusicVideoViewModel by activityViewModels()
    private val args: ArtistAlbumsFragmentArgs by navArgs()
    private lateinit var musicVideosAdapter: ArtistMusicVideosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        musicVideoViewModel.resetPagination()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artist_music_videos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        // Toolbar
        setToolbar()

        // Observe when music videos ready
        musicVideoViewModel.musicVideos.observe(viewLifecycleOwner, { musicVideos ->
            // Request indicators off
            progressBar.visibility = View.GONE
            musicVideoViewModel.searchingMusicVideos = false
            stateTv.visibility = View.GONE

            // Update artists data
            musicVideosAdapter.setMusicVideos(musicVideos)

            // Restore list scroll
            musicVideosRv.layoutManager?.onRestoreInstanceState(musicVideoViewModel.recyclerViewState)

            if (musicVideos.isEmpty()) {
                stateTv.text = getText(R.string.no_results)
                stateTv.visibility = View.VISIBLE

                // Check if it is because internet
                if (context?.let { Connectivity.isOnline(it) } == false) {
                    Toast.makeText(activity, R.string.no_internet, Toast.LENGTH_SHORT).show()
                }
            } else {
                stateTv.visibility = View.GONE
            }

            //Log.println(Log.ERROR, "DEBUG", "request $pagination")//
        })

        // Init music video list empty
        initEmptyList()

        // Listener. At end of list request more music videos data
        musicVideosRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val size = musicVideoViewModel.musicVideos.value?.size ?: Int.MAX_VALUE
                // If end of list and there is data to continue
                if (!recyclerView.canScrollVertically(1) && musicVideoViewModel.canGetMoreData()) {
                    if (!musicVideoViewModel.searchingMusicVideos) {
                        // Save list scroll data
                        musicVideoViewModel.recyclerViewState = (musicVideosRv.layoutManager as LinearLayoutManager).onSaveInstanceState()

                        // Request more albums data
                        albumViewModel.searchedArtist?.let { requestMusicVideos(it) }
                    }
                }
            }
        })

        // Click close button when playing video
        closeBtn.setOnClickListener {
            closeVideoPlayer()
        }

        // Start fetching music videos
        albumViewModel.searchedArtist?.let { requestMusicVideos(it) }
    }

    private fun initView() {
        progressBar.visibility = View.GONE
        stateTv.visibility = View.GONE
        videoView.visibility = View.GONE
        closeBtn.visibility = View.GONE
    }

    private fun setToolbar() {
        // Toolbar
        val navHostFragment = NavHostFragment.findNavController(this)
        toolbar.setupWithNavController( navHostFragment)
        // Title
        toolbar.title = getString(R.string.music_videos)

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
                musicVideoViewModel.waitShare = true
                Toast.makeText(activity, R.string.select_music_video, Toast.LENGTH_SHORT).show()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initEmptyList() {
        // Init music video list empty
        musicVideosAdapter = ArtistMusicVideosAdapter(ArrayList(), this)
        linearLayoutManager = LinearLayoutManager(activity)
        musicVideosRv.layoutManager = linearLayoutManager
        musicVideosRv.adapter = musicVideosAdapter
    }

    /**
     * Start request to get music videos.
     */
    private fun requestMusicVideos(artistName: String) {
        progressBar.visibility = View.VISIBLE

        // Start request
        lifecycleScope.launch {
            musicVideoViewModel.searchArtistMusicVideos(artistName)
        }
    }

    /**
     * Click video to share.
     */
    override fun onClickMusicVideo(musicVideo: MusicVideo) {
        if (musicVideoViewModel.waitShare) {
            musicVideoViewModel.waitShare = false
            // Share artist and music video
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, albumViewModel.searchedArtist + " - " + musicVideo.trackName)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, albumViewModel.searchedArtist + " - " + musicVideo.trackName)
            startActivity(shareIntent)
        }
    }

    /**
     * Click play video. Starts to play a video.
     */
    override fun onPlayMusicVideo(musicVideo: MusicVideo) {
        if (context?.let { Connectivity.isOnline(it) } == false) {
            Toast.makeText(activity, R.string.no_internet, Toast.LENGTH_SHORT).show()
            return
        }

        startVideoPlayer(musicVideo)
    }

    /**
     * Show ui elements of the video player.
     */
    private fun showVideoPlayer() {
        // Turn on indicators
        videoView.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        closeBtn.visibility = View.VISIBLE
    }

    /**
     * Hide ui elements of the video player.
     */
    private fun hideVideoPlayer() {
        videoView.visibility = View.GONE
        progressBar.visibility = View.GONE
        closeBtn.visibility = View.GONE
    }

    /**
     * Play a video. CLose at the end.
     */
    private fun startVideoPlayer(musicVideo: MusicVideo) {
        // Turn on indicators
        showVideoPlayer()

        // Build player
        val uri = Uri.parse(musicVideo.previewUrl)
        videoView.setMediaController(MediaController(context))
        videoView.setVideoURI(uri)
        videoView.requestFocus()

        // Listeners
        videoView.setOnPreparedListener {
            progressBar.visibility = View.GONE
        }
        videoView.setOnCompletionListener {
            closeVideoPlayer()
        }

        // Start
        videoView.start()
    }

    /**
     * Stop video and close player.
     */
    private fun closeVideoPlayer() {
        // Stop video if it's playing
        if (videoView.isPlaying)
            videoView.stopPlayback()
        // Turn off indicators
        hideVideoPlayer()
    }

    override fun onPause() {
        closeVideoPlayer()
        super.onPause()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            ArtistMusicVideosFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }


}