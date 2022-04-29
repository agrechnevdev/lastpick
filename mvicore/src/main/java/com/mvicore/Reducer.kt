package com.mvicore

interface Reducer<State, Action> {
    fun reduce(state: State, action: Action): State
}

typealias ReducerFun<S, A> = (state: S, action: A) -> S