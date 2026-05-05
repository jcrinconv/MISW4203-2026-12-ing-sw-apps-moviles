package com.misw.app.network.collector

import com.misw.app.model.Collector
import retrofit2.http.GET

interface CollectorApiService {

    @GET("collectors")
    suspend fun getCollectors(): List<Collector>
}