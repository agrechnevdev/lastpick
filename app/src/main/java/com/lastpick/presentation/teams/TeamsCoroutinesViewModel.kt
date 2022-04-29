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
import kotlinx.coroutines.launch

class TeamsCoroutinesViewModel(
    private val heroInteractor: HeroInteractor,
    private val heroesStorage: HeroesStorage
) : ViewModel() {

    protected val intentChannel: Channel<TeamsMviIntent> = Channel(Channel.UNLIMITED)
    val stateChannel = Channel<TeamsMviState>(Channel.UNLIMITED)

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
            bindActions()
        }


    }

    fun doIntent(intent: TeamsMviIntent) {
        viewModelScope.launch {
            intentChannel.send(intent)
        }
    }

    private suspend fun bindActions() {
        intentChannel.consumeEach { intent ->
            val action = when (intent) {
                is TeamsMviIntent.ReloadHeroes -> {
                    TeamsMviAction.ShowLoading
                    try {
                        TeamsMviAction.HeroesLoaded(heroInteractor.heroesStatsCoroutine())
                    } catch (t: Throwable) {
                        TeamsMviAction.HeroesError(t)
                    }
                }

                is TeamsMviIntent.HeroChosen -> {
                    TeamsMviAction.HeroChosen(hero = intent.hero)
                }
                is TeamsMviIntent.ClickHeroButton -> {
                    TeamsMviAction.ClickHeroButton(intent.team, intent.pos)
                }
            }

            val newState = reducer.invoke(stateChannel.receive(), action)
            stateChannel.send(newState)
        }
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
