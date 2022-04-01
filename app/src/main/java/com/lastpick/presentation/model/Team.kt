package com.lastpick.presentation.model

import com.lastpick.domain.model.Hero

/**
 * @author Grechnev Anton 31.03.2022
 */
sealed class Team {
    val mapHeroes = mutableMapOf<Position, Hero?>()

    init {
        Position.values().forEach { mapHeroes[it] = null }
    }

    object FriendTeam : Team()
    object EnemyTeam : Team()
}

fun Team.isFriend() = this is Team.FriendTeam