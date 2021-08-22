package com.example.simplemusic.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.example.simplemusic.R

/**
 * Main activity. Contains entire app changing fragments.
 */
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onSupportNavigateUp(): Boolean {
        val nav = this.findNavController(R.id.fragmentContainerView)
        return nav.navigateUp()
    }

}