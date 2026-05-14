package com.misw.app.repository.collector

import com.misw.app.model.Collector
import com.misw.app.model.CollectorAlbum

interface CollectorRepository {
    suspend fun getCollectors(): List<Collector>
    suspend fun getCollectorById(id: Int): Collector
    suspend fun getCollectorAlbums(id: Int): List<CollectorAlbum>
}
