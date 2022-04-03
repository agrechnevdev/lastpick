package com.lastpick.domain

import com.lastpick.domain.model.Hero

/**
 * @author Grechnev Anton 03.04.2022
 */
class HeroesStorageImpl : HeroesStorage {

    override var heroes: List<Hero> = emptyList()

}