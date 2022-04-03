package com.lastpick.domain

import com.lastpick.domain.model.Hero

/**
 * @author Grechnev Anton 03.04.2022
 */
interface HeroesStorage {
    var heroes: List<Hero>
}