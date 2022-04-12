package com.lastpick.di;

import com.lastpick.domain.HeroesStorage;
import com.lastpick.domain.HeroesStorageImpl;

import dagger.Module;
import dagger.Provides;

@Module
public interface AppModule {

    @Provides
    static HeroesStorage provideHeroesStorage() {
        return new HeroesStorageImpl();
    }
}