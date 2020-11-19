package com.nurbk.ps.musicplayer.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class SongModel(
    val id: Int,
    val title: String,
    val artistName: String,
    val composer: String,
    val albumName: String,
    var albumArt: String,
    val data: String,
    val trackNumber: Int,
    val year: Int,
    val duration: Long,
    val dateModified: Long,
    val dateAdded: Long,
    val albumId: Int,
    val artistId: Int,
    val bookmark: Long,
) : Parcelable {

    companion object {
        fun EMPTY(): SongModel {
            return SongModel(0, "", "", "", "", "", "", 0, 0, 0, 0, 0, 0, 0, 0)
        }
    }
}