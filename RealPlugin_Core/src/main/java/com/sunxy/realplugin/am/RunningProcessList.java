package com.sunxy.realplugin.am;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.text.TextUtils;
import android.widget.TextView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * -- 正在运行的进程列表。
 * Created by sunxy on 2018/8/22 0022.
 */
public class RunningProcessList {

    private static final Collator sCollator = Collator.getInstance();
    private Map<Integer, ProcessItem> item = new HashMap<>();
    private Context mHostContext;

    private static Comparator sComponentInfoComparator = new Comparator<ComponentInfo>() {
        @Override
        public int compare(ComponentInfo lhs, ComponentInfo rhs) {
            return sCollator.compare(lhs, rhs);
        }
    };

    private static Comparator sProviderInfoComparator = new Comparator<ProviderInfo>() {
        @Override
        public int compare(ProviderInfo lhs, ProviderInfo rhs) {
            return sCollator.compare(lhs.authority, rhs.authority);
        }
    };

    public String getStubProcessByTarget(ComponentInfo targetInfo){
        for (ProcessItem processItem : item.values()) {
            if (processItem.pkgs.contains(targetInfo.packageName) &&
                    TextUtils.equals(targetInfo.processName, processItem.targetProcessName)){
                return processItem.stubProcessName;
            }else{

            }
        }
        return null;
    }

    public void setTargetProcessName(ComponentInfo stubInfo, ComponentInfo targetInfo){
        for (ProcessItem item : item.values()) {
            if (TextUtils.equals(item.stubProcessName, stubInfo.processName)){
                if (item.pkgs.contains(targetInfo.packageName)){
                    item.pkgs.add(targetInfo.packageName);
                }
                item.targetProcessName = targetInfo.processName;
            }
        }
    }

    public boolean isStubInfoUsed(ActivityInfo stubInfo, ActivityInfo targetInfo, String stubProcessName){
        for (Integer pid : item.keySet()) {
            ProcessItem item = this.item.get(pid);
            if (TextUtils.equals(item.stubProcessName, stubProcessName)){
                Set<ActivityInfo> infos = item.activityInfoMap.get(stubInfo.name);
                if (infos != null && infos.size() > 0){
                    for (ActivityInfo info : infos) {
                        if (TextUtils.equals(info.name, targetInfo.name)
                                && TextUtils.equals(info.packageName, targetInfo.packageName)){
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public boolean isProcessRunning(String stubProcessName){
        for (ProcessItem processItem : item.values()) {
            if (TextUtils.equals(stubProcessName, processItem.stubProcessName)){
                return true;
            }
        }
        return false;
    }

    public boolean isPkgEmpty(String stubProcessName){
        for (ProcessItem processItem : item.values()) {
            if (TextUtils.equals(stubProcessName, processItem.stubProcessName)){
                return processItem.pkgs.size() <= 0;
            }
        }
        return true;
    }


    /**
     * 正在运行的进程item
     */
    private class ProcessItem{

        private String stubProcessName;
        private String targetProcessName;
        private int pid;
        private int uid;
        private long startTime;

        private List<String> pkgs = new ArrayList<>(1);

        //正在运行的插件activityInfo
        //key=ActivityInfo.name, value=插件的ActivityInfo
        private Map<String, ActivityInfo> targetActivityInfos = new HashMap<>();

        //正在运行的插件ProviderInfo
        //key=ProviderInfo.authority, value=插件的ProviderInfo
        private Map<String, ProviderInfo> targetProviderInfos = new HashMap<>();

        //正在运行的插件ServiceInfo
        //key=ServiceInfo.name, value=插件的ServiceInfo
        private Map<String, ServiceInfo> targetServiceInfos = new HashMap<>();

        //正在运行的插件activityInfo与代理ActivityInfo的映射
        //key=代理ActivityInfo.name, value=插件的ActivityInfo.name,
        private Map<String, Set<ActivityInfo>> activityInfoMap = new HashMap<>();

        //正在运行的插件ProviderInfo与代理ProviderInfo的映射
        //key=代理ProviderInfo.authority, value=插件的ProviderInfo.authority,
        private Map<String, Set<ProviderInfo>> providerInfosMap = new HashMap<String, Set<ProviderInfo>>(4);

        //正在运行的插件ServiceInfo与代理ServiceInfo的映射
        //key=代理ServiceInfo.name, value=插件的ServiceInfo.name,
        private Map<String, Set<ServiceInfo>> serviceInfosMap = new HashMap<String, Set<ServiceInfo>>(4);


        private void updatePkgs(){
            ArrayList<String> newList = new ArrayList<>();
            for (ActivityInfo info : targetActivityInfos.values()) {
                newList.add(info.packageName);
            }
            for (ServiceInfo info : targetServiceInfos.values()) {
                newList.add(info.packageName);
            }
            for (ProviderInfo info : targetProviderInfos.values()) {
                newList.add(info.packageName);
            }
            pkgs.clear();
            pkgs.addAll(newList);
        }

        void addActivityInfo(String stubActivityName, ActivityInfo info){
            if (!targetActivityInfos.containsKey(info.name)){
                targetActivityInfos.put(info.name, info);
            }

            if (!pkgs.contains(info.packageName)){
                pkgs.add(info.packageName);
            }

            Set<ActivityInfo> activityInfoSet = activityInfoMap.get(stubActivityName);
            if (activityInfoSet == null){
                activityInfoSet = new TreeSet<ActivityInfo>(sComponentInfoComparator);
                activityInfoMap.put(stubActivityName, activityInfoSet);
            }
            activityInfoSet.add(info);
        }

        void removeActivityInfo(String stubActivityName, ActivityInfo info){
            targetActivityInfos.remove(info.name);
            if (stubActivityName != null){
                for (Set<ActivityInfo> set : activityInfoMap.values()) {
                    set.remove(info);
                }
            }else{
                Set<ActivityInfo> activityInfoSet = activityInfoMap.get(stubActivityName);
                if (activityInfoSet != null){
                    activityInfoSet.remove(info);
                }
            }
            updatePkgs();
        }
    }


}
