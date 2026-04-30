package com.misw.app.network.musician

import com.misw.app.model.Musician
import com.misw.app.network.RetrofitClient

class MusicianRemoteDataSource {
    private val musicianApiService: MusicianApiService by lazy {
        RetrofitClient.musicianApiService
    }

    suspend fun fetchMusicians(): List<Musician> {
        return try {
            musicianApiService.getMusicians()
        } catch (e: Exception) {
            emptyList()
        }
    }
}