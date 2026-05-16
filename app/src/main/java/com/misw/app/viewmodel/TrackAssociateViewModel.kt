package com.misw.app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.misw.app.model.TrackRequest
import com.misw.app.repository.album.AlbumRepositoryImpl
import kotlinx.coroutines.launch

class TrackAssociateViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AlbumRepositoryImpl(application)

    private val _associationSuccess = MutableLiveData<Boolean>()
    val associationSuccess: LiveData<Boolean> get() = _associationSuccess

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun associateTrack(albumId: Int, trackName: String, minutos: String, segundos: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val duration = "$minutos:$segundos"
                val trackRequest = TrackRequest(name = trackName, duration = duration)
                repository.addTrack(albumId, trackRequest)
                _associationSuccess.value = true
            } catch (e: Exception) {
                Log.e("TrackAssociate", "Error associating track: ${e.message}", e)
                _error.value = e.message
                _associationSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
}
