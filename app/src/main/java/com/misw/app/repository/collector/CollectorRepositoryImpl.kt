package com.misw.app.repository.collector

import com.misw.app.model.Collector
import com.misw.app.network.collector.CollectorRemoteDataSource

class CollectorRepositoryImpl(
    private val remoteDataSource: CollectorRemoteDataSource = CollectorRemoteDataSource()
) : CollectorRepository {

    override suspend fun getCollectors(): List<Collector> {
        return remoteDataSource.fetchCollectors()
    }
    
}