package com.misw.app.network.musician

import com.misw.app.model.Musician
import com.misw.app.model.PerformerPrize
import retrofit2.http.GET
import retrofit2.http.Path

interface MusicianApiService {
    @GET("musicians")
    suspend fun getMusicians(): List<Musician>

    @GET("musicians/{id}")
    suspend fun getMusicianById(@Path("id") id: Int): Musician

    @GET("musicians/{id}/performerPrizes")
    suspend fun getPerformerPrizesByMusicianId(@Path("id") id: Int): List<PerformerPrize>
}