package com.mvicore

import io.reactivex.Observable

interface MviView<Action, State> {
    val actions: Observable<Action>
    fun render(state: State)
}