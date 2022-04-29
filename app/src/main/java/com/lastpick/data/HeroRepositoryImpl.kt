package com.lastpick.data

import com.lastpick.data.model.HeroStats
import io.reactivex.Single

class HeroRepositoryImpl(private val openDotaApi: OpenDotaApi) : HeroRepository {

    override suspend fun heroStatsCoroutine() = openDotaApi.heroStatsCoroutine().body() as List<HeroStats>

    override fun heroStats(): Single<List<HeroStats>> = openDotaApi.heroStats()
}