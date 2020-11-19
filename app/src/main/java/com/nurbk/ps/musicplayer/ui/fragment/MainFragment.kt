package com.nurbk.ps.musicplayer.ui.fragment

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.nurbk.ps.musicplayer.R
import com.nurbk.ps.musicplayer.adapter.ViewPagerAdapter
import com.nurbk.ps.musicplayer.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var mBinding: FragmentMainBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FragmentMainBinding.bind(view)
        initViewPage()
    }

    private fun initViewPage() {
        val viewPagerAdapter = ViewPagerAdapter(requireActivity())
        viewPagerAdapter.addFragment(SongsFragment(), "Songs")
        viewPagerAdapter.addFragment(AlbumFragment(), "Album")
        mBinding.viewPager.adapter = viewPagerAdapter
        mBinding.viewPager.isUserInputEnabled = true
        TabLayoutMediator(
            mBinding.tableLayout, mBinding.viewPager
        ) { tab: TabLayout.Tab, position: Int ->
            when (position) {
                0 -> {
                    tab.text = "Songs"
                }
                1 -> {
                    tab.text = "Album"
                }
            }
        }.attach()

    }
}