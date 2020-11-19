package com.nurbk.ps.musicplayer.ui.fragment

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.nurbk.ps.musicplayer.R
import com.nurbk.ps.musicplayer.adapter.MusicAdapter
import com.nurbk.ps.musicplayer.databinding.FragmentDetailsAlbumBinding
import com.nurbk.ps.musicplayer.model.AlbumModel
import com.nurbk.ps.musicplayer.model.SongModel
import com.nurbk.ps.musicplayer.other.*
import com.nurbk.ps.musicplayer.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class AlbumDetailsFragment : Fragment(R.layout.fragment_details_album) {

    private lateinit var mBinding: FragmentDetailsAlbumBinding
    private val musicAdapter = MusicAdapter(arrayListOf())
    private lateinit var album: AlbumModel
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FragmentDetailsAlbumBinding.bind(view)

        mBinding.toolbar.apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }



            requireArguments().apply {
                musicAdapter.apply {
                    album = getParcelable(SONG)!!

                    getImage(requireContext(),
                        if (datas.size > 0) datas[0].albumId.toLong() else 0L,
                        mBinding.appBarImage)

                }

                mBinding.ctl.apply {
                    title = album.name
                    setExpandedTitleColor(Color.WHITE)
                    setCollapsedTitleTextColor(Color.WHITE)
                }
            }

            mBinding.rcDataSongs.apply {
                adapter = musicAdapter
                layoutManager = LinearLayoutManager(requireContext())
//                setHasFixedSize(true)
            }
        }
        musicAdapter.setItemClickListener { song, poistion ->
            val bundle = Bundle()
            bundle.putParcelable(SONG, song as SongModel)
            bundle.putInt(POSITION, poistion)
            bundle.putParcelableArrayList(ALL_DATA, musicAdapter.datas)
            findNavController()
                .navigate(R.id.globalActionToSongFragment, bundle)
        }




        requireActivity().runOnUiThread {
            viewModel.getAlbumSongDataLiveData.observe(viewLifecycleOwner) {

                musicAdapter.datas.clear()
                musicAdapter.datas.addAll(it)
                musicAdapter.notifyDataSetChanged()
            }
        }

    }


    override fun onResume() {
        super.onResume()
        viewModel.viewModelScope.launch {
            viewModel.getAlbumSong(requireContext(), album)
        }
    }


}