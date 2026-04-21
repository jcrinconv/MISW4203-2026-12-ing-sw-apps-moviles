package com.misw.app.repository

import com.misw.app.model.Album

interface AlbumRepository {
    suspend fun getAlbumById(id: Int): Album?
}