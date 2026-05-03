package com.misw.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.util.Log
import com.misw.app.model.Album
import com.misw.app.model.Musician
import com.misw.app.model.MusicianPrizeItem
import com.misw.app.network.musician.MusicianRemoteDataSource
import com.misw.app.network.prize.PrizeRemoteDataSource
import com.misw.app.repository.musician.MusicianRepository
import com.misw.app.repository.musician.MusicianRepositoryImpl
import com.misw.app.repository.prize.PrizeRepository
import com.misw.app.repository.prize.PrizeRepositoryImpl
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class MusicianDetailViewModel : ViewModel() {

    private val musicianRepository : MusicianRepository = MusicianRepositoryImpl(MusicianRemoteDataSource())
    private val prizeRepository : PrizeRepository = PrizeRepositoryImpl(PrizeRemoteDataSource())

    private val _musician = MutableLiveData<Musician>()
    val musician: LiveData<Musician> get() = _musician

    private val _prizes = MutableLiveData<List<MusicianPrizeItem>>()
    val prizes: LiveData<List<MusicianPrizeItem>> get() = _prizes

    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> get() = _albums

    private val _query = MutableLiveData<String>("")
    val query: LiveData<String> get() = _query

    private var originalAlbums: List<Album> = emptyList()
    private var currentSortCriterion = SortCriterion.NAME
    private var currentSortOrder = SortOrder.ASCENDING

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun loadMusician(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val musician = musicianRepository.getMusicianById(id)
                _musician.value = musician

                val prizesList = if (musician.performerPrizes.isEmpty()) {
                    emptyList()
                } else {
                    musician.performerPrizes.map { performerPrize ->
                        async {
                            val prize = prizeRepository.getPrizeById(performerPrize.id)
                            MusicianPrizeItem(
                                name = prize.name,
                                premiationDate = performerPrize.premiationDate
                            )
                        }
                    }.awaitAll()
                }

                _prizes.value = prizesList
                
                originalAlbums = musician.albums
                updateAlbumList()

            } catch (e: Exception) {
                _error.value = e.message
                Log.e("MusicianDetail", "Error: ${e.message}")
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
        currentSortCriterion = criterion
        updateAlbumList()
    }

    fun toggleSortOrder() {
        currentSortOrder = if (currentSortOrder == SortOrder.ASCENDING) {
            SortOrder.DESCENDING
        } else {
            SortOrder.ASCENDING
        }
        updateAlbumList()
    }

    private fun updateAlbumList() {
        val currentText = _query.value ?: ""

        val filtered = if (currentText.isEmpty()) {
            originalAlbums
        } else {
            originalAlbums.filter { it.name.contains(currentText, ignoreCase = true) }
        }

        val processedList = when (currentSortCriterion) {
            SortCriterion.NAME -> {
                if (currentSortOrder == SortOrder.ASCENDING) filtered.sortedBy { it.name }
                else filtered.sortedByDescending { it.name }
            }
            SortCriterion.RELEASE_DATE -> {
                if (currentSortOrder == SortOrder.ASCENDING) filtered.sortedBy { it.releaseDate }
                else filtered.sortedByDescending { it.releaseDate }
            }
        }

        _albums.value = processedList
    }
}