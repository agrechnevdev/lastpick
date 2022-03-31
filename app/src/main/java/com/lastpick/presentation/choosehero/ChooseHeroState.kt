package com.lastpick.presentation.choosehero

import com.lastpick.domain.model.Hero
import com.ww.roxie.BaseState

data class ChooseHeroState(
    val screenStatus: ScreenStatus
) : BaseState {

    sealed class ScreenStatus {
        object Loading : ScreenStatus()
        data class Error(val errorMessage: String) : ScreenStatus()
        data class Heroes(val listHeroes: List<Hero> = emptyList()) : ScreenStatus()
    }
}