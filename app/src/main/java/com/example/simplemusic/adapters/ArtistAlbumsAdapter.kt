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
import com.example.simplemusic.models.multimediacontent.ArtistAlbum

class ArtistAlbumsAdapter(private  val albums: List<ArtistAlbum>,
                          private val navController: NavController) : RecyclerView.Adapter<ArtistAlbumsAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.nameTv)
        val container: ConstraintLayout = view.findViewById(R.id.container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.album_viewholder, parent, false)
        return ArtistAlbumsAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val album = albums[position]

        holder.name.text = album.collectionName

        holder.container.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return albums.size
    }

}