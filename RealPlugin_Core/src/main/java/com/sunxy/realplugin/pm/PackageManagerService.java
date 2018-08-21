package com.sunxy.realplugin.pm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.RemoteException;

import com.sunxy.realplugin.am.ActivityManageService;
import com.sunxy.realplugin.parser.PluginPackageMap;
import com.sunxy.realplugin.utils.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * -- 自定义 packageManagerService。
 * <p>
 * Created by sunxy on 2018/8/17 0017.
 */
public class PackageManagerService extends IPluginManager.Stub{

    private Context mContext;
    private ActivityManageService activityManageService;
    private Map<String, PluginPackageMap> pluginAllMap = new HashMap<>(20);



    public PackageManagerService(Context mContext) {
        this.mContext = mContext;
        activityManageService = new ActivityManageService(mContext);
    }

    public void main(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                onCreateInner();
            }
        }).start();
    }

    private void onCreateInner() {
        File file = new File(FileUtils.getPluginPath(mContext));
        File[] files = file.listFiles();
        for (File pluginFile : files) {
            File apkFile = new File(pluginFile, "apk/base.apk");
            if (apkFile.exists()){
                //解析apk
                try {
                    PluginPackageMap pluginPackageMap = new PluginPackageMap(mContext, apkFile);
                    pluginAllMap.put(pluginPackageMap.getPackageName(), pluginPackageMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        activityManageService.onCreate(PackageManagerService.this);
    }

    /**
     * 安装
     */
    @Override
    public int installPackage(String pluginFile, int flags) throws RemoteException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(pluginFile, 0);
        String apkPath = FileUtils.getPluginApkPath(mContext, info.packageName);
        FileUtils.copyFile(pluginFile, apkPath);

        //解析apk
        try {
            PluginPackageMap pluginPackageMap = new PluginPackageMap(mContext, new File(apkPath));
            pluginAllMap.put(pluginPackageMap.getPackageName(), pluginPackageMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    @Override
    public int deletePackage(String packageName, int flags) throws RemoteException {
        return 0;
    }

    @Override
    public ApplicationInfo getApplicationInfo(String packageName, int flags) throws RemoteException {
        PluginPackageMap pluginPackageMap = pluginAllMap.get(packageName);
        if (pluginPackageMap != null){
            try {
                return pluginPackageMap.getApplicationInfo(flags);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public ActivityInfo getActivityInfo(ComponentName className, int flags) throws RemoteException {
        PluginPackageMap pluginPackageMap = pluginAllMap.get(className.getPackageName());
        if (pluginPackageMap != null){
            return pluginPackageMap.getActivityInfo(className, flags);
        }
        return null;
    }

    @Override
    public ActivityInfo selectStubActivityInfoByIntent(Intent targetIntent) throws RemoteException {
        ActivityInfo activityInfo = getActivityInfo(targetIntent.getComponent(), 0);
        if (activityInfo != null){
            selectProxyActivity(activityInfo);
        }
        return activityInfo;
    }

    private void selectProxyActivity(ActivityInfo ai) {

    }

    @Override
    public boolean waitForReady() throws RemoteException {
        return false;
    }

    @Override
    public PackageInfo getPackageInfo(String packageName, int flags) throws RemoteException {
        return null;
    }

    @Override
    public boolean isPluginPackage(String packageName) throws RemoteException {
        return false;
    }

    @Override
    public ActivityInfo getReceiverInfo(ComponentName className, int flags) throws RemoteException {
        return null;
    }

    @Override
    public ServiceInfo getServiceInfo(ComponentName className, int flags) throws RemoteException {
        return null;
    }

    @Override
    public ProviderInfo getProviderInfo(ComponentName className, int flags) throws RemoteException {
        return null;
    }

    @Override
    public ResolveInfo resolveIntent(Intent intent, String resolvedType, int flags) throws RemoteException {
        return null;
    }

    @Override
    public List<ResolveInfo> queryIntentActivities(Intent intent, String resolvedType, int flags) throws RemoteException {
        return null;
    }

    @Override
    public List<ResolveInfo> queryIntentReceivers(Intent intent, String resolvedType, int flags) throws RemoteException {
        return null;
    }

    @Override
    public ResolveInfo resolveService(Intent intent, String resolvedType, int flags) throws RemoteException {
        return null;
    }

    @Override
    public List<ResolveInfo> queryIntentServices(Intent intent, String resolvedType, int flags) throws RemoteException {
        return null;
    }

    @Override
    public List<ResolveInfo> queryIntentContentProviders(Intent intent, String resolvedType, int flags) throws RemoteException {
        return null;
    }

    @Override
    public List<PackageInfo> getInstalledPackages(int flags) throws RemoteException {
        return null;
    }

    @Override
    public List<ApplicationInfo> getInstalledApplications(int flags) throws RemoteException {
        return null;
    }

    @Override
    public PermissionInfo getPermissionInfo(String name, int flags) throws RemoteException {
        return null;
    }

    @Override
    public List<PermissionInfo> queryPermissionsByGroup(String group, int flags) throws RemoteException {
        return null;
    }

    @Override
    public PermissionGroupInfo getPermissionGroupInfo(String name, int flags) throws RemoteException {
        return null;
    }

    @Override
    public List<PermissionGroupInfo> getAllPermissionGroups(int flags) throws RemoteException {
        return null;
    }

    @Override
    public ProviderInfo resolveContentProvider(String name, int flags) throws RemoteException {
        return null;
    }

    @Override
    public List<ActivityInfo> getReceivers(String packageName, int flags) throws RemoteException {
        return null;
    }

    @Override
    public List<IntentFilter> getReceiverIntentFilter(ActivityInfo info) throws RemoteException {
        return null;
    }

    @Override
    public int checkSignatures(String pkg1, String pkg2) throws RemoteException {
        return 0;
    }

    @Override
    public ActivityInfo selectStubActivityInfo(ActivityInfo targetInfo) throws RemoteException {
        return null;
    }

    @Override
    public ServiceInfo selectStubServiceInfo(ServiceInfo targetInfo) throws RemoteException {
        return null;
    }

    @Override
    public ServiceInfo selectStubServiceInfoByIntent(Intent targetIntent) throws RemoteException {
        return null;
    }

    @Override
    public ServiceInfo getTargetServiceInfo(ServiceInfo stubInfo) throws RemoteException {
        return null;
    }

    @Override
    public ProviderInfo selectStubProviderInfo(String name) throws RemoteException {
        return null;
    }

    @Override
    public List<String> getPackageNameByPid(int pid) throws RemoteException {
        return null;
    }

    @Override
    public String getProcessNameByPid(int pid) throws RemoteException {
        return null;
    }

    @Override
    public boolean killBackgroundProcesses(String packageName) throws RemoteException {
        return false;
    }

    @Override
    public boolean killApplicationProcess(String pluginPackageName) throws RemoteException {
        return false;
    }

    @Override
    public boolean forceStopPackage(String pluginPackageName) throws RemoteException {
        return false;
    }

    @Override
    public void onActivityCreated(ActivityInfo stubInfo, ActivityInfo targetInfo) throws RemoteException {

    }

    @Override
    public void onActivityDestory(ActivityInfo stubInfo, ActivityInfo targetInfo) throws RemoteException {

    }

    @Override
    public void onServiceCreated(ServiceInfo stubInfo, ServiceInfo targetInfo) throws RemoteException {

    }

    @Override
    public void onServiceDestory(ServiceInfo stubInfo, ServiceInfo targetInfo) throws RemoteException {

    }

    @Override
    public void onProviderCreated(ProviderInfo stubInfo, ProviderInfo targetInfo) throws RemoteException {

    }

    @Override
    public void reportMyProcessName(String stubProcessName, String targetProcessName, String targetPkg) throws RemoteException {

    }

    @Override
    public void onActivtyOnNewIntent(ActivityInfo stubInfo, ActivityInfo targetInfo, Intent intent) throws RemoteException {

    }

    @Override
    public int getMyPid() throws RemoteException {
        return 0;
    }
}
