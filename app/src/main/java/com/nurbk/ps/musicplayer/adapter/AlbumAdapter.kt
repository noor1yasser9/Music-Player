package com.nurbk.ps.musicplayer.adapter


import com.nurbk.ps.musicplayer.R
import com.nurbk.ps.musicplayer.model.AlbumModel
import com.nurbk.ps.musicplayer.other.getImage
import kotlinx.android.synthetic.main.item_album_layout.view.*


class AlbumAdapter(val data: ArrayList<AlbumModel>) :
    BaseSongAdapter(R.layout.item_album_layout) {

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {

        val song = data[position]
        holder.itemView.rootView.apply {
            album_name.text = song.name
            album_artist.text = song.noOfSong.toString()+" Songs"
            getImage(context, song.albumSongs[0].albumId.toLong(), album_image!!, R.drawable.xd)
            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(song, position)
                }
            }
        }

    }
}