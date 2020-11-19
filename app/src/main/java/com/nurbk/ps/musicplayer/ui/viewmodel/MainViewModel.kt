package com.nurbk.ps.musicplayer.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nurbk.ps.musicplayer.db.SongDataLab
import com.nurbk.ps.musicplayer.model.AlbumModel
import com.nurbk.ps.musicplayer.model.SongModel
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {


    private val _getSongsDataLiveData = MutableLiveData<List<SongModel>>()

    val getSongsDataLiveData: LiveData<List<SongModel>> = _getSongsDataLiveData

    fun getSongs(context: Context) = viewModelScope.launch {
        _getSongsDataLiveData.postValue(SongDataLab.get(context).getSongs())
    }


    private val _getAlbumDataLiveData = MutableLiveData<List<AlbumModel>>()

    val getAlbumDataLiveData: LiveData<List<AlbumModel>> = _getAlbumDataLiveData

    fun getAlbum(context: Context) = viewModelScope.launch {
        _getAlbumDataLiveData.postValue(SongDataLab.get(context).albums)
    }


    private val _getAlbumSongDataLiveData = MutableLiveData<List<SongModel>>()

    val getAlbumSongDataLiveData: LiveData<List<SongModel>> = _getAlbumSongDataLiveData

    fun getAlbumSong(context: Context, albumModel: AlbumModel) = viewModelScope.launch {
        _getAlbumSongDataLiveData.postValue(albumModel.albumSongs!!)
    }


    init {
        getSongs(getApplication())
        getAlbum(getApplication())
    }

}