package com.nurbk.ps.musicplayer.ui.activity

import android.app.NotificationManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.nurbk.ps.musicplayer.R
import com.nurbk.ps.musicplayer.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.toolbar2)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        navHostFragment.findNavController()
            .addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.albumDetailsFragment,
                    R.id.playFragment,
                    -> {
                        mBinding.toolbar2.visibility = View.GONE
                    }
                    else ->
                        mBinding.toolbar2.visibility = View.VISIBLE

                }
            }
    }





}