package com.lastpick.di;

import android.app.Application;

import com.lastpick.presentation.choosehero.ChooseHeroActivity;

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
        Builder application(Application application);

        Builder netModule(NetModule appModule);

    }
}