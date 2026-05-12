package com.misw.app.network

import android.content.Context
import com.misw.app.model.Album
import com.misw.app.model.Collector
import com.misw.app.model.Musician
import com.misw.app.model.PerformerPrize

class CacheManager private constructor() {
    companion object {
        @Volatile
        private var instance: CacheManager? = null
        fun getInstance(@Suppress("UNUSED_PARAMETER") context: Context) =
            instance ?: synchronized(this) {
                instance ?: CacheManager().also {
                    instance = it
                }
            }
    }

    private var albums: List<Album> = listOf()
    private var musicians: List<Musician> = listOf()
    private var collectors: List<Collector> = listOf()

    private val albumDetails = mutableMapOf<Int, Album>()
    private val musicianDetails = mutableMapOf<Int, Musician>()
    private val collectorDetails = mutableMapOf<Int, Collector>()
    private val performerPrizes = mutableMapOf<Int, List<PerformerPrize>>()

    fun addAlbums(newAlbums: List<Album>) {
        if (albums.isEmpty()) {
            albums = newAlbums
        }
    }

    fun getAlbums(): List<Album> {
        return albums
    }

    fun addMusicians(newMusicians: List<Musician>) {
        if (musicians.isEmpty()) {
            musicians = newMusicians
        }
    }

    fun getMusicians(): List<Musician> {
        return musicians
    }

    fun addCollectors(newCollectors: List<Collector>) {
        if (collectors.isEmpty()) {
            collectors = newCollectors
        }
    }

    fun getCollectors(): List<Collector> {
        return collectors
    }

    fun addAlbumDetail(id: Int, album: Album) {
        albumDetails[id] = album
    }

    fun getAlbumDetail(id: Int): Album? {
        return albumDetails[id]
    }

    fun addMusicianDetail(id: Int, musician: Musician) {
        musicianDetails[id] = musician
    }

    fun getMusicianDetail(id: Int): Musician? {
        return musicianDetails[id]
    }

    fun addCollectorDetail(id: Int, collector: Collector) {
        collectorDetails[id] = collector
    }

    fun getCollectorDetail(id: Int): Collector? {
        return collectorDetails[id]
    }

    fun addPerformerPrizes(musicianId: Int, prizes: List<PerformerPrize>) {
        performerPrizes[musicianId] = prizes
    }

    fun getPerformerPrizes(musicianId: Int): List<PerformerPrize>? {
        return performerPrizes[musicianId]
    }

    fun clearCache() {
        albums = listOf()
        musicians = listOf()
        collectors = listOf()
        albumDetails.clear()
        musicianDetails.clear()
        collectorDetails.clear()
        performerPrizes.clear()
    }
}
