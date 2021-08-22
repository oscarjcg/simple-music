package com.example.simplemusic.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.simplemusic.R
import com.example.simplemusic.models.multimediacontent.ArtistAlbum
import com.example.simplemusic.models.multimediacontent.MusicVideo
import java.text.SimpleDateFormat
import java.util.*

private const val RELEASE_DATE_FORMAT = "yyyy"

/**
 * Adapter for a music video list.
 */
class ArtistMusicVideosAdapter(private var musicVideos: List<MusicVideo>,
                               private val actionInterface: ActionInterface) : RecyclerView.Adapter<ArtistMusicVideosAdapter.ViewHolder>() {

    interface ActionInterface {
        fun onClickMusicVideo(musicVideo: MusicVideo)
        fun onPlayMusicVideo(musicVideo: MusicVideo)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTv: TextView = view.findViewById(R.id.nameTv)
        val container: ConstraintLayout = view.findViewById(R.id.container)
        val artworkIv: ImageView = view.findViewById(R.id.artworkIv)
        val dateTv: TextView = view.findViewById(R.id.dateTv)
        val play: ImageButton = view.findViewById(R.id.playBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.music_video_viewholder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val musicVideo = musicVideos[position]

        // Music video name
        holder.nameTv.text = musicVideo.trackName

        // Music video release date
        val simpleDateFormat = SimpleDateFormat(RELEASE_DATE_FORMAT, Locale.ENGLISH)
        holder.dateTv.text = simpleDateFormat.format(musicVideo.releaseDate)

        // Artwork image
        Glide.with(holder.artworkIv)
            .load(musicVideo.artworkUrl100)
            .into(holder.artworkIv)

        // Click listener
        holder.play.setOnClickListener {
            actionInterface.onPlayMusicVideo(musicVideo)
        }

        holder.container.setOnClickListener {
            actionInterface.onClickMusicVideo(musicVideo)
        }
    }

    override fun getItemCount(): Int {
        return musicVideos.size
    }

    fun setMusicVideos( musicVideos: List<MusicVideo>) {
        this.musicVideos = musicVideos
        notifyDataSetChanged()
    }

}