package com.lastpick.presentation.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lastpick.R
import com.lastpick.domain.HeroInteractor
import com.lastpick.domain.HeroesStorage
import com.lastpick.presentation.model.Team
import com.lastpick.presentation.model.isFriend
import com.mvicore.ReducerFun
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TeamsCoroutinesViewModel(
    private val heroInteractor: HeroInteractor,
    private val heroesStorage: HeroesStorage
) : ViewModel() {

    val intentChannel: Channel<TeamsMviIntent> = Channel(Channel.UNLIMITED)
    val actionChannel: Channel<TeamsMviAction> = Channel(Channel.UNLIMITED)

    //    val stateChannel = Channel<TeamsMviState>(Channel.UNLIMITED)
    val stateFlow =
        MutableStateFlow(TeamsMviState(screenState = TeamsMviState.ScreenState.Loading))

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
        viewModelScope.launch {
            bindIntent()
        }
        viewModelScope.launch {
            bindAction()
        }
    }

    fun doIntent(intent: TeamsMviIntent) {
        viewModelScope.launch {
            intentChannel.send(intent)
        }
    }

    private suspend fun bindIntent() {
        intentChannel.consumeEach { intent ->
            when (intent) {
                is TeamsMviIntent.ReloadHeroes -> {
                    actionChannel.send(TeamsMviAction.ShowLoading)
                    try {
                        actionChannel.send(TeamsMviAction.HeroesLoaded(heroInteractor.heroesStatsCoroutine()))
                    } catch (t: Throwable) {
                        actionChannel.send(TeamsMviAction.HeroesError(t))
                    }
                }

                is TeamsMviIntent.HeroChosen -> {
                    actionChannel.send(TeamsMviAction.HeroChosen(hero = intent.hero))
                }
                is TeamsMviIntent.ClickHeroButton -> {
                    actionChannel.send(TeamsMviAction.ClickHeroButton(intent.team, intent.pos))
                }
            }
        }
    }

    private suspend fun bindAction() {
        actionChannel.consumeEach { action ->
            val newState = reducer.invoke(stateFlow.value, action)
            stateFlow.emit(newState)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val heroInteractor: HeroInteractor,
        private val heroesStorage: HeroesStorage
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TeamsCoroutinesViewModel(heroInteractor, heroesStorage) as T
        }
    }
}
