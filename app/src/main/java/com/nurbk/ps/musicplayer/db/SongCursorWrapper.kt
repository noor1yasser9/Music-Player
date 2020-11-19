package com.nurbk.ps.musicplayer.db

import android.database.Cursor
import android.database.CursorWrapper
import android.provider.MediaStore
import com.nurbk.ps.musicplayer.model.SongModel


class SongCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {
    val song: SongModel
        get() {
            val id = getInt(getColumnIndex(MediaStore.Audio.Media._ID))
            val title = getString(getColumnIndex(MediaStore.Audio.Media.TITLE))
            val artistName = getString(getColumnIndex(MediaStore.Audio.Media.ARTIST))
            val composer = getString(getColumnIndex(MediaStore.Audio.Media.COMPOSER))
            val albumName = getString(getColumnIndex(MediaStore.Audio.Media.ALBUM))
            val data = getString(getColumnIndex(MediaStore.Audio.Media.DATA))
            val trackNumber = getInt(getColumnIndex(MediaStore.Audio.Media.TRACK))
            val year = getInt(getColumnIndex(MediaStore.Audio.Media.YEAR))
            val duration = getLong(getColumnIndex(MediaStore.Audio.Media.DURATION))
            val dateModified = getLong(getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED))
            val dateAdded = getLong(getColumnIndex(MediaStore.Audio.Media.DATE_ADDED))
            val albumId = getInt(getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
            val artistId = getInt(getColumnIndex(MediaStore.Audio.Media.ARTIST_ID))
            val bookmark = getLong(getColumnIndex(MediaStore.Audio.Media.BOOKMARK))
            return SongModel(id,
                title?:"",
                artistName?:"",
                composer?:"",
                albumName?:"",
                "",
                data,
                trackNumber,
                year,
                duration,
                dateModified,
                dateAdded,
                albumId,
                artistId,
                bookmark)
        }
}
