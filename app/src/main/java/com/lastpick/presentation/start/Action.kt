package com.lastpick.presentation.start

import com.ww.roxie.BaseAction

sealed class Action : BaseAction {
    object ShowImage : Action()
}