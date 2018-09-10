package com.sunxy.realplugin.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.sunxy.realplugin.compat.ActivityThreadCompat;
import com.sunxy.realplugin.hook.HookFactory;
import com.sunxy.realplugin.pm.IPluginManager;
import com.sunxy.realplugin.utils.ReflectUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/17 0017.
 */
public class PluginManager implements ServiceConnection {

    private IPluginManager mPluginManager;
    private Context mHostContext;
    private String processName;

    private final static PluginManager ourInstance = new PluginManager();

    public static PluginManager getInstance(){
        return ourInstance;
    }

    private PluginManager() {
    }

    public void init(Context context){
        mHostContext = context;
        PluginManager.getInstance().connectToService();
        HookFactory.getInstance().installHook(context, context.getClassLoader());
        Log.v("sunxiaoyu", "pluginManager init end");
    }

    public synchronized void connectToService(){
        if (mPluginManager == null){
            Intent intent = new Intent(mHostContext, PluginManagerService.class);
            mHostContext.startService(intent);
            mHostContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
        }
        Log.v("sunxiaoyu", "connectToService");
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mPluginManager = IPluginManager.Stub.asInterface(service);
        Log.v("sunxiaoyu", "onServiceConnected : " + mPluginManager);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mPluginManager = null;
    }

    public boolean isConnected() {
        return mHostContext != null && mPluginManager != null;
    }

    public int installPackage(String filePath, int flags){
            try {
                if (mPluginManager != null){
                    return mPluginManager.installPackage(filePath, flags);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return -1;
    }

    public ActivityInfo resolveActivityInfo(Intent intent, int flags) throws RemoteException {
        if (mPluginManager != null){
            return mPluginManager.getActivityInfo(intent.getComponent(), flags);
        }
        return null;
    }

    public ActivityInfo selectProxyActivity(Intent intent) {
        if (mPluginManager!=null) {
            try {
                return mPluginManager.selectStubActivityInfoByIntent(intent);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public ApplicationInfo getApplicationInfo(ComponentName componentName, int flag) {
        if (mPluginManager != null) {
            try {
                return mPluginManager.getApplicationInfo(componentName.getPackageName(), flag);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return  null;
    }

    public void onActivityCreated(ActivityInfo stubInfo, ActivityInfo targetInfo) throws RemoteException {
        try {
            if (mPluginManager != null) {
                mPluginManager.onActivityCreated(stubInfo, targetInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPluginService(){
        if (TextUtils.isEmpty(processName)){
            processName = getProcessName();
        }
        if (processName != null){
            return processName.contains(":Plugin");
        }
        return false;
    }

    /**
     * 获取进程名。
     */
    private String getProcessName(){
        try {
            Object currentActivityThread = ActivityThreadCompat.currentActivityThread();
            Method getProcessName = ReflectUtils.findMethod(currentActivityThread, "getProcessName");
            return (String) getProcessName.invoke(currentActivityThread);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
