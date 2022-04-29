package com.lastpick.presentation.teams

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lastpick.R
import com.lastpick.domain.HeroInteractor
import com.lastpick.domain.HeroesStorage
import com.lastpick.presentation.model.Team
import com.lastpick.presentation.model.isFriend
import com.mvicore.ReducerFun
import com.ww.roxie.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TeamsViewModel(
    private val heroInteractor: HeroInteractor,
    private val heroesStorage: HeroesStorage
) : BaseViewModel<TeamsMviAction, TeamsMviState>() {

    override val initialState = TeamsMviState(screenState = TeamsMviState.ScreenState.Loading)

    private val reducer: ReducerFun<TeamsMviState, TeamsMviAction> = { state, action ->
        when (action) {
            is TeamsMviAction.ShowLoading -> state.copy(
                screenState = TeamsMviState.ScreenState.Loading
            )
            is TeamsMviAction.HeroesLoaded -> {
                heroesStorage.heroes = action.list
                state.copy(
                    screenState = TeamsMviState.ScreenState.ScreenShow()
                )
            }
            is TeamsMviAction.HeroesError -> state.copy(
                screenState = TeamsMviState.ScreenState.Error(errorMessage = R.string.choose_hero_error)
            )
            is TeamsMviAction.ClickHeroButton -> {
                if (state.screenState is TeamsMviState.ScreenState.ScreenShow) {
                    state.copy(
                        screenState = state.screenState.copy(
                            lastChoice = TeamsMviState.LastChoice(
                                action.team,
                                action.pos
                            ),
                            bottomSheetOpen = true
                        )
                    )
                } else state
            }
            is TeamsMviAction.HeroChosen -> {
                val screenState = (state.screenState as TeamsMviState.ScreenState.ScreenShow)
                val lastChoice = screenState.lastChoice
                val pos = lastChoice.position
                val team = lastChoice.team
                val heroChosen = action.hero
                if (team.isFriend()) {
                    val map = screenState.friendTeam.mapHeroes.toMutableMap()
                    map[pos] = heroChosen
                    state.copy(
                        screenState = screenState.copy(
                            friendTeam = Team.FriendTeam(map),
                            bottomSheetOpen = false
                        )
                    )
                } else {
                    val map = screenState.enemyTeam.mapHeroes.toMutableMap()
                    map[pos] = heroChosen
                    state.copy(
                        screenState = screenState.copy(
                            enemyTeam = Team.EnemyTeam(map),
                            bottomSheetOpen = false
                        )
                    )
                }
            }
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val loadHeroes =
            actions.filter { it is TeamsMviAction.ShowLoading }
                .switchMap {
                    heroInteractor.heroesStats()
                        .subscribeOn(Schedulers.io())
                        .toObservable()
                        .map<TeamsMviAction> { TeamsMviAction.HeroesLoaded(it) }
                        .onErrorReturn { TeamsMviAction.HeroesError(it) }
                }

        val heroChosen =
            actions.filter { it is TeamsMviAction.HeroChosen }
                .map {
                    val action = it as TeamsMviAction.HeroChosen
                    TeamsMviAction.HeroChosen(hero = action.hero)
                }

        val currentChoice =
            actions.filter { it is TeamsMviAction.ClickHeroButton }

        val allactions = Observable.merge(loadHeroes, heroChosen, currentChoice)

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

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val heroInteractor: HeroInteractor,
        private val heroesStorage: HeroesStorage
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TeamsViewModel(heroInteractor, heroesStorage) as T
        }
    }
}