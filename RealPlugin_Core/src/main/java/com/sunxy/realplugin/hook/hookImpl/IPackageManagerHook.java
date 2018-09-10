package com.sunxy.realplugin.hook.hookImpl;

import android.content.Context;

import com.sunxy.realplugin.compat.ActivityThreadCompat;
import com.sunxy.realplugin.hook.base.BaseClassHandle;
import com.sunxy.realplugin.hook.base.BaseProxyHook;
import com.sunxy.realplugin.hook.handleImpl.IPackageManagerClassHandle;
import com.sunxy.realplugin.utils.ReflectMethodUtils;
import com.sunxy.realplugin.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/21 0021.
 */
public class IPackageManagerHook extends BaseProxyHook {


    public IPackageManagerHook(Context mHostContext) {
        super(mHostContext);
        setEnable(true);
    }

    @Override
    protected BaseClassHandle createHookHandle() {
        return new IPackageManagerClassHandle(mHostContext);
    }

    @Override
    public void onInit(ClassLoader classLoader) {
        try {
            Object activityThreadObj = ActivityThreadCompat.currentActivityThread();
            Field sPackageManagerField = ReflectUtils.findField(ActivityThreadCompat.activityThreadClass(), "sPackageManager");
            Object sPackageManager = sPackageManagerField.get(activityThreadObj);
            setRealObj(sPackageManager);
            Class<?> iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager");
            Object proxy = Proxy.newProxyInstance(iPackageManagerInterface.getClassLoader(),
                    new Class[]{iPackageManagerInterface}, this);
            sPackageManagerField.set(activityThreadObj, proxy);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
