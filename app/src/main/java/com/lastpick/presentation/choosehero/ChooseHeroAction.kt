package com.lastpick.presentation.choosehero

import com.lastpick.domain.model.Hero
import com.lastpick.presentation.model.Position
import com.lastpick.presentation.model.Team
import com.ww.roxie.BaseAction

sealed class ChooseHeroAction : BaseAction {
    object ReloadHeroes : ChooseHeroAction()
    data class HeroesLoaded(val list: List<Hero>) : ChooseHeroAction()
    data class HeroesError(val throwable: Throwable?) : ChooseHeroAction()

    data class ChooseHero(val team: Team, val position: Position, val hero: Hero) : ChooseHeroAction()
    // object Hero : ChooseHeroAction()
}