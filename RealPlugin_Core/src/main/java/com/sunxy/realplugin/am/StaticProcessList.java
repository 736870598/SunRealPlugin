package com.sunxy.realplugin.am;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.text.TextUtils;
import android.util.Log;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/22 0022.
 */
public class StaticProcessList {

    private static final String CATEGORY_ACTIVITY_PTOXY_SYUB = "com.sunxy.android";

    private Map<String, ProcessItem> items = new HashMap<>(10);
    private List<String> mOtherProcessNames = new ArrayList<>();

    private Context mHostContext;

    private static final Comparator<ComponentInfo> sComponentInfoComparator = new Comparator<ComponentInfo>() {
        @Override
        public int compare(ComponentInfo lhs, ComponentInfo rhs) {
            return Collator.getInstance().compare(lhs.name, rhs.name);
        }
    };

    public void onCreate(Context mHostContext){
        this.mHostContext = mHostContext;
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(CATEGORY_ACTIVITY_PTOXY_SYUB);
            intent.setPackage(mHostContext.getPackageName());

            PackageManager pm = mHostContext.getPackageManager();
            List<ResolveInfo> activities = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA);
            for (ResolveInfo activity : activities) {
                addActivityInfo(activity.activityInfo);
            }

            List<ResolveInfo> services = pm.queryIntentServices(intent, 0);
            for (ResolveInfo service : services) {
                addServiceInfo(service.serviceInfo);
            }

            PackageInfo packageInfo = pm.getPackageInfo(mHostContext.getPackageName(), PackageManager.GET_PROVIDERS);

            if (packageInfo.providers != null &&packageInfo.providers.length > 0){
                for (ProviderInfo providerInfo : packageInfo.providers) {
                    if (providerInfo.name != null){
                        addProviderInfo(providerInfo);
                    }
                }
            }

            mOtherProcessNames.clear();

            PackageInfo packageInfo1 = pm.getPackageInfo(mHostContext.getPackageName(),
                    PackageManager.GET_ACTIVITIES
                            | PackageManager.GET_RECEIVERS
                            | PackageManager.GET_PROVIDERS
                            | PackageManager.GET_SERVICES);

            if (packageInfo1.activities != null){
                for (ActivityInfo info : packageInfo1.activities) {
                    if (!mOtherProcessNames.contains(info.packageName) &&
                            !items.containsKey(info.processName)){
                        mOtherProcessNames.add(info.processName);
                    }
                }
            }

            if (packageInfo1.receivers != null) {
                for (ActivityInfo info : packageInfo1.receivers) {
                    if (!mOtherProcessNames.contains(info.processName) && !items.containsKey(info.processName)) {
                        mOtherProcessNames.add(info.processName);
                    }
                }
            }

            if (packageInfo1.providers != null) {
                for (ProviderInfo info : packageInfo1.providers) {
                    if (!mOtherProcessNames.contains(info.processName) && !items.containsKey(info.processName)) {
                        mOtherProcessNames.add(info.processName);
                    }
                }
            }

            if (packageInfo1.services != null) {
                for (ServiceInfo info : packageInfo1.services) {
                    if (!mOtherProcessNames.contains(info.processName) && !items.containsKey(info.processName)) {
                        mOtherProcessNames.add(info.processName);
                    }
                }
            }

        }catch (Exception e){

        }
    }

    private void addActivityInfo(ActivityInfo info){
        if (TextUtils.isEmpty(info.packageName)){
            info.processName = info.packageName;
        }

        ProcessItem item = items.get(info.processName);
        if (item == null){
            item = new ProcessItem();
            item.name = info.name;
            items.put(info.processName, item);
        }
        item.addActivityInfo(info);
    }

    private void addServiceInfo(ServiceInfo info){
        if (TextUtils.isEmpty(info.processName)){
            info.processName = info.packageName;
        }
        ProcessItem item = items.get(info.processName);
        if (item == null){
            item = new ProcessItem();
            item.name = info.name;
            items.put(info.processName, item);
        }
        item.addServiceInfo(info);
    }

    private void addProviderInfo(ProviderInfo info) {
        if (TextUtils.isEmpty(info.processName)) {
            info.processName = info.packageName;
        }
        ProcessItem item = items.get(info.processName);
        if (item == null) {
            item = new ProcessItem();
            item.name = info.processName;
            items.put(info.processName, item);
        }
        item.addProviderInfo(info);
    }


    public List<ActivityInfo> getActivityInfoForProcessName(String processName){
        ProcessItem item = items.get(processName);
        ArrayList<ActivityInfo> activityInfos = new ArrayList<>(item.activityInfos.values());
        Collections.sort(activityInfos, sComponentInfoComparator);
        return activityInfos;
    }

    public List<String> getProcessNames(){
        return new ArrayList<>(items.keySet());
    }

    /**
     * 我们预注册的进程
     */
    private class ProcessItem{

        private String name;

        //key=ActivityInfo.name,value=ActivityInfo
        private Map<String, ActivityInfo> activityInfos = new HashMap<String, ActivityInfo>(4);
        //key=ServiceInfo.name,value=ServiceInfo
        private Map<String, ServiceInfo> serviceInfos = new HashMap<String, ServiceInfo>(1);
        //key=ProviderInfo.authority,value=ProviderInfo
        private Map<String, ProviderInfo> providerInfos = new HashMap<String, ProviderInfo>(1);

        private void addActivityInfo(ActivityInfo info) {
            if (!activityInfos.containsKey(info.name)){
                activityInfos.put(info.name, info);
            }
        }

        private void addServiceInfo(ServiceInfo info) {
            if (!serviceInfos.containsKey(info.name)) {
                serviceInfos.put(info.name, info);
            }
        }

        private void addProviderInfo(ProviderInfo info) {
            if (!providerInfos.containsKey(info.authority)) {
                providerInfos.put(info.authority, info);
            }
        }
    }
}
