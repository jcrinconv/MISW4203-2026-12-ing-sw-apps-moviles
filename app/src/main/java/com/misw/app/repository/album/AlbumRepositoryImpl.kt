package com.misw.app.repository.album

import android.content.Context
import android.util.Log
import com.misw.app.model.Album
import com.misw.app.model.AlbumRequest
import com.misw.app.model.Track
import com.misw.app.model.TrackRequest
import com.misw.app.network.album.AlbumRemoteDataSource
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

    override suspend fun createAlbum(album: AlbumRequest): Album {
        return remoteDataSource.createAlbum(album)
    }

    override suspend fun addTrack(albumId: Int, track: TrackRequest): Track {
        return remoteDataSource.addTrack(albumId, track)
    }

    override suspend fun getGenres(): List<String> {
        val potentialResp = CacheManager.getInstance(context).getGenres()
        return if (potentialResp.isEmpty()) {
            Log.d("Cache decision", "get genres from network")
            val genres = remoteDataSource.fetchGenres()
            CacheManager.getInstance(context).addGenres(genres)
            genres
        } else {
            Log.d("Cache decision", "return ${potentialResp.size} genres from cache")
            potentialResp
        }
    }

    override suspend fun getRecordLabels(): List<String> {
        val potentialResp = CacheManager.getInstance(context).getRecordLabels()
        return if (potentialResp.isEmpty()) {
            Log.d("Cache decision", "get record labels from network")
            val recordLabels = remoteDataSource.fetchRecordLabels()
            CacheManager.getInstance(context).addRecordLabels(recordLabels)
            recordLabels
        } else {
            Log.d("Cache decision", "return ${potentialResp.size} record labels from cache")
            potentialResp
        }
    }
}
