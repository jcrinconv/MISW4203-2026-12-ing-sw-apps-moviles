package com.misw.app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.misw.app.R
import com.misw.app.model.Album
import com.misw.app.repository.album.AlbumRepository
import com.misw.app.repository.album.AlbumRepositoryImpl
import kotlinx.coroutines.launch

class AlbumViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AlbumRepository = AlbumRepositoryImpl(application)

    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> get() = _albums

    private val _query = MutableLiveData<String>("")
    val query: LiveData<String> get() = _query

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private var originalList: List<Album> = emptyList()
    private var currentCriterion = SortCriterion.NAME
    private var currentOrder = SortOrder.ASCENDING

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        fetchAlbums()
    }

    fun fetchAlbums() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = repository.getAlbums()
                originalList = result
                updateAlbumList()
            } catch (e: Exception) {
                Log.e("AlbumViewModel", "Error fetching albums: ${e::class.simpleName} - ${e.message}", e)
                _error.value = getApplication<Application>().getString(R.string.error_loading_content)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterAlbums(text: String) {
        _query.value = text
        updateAlbumList()
    }

    fun setSortCriterion(criterion: SortCriterion) {
        currentCriterion = criterion
        updateAlbumList()
    }

    fun toggleSortOrder() {
        currentOrder = if (currentOrder == SortOrder.ASCENDING) {
            SortOrder.DESCENDING
        } else {
            SortOrder.ASCENDING
        }
        updateAlbumList()
    }

    private fun updateAlbumList() {
        val currentText = _query.value ?: ""

        val filtered = if (currentText.isEmpty()) {
            originalList
        } else {
            originalList.filter { it.name.contains(currentText, ignoreCase = true) }
        }

        val processedList = when (currentCriterion) {
            SortCriterion.NAME -> {
                if (currentOrder == SortOrder.ASCENDING) filtered.sortedBy { it.name }
                else filtered.sortedByDescending { it.name }
            }
            SortCriterion.RELEASE_DATE -> {
                if (currentOrder == SortOrder.ASCENDING) filtered.sortedBy { it.releaseDate }
                else filtered.sortedByDescending { it.releaseDate }
            }
        }

        _albums.value = processedList
    }
}
