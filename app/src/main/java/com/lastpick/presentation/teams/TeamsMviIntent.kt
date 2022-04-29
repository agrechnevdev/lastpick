package com.lastpick.presentation.teams

import com.lastpick.domain.model.Hero
import com.lastpick.presentation.model.Position
import com.lastpick.presentation.model.Team
import com.mvicore.MviIntent

sealed class TeamsMviIntent : MviIntent {
    object ReloadHeroes : TeamsMviIntent()
    data class ClickHeroButton(val team: Team, val pos: Position) : TeamsMviIntent()
    data class HeroChosen(val hero: Hero) : TeamsMviIntent()
}
