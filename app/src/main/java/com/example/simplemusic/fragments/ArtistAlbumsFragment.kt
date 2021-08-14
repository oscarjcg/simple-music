package com.example.simplemusic.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplemusic.R
import com.example.simplemusic.adapters.ArtistAlbumsAdapter
import com.example.simplemusic.viewmodels.AlbumViewModel
import kotlinx.coroutines.launch


class ArtistAlbumsFragment : Fragment() {

    private lateinit var albumRv: RecyclerView
    private lateinit var albumsAdapter: ArtistAlbumsAdapter

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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_artist_albums, container, false)
        initView(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigation
        navController = findNavController()

        // Observe when albums ready
        albumViewModel.albums.observe(viewLifecycleOwner, { albums ->
            albumsAdapter = ArtistAlbumsAdapter(albums, navController)
            albumRv.adapter = albumsAdapter
        })

        // Start fetching albums
        lifecycleScope.launch {
            albumViewModel.searchArtistAlbum(args.artitstId,20)
        }

        // Init album list empty
        albumsAdapter = ArtistAlbumsAdapter(ArrayList(), navController)
        albumRv.layoutManager = LinearLayoutManager(activity)
        albumRv.adapter = albumsAdapter
    }

    private fun initView(view: View) {
        albumRv = view.findViewById(R.id.albumRv)
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