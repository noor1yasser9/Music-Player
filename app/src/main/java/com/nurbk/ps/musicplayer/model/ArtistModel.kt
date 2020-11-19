package com.nurbk.ps.musicplayer.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*


class ArtistModel(val albums: ArrayList<AlbumModel>) {

    val artistName: String
        get() = if (albums.size > 0) {
            albums[0].artistName
        } else {
            ""
        }
    val artistImage: String
        get() {
            return if (albums.size > 0) {
                albums[0].coverArt
            } else {
                ""
            }
        }
    val albumCount: Int
        get() {
            return if (albums.size > 0) {
                albums.size
            } else {
                0
            }
        }
    val songCount: Int
        get() {
            var sum = 0
            for (albumModel in albums) {
                sum += albumModel.noOfSong
            }
            return sum
        }

}
