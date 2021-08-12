package com.example.simplemusic.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplemusic.R
import com.example.simplemusic.adapters.ArtistAdapter
import com.example.simplemusic.viewmodels.ArtistViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var searchEt: EditText
    private lateinit var artistRv: RecyclerView
    private lateinit var artistAdapter: ArtistAdapter

    private val artistViewModel: ArtistViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

        artistViewModel.artists.observe(this, { artists ->
            //Toast.makeText(this, artists.size.toString(), Toast.LENGTH_SHORT).show()
            artistAdapter = ArtistAdapter(artists)
            artistRv.adapter = artistAdapter
        })

        // Search
        searchEt.doAfterTextChanged {
            lifecycleScope.launch {
                artistViewModel.searchArtist(searchEt.text.toString(),20)
            }
        }

        // Init artist list empty
        artistAdapter = ArtistAdapter(ArrayList())
        artistRv.layoutManager = LinearLayoutManager(this)
        artistRv.adapter = artistAdapter
    }

    private fun initView() {
        artistRv = findViewById(R.id.artistRv)
        searchEt = findViewById(R.id.searchEt)
    }
}