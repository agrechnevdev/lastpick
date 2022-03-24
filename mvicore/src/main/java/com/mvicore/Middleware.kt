package com.mvicore

import io.reactivex.Observable

interface Middleware<Action, State> {
    fun bind(actions: Observable<Action>, state: Observable<State>): Observable<Action>
}