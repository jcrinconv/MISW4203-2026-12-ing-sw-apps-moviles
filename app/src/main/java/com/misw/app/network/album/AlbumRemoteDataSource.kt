package com.misw.app.network.album

import com.misw.app.model.Album
import com.misw.app.model.AlbumRequest
import com.misw.app.model.Track
import com.misw.app.model.TrackRequest
import com.misw.app.network.RetrofitClient

open class AlbumRemoteDataSource {

    private val albumApiService: AlbumApiService by lazy {
        RetrofitClient.albumApiService
    }

    open suspend fun fetchAlbums(): List<Album> {
        return albumApiService.getAlbums()
    }

    open suspend fun fetchAlbumById(id: Int): Album {
        return albumApiService.getAlbumById(id)
    }

    open suspend fun createAlbum(album: AlbumRequest): Album {
        return albumApiService.createAlbum((album))
    }

    open suspend fun addTrack(albumId: Int, track: TrackRequest): Track {
        return  albumApiService.addTrack(albumId, track)
    }

    open suspend fun fetchGenres(): List<String> {
        return albumApiService.getGenres()
    }

    open suspend fun fetchRecordLabels(): List<String> {
        return albumApiService.getRecordLabels()
    }
}