package com.example.simplemusic.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.simplemusic.R
import com.example.simplemusic.activities.MainActivity
import com.example.simplemusic.models.multimediacontent.AlbumSong

/**
 * Adapter for a songs list. Requires a list of ids to detect liked songs.
 */
class AlbumSongsAdapter(private  var songs: List<AlbumSong>,
                        private val activity: MainActivity,
                        private val actionInterface: ActionInterface,
                        private var likesSongId: List<Long>
) : RecyclerView.Adapter<AlbumSongsAdapter.ViewHolder>() {

    interface ActionInterface {
        fun onClickLike(song: AlbumSong)
        fun onClickPlay(song: AlbumSong)
        fun onClickPause(song: AlbumSong)
        fun onClickSong(song: AlbumSong)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.nameTv)
        val container: ConstraintLayout = view.findViewById(R.id.container)
        val like: ImageButton = view.findViewById(R.id.likeBtn)
        val play: ImageButton = view.findViewById(R.id.playBtn)
        val pause: ImageButton = view.findViewById(R.id.pauseBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_viewholder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]

        // Like button style
        setLikeButtonStyle(holder)

        // Song name
        holder.name.text = song.trackName

        // Set helper variable. Liked song or not
        if (song.like == null) {
            song.like = likesSongId.contains(song.trackId)
        }

        // Set like icon color
        if (song.like!!)
            holder.like.setColorFilter(ContextCompat.getColor( activity, R.color.primary_dark))
        else
            holder.like.setColorFilter(ContextCompat.getColor( activity, R.color.white))


        // Click listeners
        holder.like.setOnClickListener {
            actionInterface.onClickLike(song)
        }

        holder.play.setOnClickListener {
            actionInterface.onClickPlay(song)
        }

        holder.pause.setOnClickListener {
            actionInterface.onClickPause(song)
        }

        holder.container.setOnClickListener {
            actionInterface.onClickSong(song)
        }
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    /**
     * List of ids to identify when a user has used the like bottom.
     */
    fun setLikesSongId(likesSongId: List<Long>) {
        this.likesSongId = likesSongId
        notifyDataSetChanged()
    }

    fun setSongs(songs: List<AlbumSong>) {
        this.songs = songs
        notifyDataSetChanged()
    }

    private fun setLikeButtonStyle(holder: ViewHolder) {
        holder.like.setColorFilter(Color.WHITE)
    }

}