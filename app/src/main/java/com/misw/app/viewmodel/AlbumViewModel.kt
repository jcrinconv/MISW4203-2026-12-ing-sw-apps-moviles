package com.misw.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.app.model.Album
import com.misw.app.repository.AlbumRepository
import com.misw.app.repository.AlbumRepositoryImpl
import kotlinx.coroutines.launch

enum class SortCriterion {
    NAME, RELEASE_DATE
}

class AlbumViewModel(
    private val repository: AlbumRepository = AlbumRepositoryImpl()
) : ViewModel() {
    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> get() = _albums

    private val _query = MutableLiveData<String>("")
    val query: LiveData<String> get() = _query

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private var originalList: List<Album> = emptyList()
    private var currentCriterion = SortCriterion.NAME
    private var currentOrder = SortOrder.ASCENDING

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        fetchAlbums()
    }

    private fun fetchAlbums() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getAlbums()
                originalList = result
                updateAlbumList()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar álbumes"
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