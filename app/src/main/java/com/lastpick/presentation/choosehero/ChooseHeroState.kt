package com.lastpick.presentation.choosehero

import androidx.annotation.StringRes
import com.lastpick.presentation.model.Team
import com.ww.roxie.BaseState

data class ChooseHeroState(
    val screenState: ScreenState
) : BaseState {

    sealed class ScreenState {
        object Loading : ScreenState()
        data class Error(@StringRes val errorMessage: Int) : ScreenState()
        data class ScreenShow(
            val friendTeam: Team.FriendTeam = Team.FriendTeam,
            val enemyTeam: Team.EnemyTeam = Team.EnemyTeam
        ) : ScreenState()
    }
}