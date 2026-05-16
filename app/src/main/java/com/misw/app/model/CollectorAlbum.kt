package com.misw.app.model

data class CollectorAlbum(
    val id: Int,
    val price: Int,
    val status: String,
    val album: Album? = null
)
