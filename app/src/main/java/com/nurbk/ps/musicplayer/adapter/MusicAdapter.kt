package com.nurbk.ps.musicplayer.adapter

import android.widget.Filter
import android.widget.Filterable
import com.nurbk.ps.musicplayer.R
import com.nurbk.ps.musicplayer.model.SongModel
import com.nurbk.ps.musicplayer.other.getImage
import kotlinx.android.synthetic.main.item_song_layout.view.*


class MusicAdapter(var datas: ArrayList<SongModel>) :
    BaseSongAdapter(R.layout.item_song_layout), Filterable {
    private var contactListFiltered: ArrayList<SongModel>? = null

    init {
        contactListFiltered = datas

    }

    override fun getItemCount(): Int {
        return contactListFiltered!!.size
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {


        holder.itemView.apply {

            val song = contactListFiltered!![position]

                getImage(context, song.albumId.toLong(), imageView!!)
                textViewSongTitle.text = song.albumName
                textViewArtistName.text = song.artistName
            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(song, position)
                }
            }
        }

    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                contactListFiltered = if (charString.isEmpty()) {
                    datas
                } else {
                    val filteredList = ArrayList<SongModel>()
                    for (row in datas) {
                        if (row.albumName.toLowerCase()
                                .contains(charString.toLowerCase()) || row.albumName!!.toLowerCase()
                                .contains(charString.toLowerCase())
                        ) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = contactListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                contactListFiltered = filterResults.values as ArrayList<SongModel>
                notifyDataSetChanged()
            }
        }
    }
}