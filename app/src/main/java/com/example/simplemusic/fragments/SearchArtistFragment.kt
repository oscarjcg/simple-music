package com.example.simplemusic.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplemusic.R
import com.example.simplemusic.activities.MainActivity
import com.example.simplemusic.adapters.ArtistAdapter
import com.example.simplemusic.viewmodels.ArtistViewModel
import kotlinx.coroutines.launch

class SearchArtistFragment : Fragment() {

    private lateinit var searchEt: EditText
    private lateinit var artistRv: RecyclerView
    private lateinit var artistAdapter: ArtistAdapter

    private val artistViewModel: ArtistViewModel by activityViewModels()
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
        val view = inflater.inflate(R.layout.fragment_search_artist, container, false)

        initView(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigation
        navController = findNavController()

        artistViewModel.artists.observe(viewLifecycleOwner, { artists ->
            //Toast.makeText(this, artists.size.toString(), Toast.LENGTH_SHORT).show()
            artistAdapter = ArtistAdapter(artists, navController)
            artistRv.adapter = artistAdapter
        })

        // Search
        searchEt.doAfterTextChanged {
            lifecycleScope.launch {
                artistViewModel.searchArtist(searchEt.text.toString(),20)
            }
        }

        // Init artist list empty
        artistAdapter = ArtistAdapter(ArrayList(), navController)
        artistRv.layoutManager = LinearLayoutManager(context)
        artistRv.adapter = artistAdapter


    }

    private fun initView(view: View) {
        artistRv = view.findViewById(R.id.artistRv)
        searchEt = view.findViewById(R.id.searchEt)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            SearchArtistFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}