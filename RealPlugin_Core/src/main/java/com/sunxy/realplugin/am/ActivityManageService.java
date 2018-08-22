package com.sunxy.realplugin.am;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.RemoteException;
import android.text.Html;

import com.sunxy.realplugin.pm.IPluginManager;

import java.util.List;

/**
 * -- AMS
 * <p>
 * Created by sunxy on 2018/8/21 0021.
 */
public class ActivityManageService {

    private Context context;
    private IPluginManager pluginManager;
    private RunningProcessList mRunningProcessList = new RunningProcessList();
    private StaticProcessList mStaticProcessList = new StaticProcessList();

    public ActivityManageService(Context context) {
        this.context = context;
    }

    public void onCreate(IPluginManager pluginManager){
        this.pluginManager = pluginManager;
        mStaticProcessList.onCreate(context);
    }

    public ActivityInfo selectStubActivityInfo(int callingPid, int callingUid, ActivityInfo targetInfo) {

        String stubPlugin = mRunningProcessList.getStubProcessByTarget(targetInfo);
        if (stubPlugin != null){
            //进程已经起来了
            List<ActivityInfo> stubInfos = mStaticProcessList.getActivityInfoForProcessName(stubPlugin);
            for (ActivityInfo activityInfo : stubInfos) {
                //启动模式一样。
                if (activityInfo.launchMode == targetInfo.launchMode){
                    if (activityInfo.launchMode == ActivityInfo.LAUNCH_MULTIPLE){
                        mRunningProcessList.setTargetProcessName(activityInfo, targetInfo);
                        return activityInfo;
                    }else if(mRunningProcessList.isStubInfoUsed(activityInfo, targetInfo, stubPlugin)){
                        mRunningProcessList.setTargetProcessName(activityInfo, targetInfo);
                        return activityInfo;
                    }
                }
            }
        }else{
            //进程没有起来
            List<String> stubProcssNames = mStaticProcessList.getProcessNames();
            for (String stubProcssName : stubProcssNames) {
                List<ActivityInfo> stubInfos = mStaticProcessList.getActivityInfoForProcessName(stubProcssName);
                if (!mRunningProcessList.isProcessRunning(stubProcssName)){
                    // 进程没有运行
                    for (ActivityInfo stubInfo : stubInfos) {
                        if (stubInfo.launchMode == ActivityInfo.LAUNCH_MULTIPLE){
                            mRunningProcessList.setTargetProcessName(stubInfo, targetInfo);
                            return stubInfo;
                        }else if(!mRunningProcessList.isStubInfoUsed(stubInfo, targetInfo, stubPlugin)){
                            mRunningProcessList.setTargetProcessName(stubInfo, targetInfo);
                            return stubInfo;
                        }
                    }
                }else{
                    // 进程运行
                    if (mRunningProcessList.isProcessRunning(stubProcssName)){
                        if (mRunningProcessList.isPkgEmpty(stubProcssName)){
                            for (ActivityInfo stubInfo : stubInfos) {
                                if (stubInfo.launchMode == targetInfo.launchMode){
                                    if (stubInfo.launchMode == ActivityInfo.LAUNCH_MULTIPLE){
                                        mRunningProcessList.setTargetProcessName(stubInfo, targetInfo);
                                        return stubInfo;
                                    }else if(!mRunningProcessList.isStubInfoUsed(stubInfo, targetInfo, stubProcssName)){
                                        mRunningProcessList.setTargetProcessName(stubInfo,targetInfo);
                                        return stubInfo;
                                    }
                                }
                            }
                        }
                    }
                }
            }


        }
        return null;
    }

    public void onActivityCreated(int callingPid, int callingUid, ActivityInfo stubInfo, ActivityInfo targetInfo) {
    }
}
