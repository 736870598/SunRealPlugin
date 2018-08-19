package com.sunxy.realplugin.parser;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.os.Build;
import android.os.UserHandle;

import com.sunxy.realplugin.utils.ReflectFieldUtils;
import com.sunxy.realplugin.utils.ReflectMethodUtils;
import com.sunxy.realplugin.utils.ReflectUtils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * -- 21版本的packageParser
 *
 *  http://androidxref.com/8.1.0_r33/xref/frameworks/base/core/java/android/content/pm/PackageParser.java
 *
 * <p>
 * Created by sunxy on 2018/8/17 0017.
 */
public class PackageParser21 extends PackageParser {

    protected Class<?> sPackageUserStateClass;
    protected Class<?> sPackageParserClass;
    protected Class<?> sActivityClass;
    protected Class<?> sServiceClass;
    protected Class<?> sProviderClass;
    protected Class<?> sInstrumentationClass;
    protected Class<?> sPermissionClass;
    protected Class<?> sPermissionGroupClass;
    protected Class<?> sArraySetClass;

    // 解析apk后生产的 Package 对象
    protected Object mPackage;
    //用户状态对象， 从4.0以后为多用户操作
    protected Object mDefaultPackageUserState;
    //当前用户id。
    protected int mUserId;

    public PackageParser21(Context mContext) {
        super(mContext);
        try {
            initClasses();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void initClasses() throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        sPackageParserClass = Class.forName("android.content.pm.PackageParser");
        sActivityClass = Class.forName("android.content.pm.PackageParser$Activity");
        sServiceClass = Class.forName("android.content.pm.PackageParser$Service");
        sProviderClass = Class.forName("android.content.pm.PackageParser$Provider");
        sInstrumentationClass = Class.forName("android.content.pm.PackageParser$Instrumentation");
        sPermissionClass = Class.forName("android.content.pm.PackageParser$Permission");
        sPermissionGroupClass = Class.forName("android.content.pm.PackageParser$PermissionGroup");

        try {
            sArraySetClass = Class.forName("android.util.ArraySet");
        }catch (Exception e){ }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            sPackageUserStateClass = Class.forName("android.content.pm.PackageUserState");
            mDefaultPackageUserState = sPackageUserStateClass.newInstance();

            mUserId = (int) ReflectMethodUtils.invokeStaticMethod(UserHandle.class, "getCallingUserId", true);
        }

    }

    @Override
    public void parsePackage(File packageFile, int flags) throws Exception {
        //         PackageParser.parsePackage()
        mPackageParser = sPackageParserClass.newInstance();
        Method parsePackage = ReflectUtils.findMethod(mPackageParser, "parsePackage", File.class, int.class);
        mPackage = parsePackage.invoke(mPackageParser, packageFile, flags);
    }

    @Override
    public void collectCertificates(int flags) throws Exception {
        // public void collectCertificates(Package pkg, int flags) throws PackageParserException
        ReflectMethodUtils.invokeMethod(mPackageParser,
                "collectCertificates", false,
                mPackage, flags);
    }

    @Override
    public ActivityInfo generateActivityInfo(Object activity, int flags) throws Exception {
        /*   public static final ActivityInfo generateActivityInfo(Activity a, int flags,
            PackageUserState state, int userId) */
        return (ActivityInfo) ReflectMethodUtils.invokeStaticMethod(mPackageParser,
                "generateActivityInfo", false,
                activity, flags, mDefaultPackageUserState, mUserId
                );
    }

    @Override
    public ServiceInfo generateServiceInfo(Object service, int flags) throws Exception {
        /* public static final ServiceInfo generateServiceInfo(Service s, int flags,
            PackageUserState state, int userId)*/
        return (ServiceInfo) ReflectMethodUtils.invokeStaticMethod(mPackageParser,
                "generateServiceInfo", false,
                service, flags, mDefaultPackageUserState, mUserId);
    }

    @Override
    public ProviderInfo generateProviderInfo(Object provider, int flags) throws Exception {
         /*  public static final ProviderInfo generateProviderInfo(Provider p, int flags,
            PackageUserState state, int userId) */
        return (ProviderInfo) ReflectMethodUtils.invokeStaticMethod(mPackageParser,
                "generateProviderInfo",false,
                provider, flags, mDefaultPackageUserState, mUserId);
    }

    @Override
    public InstrumentationInfo generateInstrumentationInfo(Object instrumentation, int flags) throws Exception {
         /*  public static final InstrumentationInfo generateInstrumentationInfo(
            Instrumentation i, int flags)*/
        return (InstrumentationInfo) ReflectMethodUtils.invokeStaticMethod(mPackageParser,
                "generateInstrumentationInfo",false,
                instrumentation, flags);
    }

    @Override
    public ApplicationInfo generateApplicationInfo(int flags) throws Exception {
        /* public static ApplicationInfo generateApplicationInfo(Package p, int flags,
            PackageUserState state, int userId) */
        return (ApplicationInfo) ReflectMethodUtils.invokeStaticMethod(mPackageParser,
                "generateApplicationInfo",false,
                mPackage, flags, mDefaultPackageUserState, mUserId);
    }

    @Override
    public PermissionGroupInfo generatePermissionGroupInfo(Object permissionGroup, int flags) throws Exception {
         /*  public static final PermissionGroupInfo generatePermissionGroupInfo(
            PermissionGroup pg, int flags)*/
        return (PermissionGroupInfo) ReflectMethodUtils.invokeStaticMethod(mPackageParser,
                "generatePermissionGroupInfo",false,
                permissionGroup, flags);
    }

    @Override
    public PermissionInfo generatePermissionInfo(Object permission, int flags) throws Exception {
        //public static final PermissionInfo generatePermissionInfo(
        //          Permission p, int flags) {
        return (PermissionInfo) ReflectMethodUtils.invokeStaticMethod(mPackageParser,
                "generatePermissionInfo",false,
                permission, flags);
    }

    @Override
    public PackageInfo generatePackageInfo(int[] gids, int flags, long firstInstallTime, long lastUpdateTime, HashSet<String> grantedPermissions) throws Exception {
        //    public static PackageInfo generatePackageInfo(PackageParser.Package p,
        //            int gids[], int flags, long firstInstallTime, long lastUpdateTime,
        //            HashSet<String> grantedPermissions, PackageUserState state)

        try {
            Method method = ReflectMethodUtils.findMethod(sPackageParserClass, "generatePackageInfo", true,
                    mPackage.getClass(),
                    int[].class, int.class, long.class, long.class, Set.class, sPackageUserStateClass, int.class);
            return (PackageInfo) method.invoke(null, mPackage, gids, flags, firstInstallTime, lastUpdateTime, grantedPermissions, mDefaultPackageUserState, mUserId);
        } catch (NoSuchMethodException e) {
        }

        try {
            Method method = ReflectMethodUtils.findMethod(sPackageParserClass, "generatePackageInfo",true,
                    mPackage.getClass(),
                    int[].class, int.class, long.class, long.class, HashSet.class, sPackageUserStateClass, int.class);
            return (PackageInfo) method.invoke(null, mPackage, gids, flags, firstInstallTime, lastUpdateTime, grantedPermissions, mDefaultPackageUserState, mUserId);
        } catch (NoSuchMethodException e) {
        }

        try {
            Method method =  ReflectMethodUtils.findMethod(sPackageParserClass, "generatePackageInfo",true,
                    mPackage.getClass(),
                    int[].class, int.class, long.class, long.class, sArraySetClass, sPackageUserStateClass, int.class);

            Object grantedPermissionsArray = null;
            try {
                Constructor constructor = sArraySetClass.getConstructor(Collection.class);
                grantedPermissionsArray = constructor.newInstance(grantedPermissions);
            } catch (Exception e) {
            }
            if (grantedPermissionsArray == null) {
                grantedPermissionsArray = grantedPermissions;            }
            return (PackageInfo) method.invoke(null, mPackage, gids, flags, firstInstallTime, lastUpdateTime, grantedPermissionsArray, mDefaultPackageUserState, mUserId);
        } catch (NoSuchMethodException e) {
        }

        return (PackageInfo) ReflectMethodUtils.invokeMethod(mPackageParser,
                "generatePackageInfo",false,
                mPackage, gids, flags, firstInstallTime, lastUpdateTime, grantedPermissions, mDefaultPackageUserState);
    }

    @Override
    public List getActivities() throws Exception {
        return (List) ReflectFieldUtils.readField(mPackage, "activities");
    }

    @Override
    public List getServices() throws Exception {
        return (List) ReflectFieldUtils.readField(mPackage, "services");
    }

    @Override
    public List getProviders() throws Exception {
        return (List) ReflectFieldUtils.readField(mPackage, "providers");
    }

    @Override
    public List getPermissions() throws Exception {
        return (List) ReflectFieldUtils.readField(mPackage, "permissions");
    }

    @Override
    public List getPermissionGroups() throws Exception {
        return (List) ReflectFieldUtils.readField(mPackage, "permissionGroups");
    }

    @Override
    public List getRequestedPermissions() throws Exception {
        return (List) ReflectFieldUtils.readField(mPackage, "requestedPermissions");
    }

    @Override
    public List getReceivers() throws Exception {
        return (List) ReflectFieldUtils.readField(mPackage, "receivers");
    }

    @Override
    public List getInstrumentations() throws Exception {
        return (List) ReflectFieldUtils.readField(mPackage, "instrumentation");
    }

    @Override
    public String getPackageName() throws Exception {
        return (String) ReflectFieldUtils.readField(mPackage, "packageName");
    }

    @Override
    public String readNameFromComponent(Object data) throws Exception {
        return (String) ReflectFieldUtils.readField(data, "className");
    }

    @Override
    public List<IntentFilter> readIntentFilterFromComponent(Object data) throws Exception {
        return (List<IntentFilter>) ReflectFieldUtils.readField(data, "intents");
    }

    @Override
    public void writeSignature(Signature[] signatures) throws Exception {
        ReflectFieldUtils.writeField(mPackage, "mSignatures", signatures);
    }

}
