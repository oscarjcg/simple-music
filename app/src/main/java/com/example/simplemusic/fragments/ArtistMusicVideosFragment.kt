package com.example.simplemusic.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplemusic.R
import com.example.simplemusic.adapters.ArtistMusicVideosAdapter
import com.example.simplemusic.databinding.FragmentArtistMusicVideosBinding
import com.example.simplemusic.utils.UIEvent
import com.example.simplemusic.models.multimediacontent.MusicVideo
import com.example.simplemusic.utils.Connectivity
import com.example.simplemusic.viewmodels.MusicVideoViewModel

/**
 * Shows a music video list. It can play videos.
 */
class ArtistMusicVideosFragment : Fragment(), ArtistMusicVideosAdapter.ActionInterface {

    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var binding: FragmentArtistMusicVideosBinding
    private val musicVideoViewModel: MusicVideoViewModel by activityViewModels()
    private val args: ArtistMusicVideosFragmentArgs by navArgs()
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
        binding = FragmentArtistMusicVideosBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = musicVideoViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar()

        observeMusicVideos()

        observeUIEvents()

        initEmptyList()

        // Listener. At end of list request more music videos data
        albumListOnEndListener()

        // Click close button when playing video
        binding.close.setOnClickListener {
            closeVideoPlayer()
        }

        requestMusicVideos(args.artistName)
    }

    private fun setToolbar() {
        // Toolbar
        val navHostFragment = NavHostFragment.findNavController(this)
        binding.toolbar.setupWithNavController( navHostFragment)
        binding.toolbar.title = getString(R.string.music_videos)

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
                musicVideoViewModel.waitShare = true
                Toast.makeText(activity, R.string.select_music_video, Toast.LENGTH_SHORT).show()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initEmptyList() {
        musicVideosAdapter = ArtistMusicVideosAdapter(ArrayList(), this)
        linearLayoutManager = LinearLayoutManager(activity)
        binding.musicVideos.layoutManager = linearLayoutManager
        binding.musicVideos.adapter = musicVideosAdapter

        musicVideoViewModel.musicVideos.value = ArrayList()
    }

    private fun observeMusicVideos() {
        musicVideoViewModel.musicVideos.observe(viewLifecycleOwner, { musicVideos ->
            musicVideosAdapter.setMusicVideos(musicVideos)

            // Restore list scroll
            binding.musicVideos.layoutManager?.onRestoreInstanceState(musicVideoViewModel.recyclerViewState)
        })
    }

    private fun observeUIEvents() {
        musicVideoViewModel.uiState.observe(viewLifecycleOwner, {
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
                musicVideoViewModel.setStateInfo(true, getText(R.string.no_results) as String)
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
        binding.musicVideos.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                // If end of list and there is data to continue
                if (!recyclerView.canScrollVertically(1) && musicVideoViewModel.canGetMoreData()) {
                    if (!musicVideoViewModel.loading.value!!) {
                        // Save list scroll data
                        musicVideoViewModel.recyclerViewState = (binding.musicVideos.layoutManager as LinearLayoutManager).onSaveInstanceState()

                        requestMusicVideos(args.artistName)
                    }
                }
            }
        })
    }

    private fun requestMusicVideos(artistName: String) {
        musicVideoViewModel.searchArtistMusicVideos(artistName)
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
                putExtra(Intent.EXTRA_TEXT, args.artistName + " - " + musicVideo.trackName)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, args.artistName + " - " + musicVideo.trackName)
            startActivity(shareIntent)
        }
    }

    /**
     * Click play video. Starts to play a video.
     */
    override fun onPlayMusicVideo(musicVideo: MusicVideo) {
        if (!isInternetAvailable()) {
            return
        }

        startVideoPlayer(musicVideo)
    }

    /**
     * Play a video. CLose at the end.
     */
    private fun startVideoPlayer(musicVideo: MusicVideo) {
        musicVideoViewModel.setShowVideoPlayer(true)

        // Build player
        val uri = Uri.parse(musicVideo.previewUrl)
        binding.video.setMediaController(MediaController(context))
        binding.video.setVideoURI(uri)
        binding.video.requestFocus()

        // Listeners
        binding.video.setOnPreparedListener {
            musicVideoViewModel.setLoading(false)
        }
        binding.video.setOnCompletionListener {
            closeVideoPlayer()
        }

        binding.video.start()
    }

    /**
     * Stop video and close player.
     */
    private fun closeVideoPlayer() {
        if (binding.video.isPlaying)
            binding.video.stopPlayback()

        musicVideoViewModel.setShowVideoPlayer(false)
    }

    override fun onPause() {
        closeVideoPlayer()
        super.onPause()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ArtistMusicVideosFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
