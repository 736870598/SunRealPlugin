package com.sunxy.realplugin.parser;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.sunxy.realplugin.utils.ReflectMethodUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

/**
 * -- 22版本的packageParser
 * <p>
 * Created by sunxy on 2018/8/17 0017.
 */
public class PackageParser22 extends PackageParser21 {
    public PackageParser22(Context mContext) {
        super(mContext);
    }
    @Override
    public PackageInfo generatePackageInfo(
            int gids[], int flags, long firstInstallTime, long lastUpdateTime,
            HashSet<String> grantedPermissions) throws Exception {
        try {
            return super.generatePackageInfo(gids, flags, firstInstallTime, lastUpdateTime, grantedPermissions);
        } catch (Exception e) {
        }
        Method method = ReflectMethodUtils.findMethod(sPackageParserClass, "generatePackageInfo",
                mPackage.getClass(),
                int[].class, int.class, long.class, long.class, sArraySetClass, sPackageUserStateClass, int.class);
        Object grantedPermissionsArray = null;
        try {
            Constructor constructor = sArraySetClass.getConstructor(Collection.class);
            grantedPermissionsArray = constructor.newInstance(constructor, grantedPermissions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (grantedPermissionsArray == null) {
            grantedPermissionsArray = grantedPermissions;
        }
        return (PackageInfo) method.invoke(null, mPackage, gids, flags, firstInstallTime, lastUpdateTime, grantedPermissionsArray, mDefaultPackageUserState, mUserId);
    }
}
