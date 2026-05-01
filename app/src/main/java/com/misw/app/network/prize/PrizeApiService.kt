package com.misw.app.network.prize

import com.misw.app.model.Prize
import retrofit2.http.GET
import retrofit2.http.Path

interface PrizeApiService {

    @GET("prizes/{id}")
    suspend fun getPrizeById(@Path("id") id: Int): Prize
}