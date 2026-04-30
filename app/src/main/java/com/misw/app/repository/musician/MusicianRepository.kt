package com.misw.app.repository.musician

import com.misw.app.model.Musician

interface MusicianRepository {
    suspend fun getMusicians(): List<Musician>
}