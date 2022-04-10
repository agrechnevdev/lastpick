package com.lastpick.presentation.choosehero

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lastpick.R
import com.lastpick.domain.HeroInteractor
import com.lastpick.domain.HeroesStorage
import com.lastpick.presentation.model.Team
import com.lastpick.presentation.model.isFriend
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ChooseHeroViewModel(
    initialState: ChooseHeroState?,
    private val heroInteractor: HeroInteractor,
    private val heroesStorage: HeroesStorage
) : BaseViewModel<ChooseHeroAction, ChooseHeroState>() {

    override val initialState =
        initialState ?: ChooseHeroState(screenState = ChooseHeroState.ScreenState.Loading)

    private val reducer: Reducer<ChooseHeroState, ChooseHeroAction> = { state, action ->
        when (action) {
            is ChooseHeroAction.ReloadHeroes -> state.copy(
                screenState = ChooseHeroState.ScreenState.Loading
            )
            is ChooseHeroAction.HeroesLoaded -> {
                heroesStorage.heroes = action.list
                state.copy(
                    screenState = ChooseHeroState.ScreenState.ScreenShow()
                )
            }
            is ChooseHeroAction.HeroesError -> state.copy(
                screenState = ChooseHeroState.ScreenState.Error(errorMessage = R.string.choose_hero_error)
            )
            is ChooseHeroAction.ChooseHero -> {
                if (action.team.isFriend()) {
                    val map =
                        (state.screenState as ChooseHeroState.ScreenState.ScreenShow).friendTeam.mapHeroes.toMutableMap()
                    map[action.position] = action.hero
                    val newTeam = Team.FriendTeam(map)

                    state.copy(screenState = state.screenState.copy(friendTeam = newTeam))
                } else {
                    val map =
                        (state.screenState as ChooseHeroState.ScreenState.ScreenShow).enemyTeam.mapHeroes.toMutableMap()
                    map[action.position] = action.hero
                    val newTeam = Team.EnemyTeam(map)
                    state.copy(screenState = state.screenState.copy(enemyTeam = newTeam))
                }
            }
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {

        val loadHeroes =
            actions.filter { it is ChooseHeroAction.ReloadHeroes }
                .switchMap {
                    heroInteractor.heroesStats()
                        .subscribeOn(Schedulers.io())
                        .toObservable()
                        .map<ChooseHeroAction> { ChooseHeroAction.HeroesLoaded(it) }
                        .onErrorReturn { ChooseHeroAction.HeroesError(it) }
                }

        val chooseHero =
            actions.filter { it is ChooseHeroAction.ChooseHero }
                .map {
                    val action = it as ChooseHeroAction.ChooseHero
                    ChooseHeroAction.ChooseHero(
                        team = action.team,
                        position = action.position,
                        hero = action.hero
                    )
                }

        val allactions = Observable.merge(loadHeroes, chooseHero)

        disposables.addAll(
            allactions
                .doOnNext { Log.d("", it.toString()) }
                .scan(initialState, reducer)
                .doOnNext { Log.d("", it.screenState.toString()) }
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(state::postValue, {})
        )
    }

    fun clear() {
        onCleared()
    }


    class Factory(
        private val initialState: ChooseHeroState?,
        private val heroInteractor: HeroInteractor,
        private val heroesStorage: HeroesStorage
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChooseHeroViewModel(initialState, heroInteractor, heroesStorage) as T
        }
    }
}