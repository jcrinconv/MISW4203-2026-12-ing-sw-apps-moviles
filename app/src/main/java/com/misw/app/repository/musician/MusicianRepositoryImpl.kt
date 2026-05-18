package com.misw.app.repository.musician

import android.util.Log
import com.misw.app.model.Musician
import com.misw.app.model.PerformerPrize
import com.misw.app.network.CacheManager
import com.misw.app.network.musician.MusicianRemoteDataSource

class MusicianRepositoryImpl(
    private val remoteDataSource: MusicianRemoteDataSource = MusicianRemoteDataSource()
) : MusicianRepository {

    override suspend fun getMusicians(): List<Musician> {
        val potentialResp = CacheManager.getInstance().getMusicians()
        return if (potentialResp.isEmpty()) {
            Log.d("Cache decision", "get musicians from network")
            val musicians = remoteDataSource.fetchMusicians()
            CacheManager.getInstance().addMusicians(musicians)
            musicians
        } else {
            Log.d("Cache decision", "return ${potentialResp.size} musicians from cache")
            potentialResp
        }
    }

    override suspend fun getMusicianById(id: Int): Musician {
        val potentialResp = CacheManager.getInstance().getMusicianDetail(id)
        return if (potentialResp == null) {
            Log.d("Cache decision", "get musician detail from network")
            val musician = remoteDataSource.fetchMusicianById(id)
            CacheManager.getInstance().addMusicianDetail(id, musician)
            musician
        } else {
            Log.d("Cache decision", "return musician detail from cache")
            potentialResp
        }
    }

    override suspend fun getPerformerPrizesByMusicianId(id: Int): List<PerformerPrize> {
        val potentialResp = CacheManager.getInstance().getPerformerPrizes(id)
        return if (potentialResp == null) {
            Log.d("Cache decision", "get performer prizes from network")
            val prizes = remoteDataSource.fetchPerformerPrizesByMusicianId(id)
            CacheManager.getInstance().addPerformerPrizes(id, prizes)
            prizes
        } else {
            Log.d("Cache decision", "return ${potentialResp.size} performer prizes from cache")
            potentialResp
        }
    }
}
