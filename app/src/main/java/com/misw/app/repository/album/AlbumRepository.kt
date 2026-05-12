package com.misw.app.repository.album

import com.misw.app.model.Album
import com.misw.app.model.AlbumRequest
import com.misw.app.model.Track
import com.misw.app.model.TrackRequest

interface AlbumRepository {
    suspend fun getAlbums(): List<Album>
    suspend fun getAlbumById(id: Int): Album
    suspend fun createAlbum(album: AlbumRequest): Album
    suspend fun addTrack(albumId: Int, track: TrackRequest): Track
    suspend fun getGenres(): List<String>
    suspend fun getRecordLabels(): List<String>
}