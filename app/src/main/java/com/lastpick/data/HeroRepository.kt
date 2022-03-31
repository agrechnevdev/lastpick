package com.lastpick.data

import com.lastpick.data.model.HeroStats
import io.reactivex.Single

interface HeroRepository {

    fun heroStats(): Single<List<HeroStats>>
}