package com.example.simplemusic.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
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
import android.os.Parcelable
import android.text.TextWatcher
import android.transition.TransitionManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.widget.doAfterTextChanged
import com.example.simplemusic.models.multimediacontent.Artist
import com.example.simplemusic.utils.Connectivity
import com.example.simplemusic.viewmodels.AlbumViewModel
import com.example.simplemusic.viewmodels.UserViewModel


private const val SEARCH_DEFAULT = "a"
private const val SEARCH_PAGINATION = 20

/**
 * Shows a search bar and a result list with artists.
 */
class SearchArtistFragment : Fragment(), SearchView.OnQueryTextListener, ArtistAdapter.ActionInterface {

    private lateinit var artistRv: RecyclerView
    private lateinit var artistAdapter: ArtistAdapter
    private lateinit var toolbar: Toolbar
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var progressBar: ProgressBar
    private lateinit var stateTv: TextView
    private lateinit var container: ConstraintLayout
    private lateinit var searchEt: EditText
    private lateinit var searchBtn: ImageButton
    private lateinit var clearBtn: ImageButton

    private val artistViewModel: ArtistViewModel by activityViewModels()
    private val albumViewModel: AlbumViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels() // Just init for default user
    private lateinit var navController: NavController

    private var pagination = SEARCH_PAGINATION

    // Animation
    private var anim = false

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
            // Request indicators off
            progressBar.visibility = View.GONE
            artistViewModel.searchingArtist = false
            stateTv.visibility = View.GONE

            // Save list scroll data
            val scroll = artistRv.layoutManager?.onSaveInstanceState()

            // Update artists data
            artistAdapter.setArtists(artists)

            // Restore list scroll
            artistRv.layoutManager?.onRestoreInstanceState(scroll)

            Log.println(Log.ERROR, "DEBUG", "request $pagination")//

            // If there are results, the artist list will be at the center
            if (artists.isEmpty()) {
                // Trying to search something but no results
                if (searchEt.text.isNotEmpty()) {
                    stateTv.visibility = View.VISIBLE
                    stateTv.text = getText(R.string.no_results)
                }
                // Move search bar down
                searchBarCenter()

            } else {
                stateTv.visibility = View.GONE
                // Move search bar up
                searchBarTop()

            }
        })

        // Init artist list empty
        artistAdapter = ArtistAdapter(ArrayList(), this)
        linearLayoutManager = LinearLayoutManager(context)
        artistRv.layoutManager = linearLayoutManager
        artistRv.adapter = artistAdapter

        // Listener. At end of list request more artist data
        artistRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                hideKeyboard()

                val size = artistViewModel.artists.value?.size ?: Int.MAX_VALUE
                // If end of list and there is data to continue
                if (!recyclerView.canScrollVertically(1) && pagination <= size) {
                    if (!artistViewModel.searchingArtist) {

                        // Request more artists data
                        pagination += SEARCH_PAGINATION
                        artistViewModel.searchedArtist?.let { requestSearch(it, pagination) }
                    }
                }
            }
        })

        // Search after each letter
        searchEt.doAfterTextChanged {
            resetPagination()
            requestSearch(searchEt.text.toString(), pagination)
        }

        // Search
        searchBtn.setOnClickListener {
            if (!anim) {
                resetPagination()
                hideKeyboard()
                animateButtonPressed(searchBtn)
                requestSearch(searchEt.text.toString(), pagination)
            }
        }

        // Clear search
        clearBtn.setOnClickListener {
            if (!anim) {
                resetPagination()
                hideKeyboard()
                animateButtonPressed(clearBtn)
                searchEt.text.clear()
            }
        }

        // Default user
        lifecycleScope.launch {
            userViewModel.setDefaultUser()
        }
    }

    private fun initView(view: View) {
        artistRv = view.findViewById(R.id.artistRv)
        toolbar = view.findViewById(R.id.toolbar)
        progressBar = view.findViewById(R.id.progressBar)
        stateTv = view.findViewById(R.id.stateTv)
        container = view.findViewById(R.id.container)
        searchBtn = view.findViewById(R.id.searchBtn)
        searchEt = view.findViewById(R.id.searchEt)
        clearBtn = view.findViewById(R.id.clearBtn)

        progressBar.visibility = View.GONE
        stateTv.visibility = View.GONE
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Set list to top and resets pagination.
     */
    private fun resetPagination() {
        artistRv.scrollToPosition(0);
        pagination = SEARCH_PAGINATION
    }

    private fun setToolbar() {
        // Toolbar
        val navHostFragment = NavHostFragment.findNavController(this)
        val config = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController( navHostFragment, config)

        // Menu which contains a search view
        /*
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
         */
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

    /**
     * Start a search with a limit of results
     */
    private fun requestSearch(search: String, pagination: Int) {
        if (context?.let { Connectivity.isOnline(it) } == true) {
            progressBar.visibility = View.VISIBLE

            // Start request
            lifecycleScope.launch {
                artistViewModel.searchArtist(search, pagination)
            }
        } else {
            // If no internet and no artist data, show at least info
            if (artistViewModel.artists.value == null) {
                stateTv.text = getText(R.string.no_results)
                stateTv.visibility = View.VISIBLE
            }

            Toast.makeText(activity, R.string.no_internet, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Move search bar to the center by modifying constraint
     */
    private fun searchBarCenter() {
        // Move search bar to center
        TransitionManager.beginDelayedTransition(container)
        val constraintSet = ConstraintSet()
        constraintSet.clone(container)
        constraintSet.connect(R.id.searchContainer, ConstraintSet.BOTTOM, R.id.container, ConstraintSet.BOTTOM)
        container.setConstraintSet(constraintSet)
    }

    /**
     * Move search bar to the top by modifying constraint
     */
    private fun searchBarTop() {
        // Move search bar up
        TransitionManager.beginDelayedTransition(container)
        val constraintSet = ConstraintSet()
        constraintSet.clone(container)
        constraintSet.clear(R.id.searchContainer, ConstraintSet.BOTTOM)
        container.setConstraintSet(constraintSet)
    }

    /**
     * Animate button with zoom
     */
    private fun animateButtonPressed(view: View) {
        anim = true
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
                        anim = false
                    }
            }
    }

    /**
     * Toolbar. Search after submit
     */
    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    /**
     * Toolbar. Search after each letter
     */
    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            pagination = SEARCH_PAGINATION
            if (newText.isEmpty()) {
                // Default search when no text
                requestSearch(SEARCH_DEFAULT, pagination)
            } else {
                requestSearch(newText, pagination)
            }
        }

        return true
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