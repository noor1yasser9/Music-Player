package com.nurbk.ps.musicplayer.db

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.nurbk.ps.musicplayer.model.AlbumModel
import com.nurbk.ps.musicplayer.model.ArtistModel
import com.nurbk.ps.musicplayer.model.SongModel
import java.util.*


/**
 * Created by deadsec on 9/4/17.
 */
class SongDataLab constructor(context: Context) {
    private val mContext: Context = context.applicationContext

    //    private val mDatabase: SQLiteDatabase
    private var songs: List<SongModel> = emptyList()
    fun getSong(id: Long): SongModel {
        val cursorWrapper = querySong("_id=$id", null)
        return cursorWrapper.use { cursorWrapper ->
            if (cursorWrapper.count !== 0) {
                cursorWrapper.moveToFirst()
                return cursorWrapper.song
            }
            SongModel.EMPTY()
        }
    }

    val randomSong: SongModel?
        get() {
            val r = Random()
            return songs[r.nextInt(songs.size - 1)]
        }

    fun getNextSong(currentSong: SongModel?): SongModel? {
        return try {
            songs[songs.indexOf(currentSong) + 1]
        } catch (e: Exception) {
            randomSong
        }
    }

    fun getPreviousSong(currentSong: SongModel?): SongModel? {
        return try {
            songs[songs.indexOf(currentSong) - 1]
        } catch (e: Exception) {
            randomSong
        }
    }

    fun getSongs(): List<SongModel> {
        return songs
    }

    fun querySongs(): List<SongModel> {
        val songs: MutableList<SongModel> = ArrayList<SongModel>()
        val cursor = querySong(null, null)
        cursor.use { cursor ->
            cursor.moveToFirst()
            do {
                var song: SongModel = cursor.song
                song = cursor.song
                song.albumArt = (getAlbumUri(song.albumId).toString())
                songs.add(song)
            } while (cursor.moveToNext())
        }
        return songs
    }// Organize song as: {albumId=>{song,song,song},albumId=>{song,song}}

    // Extracting and mapping the songlist

    //          Debugging Code
//        for(AlbumModel k:allAlbums) {
//            for(SongModel song: k.getAlbumSongs()) {
//                Log.i("Test","Album Title: "+song.getAlbumName()+"Album ID: "+song.getAlbumId()+" Song Name: "+song.getTitle());
//            }
//        }
//        Log.i("All Albums list: ",allAlbums.size()+"");
    // incomplete get artist context
    val albums: ArrayList<AlbumModel>
        get() {
            val songs: List<SongModel?> = getSongs()

            // Organize song as: {albumId=>{song,song,song},albumId=>{song,song}}
            val albumMap: MutableMap<Int, ArrayList<SongModel>> =
                HashMap<Int, ArrayList<SongModel>>()
            val allAlbums: ArrayList<AlbumModel> = ArrayList<AlbumModel>()
            for (song in songs) {
                if (albumMap[song!!.albumId] == null) {
                    albumMap[song.albumId] = ArrayList<SongModel>()
                    albumMap[song.albumId]!!.add(song)
                } else {
                    albumMap[song.albumId]!!.add(song)
                }
            }

            // Extracting and mapping the songlist
            for ((_, value) in albumMap) {
                val albumSpecificSongs: ArrayList<SongModel> = ArrayList<SongModel>()
                for (song in value) {
                    albumSpecificSongs.add(song)
                }
                allAlbums.add(AlbumModel(albumSpecificSongs))
            }

            //          Debugging Code
            //        for(AlbumModel k:allAlbums) {
            //            for(SongModel song: k.getAlbumSongs()) {
            //                Log.i("Test","Album Title: "+song.getAlbumName()+"Album ID: "+song.getAlbumId()+" Song Name: "+song.getTitle());
            //            }
            //        }
            //        Log.i("All Albums list: ",allAlbums.size()+"");
            return allAlbums
        }

    // Extracting and mapping the songlist

    //Debugging Code
//        for(ArtistModel k:allArtists) {
//            for(AlbumModel album: k.getAlbums()) {
//                Log.i("Test "+album.getArtistId(),"Album Title: "+album.getName());
//            }
//        }
//        Log.i("All Albums list: ",allArtists.size()+"");
//
    val artists: ArrayList<ArtistModel>
        get() {
            val albums: ArrayList<AlbumModel> = albums
            val artistMap: MutableMap<Int, ArrayList<AlbumModel>?> =
                HashMap<Int, ArrayList<AlbumModel>?>()
            val allArtists: ArrayList<ArtistModel> = ArrayList<ArtistModel>()
            for (album in albums) {
                if (artistMap[album.artistId] == null) {
                    artistMap[album.artistId] = ArrayList<AlbumModel>()
                    artistMap[album.artistId]!!.add(album)
                } else {
                    artistMap[album.artistId]!!.add(album)
                }
            }

            // Extracting and mapping the songlist
            for ((_, value) in artistMap) {
                val artistSpecificAlbum: ArrayList<AlbumModel> = ArrayList<AlbumModel>()
                for (album in value!!) {
                    artistSpecificAlbum.add(album)
                }
                allArtists.add(ArtistModel(artistSpecificAlbum))
            }

            //Debugging Code
            //        for(ArtistModel k:allArtists) {
            //            for(AlbumModel album: k.getAlbums()) {
            //                Log.i("Test "+album.getArtistId(),"Album Title: "+album.getName());
            //            }
            //        }
            //        Log.i("All Albums list: ",allArtists.size()+"");
            //
            return allArtists
        }

    private fun querySong(whereClause: String?, whereArgs: Array<String>?)
            : SongCursorWrapper {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection: String = if (whereClause != null) {
            MediaStore.Audio.Media.IS_MUSIC + "!=0 AND " + whereClause
        } else {
            MediaStore.Audio.Media.IS_MUSIC + "!=0"
        }
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        val cursor = mContext.contentResolver.query(
            uri,
            null,
            selection,  // where clause
            whereArgs,  //whereargs
            sortOrder)
        return SongCursorWrapper(cursor!!)
    }

    // returns the albumart uri
    private fun getAlbumUri(albumId: Int): Uri {
        val albumArtUri = Uri.parse("content://media/external/audio/albumart")
        return ContentUris.withAppendedId(albumArtUri, albumId.toLong())
    }

    companion object {
        private var sSongDataLab: SongDataLab? = null
        public fun get(context: Context): SongDataLab {
            if (sSongDataLab == null) {
                sSongDataLab = SongDataLab(context)
            }
            return sSongDataLab!!
        }

    }

    init {
//                mDatabase = SongDbHelper(mContext).getWritableDatabase()
        try {

            songs = querySongs()
        } catch (e: java.lang.Exception) {

        }
    }
}
