package com.misw.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.misw.app.R
import com.misw.app.model.Collector
import com.misw.app.network.EspressoIdlingResource
import com.misw.app.repository.collector.CollectorRepository
import com.misw.app.repository.collector.CollectorRepositoryImpl
import kotlinx.coroutines.launch

class CollectorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CollectorRepository = CollectorRepositoryImpl(application)

    private val _collectors = MutableLiveData<List<Collector>>()
    val collectors: LiveData<List<Collector>> get() = _collectors

    private val _collector = MutableLiveData<Collector?>()
    val collector: LiveData<Collector?> get() = _collector

    private val _query = MutableLiveData<String>("")
    val query: LiveData<String> get() = _query

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private var originalList: List<Collector> = emptyList()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchCollectors() {
        EspressoIdlingResource.increment()
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = repository.getCollectors()
                originalList = result
                updateCollectorsList()
            } catch (_: Exception) {
                _error.value =
                    getApplication<Application>().getString(R.string.error_loading_content)
            } finally {
                _isLoading.value = false
                EspressoIdlingResource.decrement()
            }
        }
    }

    fun fetchCollectorDetail(id: Int) {
        EspressoIdlingResource.increment()
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val collectorResult = repository.getCollectorById(id)
                val albumsResult = repository.getCollectorAlbums(id)

                _collector.value = collectorResult.copy(collectorAlbums = albumsResult)
            } catch (_: Exception) {
                _error.value =
                    getApplication<Application>().getString(R.string.error_loading_content)
            } finally {
                _isLoading.value = false
                EspressoIdlingResource.decrement()
            }
        }
    }

    fun filterCollectors(text: String) {
        _query.value = text
        updateCollectorsList()
    }

    private fun updateCollectorsList() {
        val currentText = _query.value ?: ""

        val filtered = if (currentText.isEmpty()) {
            originalList
        } else {
            originalList.filter { it.name.contains(currentText, ignoreCase = true) }
        }

        _collectors.value = filtered
    }
}
