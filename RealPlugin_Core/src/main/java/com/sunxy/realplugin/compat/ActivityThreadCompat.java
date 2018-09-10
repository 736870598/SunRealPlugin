package com.sunxy.realplugin.compat;

import android.os.Build;
import android.os.Handler;

import com.sunxy.realplugin.utils.ReflectFieldUtils;
import com.sunxy.realplugin.utils.ReflectMethodUtils;
import com.sunxy.realplugin.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/22 0022.
 */
public class ActivityThreadCompat {

    private static Object sActivityThread;
    private static Class sClass = null;
    private static Object iActivityManager;
    private static Handler mH;

    public static final Class activityThreadClass() throws ClassNotFoundException {
        if (sClass == null){
            sClass = Class.forName("android.app.ActivityThread");
        }
        return sClass;
    }

    public synchronized static final Object currentActivityThread() throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (sActivityThread == null){
            sActivityThread = ReflectMethodUtils.invokeStaticMethod(activityThreadClass(), "currentActivityThread", true);
        }
        return sActivityThread;
    }

    public synchronized static final Handler mH() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        if (mH == null){
            mH = (Handler) ReflectFieldUtils.readField(currentActivityThread(), "mH");
        }
        return mH;
    }
}
