package com.misw.app.network.album

import com.misw.app.model.Album
import com.misw.app.model.AlbumRequest
import com.misw.app.model.Track
import com.misw.app.model.TrackRequest
import com.misw.app.network.RetrofitClient

class AlbumRemoteDataSource {

    private val albumApiService: AlbumApiService by lazy {
        RetrofitClient.albumApiService
    }

    suspend fun fetchAlbums(): List<Album> {
        return albumApiService.getAlbums()
    }

    suspend fun fetchAlbumById(id: Int): Album {
        return albumApiService.getAlbumById(id)
    }

    suspend fun createAlbum(album: AlbumRequest): Album {
        return albumApiService.createAlbum((album))
    }

    suspend fun addTrack(albumId: Int, track: TrackRequest): Track {
        return  albumApiService.addTrack(albumId, track)
    }

    suspend fun fetchGenres(): List<String> {
        return albumApiService.getGenres()
    }

    suspend fun fetchRecordLabels(): List<String> {
        return albumApiService.getRecordLabels()
    }
}