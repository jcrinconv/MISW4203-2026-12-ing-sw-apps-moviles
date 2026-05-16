package com.misw.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.misw.app.R
import com.misw.app.repository.album.AlbumRepository
import com.misw.app.repository.album.AlbumRepositoryImpl
import kotlinx.coroutines.launch

class AlbumCreateViewModel(application: Application) : AndroidViewModel(application) {

    private val repository : AlbumRepository = AlbumRepositoryImpl(application)

    private val _genres = MutableLiveData<List<String>>()
    val genres: LiveData<List<String>> get() = _genres

    private val _recordLabels = MutableLiveData<List<String>>()
    val recordLabels: LiveData<List<String>> get() = _recordLabels

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading


    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _genres.value = repository.getGenres()
                _recordLabels.value = repository.getRecordLabels()
            } catch (_: Exception) {
                _error.value = getApplication<Application>().getString(R.string.error_loading_content)
            }
            finally {
                _isLoading.value = false
            }
        }
    }
}