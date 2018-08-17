package com.sunxy.realplugin.core;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.sunxy.realplugin.pm.PackageManagerService;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/17 0017.
 */
public class PluginManagerService extends Service {

    private static PackageManagerService packageManagerService;

    public static PackageManagerService getPackageManagerService(Context context) {
         if (packageManagerService == null){
             synchronized (PluginManagerService.class){
                 if (packageManagerService == null){
                     packageManagerService = new PackageManagerService(context);
                     packageManagerService.main();
                 }
             }
         }
         return packageManagerService;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return getPackageManagerService(this);
    }
}
