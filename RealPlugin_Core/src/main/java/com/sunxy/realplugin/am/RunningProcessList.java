package com.sunxy.realplugin.am;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.RemoteException;
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
class RunningProcessList {

    private static final Collator sCollator = Collator.getInstance();
    private static final String TAG = RunningProcessList.class.getSimpleName();
    private static Comparator sComponentInfoComparator = new Comparator<ComponentInfo>() {
        @Override
        public int compare(ComponentInfo lhs, ComponentInfo rhs) {
            return sCollator.compare(lhs.name, rhs.name);
        }
    };

    private static Comparator sProviderInfoComparator = new Comparator<ProviderInfo>() {
        @Override
        public int compare(ProviderInfo lhs, ProviderInfo rhs) {
            return sCollator.compare(lhs.authority, rhs.authority);
        }
    };

    public String getStubProcessByTarget(ComponentInfo targetInfo) {
        android.util.Log.i(TAG, "getStubProcessByTarget: ");
        for (ProcessItem processItem : items.values()) {
            if (processItem.pkgs.contains(targetInfo.packageName) && TextUtils.equals(processItem.targetProcessName, targetInfo.processName)) {
                return processItem.stubProcessName;
            } else {
                try {
                    boolean signed = false;
                    for (String pkg : processItem.pkgs) {
                    }
                    if (signed && TextUtils.equals(processItem.targetProcessName, targetInfo.processName)) {
                        return processItem.stubProcessName;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }



    private Context mHostContext;

    public void setContext(Context context) {
        this.mHostContext = context;
    }


    /**
     * 正在运行的进程item
     * <p/>
     * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/3/10.
     */
    private class ProcessItem {

        private String stubProcessName;
        private String targetProcessName;
        private int pid;
        private int uid;
        private long startTime;

        private List<String> pkgs = new ArrayList<String>(1);

        //正在运行的插件ActivityInfo
        //key=ActivityInfo.name, value=插件的ActivityInfo,
        private Map<String, ActivityInfo> targetActivityInfos = new HashMap<String, ActivityInfo>(4);


        //正在运行的插件ProviderInfo
        //key=ProviderInfo.authority, value=插件的ProviderInfo
        private Map<String, ProviderInfo> targetProviderInfos = new HashMap<String, ProviderInfo>(1);

        //正在运行的插件ServiceInfo
        //key=ServiceInfo.name, value=插件的ServiceInfo
        private Map<String, ServiceInfo> targetServiceInfos = new HashMap<String, ServiceInfo>(1);


        //正在运行的插件ActivityInfo与代理ActivityInfo的映射
        //key=代理ActivityInfo.name, value=插件的ActivityInfo.name,
        private Map<String, Set<ActivityInfo>> activityInfosMap = new HashMap<String, Set<ActivityInfo>>(4);


        //正在运行的插件ProviderInfo与代理ProviderInfo的映射
        //key=代理ProviderInfo.authority, value=插件的ProviderInfo.authority,
        private Map<String, Set<ProviderInfo>> providerInfosMap = new HashMap<String, Set<ProviderInfo>>(4);

        //正在运行的插件ServiceInfo与代理ServiceInfo的映射
        //key=代理ServiceInfo.name, value=插件的ServiceInfo.name,
        private Map<String, Set<ServiceInfo>> serviceInfosMap = new HashMap<String, Set<ServiceInfo>>(4);


        private void updatePkgs() {
            android.util.Log.i(TAG, "updatePkgs: ");
            ArrayList<String> newList = new ArrayList<String>();
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


        private void addActivityInfo(String stubActivityName, ActivityInfo info) {
            android.util.Log.i(TAG, "addActivityInfo: ");
            if (!targetActivityInfos.containsKey(info.name)) {
                targetActivityInfos.put(info.name, info);
            }

            //pkgs
            if (!pkgs.contains(info.packageName)) {
                pkgs.add(info.packageName);
            }

            //stub map to activity info
            Set<ActivityInfo> list = activityInfosMap.get(stubActivityName);
            if (list == null) {
                list = new TreeSet<ActivityInfo>(sComponentInfoComparator);
                list.add(info);
                activityInfosMap.put(stubActivityName, list);
            } else {
                list.add(info);
            }
        }

        void removeActivityInfo(String stubActivityName, ActivityInfo targetInfo) {
            android.util.Log.i(TAG, "removeActivityInfo: ");
            targetActivityInfos.remove(targetInfo.name);
            //remove form map
            if (stubActivityName == null) {
                for (Set<ActivityInfo> set : activityInfosMap.values()) {
                    set.remove(targetInfo);
                }
            } else {
                Set<ActivityInfo> list = activityInfosMap.get(stubActivityName);
                if (list != null) {
                    list.remove(targetInfo);
                }
            }
            updatePkgs();
        }







    }

    //key=pid, value=ProcessItem;
    private Map<Integer, ProcessItem> items = new HashMap<Integer, ProcessItem>(5);

    ProcessItem removeByPid(int pid) {
        return items.remove(pid);
    }




    void addActivityInfo(int pid, int uid, ActivityInfo stubInfo, ActivityInfo targetInfo) {
        android.util.Log.i(TAG, "addActivityInfo: ");
        ProcessItem item = items.get(pid);
        if (TextUtils.isEmpty(targetInfo.processName)) {
            targetInfo.processName = targetInfo.packageName;
        }
        if (item == null) {
            item = new ProcessItem();
            item.pid = pid;
            item.uid = uid;
            items.put(pid, item);
        }
        item.stubProcessName = stubInfo.processName;
        if (!item.pkgs.contains(targetInfo.packageName)) {
            item.pkgs.add(targetInfo.packageName);
        }
        item.targetProcessName = targetInfo.processName;
        item.addActivityInfo(stubInfo.name, targetInfo);
    }

    void removeActivityInfo(int pid, int uid, ActivityInfo stubInfo, ActivityInfo targetInfo) {
        android.util.Log.i(TAG, "removeActivityInfo: ");
        ProcessItem item = items.get(pid);
        if (TextUtils.isEmpty(targetInfo.processName)) {
            targetInfo.processName = targetInfo.packageName;
        }
        if (item != null) {
            item.removeActivityInfo(stubInfo.name, targetInfo);
        }
    }



    void addItem(int pid, int uid) {
        android.util.Log.i(TAG, "addItem: ");
        ProcessItem item = items.get(pid);
        if (item == null) {
            item = new ProcessItem();
            item.pid = pid;
            item.uid = uid;
            item.startTime = System.currentTimeMillis();
            items.put(pid, item);
        } else {
            item.pid = pid;
            item.uid = uid;
            item.startTime = System.currentTimeMillis();
        }
    }

    boolean isProcessRunning(String stubProcessName) {
        android.util.Log.i(TAG, "isProcessRunning: ");
        for (ProcessItem processItem : items.values()) {
            if (TextUtils.equals(stubProcessName, processItem.stubProcessName)) {
                return true;
            }
        }
        return false;
    }


    boolean isPkgCanRunInProcess(String packageName, String stubProcessName, String targetProcessName) throws RemoteException {
        android.util.Log.i(TAG, "isPkgCanRunInProcess: ");
        for (ProcessItem item : items.values()) {
            if (TextUtils.equals(stubProcessName, item.stubProcessName)) {

                if (!TextUtils.isEmpty(item.targetProcessName) && !TextUtils.equals(item.targetProcessName, targetProcessName)) {
                    continue;
                }

                if (item.pkgs.contains(packageName)) {
                    return true;
                }

                boolean signed = false;
                if (signed) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean isPkgEmpty(String stubProcessName) {
        android.util.Log.i(TAG, "isPkgEmpty: ");
        for (ProcessItem item : items.values()) {
            if (TextUtils.equals(stubProcessName, item.stubProcessName)) {
                return item.pkgs.size() <= 0;
            }
        }
        return true;
    }


    boolean isStubInfoUsed(ProviderInfo stubInfo) {
        android.util.Log.i(TAG, "isStubInfoUsed: ");
        //TODO
        return false;
    }

    boolean isStubInfoUsed(ServiceInfo stubInfo) {
        android.util.Log.i(TAG, "isStubInfoUsed: ");
        //TODO
        return false;
    }

    boolean isStubInfoUsed(ActivityInfo stubInfo, ActivityInfo targetInfo, String stubProcessName) {
        android.util.Log.i(TAG, "isStubInfoUsed: ");
        for (Integer pid : items.keySet()) {
            ProcessItem item = items.get(pid);
            if (TextUtils.equals(item.stubProcessName, stubProcessName)) {
                Set<ActivityInfo> infos = item.activityInfosMap.get(stubInfo.name);
                if (infos != null && infos.size() > 0) {
                    for (ActivityInfo info : infos) {
                        if (TextUtils.equals(info.name, targetInfo.name) && TextUtils.equals(info.packageName, targetInfo.packageName)) {
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

    List<String> getPackageNameByPid(int pid) {
        android.util.Log.i(TAG, "getPackageNameByPid: ");
        ProcessItem item = items.get(pid);
        return item != null ? item.pkgs : new ArrayList<String>();
    }

    String getTargetProcessNameByPid(int pid) {
        android.util.Log.i(TAG, "getTargetProcessNameByPid: ");
        ProcessItem item = items.get(pid);
        return item != null ? item.targetProcessName : null;
    }

    public String getStubProcessNameByPid(int pid) {
        android.util.Log.i(TAG, "getStubProcessNameByPid: ");
        ProcessItem item = items.get(pid);
        return item != null ? item.stubProcessName : null;
    }

    void setTargetProcessName(ComponentInfo stubInfo, ComponentInfo targetInfo) {
        android.util.Log.i(TAG, "setTargetProcessName: ");
        for (ProcessItem item : items.values()) {
            if (TextUtils.equals(item.stubProcessName, stubInfo.processName)) {
                if (!item.pkgs.contains(targetInfo.packageName)) {
                    item.pkgs.add(targetInfo.packageName);
                }
                item.targetProcessName = targetInfo.processName;
            }
        }
    }


    void setProcessName(int pid, String stubProcessName, String targetProcessName, String targetPkg) {
        android.util.Log.i(TAG, "setProcessName: ");
        ProcessItem item = items.get(pid);
        if (item != null) {
            if (!item.pkgs.contains(targetPkg)) {
                item.pkgs.add(targetPkg);
            }
            item.targetProcessName = targetProcessName;
            item.stubProcessName = stubProcessName;
        }
    }


    void clear() {
        android.util.Log.i(TAG, "clear: ");
        items.clear();
    }
}
