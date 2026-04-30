package com.misw.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.util.Log
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

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun loadMusician(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
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

            } catch (e: Exception) {
                _error.value = e.message
                Log.e("MusicianDetail", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}