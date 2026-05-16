package com.misw.app.network.collector

import com.misw.app.model.Collector
import com.misw.app.model.CollectorAlbum
import com.misw.app.network.RetrofitClient

class CollectorRemoteDataSource {

    private val collectorApiService: CollectorApiService by lazy {
        RetrofitClient.collectorApiService
    }

    suspend fun fetchCollectors(): List<Collector> {
        return collectorApiService.getCollectors()
    }

    suspend fun fetchCollectorById(id: Int): Collector {
        return collectorApiService.getCollectorById(id)
    }

    suspend fun fetchCollectorAlbums(id: Int): List<CollectorAlbum> {
        return collectorApiService.getCollectorAlbums(id)
    }
}
