package com.misw.app.network

import com.misw.app.model.Album
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AlbumRemoteDataSource {
    private val BASE_URL = "http://10.0.2.2:3000/"

    private val apiService: AlbumApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AlbumApiService::class.java)
    }

    suspend fun fetchAlbumById(id: Int): Album {
        return apiService.getAlbumById(id)
    }

}