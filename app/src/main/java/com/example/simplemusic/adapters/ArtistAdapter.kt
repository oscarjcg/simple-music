package com.example.simplemusic.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.simplemusic.R
import com.example.simplemusic.models.multimediacontent.Artist

/**
 * Adapter for a artist list.
 */
class ArtistAdapter(private var artists: List<Artist>,
                    private val actionInterface: ActionInterface) : RecyclerView.Adapter<ArtistAdapter.ViewHolder>() {

    interface ActionInterface {
        fun onClickArtist(artist: Artist)
    }

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

        // Artist name
        holder.name.text = artist.artistName
        // Artist music genre
        holder.genre.text = artist.primaryGenreName

        // Click listener
        holder.container.setOnClickListener {
            actionInterface.onClickArtist(artist)
        }
    }

    override fun getItemCount(): Int {
        return artists.size
    }

    fun setArtists(artists: List<Artist>) {
        this.artists = artists
        notifyDataSetChanged()
    }
}