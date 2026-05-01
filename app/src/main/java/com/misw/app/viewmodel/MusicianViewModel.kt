package com.misw.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.app.model.Musician
import com.misw.app.network.musician.MusicianRemoteDataSource
import com.misw.app.repository.musician.MusicianRepository
import com.misw.app.repository.musician.MusicianRepositoryImpl
import kotlinx.coroutines.launch

class MusicianViewModel : ViewModel() {
    private val repository: MusicianRepository = MusicianRepositoryImpl(MusicianRemoteDataSource())

    private val _musicians = MutableLiveData<List<Musician>>()
    val musicians: LiveData<List<Musician>> get() = _musicians

    private val _query = MutableLiveData<String>("")
    val query: LiveData<String> get() = _query

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private var originalList: List<Musician> = emptyList()
    private var currentOrder = SortOrder.ASCENDING

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        fetchMusicians()
    }

    fun fetchMusicians() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = repository.getMusicians()
                originalList = result
                updateMusicianList()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar artistas"
            } finally {
                _isLoading.value = false
            }
        }
    }

     fun toggleSortOrder() {
        currentOrder = if (currentOrder == SortOrder.ASCENDING) {
            SortOrder.DESCENDING
        } else {
            SortOrder.ASCENDING
        }
        updateMusicianList()
     }

    fun filterMusicians(text: String) {
        _query.value = text
        updateMusicianList()
    }

    private fun updateMusicianList() {
        val currentText = _query.value ?: ""

        val filtered = if (currentText.isEmpty()) {
            originalList
        } else {
            originalList.filter { it.name.contains(currentText, ignoreCase = true) }
        }

        val processedList =
            if (currentOrder == SortOrder.ASCENDING) filtered.sortedBy { it.name }
            else filtered.sortedByDescending { it.name }

        _musicians.value = processedList
    }
}
