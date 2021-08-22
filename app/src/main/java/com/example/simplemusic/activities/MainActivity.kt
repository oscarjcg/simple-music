package com.example.simplemusic.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.simplemusic.R
import com.example.simplemusic.database.dao.ApiCacheDao
import com.example.simplemusic.viewmodels.*
import kotlinx.coroutines.launch
import java.util.*


private const val KEY_CACHE_DATE = "cache_date"
private const val CACHE_INTERVAL_DAYS = 7
private const val DAY_MS: Long = 86400000
/**
 * Main activity. Contains entire app changing fragments.
 */
class MainActivity : AppCompatActivity() {

    private val albumViewModel: AlbumViewModel by viewModels()
    private val artistViewModel: ArtistViewModel by viewModels()
    private val musicVideoViewModel: MusicVideoViewModel by viewModels()
    private val songViewModel: SongViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check cache expiration to reset
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        val cacheDateLong = sharedPref.getLong(KEY_CACHE_DATE, Long.MIN_VALUE)

        if (cacheDateLong == Long.MIN_VALUE) {
            with (sharedPref.edit()) {
                putLong(KEY_CACHE_DATE, System.currentTimeMillis())
                apply()
            }
        } else {
            val nowLong = System.currentTimeMillis()
            val cacheDateExpirationLong = cacheDateLong + (CACHE_INTERVAL_DAYS * DAY_MS)

            // If cache was too long ago, reset to avoid accumulation
            if (nowLong > cacheDateExpirationLong) {
                lifecycleScope.launch {
                    songViewModel.deleteAll()
                    albumViewModel.deleteAll()
                    musicVideoViewModel.deleteAll()
                    artistViewModel.deleteAll()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val nav = this.findNavController(R.id.fragmentContainerView)
        return nav.navigateUp()
    }

}