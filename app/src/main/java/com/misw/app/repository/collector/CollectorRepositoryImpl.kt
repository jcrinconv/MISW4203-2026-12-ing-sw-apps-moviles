package com.misw.app.repository.collector

import android.content.Context
import android.util.Log
import com.misw.app.model.Collector
import com.misw.app.network.CacheManager
import com.misw.app.network.collector.CollectorRemoteDataSource

class CollectorRepositoryImpl(
    private val context: Context,
    private val remoteDataSource: CollectorRemoteDataSource = CollectorRemoteDataSource()
) : CollectorRepository {

    override suspend fun getCollectors(): List<Collector> {
        val potentialResp = CacheManager.getInstance(context).getCollectors()
        return if (potentialResp.isEmpty()) {
            Log.d("Cache decision", "get collectors from network")
            val collectors = remoteDataSource.fetchCollectors()
            CacheManager.getInstance(context).addCollectors(collectors)
            collectors
        } else {
            Log.d("Cache decision", "return ${potentialResp.size} collectors from cache")
            potentialResp
        }
    }
}
