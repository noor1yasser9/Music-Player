package com.nurbk.ps.musicplayer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nurbk.ps.musicplayer.MyApplication.Companion.ACTION_NEXT
import com.nurbk.ps.musicplayer.MyApplication.Companion.ACTION_PLAY
import com.nurbk.ps.musicplayer.MyApplication.Companion.ACTION_PREVIOUS
import com.nurbk.ps.musicplayer.service.MusicService

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val actionName = intent?.action
        if (actionName != null) {
            val serviceIntent = Intent(context, MusicService::class.java)
            when (actionName) {
                ACTION_PLAY -> {
                    serviceIntent.putExtra("ActionName", "playPause")
                    context!!.startService(serviceIntent)
                }
                ACTION_NEXT -> {
                    serviceIntent.putExtra("ActionName", "next")
                    context!!.startService(serviceIntent)
                }
                ACTION_PREVIOUS -> {
                    serviceIntent.putExtra("ActionName", "previous")
                    context!!.startService(serviceIntent)
                }
            }
        }
    }

}