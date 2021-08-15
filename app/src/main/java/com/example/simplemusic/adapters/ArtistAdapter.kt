package com.example.simplemusic.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.simplemusic.R
import com.example.simplemusic.fragments.SearchArtistFragmentDirections
import com.example.simplemusic.models.multimediacontent.Artist

class ArtistAdapter(private  val artists: List<Artist>,
                    private val navController: NavController) : RecyclerView.Adapter<ArtistAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.nameTv)
        val genre: TextView = view.findViewById(R.id.genreTv)
        val container: ConstraintLayout = view.findViewById(R.id.container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.artist_viewholder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val artist = artists[position]

        holder.name.text = artist.artistName
        holder.genre.text = artist.primaryGenreName

        holder.container.setOnClickListener {
            val action =  SearchArtistFragmentDirections.actionSearchArtistFragmentToArtistAlbumsFragment(artist.artistId!!)
            navController.navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return artists.size
    }
}