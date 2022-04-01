package com.lastpick.presentation.choosehero

import com.lastpick.domain.model.Hero
import com.lastpick.presentation.model.Position
import com.lastpick.presentation.model.Team

sealed class ChooseHeroChange {
    object Loading : ChooseHeroChange()
    data class HeroesLoaded(val list: List<Hero>) : ChooseHeroChange()
    data class Error(val throwable: Throwable?) : ChooseHeroChange()

    data class ChooseHero(val team: Team, val position: Position, val hero: Hero) : ChooseHeroChange()
}
