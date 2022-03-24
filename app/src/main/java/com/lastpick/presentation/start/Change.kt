package com.lastpick.presentation.start

sealed class Change {
    object Start : Change()
    object ShowNextScreen : Change()
}
