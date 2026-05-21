package com.misw.app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.misw.app.model.AlbumRequest
import com.misw.app.repository.album.AlbumRepository
import com.misw.app.repository.album.AlbumRepositoryImpl
import kotlinx.coroutines.launch
import java.util.Locale

class AlbumCreateViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AlbumRepository = AlbumRepositoryImpl(application)

    private val _genres = MutableLiveData<List<String>>().apply {
        value = com.misw.app.model.Genre.entries.map { it.displayName }
    }
    val genres: LiveData<List<String>> get() = _genres

    private val _recordLabels = MutableLiveData<List<String>>().apply {
        value = com.misw.app.model.RecordLabel.entries.map { it.displayName }
    }
    val recordLabels: LiveData<List<String>> get() = _recordLabels

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> get() = _isSuccess

    private val _nameError = MutableLiveData<String?>()
    val nameError: LiveData<String?> get() = _nameError

    private val _coverError = MutableLiveData<String?>()
    val coverError: LiveData<String?> get() = _coverError

    private val _descriptionError = MutableLiveData<String?>()
    val descriptionError: LiveData<String?> get() = _descriptionError

    private val _dateError = MutableLiveData<String?>()
    val dateError: LiveData<String?> get() = _dateError

    private val _genreError = MutableLiveData<String?>()
    val genreError: LiveData<String?> get() = _genreError

    private val _recordLabelError = MutableLiveData<String?>()
    val recordLabelError: LiveData<String?> get() = _recordLabelError

    fun areFieldsEmpty(
        name: String, cover: String, description:String, day: String, monthIndex: Int, year: String, genreIndex: Int, recordLabelIndex: Int
    ): Boolean {
        return name.isBlank() and cover.isBlank() and description.isBlank() and
                day.isBlank() and (monthIndex == 0) and year.isBlank() and (genreIndex == 0) and (recordLabelIndex == 0)
    }

    fun createAlbum(
        name: String,
        cover: String,
        description: String,
        day: String,
        monthIndex: Int,
        year: String,
        genre: String,
        recordLabel: String
    ) {

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val releaseDate = formatISO8601(day, (monthIndex + 1).toString(), year)
                val genreKey =
                    com.misw.app.model.Genre.entries.find { it.displayName == genre }?.slug ?: genre
                val recordLabelKey =
                    com.misw.app.model.RecordLabel.entries.find { it.displayName == recordLabel }?.slug ?: recordLabel
                val albumRequest = AlbumRequest(
                    name = name,
                    cover = cover,
                    releaseDate = releaseDate,
                    description = description,
                    genre = genreKey,
                    recordLabel = recordLabelKey
                )
                repository.createAlbum(albumRequest)
                _isSuccess.value = true
            } catch (e: Exception) {
                Log.e("AlbumCreateViewModel", "Error al crear el álbum", e.cause)
                _error.value = "Error al crear el álbum: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun validateFields(
        name: String,
        cover: String,
        description: String,
        day: String,
        monthIndex: Int,
        year: String,
        genre: String,
        recordLabel: String
    ): Boolean {
        var isValid = true

        if (name.isBlank()) {
            _nameError.value = "El nombre es obligatorio"
            isValid = false
        } else {
            _nameError.value = null
        }

        if (cover.isBlank()) {
            _coverError.value = "La URL de la portada es obligatoria"
            isValid = false
        } else if (!android.util.Patterns.WEB_URL.matcher(cover).matches()) {
            _coverError.value = "URL no válida"
            isValid = false
        } else {
            _coverError.value = null
        }

        if (description.isBlank()) {
            _descriptionError.value = "La descripción es obligatoria"
            isValid = false
        } else {
            _descriptionError.value = null
        }

        if (day.isBlank() || year.isBlank()) {
            _dateError.value = "Fecha incompleta"
            isValid = false
        } else {
            try {
                val d = day.toInt()
                val m = monthIndex + 1
                val y = year.toInt()
                if (d < 1 || d > 31 || m < 1 || m > 12 || y < 1900 || y > 2100) {
                    _dateError.value = "Fecha no válida"
                    isValid = false
                } else {
                    _dateError.value = null
                }
            } catch (_: Exception) {
                _dateError.value = "Fecha no válida"
                isValid = false
            }
        }

        if (genre.isBlank()) {
            _genreError.value = "El género es obligatorio"
            isValid = false
        } else {
            _genreError.value = null
        }

        if (recordLabel.isBlank()) {
            _recordLabelError.value = "El sello es obligatorio"
            isValid = false
        } else {
            _recordLabelError.value = null
        }

        return isValid
    }

    private fun formatISO8601(day: String, month: String, year: String): String {
        return String.format(
            Locale.US, "%04d-%02d-%02dT00:00:00-05:00", year.toInt(), month.toInt(), day.toInt()
        )
    }
}
