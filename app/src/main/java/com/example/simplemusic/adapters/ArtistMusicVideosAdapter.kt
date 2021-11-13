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
import com.example.simplemusic.databinding.MusicVideoViewholderBinding
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

    class ViewHolder(val binding: MusicVideoViewholderBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MusicVideoViewholderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val musicVideo = musicVideos[position]

        holder.binding.name.text = musicVideo.trackName

        val simpleDateFormat = SimpleDateFormat(RELEASE_DATE_FORMAT, Locale.ENGLISH)
        holder.binding.date.text = simpleDateFormat.format(musicVideo.releaseDate)

        Glide.with(holder.binding.artwork)
            .load(musicVideo.artworkUrl100)
            .into(holder.binding.artwork)

        holder.binding.play.setOnClickListener {
            actionInterface.onPlayMusicVideo(musicVideo)
        }

        holder.binding.container.setOnClickListener {
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
