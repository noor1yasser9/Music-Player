package com.nurbk.ps.musicplayer.ui.fragment


import android.Manifest
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nurbk.ps.musicplayer.R
import com.nurbk.ps.musicplayer.adapter.MusicAdapter
import com.nurbk.ps.musicplayer.databinding.FragmentSongsBinding
import com.nurbk.ps.musicplayer.model.SongModel
import com.nurbk.ps.musicplayer.other.ALL_DATA
import com.nurbk.ps.musicplayer.other.POSITION
import com.nurbk.ps.musicplayer.other.SONG
import com.nurbk.ps.musicplayer.other.permission
import com.nurbk.ps.musicplayer.ui.viewmodel.MainViewModel


class SongsFragment : Fragment(R.layout.fragment_songs),
    SearchView.OnQueryTextListener {

    private lateinit var mBinding: FragmentSongsBinding


    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    private val adapterSong by lazy {
        MusicAdapter(ArrayList())
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FragmentSongsBinding.bind(view)


        setHasOptionsMenu(true)



        permission(
            requireContext(),
            arrayListOf(Manifest.permission.READ_EXTERNAL_STORAGE), {

                viewModel.getSongsDataLiveData.observe(viewLifecycleOwner) { list ->
                    adapterSong.apply {
                        datas.addAll(list)
                        notifyDataSetChanged()
                    }
                }
            }, { requireActivity().finish() })


        mBinding.recycleview.apply {
            adapter = adapterSong
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }

        adapterSong.setItemClickListener { song, poistion ->
            val bundle = Bundle()
            bundle.putParcelable(SONG, song as SongModel)
            bundle.putInt(POSITION, poistion)
            bundle.putParcelableArrayList(ALL_DATA, adapterSong.datas)
            findNavController()
                .navigate(R.id.globalActionToSongFragment, bundle)
        }


    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        val item = menu.findItem(R.id.search)
        val searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(this)
    }


    override fun onQueryTextSubmit(query: String?): Boolean {

        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        adapterSong.filter.filter(newText)
        return true
    }
}


