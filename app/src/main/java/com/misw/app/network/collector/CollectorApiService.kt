package com.misw.app.network.collector

import com.misw.app.model.Collector
import retrofit2.http.GET
import retrofit2.http.Path

interface CollectorApiService {

    @GET("collectors")
    suspend fun getCollectors(): List<Collector>

    @GET("collectors/{id}")
    suspend fun getCollectorById(@Path("id") id: Int): Collector
}