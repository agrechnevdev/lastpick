package com.lastpick.presentation.teams

import androidx.annotation.StringRes
import com.lastpick.presentation.model.Position
import com.lastpick.presentation.model.Team
import com.mvicore.MviState

data class TeamsMviState(
    val screenState: ScreenState
) : MviState {

    sealed class ScreenState {
        object Loading : ScreenState()
        data class Error(@StringRes val errorMessage: Int) : ScreenState()
        data class ScreenShow(
            val friendTeam: Team.FriendTeam = Team.FriendTeam(),
            val enemyTeam: Team.EnemyTeam = Team.EnemyTeam(),
            val bottomSheetOpen: Boolean = false,
            val lastChoice: LastChoice = LastChoice()
        ) : ScreenState()
    }

    data class LastChoice(
        val team: Team = Team.FriendTeam(),
        val position: Position = Position.FIRST
    )
}