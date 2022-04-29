package com.lastpick.presentation.teams

import com.lastpick.domain.model.Hero
import com.lastpick.presentation.model.Position
import com.lastpick.presentation.model.Team
import com.mvicore.MviAction

sealed class TeamsMviAction : MviAction {
    object ShowLoading : TeamsMviAction()
    data class HeroesLoaded(val list: List<Hero>) : TeamsMviAction()
    data class HeroesError(val throwable: Throwable?) : TeamsMviAction()

    data class ClickHeroButton(val team: Team, val pos: Position) : TeamsMviAction()
    data class HeroChosen(val hero: Hero) : TeamsMviAction()
}