package com.lastpick.domain

import com.lastpick.data.HeroRepository
import com.lastpick.domain.model.Hero
import io.reactivex.Single

class HeroInteractorImpl(private val heroRepository: HeroRepository) : HeroInteractor {

    override suspend fun heroesStatsCoroutine(): List<Hero> {
        return heroRepository.heroStatsCoroutine().map { stats -> Hero(stats.localized_name, stats.img) }
    }

    override fun heroesStats(): Single<List<Hero>> {
        return heroRepository.heroStats().map { list ->
            val heroes = mutableListOf<Hero>()
            list.forEach { heroes.add(Hero(it.localized_name, it.img)) }
            heroes
        }
    }
}