package com.misw.app.repository.prize

import com.misw.app.model.Prize

interface PrizeRepository {
    suspend fun getPrizeById(id: Int): Prize
}