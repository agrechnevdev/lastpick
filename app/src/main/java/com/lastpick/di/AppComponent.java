package com.lastpick.di;

import android.content.Context;

import com.lastpick.presentation.choosehero.ChooseHeroActivity;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetModule.class, HeroModule.class})
public interface AppComponent {

    void inject(ChooseHeroActivity activity);

    @Component.Builder
    interface Builder {
        AppComponent build();

        @BindsInstance
        Builder context(Context context);

        @BindsInstance
        Builder openDotaApiUrl(@Named("openDotaApiUrl") String openDotaApiUrl);

    }
}