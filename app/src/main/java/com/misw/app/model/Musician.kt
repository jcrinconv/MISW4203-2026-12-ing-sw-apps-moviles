package com.misw.app.models

import com.misw.app.model.Album

data class Musician(
    val id: Int,
    val name: String,
    val image: String,
    val description: String,
    val birthDate: String,
    val albums: List<Album> = emptyList()
)
