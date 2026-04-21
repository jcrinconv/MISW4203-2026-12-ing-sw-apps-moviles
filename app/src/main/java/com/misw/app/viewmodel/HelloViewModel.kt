package com.misw.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.app.repository.HelloRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HelloViewModel(
    private val repository: HelloRepository = HelloRepository()
) : ViewModel() {

    private val _greeting = MutableLiveData<String>()
    val greeting: LiveData<String> get() = _greeting

    init {
        loadGreeting()
    }

    private fun loadGreeting() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.getGreeting()
            }
            _greeting.value = result
        }
    }
}
