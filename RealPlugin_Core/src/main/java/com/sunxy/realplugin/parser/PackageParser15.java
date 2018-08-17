package com.sunxy.realplugin.parser;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;

import com.sunxy.realplugin.utils.ReflectMethodUtils;

import java.util.HashSet;


/**
 * -- 15版本的packageParser
 * <p>
 * Created by sunxy on 2018/8/17 0017.
 */
public class PackageParser15 extends PackageParser20 {

    public PackageParser15(Context mContext) throws Exception {
        super(mContext);
    }


    @Override
    public ActivityInfo generateActivityInfo(Object activity, int flags) throws Exception {
        /*   public static final ActivityInfo generateActivityInfo(Activity a, int flags) */
        return (ActivityInfo) ReflectMethodUtils.invokeStaticMethod(sPackageParserClass,
                "generateActivityInfo",
                activity, flags);
    }


    @Override
    public ServiceInfo generateServiceInfo(Object service, int flags) throws Exception {
        /*  public static final ServiceInfo generateServiceInfo(Service s, int flags)*/
        return (ServiceInfo) ReflectMethodUtils.invokeStaticMethod(sPackageParserClass,
                "generateServiceInfo",
                service, flags);
    }


    @Override
    public ProviderInfo generateProviderInfo(Object provider, int flags) throws Exception {
        /* public static final ProviderInfo generateProviderInfo(Provider p, int flags)  */
        return (ProviderInfo) ReflectMethodUtils.invokeStaticMethod(sPackageParserClass,
                "generateProviderInfo",
                provider, flags);
    }

    @Override
    public InstrumentationInfo generateInstrumentationInfo(
            Object instrumentation, int flags) throws Exception {
        /*     public static final InstrumentationInfo generateInstrumentationInfo(
            Instrumentation i, int flags) */
        return (InstrumentationInfo) ReflectMethodUtils.invokeStaticMethod(sPackageParserClass,
                "generateInstrumentationInfo",
                instrumentation, flags);
    }

    @Override
    public ApplicationInfo generateApplicationInfo(int flags) throws Exception {
        /* public static ApplicationInfo generateApplicationInfo(Package p, int flags) */
        return (ApplicationInfo) ReflectMethodUtils.invokeStaticMethod(sPackageParserClass,
                "generateApplicationInfo",
                mPackage, flags);
    }


    @Override
    public PackageInfo generatePackageInfo(
            int gids[], int flags, long firstInstallTime, long lastUpdateTime,
            HashSet<String> grantedPermissions) throws Exception {
        /* public static PackageInfo generatePackageInfo(PackageParser.Package p,
            int gids[], int flags, long firstInstallTime, long lastUpdateTime) */
        return (PackageInfo) ReflectMethodUtils.invokeStaticMethod(sPackageParserClass,
                "generatePackageInfo",
                mPackage, gids, flags, firstInstallTime, lastUpdateTime);
    }

}
