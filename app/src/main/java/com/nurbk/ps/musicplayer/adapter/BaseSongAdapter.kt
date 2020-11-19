package com.nurbk.ps.musicplayer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nurbk.ps.musicplayer.model.SongModel


abstract class BaseSongAdapter(
    private val layoutId: Int
) : RecyclerView.Adapter<BaseSongAdapter.SongViewHolder>() {

    class SongViewHolder(item: View) : RecyclerView.ViewHolder(item)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            LayoutInflater.from(parent.context).inflate(
                layoutId,
                parent,
                false
            )
        )
    }


    protected var onItemClickListener: ((Any, Int) -> Unit)? = null

    fun setItemClickListener(listener: (Any, Int) -> Unit) {
        onItemClickListener = listener
    }

    protected var onMoreClickListener: ((Any, Int, View) -> Unit)? = null

    fun setMoreClickListener(listener: (Any, Int, View) -> Unit) {
        onMoreClickListener = listener
    }


}