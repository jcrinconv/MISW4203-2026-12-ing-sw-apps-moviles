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

enum class SortOrder {
    ASCENDING, DESCENDING
}

class AlbumViewModel(
    private val repository: AlbumRepository = AlbumRepositoryImpl()
) : ViewModel() {

    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> get() = _albums

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private var originalList: List<Album> = emptyList()
    
    private var currentCriterion = SortCriterion.NAME
    private var currentOrder = SortOrder.ASCENDING

    init {
        fetchAlbums()
    }

    private fun fetchAlbums() {
        viewModelScope.launch {
            try {
                val result = repository.getAlbums()
                originalList = result
                applySort()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar álbumes"
            }
        }
    }

    fun setSortCriterion(criterion: SortCriterion) {
        currentCriterion = criterion
        applySort()
    }

    fun toggleSortOrder() {
        currentOrder = if (currentOrder == SortOrder.ASCENDING) {
            SortOrder.DESCENDING
        } else {
            SortOrder.ASCENDING
        }
        applySort()
    }

    private fun applySort() {
        val sortedList = when (currentCriterion) {
            SortCriterion.NAME -> {
                if (currentOrder == SortOrder.ASCENDING) {
                    originalList.sortedBy { it.name }
                } else {
                    originalList.sortedByDescending { it.name }
                }
            }
            SortCriterion.RELEASE_DATE -> {
                if (currentOrder == SortOrder.ASCENDING) {
                    originalList.sortedBy { it.releaseDate }
                } else {
                    originalList.sortedByDescending { it.releaseDate }
                }
            }
        }
        _albums.value = sortedList
    }
}
