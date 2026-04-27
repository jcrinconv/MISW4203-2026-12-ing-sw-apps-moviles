package com.misw.app.repository

import com.misw.app.model.Album

interface AlbumRepository {
    suspend fun getAlbums(): List<Album>
    suspend fun getAlbumById(id: Int): Album
}