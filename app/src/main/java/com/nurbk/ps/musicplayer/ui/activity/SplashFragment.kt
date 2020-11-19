package com.nurbk.ps.musicplayer.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProvider
import com.nurbk.ps.musicplayer.R
import com.nurbk.ps.musicplayer.databinding.FragmentSplashBinding
import com.nurbk.ps.musicplayer.ui.viewmodel.SplashViewModel

class SplashFragment : AppCompatActivity() {

    lateinit var mBinding: FragmentSplashBinding

    val viewModel by lazy {
        ViewModelProvider(this)[SplashViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = FragmentSplashBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        val a: Animation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        a.reset()

        mBinding.imageView.clearAnimation()
        mBinding.imageView.startAnimation(a)

        viewModel.liveData.observe(this) {
            goToMainActivity()
        }

    }


    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}