package com.lastpick.presentation.choosehero

import com.ww.roxie.BaseAction

sealed class ChooseHeroAction : BaseAction {
    object LoadHeroes : ChooseHeroAction()
//    object AddHero : ChooseHeroAction()
//    object ChooseHero : ChooseHeroAction()
}