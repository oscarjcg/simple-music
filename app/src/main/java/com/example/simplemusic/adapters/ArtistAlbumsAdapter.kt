package com.example.simplemusic.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.simplemusic.R
import com.example.simplemusic.fragments.ArtistAlbumsFragmentDirections
import com.example.simplemusic.models.multimediacontent.Artist
import com.example.simplemusic.models.multimediacontent.ArtistAlbum
import java.text.SimpleDateFormat
import java.util.*

private const val RELEASE_DATE_FORMAT = "yyyy"

class ArtistAlbumsAdapter(private var albums: List<ArtistAlbum>,
                          private val actionInterface: ActionInterface) : RecyclerView.Adapter<ArtistAlbumsAdapter.ViewHolder>() {

    interface ActionInterface {
        fun onClickAlbum(album: ArtistAlbum)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTv: TextView = view.findViewById(R.id.nameTv)
        val container: ConstraintLayout = view.findViewById(R.id.container)
        val artworkIv: ImageView = view.findViewById(R.id.artworkIv)
        val dateTv: TextView = view.findViewById(R.id.dateTv)
        val tracksTv: TextView = view.findViewById(R.id.tracksTv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.album_viewholder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val album = albums[position]

        // Album name
        holder.nameTv.text = album.collectionName

        // Album release date
        val simpleDateFormat = SimpleDateFormat(RELEASE_DATE_FORMAT, Locale.ENGLISH)
        holder.dateTv.text = simpleDateFormat.format(album.releaseDate)

        // Album tracks
        holder.tracksTv.text = album.trackCount.toString()

        // Artwork image
        Glide.with(holder.artworkIv)
            .load(album.artworkUrl100)
            .into(holder.artworkIv)


        // Go to album songs
        holder.container.setOnClickListener {
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