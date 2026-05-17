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

    fun areFieldsEmpty(trackName: String, minutos: String, segundos: String): Boolean {
        return trackName.isBlank() and minutos.isBlank() and segundos.isBlank()
    }

    fun isFormValid(trackName: String, minutos: String, segundos: String): Boolean {
        if (trackName.isBlank()) return false
        if (minutos.isBlank()) return false
        if (segundos.isBlank()) return false
        minutos.toIntOrNull() ?: return false
        val segundosInt = segundos.toIntOrNull() ?: return false
        return segundosInt <= 59
    }

    fun associateTrack(albumId: Int, trackName: String, minutos: String, segundos: String) {
        viewModelScope.launch {
            // Validación de campos
            when {
                trackName.isBlank() -> {
                    _error.value = "El nombre del track no puede estar vacío"
                    _associationSuccess.value = false
                    return@launch
                }
                minutos.isBlank() -> {
                    _error.value = "Los minutos no pueden estar vacíos"
                    _associationSuccess.value = false
                    return@launch
                }
                segundos.isBlank() -> {
                    _error.value = "Los segundos no pueden estar vacíos"
                    _associationSuccess.value = false
                    return@launch
                }
            }

            // Validar que sean números válidos
            val minutosInt = minutos.toIntOrNull()
            val segundosInt = segundos.toIntOrNull()

            when {
                minutosInt == null -> {
                    _error.value = "Los minutos deben ser un número válido"
                    _associationSuccess.value = false
                    return@launch
                }
                segundosInt == null -> {
                    _error.value = "Los segundos deben ser un número válido"
                    _associationSuccess.value = false
                    return@launch
                }
                segundosInt > 59 -> {
                    _error.value = "Los segundos no pueden ser mayores a 59"
                    _associationSuccess.value = false
                    return@launch
                }
            }

            _isLoading.value = true
            _error.value = null
            try {
                val formattedMinutos = minutos.padStart(2, '0')
                val formattedSegundos = segundos.padStart(2, '0')
                val duration = "$formattedMinutos:$formattedSegundos"
                val trackRequest = TrackRequest(name = trackName, duration = duration)
                repository.addTrack(albumId, trackRequest)
                _associationSuccess.value = true
            } catch (e: Exception) {
                Log.e("TrackAssociate", "Error associating track: ${e.message}", e)
                _error.value = "Error al asociar el track: ${e.message}"
                _associationSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
}
