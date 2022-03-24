package com.lastpick.di;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Provides
    @Singleton
    @Named("string")
    String provideString() {
        return "";
    }
}