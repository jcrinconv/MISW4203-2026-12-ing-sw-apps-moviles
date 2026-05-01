package com.misw.app.repository.prize

import com.misw.app.model.Prize
import com.misw.app.network.prize.PrizeRemoteDataSource

class PrizeRepositoryImpl(
    private val remoteDataSource: PrizeRemoteDataSource = PrizeRemoteDataSource()
) : PrizeRepository {

    override suspend fun getPrizeById(id: Int): Prize {
        return remoteDataSource.fetchPrizeById(id)
    }
}