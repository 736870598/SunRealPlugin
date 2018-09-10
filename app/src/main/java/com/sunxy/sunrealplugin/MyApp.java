package com.sunxy.sunrealplugin;

import android.app.Application;
import android.util.Log;

import com.sunxy.realplugin.compat.ActivityThreadCompat;
import com.sunxy.realplugin.core.PluginManager;
import com.sunxy.realplugin.hook.HookFactory;
import com.sunxy.realplugin.utils.ReflectMethodUtils;
import com.sunxy.realplugin.utils.ReflectUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/17 0017.
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("sunxiaoyu", "Application onCreate " );
        PluginManager.getInstance().init(this);
    }
}
