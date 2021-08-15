package com.example.simplemusic.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplemusic.R
import com.example.simplemusic.adapters.AlbumSongsAdapter
import com.example.simplemusic.adapters.ArtistAlbumsAdapter
import com.example.simplemusic.models.multimediacontent.AlbumSong
import com.example.simplemusic.viewmodels.SongViewModel
import kotlinx.coroutines.launch

class AlbumSongsFragment : Fragment() {

    private lateinit var songRv: RecyclerView
    private lateinit var songsAdapter: AlbumSongsAdapter
    private lateinit var toolbar: Toolbar

    private val args: AlbumSongsFragmentArgs by navArgs()
    private lateinit var navController: NavController
    private val songViewModel: SongViewModel by activityViewModels()

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
            songsAdapter = AlbumSongsAdapter(songs, navController)
            songRv.adapter = songsAdapter
        })

        // Start fetching songs
        lifecycleScope.launch {
            songViewModel.searchAlbumSongs(args.albumId,20)
        }

        // Init songs list empty
        songsAdapter = AlbumSongsAdapter(ArrayList(), navController)
        songRv.layoutManager = GridLayoutManager(activity, 2)
        songRv.adapter = songsAdapter
    }

    private fun initView(view: View) {
        songRv = view.findViewById(R.id.songRv)
        toolbar = view.findViewById(R.id.toolbar)
    }

    private fun setToolbar() {
        // Toolbar
        val navHostFragment = NavHostFragment.findNavController(this)
        toolbar.setupWithNavController( navHostFragment)
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