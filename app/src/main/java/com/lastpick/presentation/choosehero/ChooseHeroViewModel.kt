package com.lastpick.presentation.choosehero

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lastpick.R
import com.lastpick.domain.HeroInteractor
import com.lastpick.presentation.model.Team
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class ChooseHeroViewModel(
    initialState: ChooseHeroState?,
    private val heroInteractor: HeroInteractor
) : BaseViewModel<ChooseHeroAction, ChooseHeroState>() {

    override val initialState = initialState ?: ChooseHeroState(screenState = ChooseHeroState.ScreenState.Loading)

    private val reducer: Reducer<ChooseHeroState, ChooseHeroChange> = { state, change ->
        when (change) {
            is ChooseHeroChange.Loading -> state.copy(
                screenState = ChooseHeroState.ScreenState.Loading
            )
            is ChooseHeroChange.HeroesLoaded -> state.copy(
                screenState = ChooseHeroState.ScreenState.Heroes(listHeroes = change.list)
            )
            is ChooseHeroChange.Error -> state.copy(
                screenState = ChooseHeroState.ScreenState.Error(errorMessage = R.string.choose_hero_error)
            )
            is ChooseHeroChange.ChooseHero -> {
                if (change.team is Team.FriendTeam) {
                    val newTeam = state.friendTeam
                    newTeam.mapHeroes[change.position] = change.hero
                    state.copy(friendTeam = newTeam)
                } else {
                    val newTeam = state.enemyTeam
                    newTeam.mapHeroes[change.position] = change.hero
                    state.copy(enemyTeam = newTeam)
                }
            }
        }

    }

    init {
        bindActions()
    }

    private fun bindActions() {

        val loadHeroes =
            actions.filter { it is ChooseHeroAction.LoadHeroes }
                .switchMap {
                    heroInteractor.heroesStats()
                        .subscribeOn(Schedulers.io())
                        .toObservable()
                        .map<ChooseHeroChange> { ChooseHeroChange.HeroesLoaded(it) }
                        .onErrorReturn { ChooseHeroChange.Error(it) }
                        .startWith(ChooseHeroChange.Loading)
                }

        val chooseHero =
            actions.filter { it is ChooseHeroAction.ChooseHero }
                .map {
                    val action = it as ChooseHeroAction.ChooseHero
                    ChooseHeroChange.ChooseHero(team = action.team, position = action.position, hero = action.hero)
                }

        val allChanges = Observable.merge(loadHeroes, chooseHero)

        disposables.addAll(
            allChanges
                .scan(initialState, reducer)
                .distinctUntilChanged()
                .subscribe(state::postValue, {})
        )
    }

    class Factory(
        private val initialState: ChooseHeroState?,
        private val heroInteractor: HeroInteractor
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChooseHeroViewModel(initialState, heroInteractor) as T
        }
    }
}