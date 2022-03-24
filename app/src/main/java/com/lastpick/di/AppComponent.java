package com.lastpick.di;

import android.app.Application;

import com.lastpick.presentation.start.StartActivity;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface AppComponent {

    void inject(StartActivity activity);

    @Component.Builder
    interface Builder {
        AppComponent build();
        @BindsInstance
        Builder application(Application application);

        Builder netModule(NetModule appModule);

    }
}