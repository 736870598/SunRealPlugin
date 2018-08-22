package com.sunxy.realplugin.compat;

import com.sunxy.realplugin.utils.ReflectMethodUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/22 0022.
 */
public class ActivityThreadCompat {

    private static Object sActivityThread;
    private static Class sClass = null;

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
}
