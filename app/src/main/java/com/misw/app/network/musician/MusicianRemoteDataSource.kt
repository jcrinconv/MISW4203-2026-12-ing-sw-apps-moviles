package com.misw.app.network.musician

import com.misw.app.model.Musician
import com.misw.app.model.PerformerPrize
import com.misw.app.network.RetrofitClient

class MusicianRemoteDataSource {
    private val musicianApiService: MusicianApiService by lazy {
        RetrofitClient.musicianApiService
    }

    suspend fun fetchMusicians(): List<Musician> {
        return musicianApiService.getMusicians()
    }

    suspend fun fetchMusicianById(id: Int): Musician {
        return musicianApiService.getMusicianById(id)
    }

    suspend fun fetchPerformerPrizesByMusicianId(id: Int): List<PerformerPrize> {
        return musicianApiService.getPerformerPrizesByMusicianId(id)
    }
}
