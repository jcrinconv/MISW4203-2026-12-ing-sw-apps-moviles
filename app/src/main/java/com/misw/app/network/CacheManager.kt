package com.misw.app.network

import android.content.Context
import com.misw.app.model.Album
import com.misw.app.model.Collector
import com.misw.app.model.Comment
import com.misw.app.model.Musician

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
    private var comments: HashMap<Int, List<Comment>> = hashMapOf()

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
}
