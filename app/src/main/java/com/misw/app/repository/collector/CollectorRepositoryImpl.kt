package com.misw.app.repository.collector

import android.util.Log
import com.misw.app.model.Collector
import com.misw.app.model.CollectorAlbum
import com.misw.app.network.CacheManager
import com.misw.app.network.collector.CollectorRemoteDataSource

class CollectorRepositoryImpl(
    private val remoteDataSource: CollectorRemoteDataSource = CollectorRemoteDataSource()
) : CollectorRepository {

    override suspend fun getCollectors(): List<Collector> {
        val potentialResp = CacheManager.getInstance().getCollectors()
        return if (potentialResp.isEmpty()) {
            Log.d("Cache decision", "get collectors from network")
            val collectors = remoteDataSource.fetchCollectors()
            CacheManager.getInstance().addCollectors(collectors)
            collectors
        } else {
            Log.d("Cache decision", "return ${potentialResp.size} collectors from cache")
            potentialResp
        }
    }

    override suspend fun getCollectorById(id: Int): Collector {
        val potentialResp = CacheManager.getInstance().getCollectorDetail(id)
        return if (potentialResp == null) {
            Log.d("Cache decision", "get collector $id from network")
            val collector = remoteDataSource.fetchCollectorById(id)
            CacheManager.getInstance().addCollectorDetail(id, collector)
            collector
        } else {
            Log.d("Cache decision", "return collector $id from cache")
            potentialResp
        }
    }

    override suspend fun getCollectorAlbums(id: Int): List<CollectorAlbum> {
        val potentialResp = CacheManager.getInstance().getCollectorAlbums(id)
        return if (potentialResp == null) {
            Log.d("Cache decision", "get collector albums for $id from network")
            val albums = remoteDataSource.fetchCollectorAlbums(id)
            CacheManager.getInstance().addCollectorAlbums(id, albums)
            albums
        } else {
            Log.d("Cache decision", "return collector albums for $id from cache")
            potentialResp
        }
    }
}
