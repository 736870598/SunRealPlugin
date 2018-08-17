package com.sunxy.realplugin.parser;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;

import com.sunxy.realplugin.utils.ReflectMethodUtils;

import java.lang.reflect.Method;
import java.util.HashSet;

/**
 * -- 16版本的packageParser
 * <p>
 * Created by sunxy on 2018/8/17 0017.
 */
public class PackageParser16 extends PackageParser20 {
    public PackageParser16(Context mContext) throws Exception {
        super(mContext);
        mStopped = false;
        mEnabledState = 0;
    }
    private boolean mStopped;
    private int mEnabledState;
    @Override
    public ActivityInfo generateActivityInfo(Object activity, int flags) throws Exception {
        /*public static final ActivityInfo generateActivityInfo(Activity a, int flags, boolean stopped, int enabledState, int userId)  */
        return (ActivityInfo) ReflectMethodUtils.invokeStaticMethod( sPackageParserClass,
                "generateActivityInfo",
                activity, flags, mStopped, mEnabledState, mUserId);
    }


    @Override
    public ServiceInfo generateServiceInfo(Object service, int flags) throws Exception {
        /*public static final ServiceInfo generateServiceInfo(Service s, int flags, boolean stopped, int enabledState, int userId)*/
        return (ServiceInfo) ReflectMethodUtils.invokeStaticMethod( sPackageParserClass,
                "generateServiceInfo",
                service, flags, mStopped, mEnabledState, mUserId);
    }


    @Override
    public ProviderInfo generateProviderInfo(Object provider, int flags) throws Exception {
        /*     public static final ProviderInfo generateProviderInfo(Provider p, int flags, boolean stopped,
            int enabledState, int userId)  */
        return (ProviderInfo) ReflectMethodUtils.invokeStaticMethod( sPackageParserClass,
                "generateProviderInfo",
                provider, flags, mStopped, mEnabledState, mUserId);
    }


    @Override
    public ApplicationInfo generateApplicationInfo(int flags) throws Exception {
        /*   public static ApplicationInfo generateApplicationInfo(Package p, int flags,
            boolean stopped, int enabledState, int userId) */
        return (ApplicationInfo) ReflectMethodUtils.invokeStaticMethod( sPackageParserClass,
                "generateApplicationInfo",
                mPackage, flags, mStopped, mEnabledState, mUserId);
    }

    @Override
    public PackageInfo generatePackageInfo(
            int gids[], int flags, long firstInstallTime, long lastUpdateTime,
            HashSet<String> grantedPermissions) throws Exception {
        /*     public static PackageInfo generatePackageInfo(PackageParser.Package p,
            int gids[], int flags, long firstInstallTime, long lastUpdateTime,
            HashSet<String> grantedPermissions, boolean stopped, int enabledState, int userId)*/
        return (PackageInfo) ReflectMethodUtils.invokeMethod(sPackageParserClass,
                "generatePackageInfo",
                mPackage, gids, flags, firstInstallTime, lastUpdateTime, grantedPermissions, mStopped, mEnabledState, mUserId);
    }
}
