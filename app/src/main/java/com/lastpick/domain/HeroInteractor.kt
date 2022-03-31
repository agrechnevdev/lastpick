package com.lastpick.domain

import com.lastpick.domain.model.Hero
import io.reactivex.Single

interface HeroInteractor {

    fun heroesStats(): Single<List<Hero>>

}