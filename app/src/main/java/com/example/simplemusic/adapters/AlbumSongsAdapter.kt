package com.example.simplemusic.adapters

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.StateListDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.simplemusic.R
import com.example.simplemusic.activities.MainActivity
import com.example.simplemusic.models.multimediacontent.AlbumSong

class AlbumSongsAdapter(private  var songs: List<AlbumSong>,
                        private val activity: MainActivity) : RecyclerView.Adapter<AlbumSongsAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.nameTv)
        val container: ConstraintLayout = view.findViewById(R.id.container)
        val like: ImageButton = view.findViewById(R.id.likeBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_viewholder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val album = songs[position]

        // Like button style
        setLikeButtonStyle(holder)

        holder.name.text = album.trackName

        holder.container.setOnClickListener {

        }

        holder.like.setOnClickListener {
            holder.like.setColorFilter(ContextCompat.getColor( activity, R.color.primary_dark))
        }
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    fun setSongs(songs: List<AlbumSong>) {
        this.songs = songs
        notifyDataSetChanged()
    }

    private fun setLikeButtonStyle(holder: ViewHolder) {
        // Like button style
        /*
        val fieldDefaultBg = getStyle(4, ContextCompat.getColor( activity, R.color.accent))
        val fieldPressedBg = getStyle(4, ContextCompat.getColor( activity, R.color.accent))
        val fieldInactiveBg = getStyle(4, ContextCompat.getColor( activity, R.color.accent))

        val stateListDrawable = StateListDrawable()
        stateListDrawable.addState(intArrayOf(android.R.attr.state_pressed), fieldPressedBg)
        stateListDrawable.addState(intArrayOf(-android.R.attr.state_enabled), fieldInactiveBg)
        stateListDrawable.addState(intArrayOf(), fieldDefaultBg)

        holder.like.background = InsetDrawable( stateListDrawable, 0)
         */
        holder.like.setColorFilter(Color.WHITE)
    }

    private fun getStyle(stroke: Int, color: Int): GradientDrawable {
        val style = GradientDrawable();
        style.setColor(color)
        style.setStroke(stroke, color)
        return style
    }
}