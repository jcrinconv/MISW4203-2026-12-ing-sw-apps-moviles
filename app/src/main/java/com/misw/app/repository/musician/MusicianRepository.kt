package com.misw.app.repository.musician

import com.misw.app.model.Musician
import com.misw.app.model.PerformerPrize

interface MusicianRepository {
    suspend fun getMusicians(): List<Musician>
    suspend fun getMusicianById(id: Int): Musician

    suspend fun getPerformerPrizesByMusicianId(id: Int): List<PerformerPrize>
}