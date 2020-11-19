package com.nurbk.ps.musicplayer.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class AlbumModel(val songs: ArrayList<SongModel>) : Parcelable {
    var id = 0


    val albumSongs: ArrayList<SongModel> = songs
    val name: String
        get() = if (songs.size > 0) {
            songs[0].albumName
        } else {
            " "
        }
    val noOfSong: Int
        get() = songs.size
    val coverArt: String
        get() {
            return if (songs.size > 0) {
                songs[0].albumArt
            } else {
                " "
            }
        }
    val artistId: Int
        get() {
            return if (songs.size > 0) {
                songs[0].artistId
            } else {
                0
            }
        }
    val artistName: String
        get() {
            return if (songs.size > 0) {
                songs[0].artistName
            } else {
                ""
            }
        }

}
