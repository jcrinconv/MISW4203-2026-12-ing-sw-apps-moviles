package com.misw.app.repository.musician

import com.misw.app.model.Musician
import com.misw.app.network.musician.MusicianRemoteDataSource

class MusicianRepositoryImpl(
    private val remoteDataSource: MusicianRemoteDataSource = MusicianRemoteDataSource()
) : MusicianRepository {

    override suspend fun getMusicians(): List<Musician> {
        return remoteDataSource.fetchMusicians()
    }
}