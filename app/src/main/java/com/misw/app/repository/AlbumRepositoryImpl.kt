package com.misw.app.repository

import com.misw.app.model.Album
import com.misw.app.network.AlbumRemoteDataSource

class AlbumRepositoryImpl(
    private val remoteDataSource: AlbumRemoteDataSource = AlbumRemoteDataSource()
) : AlbumRepository {

    override suspend fun getAlbums(): List<Album> {
        return remoteDataSource.fetchAlbums()
    }

    override suspend fun getAlbumById(id: Int): Album {
        return remoteDataSource.fetchAlbumById(id)
    }
}