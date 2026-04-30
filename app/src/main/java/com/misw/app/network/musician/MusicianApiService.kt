package com.misw.app.network.musician

import com.misw.app.model.Musician
import retrofit2.http.GET

interface MusicianApiService {
    @GET("musicians")
    suspend fun getMusicians(): List<Musician>
}