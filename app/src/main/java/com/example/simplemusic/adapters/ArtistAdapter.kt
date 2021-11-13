package com.example.simplemusic.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.simplemusic.R
import com.example.simplemusic.databinding.ArtistViewholderBinding
import com.example.simplemusic.models.multimediacontent.Artist

/**
 * Adapter for a artist list.
 */
class ArtistAdapter(private var artists: List<Artist>,
                    private val actionInterface: ActionInterface) : RecyclerView.Adapter<ArtistAdapter.ViewHolder>() {

    interface ActionInterface {
        fun onClickArtist(artist: Artist)
    }

    class ViewHolder(val binding: ArtistViewholderBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ArtistViewholderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val artist = artists[position]

        holder.binding.name.text = artist.artistName
        holder.binding.genre.text = artist.primaryGenreName

        holder.binding.container.setOnClickListener {
            actionInterface.onClickArtist(artist)
        }
    }

    override fun getItemCount(): Int {
        return artists.size
    }

    fun setArtists(artists: List<Artist>) {
        // Either reload everything or just new inserted data
        if (this.artists.size == artists.size) {
            this.artists = artists
            notifyDataSetChanged()
        } else {
            val start = this.artists.size
            this.artists = artists
            notifyItemRangeInserted(start, artists.size)
        }
    }
}
