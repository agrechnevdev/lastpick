package com.lastpick;

import android.app.Application;
import android.content.Context;

import com.lastpick.di.AppComponent;
import com.lastpick.di.DaggerAppComponent;
import com.lastpick.di.NetModule;

public class BaseApp extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .application(this)
                .netModule(new NetModule("", getApplicationContext()))
                .build();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }

    public AppComponent getNetComponent() {
        return appComponent;
    }

}
