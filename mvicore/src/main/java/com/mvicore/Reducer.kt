package com.mvicore

interface Reducer<State, Action> {
    fun reduce(state: State, action: Action): State
}