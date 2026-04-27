package com.misw.app.network

import com.misw.app.model.Album

class AlbumRemoteDataSource {

    private val apiService: AlbumApiService by lazy {
        RetrofitClient.apiService
    }

    suspend fun fetchAlbums(): List<Album> {
        return try {
            apiService.getAlbums()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fetchAlbumById(id: Int): Album {
        return apiService.getAlbumById(id)
    }

}