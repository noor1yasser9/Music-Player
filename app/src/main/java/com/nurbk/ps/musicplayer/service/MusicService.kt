package com.nurbk.ps.musicplayer.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import androidx.core.net.toUri
import com.nurbk.ps.musicplayer.db.SongDataLab
import com.nurbk.ps.musicplayer.model.SongModel

class MusicService : Service(), MediaPlayer.OnCompletionListener {

    val mBinder: IBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    var musicFiles: List<SongModel> = ArrayList<SongModel>()
    val songs: List<SongModel>
        get() = SongDataLab.get(this).getSongs()
    var uri: Uri? = null
    var position = -1
    var actionPlaying: ActionPlaying? = null

    override fun onCreate() {
        super.onCreate()
        musicFiles = songs
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    inner class MyBinder : Binder() {
        fun getService() = this@MusicService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        position = intent!!.getIntExtra("service", -1)
        val actionName = intent.getStringExtra("ActionName")

        if (position != -1 && intent.hasExtra("service"))
            playMedia(position)

        if (actionName != null) {
            when (actionName) {
                "playPause" -> {
                    Toast.makeText(this, "playPause", Toast.LENGTH_LONG).show()
                    if (actionPlaying != null) {
                        actionPlaying!!.playPauseBtnClicked()
                    }
                }
                "next" -> {
                    Toast.makeText(this, "next", Toast.LENGTH_LONG).show()
                    if (actionPlaying != null) {
                        actionPlaying!!.nextBtnClicked()
                    }
                }
                "previous" -> {
                    Toast.makeText(this, "previous", Toast.LENGTH_LONG).show()
                    if (actionPlaying != null) {
                        actionPlaying!!.prevBtnClicked()
                    }
                }
            }
        }

        return START_STICKY
    }

    private fun playMedia(startPosition: Int) {

        musicFiles = songs
        position = startPosition
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
            }

            createMediaPlayer(position)
            mediaPlayer!!.start()
        } else {
            createMediaPlayer(position)
            mediaPlayer!!.start()
        }

    }


    fun start() {
        if (mediaPlayer != null)
            mediaPlayer!!.start()
    }

    fun pause() {
        mediaPlayer!!.pause()
    }

    fun isPlaying(): Boolean {
        if (mediaPlayer == null)
            return false
        return mediaPlayer!!.isPlaying
    }

    fun stop() {
        mediaPlayer!!.stop()
    }

    fun release() {
        mediaPlayer!!.release()
    }

    fun getDuration(): Int {
        return mediaPlayer!!.duration
    }

    fun seekTo(position: Int) {
        mediaPlayer!!.seekTo(position)
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer!!.currentPosition
    }

    fun createMediaPlayer(position: Int) {
        uri = musicFiles[position].data!!.toUri()
        mediaPlayer = MediaPlayer.create(baseContext, uri)
        onCompleted()
    }

    fun onCompleted() {
        mediaPlayer!!.setOnCompletionListener(this)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if (actionPlaying != null) {
            actionPlaying!!.nextBtnClicked()
        }
        createMediaPlayer(position)
        mediaPlayer!!.start()
        onCompleted()
    }

    fun setCallback(actionPlaying: ActionPlaying) {
        this.actionPlaying = actionPlaying
    }
}