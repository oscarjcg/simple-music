package com.example.simplemusic.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
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
import android.transition.TransitionManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.widget.doAfterTextChanged
import com.example.simplemusic.databinding.FragmentArtistMusicVideosBinding
import com.example.simplemusic.databinding.FragmentSearchArtistBinding
import com.example.simplemusic.models.multimediacontent.Artist
import com.example.simplemusic.utils.Connectivity
import com.example.simplemusic.viewmodels.AlbumViewModel
import com.example.simplemusic.viewmodels.UserViewModel

/**
 * Shows a search bar and a result list with artists.
 */
class SearchArtistFragment : Fragment(), ArtistAdapter.ActionInterface {

    private lateinit var artistAdapter: ArtistAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var binding: FragmentSearchArtistBinding
    private val artistViewModel: ArtistViewModel by activityViewModels()
    private val albumViewModel: AlbumViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels() // Just init for default user
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
        artistViewModel.resetPagination()

        // Inflate the layout for this fragment
        binding = FragmentSearchArtistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        // Navigation
        navController = findNavController()

        // Toolbar
        setToolbar()

        // Observe artists
        observeArtists()

        // Init artist list empty
        initEmptyList()

        // Listener. At end of list request more artist data
        artistListOnEndListener()

        // Search
        searchListener()

        // Clear search
        clearListener()

        // Default user
        lifecycleScope.launch {
            userViewModel.setDefaultUser()
        }
    }


    private fun initView() {
        binding.progressBar.visibility = View.GONE
        binding.stateTv.visibility = View.GONE
        artistViewModel.anim = false
    }

    private fun initEmptyList() {
        artistAdapter = ArtistAdapter(ArrayList(), this)
        linearLayoutManager = LinearLayoutManager(context)
        binding.artistRv.layoutManager = linearLayoutManager
        binding.artistRv.adapter = artistAdapter
    }

    private fun searchListener() {
        // Search after each letter
        binding.searchEt.doAfterTextChanged {
            resetPagination()
            requestSearch(binding.searchEt.text.toString())
        }

        // Search
        binding.searchBtn.setOnClickListener {
            if (!artistViewModel.anim) {
                resetPagination()
                hideKeyboard()
                animateButtonPressed(binding.searchBtn)
                requestSearch(binding.searchEt.text.toString())
            }
        }
    }

    private fun clearListener() {
        binding.clearBtn.setOnClickListener {
            if (!artistViewModel.anim) {
                resetPagination()
                hideKeyboard()
                animateButtonPressed(binding.clearBtn)
                binding.searchEt.text.clear()
            }
        }
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Set list to top and resets pagination.
     */
    private fun resetPagination() {
        binding.artistRv.scrollToPosition(0)
        artistViewModel.resetPagination()
    }

    private fun setToolbar() {
        // Toolbar
        val navHostFragment = NavHostFragment.findNavController(this)
        val config = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navHostFragment, config)

        // Hide title
        (requireActivity() as MainActivity).title = ""
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Search as icon that deploys an input
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Start a search with a limit of results
     */
    private fun requestSearch(search: String) {
        binding.progressBar.visibility = View.VISIBLE

        // Start request
        lifecycleScope.launch {
            artistViewModel.searchArtist(search)
        }
    }

    /**
     * Move search bar to the center by modifying constraint
     */
    private fun searchBarCenter() {
        // Move search bar to center
        TransitionManager.beginDelayedTransition(binding.container)
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.container)
        constraintSet.connect(R.id.searchContainer, ConstraintSet.BOTTOM, R.id.container, ConstraintSet.BOTTOM)
        binding.container.setConstraintSet(constraintSet)
    }

    /**
     * Move search bar to the top by modifying constraint
     */
    private fun searchBarTop() {
        // Move search bar up
        TransitionManager.beginDelayedTransition(binding.container)
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.container)
        constraintSet.clear(R.id.searchContainer, ConstraintSet.BOTTOM)
        binding.container.setConstraintSet(constraintSet)
    }

    /**
     * Animate button with zoom
     */
    private fun animateButtonPressed(view: View) {
        artistViewModel.anim = true
        val zoom = 0.1f
        val time: Long = 200

        view.clearAnimation()
        view.animate()
            .scaleXBy(-zoom)
            .scaleYBy(-zoom)
            .setDuration(time)
            .withEndAction {
                view.animate()
                    .scaleXBy(zoom)
                    .scaleYBy(zoom)
                    .setDuration(time)
                    .withEndAction {
                        artistViewModel.anim = false
                    }
            }
    }

    private fun observeArtists() {
        artistViewModel.artists.observe(viewLifecycleOwner, { artists ->
            // Request indicators off
            binding.progressBar.visibility = View.GONE
            artistViewModel.searchingArtist = false
            binding.stateTv.visibility = View.GONE

            // Save list scroll data
            val scroll = binding.artistRv.layoutManager?.onSaveInstanceState()

            // Update artists data
            artistAdapter.setArtists(artists)

            // Restore list scroll
            binding.artistRv.layoutManager?.onRestoreInstanceState(scroll)

            // If there are results, the artist list will be at the center
            if (artists.isEmpty()) {
                // Trying to search something but no results
                if (binding.searchEt.text.isNotEmpty()) {
                    binding.stateTv.visibility = View.VISIBLE
                    binding.stateTv.text = getText(R.string.no_results)
                }
                // Move search bar down
                searchBarCenter()

                // Check is is because internet
                if (context?.let { Connectivity.isOnline(it) } == false) {
                    Toast.makeText(activity, R.string.no_internet, Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.stateTv.visibility = View.GONE
                // Move search bar up
                searchBarTop()
            }
        })
    }

    private fun artistListOnEndListener() {
        binding.artistRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                hideKeyboard()

                val size = artistViewModel.artists.value?.size ?: Int.MAX_VALUE
                // If end of list and there is data to continue
                if (!recyclerView.canScrollVertically(1) && artistViewModel.canGetMoreData()) {
                    if (!artistViewModel.searchingArtist) {

                        // Request more artists data
                        artistViewModel.searchedArtist?.let { requestSearch(it) }
                    }
                }
            }
        })
    }

    /**
     * Go to albums after an artist is selected
     */
    override fun onClickArtist(artist: Artist) {
        hideKeyboard()
        // Pass artist name
        albumViewModel.searchedArtist = artist.artistName
        // Go to albums
        val action =  SearchArtistFragmentDirections.actionSearchArtistFragmentToArtistAlbumsFragment(artist.artistId!!)
        navController.navigate(action)
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
