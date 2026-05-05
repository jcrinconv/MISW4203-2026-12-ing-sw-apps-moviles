package com.misw.app.repository.collector

import com.misw.app.model.Collector

interface CollectorRepository {
    suspend fun getCollectors(): List<Collector>
}