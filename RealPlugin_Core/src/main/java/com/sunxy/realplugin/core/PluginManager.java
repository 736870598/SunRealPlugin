package com.sunxy.realplugin.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.sunxy.realplugin.pm.IPluginManager;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/17 0017.
 */
public class PluginManager implements ServiceConnection {

    private IPluginManager mPluginManager;
    private Context mHostContext;

    private final static PluginManager ourInstance = new PluginManager();

    public static PluginManager getInstance(){
        return ourInstance;
    }

    private PluginManager() {
    }

    public void init(Context context){
        mHostContext = context;
    }

    public void connectToService(){
        if (mPluginManager == null){
            Intent intent = new Intent(mHostContext, PluginManagerService.class);
            mHostContext.startService(intent);
            mHostContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mPluginManager = IPluginManager.Stub.asInterface(service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

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
}
