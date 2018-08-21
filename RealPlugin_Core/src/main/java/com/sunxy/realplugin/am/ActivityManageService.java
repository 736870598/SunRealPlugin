package com.sunxy.realplugin.am;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.RemoteException;

import com.sunxy.realplugin.pm.IPluginManager;

/**
 * -- AMS
 * <p>
 * Created by sunxy on 2018/8/21 0021.
 */
public class ActivityManageService {

    private Context context;
    private IPluginManager pluginManager;

    public ActivityManageService(Context context) {
        this.context = context;
    }

    public void onCreate(IPluginManager pluginManager){
        this.pluginManager = pluginManager;
    }

    public ActivityInfo selectStubActivityInfo(int callingPid, int callingUid, ActivityInfo targetInfo)
        throws RemoteException{

        return null;
    }
}
