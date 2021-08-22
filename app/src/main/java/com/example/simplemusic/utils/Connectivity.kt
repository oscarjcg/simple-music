package com.example.simplemusic.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

import android.net.NetworkInfo
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService

/**
 * Check if Internet is available.
 */
class Connectivity {
    companion object {
        fun isOnline(context: Context): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                if (connectivityManager != null) {
                    val capabilities =
                        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    if (capabilities != null) {
                        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                            //Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                            return true
                        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                            //Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                            return true
                        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                            //Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                            return true
                        }
                    }
                }
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                return internetCheck(context)
            return false
        }

        private fun internetCheck(c: Context): Boolean {
            val cmg = c.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cmg.activeNetworkInfo?.isConnectedOrConnecting == true
        }
    }
}