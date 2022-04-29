package com.lastpick.data

import com.lastpick.data.model.HeroStats
import io.reactivex.Single

interface HeroRepository {

    suspend fun heroStatsCoroutine(): List<HeroStats>

    fun heroStats(): Single<List<HeroStats>>
}