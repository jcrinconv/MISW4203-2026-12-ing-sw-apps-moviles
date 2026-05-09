package com.misw.app.repository

import android.content.Context
import android.util.Log
import com.misw.app.model.Album
import com.misw.app.network.AlbumRemoteDataSource
import com.misw.app.network.CacheManager

class AlbumRepositoryImpl(
    private val context: Context,
    private val remoteDataSource: AlbumRemoteDataSource = AlbumRemoteDataSource()
) : AlbumRepository {

    override suspend fun getAlbums(): List<Album> {
        val potentialResp = CacheManager.getInstance(context).getAlbums()
        return if (potentialResp.isEmpty()) {
            Log.d("Cache decision", "get albums from network")
            val albums = remoteDataSource.fetchAlbums()
            CacheManager.getInstance(context).addAlbums(albums)
            albums
        } else {
            Log.d("Cache decision", "return ${potentialResp.size} albums from cache")
            potentialResp
        }
    }

    override suspend fun getAlbumById(id: Int): Album {
        // For now, we fetch from network as CacheManager only stores the list
        return remoteDataSource.fetchAlbumById(id)
    }
}
