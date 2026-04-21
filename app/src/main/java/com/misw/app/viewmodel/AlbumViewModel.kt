package com.misw.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.app.model.Album
import com.misw.app.repository.AlbumRepository
import com.misw.app.repository.AlbumRepositoryImpl
import kotlinx.coroutines.launch

class AlbumViewModel(
    private val repository: AlbumRepository = AlbumRepositoryImpl()
) : ViewModel() {

    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> get() = _albums

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    init {
        fetchAlbums()
    }

    private fun fetchAlbums() {
        viewModelScope.launch {
            try {
                val result = repository.getAlbums()
                _albums.value = result
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar álbumes"
            }
        }
    }
}
