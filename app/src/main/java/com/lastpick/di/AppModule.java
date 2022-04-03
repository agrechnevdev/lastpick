package com.lastpick.di;

import com.lastpick.domain.HeroesStorage;
import com.lastpick.domain.HeroesStorageImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Provides
    HeroesStorage provideHeroesStorage() {
        return new HeroesStorageImpl();
    }
}