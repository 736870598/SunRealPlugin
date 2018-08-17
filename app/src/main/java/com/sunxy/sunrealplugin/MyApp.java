package com.sunxy.sunrealplugin;

import android.app.Application;

import com.sunxy.realplugin.core.PluginManager;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/17 0017.
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PluginManager.getInstance().init(this);
        PluginManager.getInstance().connectToService();
    }
}
