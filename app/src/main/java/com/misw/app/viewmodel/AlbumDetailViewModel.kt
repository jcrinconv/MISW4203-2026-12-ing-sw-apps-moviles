package com.misw.app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.app.model.Album
import com.misw.app.network.AlbumRemoteDataSource
import com.misw.app.repository.AlbumRepository
import com.misw.app.repository.AlbumRepositoryImpl
import kotlinx.coroutines.launch

class AlbumDetailViewModel : ViewModel() {

    private val repository : AlbumRepository = AlbumRepositoryImpl(AlbumRemoteDataSource())

    private val _album = MutableLiveData<Album>()
    val album: LiveData<Album> get() = _album

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun loadAlbum(id: Int) {
        viewModelScope.launch {
            try {
                _album.value = repository.getAlbumById(id)
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("AlbumDetail", "Error: ${e.message}")
            }
        }
    }
}