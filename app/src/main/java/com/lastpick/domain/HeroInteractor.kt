package com.lastpick.domain

import com.lastpick.domain.model.Hero
import io.reactivex.Single

interface HeroInteractor {

    suspend fun heroesStatsCoroutine(): List<Hero>

    fun heroesStats(): Single<List<Hero>>

}