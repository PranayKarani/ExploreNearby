package com.yuluassignment;

import android.app.Application;

public class MyApp extends Application {

    private static MyApp myApp;

    public static synchronized MyApp get() {

        return myApp;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
    }

}
