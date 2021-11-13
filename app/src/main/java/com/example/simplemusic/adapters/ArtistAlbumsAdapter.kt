package com.example.simplemusic.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.simplemusic.R
import com.example.simplemusic.databinding.AlbumViewholderBinding
import com.example.simplemusic.models.multimediacontent.ArtistAlbum
import java.text.SimpleDateFormat
import java.util.*

private const val RELEASE_DATE_FORMAT = "yyyy"

/**
 * Adapter for a album list.
 */
class ArtistAlbumsAdapter(private var albums: List<ArtistAlbum>,
                          private val actionInterface: ActionInterface) : RecyclerView.Adapter<ArtistAlbumsAdapter.ViewHolder>() {

    interface ActionInterface {
        fun onClickAlbum(album: ArtistAlbum)
    }

    class ViewHolder(val binding: AlbumViewholderBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AlbumViewholderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val album = albums[position]

        holder.binding.name.text = album.collectionName

        val simpleDateFormat = SimpleDateFormat(RELEASE_DATE_FORMAT, Locale.ENGLISH)
        holder.binding.date.text = simpleDateFormat.format(album.releaseDate)

        holder.binding.tracks.text = album.trackCount.toString()

        Glide.with(holder.binding.artwork)
            .load(album.artworkUrl100)
            .into(holder.binding.artwork)

        holder.binding.container.setOnClickListener {
            actionInterface.onClickAlbum(album)
        }
    }

    override fun getItemCount(): Int {
        return albums.size
    }

    fun setAlbums( albums: List<ArtistAlbum>) {
        this.albums = albums
        notifyDataSetChanged()
    }

}
