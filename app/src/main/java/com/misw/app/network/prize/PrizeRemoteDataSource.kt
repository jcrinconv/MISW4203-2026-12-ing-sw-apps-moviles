package com.misw.app.network.prize

import com.misw.app.model.Prize
import com.misw.app.network.RetrofitClient

class PrizeRemoteDataSource {

    private val prizeApiService: PrizeApiService by lazy {
        RetrofitClient.prizeApiService
    }

    suspend fun fetchPrizeById(id: Int): Prize {
        return prizeApiService.getPrizeById(id)
    }
}