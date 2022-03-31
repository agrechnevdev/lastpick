package com.lastpick.data

import com.lastpick.data.model.HeroStats
import io.reactivex.Single

class HeroRepositoryImpl(private val openDotaApi: OpenDotaApi) : HeroRepository {

    override fun heroStats(): Single<List<HeroStats>> = openDotaApi.heroStats()
}