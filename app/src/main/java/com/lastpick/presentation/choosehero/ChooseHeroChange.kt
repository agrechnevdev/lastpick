package com.lastpick.presentation.choosehero

import com.lastpick.domain.model.Hero

sealed class ChooseHeroChange {
    object Loading : ChooseHeroChange()
    data class HeroesLoaded(val list: List<Hero>) : ChooseHeroChange()
    data class Error(val throwable: Throwable?) : ChooseHeroChange()
}
