package com.lastpick.presentation.model

import com.lastpick.domain.model.Hero

/**
 * @author Grechnev Anton 31.03.2022
 */
sealed class Team(val mapHeroes: Map<Position, Hero?>) {

    class FriendTeam(map: Map<Position, Hero?> = initMap()) : Team(map)
    class EnemyTeam(map: Map<Position, Hero?> = initMap()) : Team(map)

    private companion object {
        fun initMap(): Map<Position, Hero?> {
            val map = mutableMapOf<Position, Hero?>()
            Position.values().forEach { map[it] = null }
            return map
        }
    }
}

fun Team.isFriend() = this is Team.FriendTeam