package com.misw.app.network.musician

import com.misw.app.model.Musician
import retrofit2.http.GET
import retrofit2.http.Path

interface MusicianApiService {
    @GET("musicians")
    suspend fun getMusicians(): List<Musician>

    @GET("musicians/{id}")
    suspend fun getMusicianById(@Path("id") id: Int): Musician
}