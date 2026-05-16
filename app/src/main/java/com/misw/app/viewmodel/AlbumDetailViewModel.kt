package com.misw.app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.misw.app.model.Album
import com.misw.app.repository.album.AlbumRepository
import com.misw.app.repository.album.AlbumRepositoryImpl
import kotlinx.coroutines.launch

class AlbumDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository : AlbumRepository = AlbumRepositoryImpl(application)

    private val _album = MutableLiveData<Album>()
    val album: LiveData<Album> get() = _album

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun loadAlbum(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _album.value = repository.getAlbumById(id)
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("AlbumDetail", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}