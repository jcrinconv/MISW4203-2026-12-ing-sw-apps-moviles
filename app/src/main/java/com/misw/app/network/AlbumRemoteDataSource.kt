package com.misw.app.network

import com.misw.app.model.Album

class AlbumRemoteDataSource {

    private val albumApiService: AlbumApiService by lazy {
        RetrofitClient.albumApiService
    }

    suspend fun fetchAlbums(): List<Album> {
        return try {
            albumApiService.getAlbums()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fetchAlbumById(id: Int): Album {
        return albumApiService.getAlbumById(id)
    }

}