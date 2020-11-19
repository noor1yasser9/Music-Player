package com.nurbk.ps.musicplayer.ui.fragment


import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.nurbk.ps.musicplayer.MyApplication
import com.nurbk.ps.musicplayer.R
import com.nurbk.ps.musicplayer.databinding.FragmentPlayerBinding
import com.nurbk.ps.musicplayer.model.SongModel
import com.nurbk.ps.musicplayer.other.*
import com.nurbk.ps.musicplayer.receiver.NotificationReceiver
import com.nurbk.ps.musicplayer.service.*
import com.nurbk.ps.musicplayer.ui.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_player.*
import kotlin.random.Random


class PlayerFragment : Fragment(R.layout.fragment_player),
    ActionPlaying, ServiceConnection {

    private lateinit var mBinding: FragmentPlayerBinding
    private lateinit var song: SongModel
    private var poistion: Int = 0
    private lateinit var uri: Uri
    private lateinit var allSong: List<SongModel>

    private var playThread: Thread? = null
    private var prevThread: Thread? = null
    private var nextThread: Thread? = null

    private val handle = Handler()
    private var isShuffle = false
    private var isRepeat = false

    private var musicService: MusicService? = null
    private var mediaSessionCompat: MediaSessionCompat? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FragmentPlayerBinding.bind(view)


        requireArguments().apply {
            song = getParcelable(SONG)!!
            allSong = getParcelableArrayList(ALL_DATA)!!
            poistion = allSong.indexOf(song)
        }

        mediaSessionCompat = MediaSessionCompat(requireContext(), "My Audio")

        if (savedInstanceState != null) {
            poistion = savedInstanceState.getInt(POSITION)
            song = savedInstanceState.getParcelable(SONG)!!
            val uri = savedInstanceState.getString(URI_SONG)
            val maxSeek = savedInstanceState.getInt(MAX_SEEK_BAR)
            val currentSeek = savedInstanceState.getInt(CURRENT_POSITION_SEEK_BAR)
            val isPlay = savedInstanceState.getBoolean(MEDIA_IS_START)

            musicService!!.createMediaPlayer(poistion)
            musicService!!.seekTo(currentSeek)
            if (isPlay)
                musicService!!.start()

            mBinding.seek.max = maxSeek
            mBinding.seek.progress = currentSeek

            metaData(
                requireContext(), song.albumId.toLong(),
                mBinding.imageplayers,
                mBinding.linearLayout,
                mBinding.songName,
                mBinding.artistName,
                mBinding.totalTime,
                mBinding.currentTime
            )
            mBinding.seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    if (musicService != null && fromUser) {
                        musicService!!.seekTo(progress)
                    }

                    mBinding.totalTime.text = Helper.toTimeFormat(song.duration - progress)
                    mBinding.totalTime.text = Helper.toTimeFormat(progress.toLong())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            })

            onHandel()

        } else
            initView(song)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(POSITION, poistion)
        outState.putInt(MAX_SEEK_BAR, mBinding.seek.max)
        outState.putInt(CURRENT_POSITION_SEEK_BAR, mBinding.seek.progress)
        outState.putBoolean(MEDIA_IS_START, musicService!!.isPlaying())
        outState.putString(URI_SONG, song.data)
        outState.putParcelable(SONG, song)
    }

    val intent by lazy {
        Intent(requireActivity(), MusicService::class.java)
    }

    private fun initView(song: SongModel) {
        uri = Uri.parse(song.data)



        metaData(
            requireContext(), song.albumId.toLong(),
            mBinding.imageplayers,
            mBinding.linearLayout,
            mBinding.songName,
            mBinding.artistName,
            mBinding.totalTime,
            mBinding.currentTime
        )

        mBinding.seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean,
            ) {
                if (musicService != null && fromUser) {
                    musicService!!.seekTo(progress)
                }

                mBinding.totalTime.text = Helper.toTimeFormat(song.duration - progress)
                mBinding.currentTime.text = Helper.toTimeFormat(progress.toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })


        mBinding.artistName.text = song.title
        mBinding.songName.text = song.albumName
        mBinding.pause.setImageResource(R.drawable.pause_24dp)


        intent.putExtra("service", poistion)
        ActivityCompat.startForegroundService(requireContext(), intent)
        mBinding.seek.max = song.duration.toInt()

        onHandel()

    }


    override fun onResume() {
        val intent = Intent(requireActivity(), MusicService::class.java)
        requireActivity().bindService(intent, this, Context.BIND_AUTO_CREATE)

        playThreadBtn()
        nextThreadBtn()
        prevThreadBtn()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unbindService(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        musicService!!.stop()
        musicService!!.release()
        musicService!!.stopForeground(true)
        requireActivity().stopService(intent)
    }

    private fun prevThreadBtn() {
        prevThread = Thread {
            try {
                mBinding.previous.setOnClickListener {
                    prevBtnClicked()
                }
            } catch (e: Exception) {

            }
        }
        prevThread!!.start()
    }

    override fun prevBtnClicked() {

        if (isShuffle && !isRepeat) {
            poistion = getShuffleNum(allSong.size - 1)
        } else if (!isShuffle && !isRepeat) {
            poistion = if ((poistion - 1) < 0) (allSong.size - 1) else (poistion - 1)
        }
        song = allSong[poistion]
        initView(song)
        showNotification(R.drawable.pause_24dp)

    }

    private fun nextThreadBtn() {
        nextThread = Thread {
            try {
                mBinding.next.setOnClickListener {
                    nextBtnClicked()
                }
            } catch (e: Exception) {

            }
        }
        nextThread!!.start()
    }

    override fun nextBtnClicked() {
        if (isShuffle && !isRepeat) {
            poistion = getShuffleNum(allSong.size - 1)
        } else if (!isShuffle && !isRepeat) {
            poistion = ((poistion + 1) % allSong.size)
        }
        song = allSong[poistion]
        initView(song)
        showNotification(R.drawable.pause_24dp)

    }

    private fun getShuffleNum(i: Int): Int {
        val random = Random(i)
        return random.nextInt(i + 1)
    }

    private fun playThreadBtn() {
        playThread = Thread {
            try {
                mBinding.pause.setOnClickListener {
                    playPauseBtnClicked()
                }
            } catch (e: Exception) {

            }

        }
        playThread!!.start()
    }


    override fun playPauseBtnClicked() {
        if (musicService!!.isPlaying()) {
            mBinding.pause.setImageResource(R.drawable.play_arrow_24dp)
            musicService!!.pause()
            mBinding.seek.max = musicService!!.getDuration()
            showNotification(R.drawable.play_arrow_24dp)
        } else {
            mBinding.pause.setImageResource(R.drawable.pause_24dp)
            musicService!!.start()
            seek.max = musicService!!.getDuration()
            showNotification(R.drawable.pause_24dp)

        }
        onHandel()
    }

    private fun onHandel() {
        requireActivity().runOnUiThread(object : Runnable {
            override fun run() {
                if (musicService != null) {
                    try {
                        val mCurrentPosition = musicService!!.getCurrentPosition()
                        mBinding.seek.progress = mCurrentPosition
                    } catch (e: Exception) {

                    }

                }
                handle.postDelayed(this, 1000)
            }
        })
    }


    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        val myBinder = binder as MusicService.MyBinder
        musicService = myBinder.getService()
        musicService!!.musicFiles = allSong
        musicService!!.setCallback(this)
        showNotification(R.drawable.pause_24dp)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
    }

    private fun showNotification(playPauseBtn: Int) {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.putExtra("song", "song")
        val contentIntent = PendingIntent.getActivity(requireContext(), 0, intent, 0)

        val prevIntent = Intent(requireActivity(), NotificationReceiver::class.java)
        prevIntent.action = MyApplication.ACTION_PREVIOUS
        val contentPrev = PendingIntent
            .getBroadcast(requireContext(), 0, prevIntent, 0)
        val nextIntent = Intent(requireActivity(), NotificationReceiver::class.java)
        nextIntent.action = MyApplication.ACTION_NEXT
        val contentNext = PendingIntent
            .getBroadcast(requireContext(), 0, nextIntent, 0)
        val playIntent = Intent(requireActivity(), NotificationReceiver::class.java)
        playIntent.action = MyApplication.ACTION_PLAY
        val contentPlay = PendingIntent
            .getBroadcast(requireContext(), 0, playIntent, 0)
        val picture = getAlbumArt(
            song.data
        )
        var thump: Bitmap? = null
        thump = if (picture != null)
            BitmapFactory
                .decodeByteArray(picture, 0, picture.size)
        else
            BitmapFactory.decodeResource(resources, R.drawable.xd)

        val style = androidx.media.app.NotificationCompat.MediaStyle()
        val notification = NotificationCompat
            .Builder(requireContext(), MyApplication.CHANNEL_ID_1)
            .setSmallIcon(playPauseBtn)
            .setLargeIcon(thump)
            .setContentTitle(song.title)
            .setContentText(song.artistName)
            .addAction(R.drawable.previous_24dp, MyApplication.ACTION_PREVIOUS, contentPrev)
            .addAction(playPauseBtn, MyApplication.ACTION_PLAY, contentPlay)
            .addAction(R.drawable.next_24dp, MyApplication.ACTION_NEXT, contentNext)
            .setSound(null)
            .setStyle(
                style.setMediaSession(mediaSessionCompat!!.sessionToken)
            )
            .setProgress(song.duration.toInt(), 0, true)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)

        musicService!!.startForeground(1, notification.build())
    }


}
