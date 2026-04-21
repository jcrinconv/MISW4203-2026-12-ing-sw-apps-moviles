package com.misw.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.app.model.Album
import com.misw.app.repository.AlbumRepository
import kotlinx.coroutines.launch

class AlbumDetailViewModel(
    private val repository: AlbumRepository
) : ViewModel() {

    private val _album = MutableLiveData<Album>()
    val album: LiveData<Album> get() = _album

    fun loadAlbum(id: Int) {
        viewModelScope.launch {
            try {
                _album.value = repository.getAlbumById(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}