package com.mvicore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel

abstract class BaseViewModel<A : MviAction, S : MviState> : ViewModel() {
    protected val actions: Channel<A> = Channel(Channel.UNLIMITED)
    protected val stateChannel = Channel<MviState>(Channel.UNLIMITED)

    protected abstract val initialState: S

    protected val state = MutableLiveData<S>()

    private val tag by lazy { javaClass.simpleName }

    /**
     * Returns the current state. It is equal to the last value returned by the store's reducer.
     */
    val observableState: LiveData<S> = MediatorLiveData<S>().apply {
        addSource(state) { data ->
            setValue(data)
        }
    }

    /**
     * Dispatches an action. This is the only way to trigger a state change.
     */
    fun dispatch(action: A) {
//        actions.onNext(action)
    }

    override fun onCleared() {
//        disposables.clear()
    }
}
