package com.lastpick.presentation.choosehero

import com.lastpick.domain.model.Hero
import com.ww.roxie.BaseState

data class ChooseHeroState(
    val isLoading: Boolean = false,
    val isIdle: Boolean = false,
    val mapFriendHeroes: HashMap<Int, Hero?> = hashMapOf(
        1 to null,
        2 to null,
        3 to null,
        4 to null,
        5 to null
    ),
    val mapEnemyHeroes: HashMap<Int, Hero?> = hashMapOf(
        1 to null,
        2 to null,
        3 to null,
        4 to null,
        5 to null
    ),
    val errorMessage: String? = null,
    val listHeroes: List<Hero> = emptyList()
) : BaseState