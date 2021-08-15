package com.example.simplemusic.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplemusic.R
import com.example.simplemusic.activities.MainActivity
import com.example.simplemusic.adapters.ArtistAdapter
import com.example.simplemusic.viewmodels.ArtistViewModel
import kotlinx.coroutines.launch

private const val SEARCH_DEFAULT = "a"
private const val SEARCH_DEFAULT_SIZE = 20

class SearchArtistFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var artistRv: RecyclerView
    private lateinit var artistAdapter: ArtistAdapter
    private lateinit var toolbar: Toolbar

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

        // Toolbar
        setToolbar()

        // Observe artists
        artistViewModel.artists.observe(viewLifecycleOwner, { artists ->
            //Toast.makeText(this, artists.size.toString(), Toast.LENGTH_SHORT).show()
            artistAdapter = ArtistAdapter(artists, navController)
            artistRv.adapter = artistAdapter
        })

        // Init artist list empty
        artistAdapter = ArtistAdapter(ArrayList(), navController)
        artistRv.layoutManager = LinearLayoutManager(context)
        artistRv.adapter = artistAdapter

        // Default search when no text
        if (artistViewModel.searchedArtist == null)
            defaultSearch()
    }

    private fun initView(view: View) {
        artistRv = view.findViewById(R.id.artistRv)
        toolbar = view.findViewById(R.id.toolbar)
    }

    private fun setToolbar() {
        // Toolbar
        val navHostFragment = NavHostFragment.findNavController(this)
        val config = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController( navHostFragment, config)

        // Menu which contains a search view
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        // Hide title
        (requireActivity() as MainActivity).title = ""
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Search as icon that deploys an input
        inflater.inflate(R.menu.menu, menu)
        val menuItem = menu.findItem(R.id.action_search)
        val searchView = menuItem?.actionView as SearchView

        searchView.isIconified = true
        searchView.setOnQueryTextListener(this)

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun defaultSearch() {
        // Default search
        lifecycleScope.launch {
            artistViewModel.searchArtist(SEARCH_DEFAULT, SEARCH_DEFAULT_SIZE)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            if (newText.isEmpty()) {
                // Default search when no text
                defaultSearch()
            } else {
                lifecycleScope.launch {
                    artistViewModel.searchArtist(newText, 20)
                }
            }
        }

        return true
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