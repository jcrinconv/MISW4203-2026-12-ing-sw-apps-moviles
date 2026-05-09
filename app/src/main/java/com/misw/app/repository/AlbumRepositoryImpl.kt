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
        val potentialResp = CacheManager.getInstance(context).getAlbumDetail(id)
        return if (potentialResp == null) {
            Log.d("Cache decision", "get album detail from network")
            val album = remoteDataSource.fetchAlbumById(id)
            CacheManager.getInstance(context).addAlbumDetail(id, album)
            album
        } else {
            Log.d("Cache decision", "return album detail from cache")
            potentialResp
        }
    }
}
