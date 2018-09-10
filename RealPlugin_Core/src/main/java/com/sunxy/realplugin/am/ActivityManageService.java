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

        //先从正在运行的进程中查找看是否有符合条件的进程，如果有则直接使用之
        String stubProcessName1 = mRunningProcessList.getStubProcessByTarget(targetInfo);
        if (stubProcessName1 != null) {
            List<ActivityInfo> stubInfos = mStaticProcessList.getActivityInfoForProcessName(stubProcessName1);
            for (ActivityInfo stubInfo : stubInfos) {
                if (stubInfo.launchMode == targetInfo.launchMode) {
                    //ActivityInfo.LAUNCH_MULTIPLE   ===  standard
                    if (stubInfo.launchMode == ActivityInfo.LAUNCH_MULTIPLE) {
                        mRunningProcessList.setTargetProcessName(stubInfo, targetInfo);
                        return stubInfo;
                    } else if (!mRunningProcessList.isStubInfoUsed(stubInfo, targetInfo, stubProcessName1)) {
                        mRunningProcessList.setTargetProcessName(stubInfo, targetInfo);
                        return stubInfo;
                    }
                }
            }
        }

        List<String> stubProcessNames = mStaticProcessList.getProcessNames();
        for (String stubProcessName : stubProcessNames) {
            List<ActivityInfo> stubInfos = mStaticProcessList.getActivityInfoForProcessName(stubProcessName);
            if (mRunningProcessList.isProcessRunning(stubProcessName)) {//该预定义的进程正在运行。
                if (mRunningProcessList.isPkgEmpty(stubProcessName)) {//空进程，没有运行任何插件包。
                    for (ActivityInfo stubInfo : stubInfos) {
                        if (stubInfo.launchMode == targetInfo.launchMode) {
                            if (stubInfo.launchMode == ActivityInfo.LAUNCH_MULTIPLE) {
                                mRunningProcessList.setTargetProcessName(stubInfo, targetInfo);
                                return stubInfo;
                            } else if (!mRunningProcessList.isStubInfoUsed(stubInfo, targetInfo, stubProcessName1)) {
                                mRunningProcessList.setTargetProcessName(stubInfo, targetInfo);
                                return stubInfo;
                            }
                        }
                    }
                    throw new RuntimeException("没有找到合适的StubInfo");
                }
            } else { //该预定义的进程没有。
                for (ActivityInfo stubInfo : stubInfos) {
                    if (stubInfo.launchMode == targetInfo.launchMode) {
                        if (stubInfo.launchMode == ActivityInfo.LAUNCH_MULTIPLE) {
                            mRunningProcessList.setTargetProcessName(stubInfo, targetInfo);
                            return stubInfo;
                        } else if (!mRunningProcessList.isStubInfoUsed(stubInfo, targetInfo, stubProcessName1)) {
                            mRunningProcessList.setTargetProcessName(stubInfo, targetInfo);
                            return stubInfo;
                        }
                    }
                }
                throw new RuntimeException("没有找到合适的StubInfo");
            }
        }
        throw new RuntimeException("没有可用的进程了");

    }

    public void onActivityCreated(int callingPid, int callingUid, ActivityInfo stubInfo, ActivityInfo targetInfo) {
        mRunningProcessList.addActivityInfo(callingPid, callingUid, stubInfo, targetInfo);
    }
}
