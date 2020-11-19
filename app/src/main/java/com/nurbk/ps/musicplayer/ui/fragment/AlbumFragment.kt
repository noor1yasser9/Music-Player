package com.nurbk.ps.musicplayer.ui.fragment

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.nurbk.ps.musicplayer.R
import com.nurbk.ps.musicplayer.adapter.AlbumAdapter
import com.nurbk.ps.musicplayer.databinding.FragmentAlbumsBinding
import com.nurbk.ps.musicplayer.model.AlbumModel
import com.nurbk.ps.musicplayer.other.ALL_DATA
import com.nurbk.ps.musicplayer.other.SONG
import com.nurbk.ps.musicplayer.other.permission
import com.nurbk.ps.musicplayer.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch


class AlbumFragment : Fragment(R.layout.fragment_albums) {

    private lateinit var mBinding: FragmentAlbumsBinding

    private val adapterAlbum by lazy {
        AlbumAdapter(arrayListOf())
    }

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FragmentAlbumsBinding.bind(view)
        setHasOptionsMenu(false)
        mBinding.albumsRecycleview.apply {
            adapter = adapterAlbum
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
        }
        permission(
            requireContext(),
            arrayListOf(Manifest.permission.READ_EXTERNAL_STORAGE), {
                viewModel.getAlbumDataLiveData.observe(viewLifecycleOwner) { list ->
                    adapterAlbum.data.addAll(list)
                    adapterAlbum.notifyDataSetChanged()
                }
            }, { requireActivity().finish() })


        adapterAlbum.setItemClickListener { album, position ->

            val album = (album as AlbumModel)

            val bundle = Bundle()
            bundle.putParcelable(SONG, album)
            findNavController()
                .navigate(R.id.globalActionToAlbumDetailsFragment, bundle)
        }
    }
}