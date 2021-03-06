package com.example.simplemusic.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import android.transition.TransitionManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.widget.doAfterTextChanged
import com.example.simplemusic.databinding.FragmentSearchArtistBinding
import com.example.simplemusic.utils.UIEvent
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
        binding.lifecycleOwner = this
        binding.viewModel = artistViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        navController = findNavController()

        setToolbar()

        observeArtists()

        observeUIEvents()

        initEmptyList()

        // Listener. At end of list request more artist data
        artistListOnEndListener()

        searchListener()

        clearListener()

        userViewModel.setDefaultUser()
    }


    private fun initView() {
        artistViewModel.animating = false
    }

    private fun initEmptyList() {
        artistAdapter = ArtistAdapter(ArrayList(), this)
        linearLayoutManager = LinearLayoutManager(context)
        binding.artists.layoutManager = linearLayoutManager
        binding.artists.adapter = artistAdapter

        artistViewModel.artists.value = ArrayList()
    }

    private fun searchListener() {
        // Search after each letter
        binding.searchTerm.doAfterTextChanged {
            resetPagination()
            requestSearch(binding.searchTerm.text.toString())
        }

        // Search
        binding.search.setOnClickListener {
            if (!artistViewModel.animating) {
                resetPagination()
                hideKeyboard()
                animateButtonPressed(binding.search)
                requestSearch(binding.searchTerm.text.toString())
            }
        }
    }

    private fun clearListener() {
        binding.clear.setOnClickListener {
            if (!artistViewModel.animating) {
                resetPagination()
                hideKeyboard()
                animateButtonPressed(binding.clear)
                binding.searchTerm.text.clear()
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
        binding.artists.scrollToPosition(0)
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
        // Binding not working. Progress bar not showing
        binding.progressBar.visibility = View.VISIBLE
        artistViewModel.searchArtist(search)
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
        artistViewModel.animating = true
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
                        artistViewModel.animating = false
                    }
            }
    }

    private fun observeArtists() {
        artistViewModel.artists.observe(viewLifecycleOwner, { artists ->
            // Binding not working. Progress bar not showing
            binding.progressBar.visibility = View.GONE

            // Save list scroll data
            val scroll = binding.artists.layoutManager?.onSaveInstanceState()

            artistAdapter.setArtists(artists)

            // Restore list scroll
            binding.artists.layoutManager?.onRestoreInstanceState(scroll)

            // If there are results, the artist list will be at the center
            if (artists.isEmpty()) {
                searchBarCenter()
            } else {
                searchBarTop()
            }
        })
    }

    private fun observeUIEvents() {
        artistViewModel.uiState.observe(viewLifecycleOwner, {
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
                artistViewModel.setStateInfo(true, getText(R.string.no_results) as String)
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

    private fun artistListOnEndListener() {
        binding.artists.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                hideKeyboard()

                // If end of list and there is data to continue
                if (!recyclerView.canScrollVertically(1) && artistViewModel.canGetMoreData()) {
                    if (!artistViewModel.loading.value!!) {
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
        // Go to albums
        val action =  SearchArtistFragmentDirections
            .actionSearchArtistFragmentToArtistAlbumsFragment(artist.artistId!!, artist.artistName!!)
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
