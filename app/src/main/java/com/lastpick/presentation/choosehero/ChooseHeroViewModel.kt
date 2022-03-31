package com.lastpick.presentation.choosehero

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lastpick.domain.HeroInteractor
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.reactivex.schedulers.Schedulers

class ChooseHeroViewModel(
    initialState: ChooseHeroState?,
    private val heroInteractor: HeroInteractor
) : BaseViewModel<ChooseHeroAction, ChooseHeroState>() {

    override val initialState = initialState ?: ChooseHeroState(isIdle = true)

    private val reducer: Reducer<ChooseHeroState, ChooseHeroChange> = { state, change ->
        when (change) {
            is ChooseHeroChange.Loading -> state.copy(
                isLoading = true,
                isIdle = false,
                listHeroes = emptyList(),
                errorMessage = null
            )
            is ChooseHeroChange.HeroesLoaded -> state.copy(
                isLoading = false,
                listHeroes = change.list
            )
            is ChooseHeroChange.Error -> state.copy(
                isLoading = false,
                errorMessage = "Произошла ошибка, нажмите, чтобы перезагрузить"
            )
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

//        val allChanges = Observable.merge(loadHeroes)

        disposables.addAll(loadHeroes
            .scan(initialState, reducer)
            .filter { !it.isIdle }
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