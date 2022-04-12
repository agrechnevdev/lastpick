package com.lastpick.di;

import com.lastpick.data.HeroRepository;
import com.lastpick.data.HeroRepositoryImpl;
import com.lastpick.data.OpenDotaApi;
import com.lastpick.domain.HeroInteractor;
import com.lastpick.domain.HeroInteractorImpl;

import dagger.Module;
import dagger.Provides;

@Module
public interface HeroModule {
    @Provides
    static HeroRepository provideHeroRepository(OpenDotaApi openDotaApi) {
        return new HeroRepositoryImpl(openDotaApi);
    }

    @Provides
    static  HeroInteractor provideHeroInteractor(HeroRepository heroRepository) {
        return new HeroInteractorImpl(heroRepository);
    }
}
