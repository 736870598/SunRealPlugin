package com.sunxy.realplugin.hook.hookImpl;

import android.app.Instrumentation;
import android.content.Context;

import com.sunxy.realplugin.compat.ActivityThreadCompat;
import com.sunxy.realplugin.hook.base.BaseClassHandle;
import com.sunxy.realplugin.hook.base.BaseHook;
import com.sunxy.realplugin.hook.handleImpl.PluginInstrumentation;
import com.sunxy.realplugin.utils.FileUtils;
import com.sunxy.realplugin.utils.ReflectFieldUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/22 0022.
 */
public class InstrumentationHook extends BaseHook{


    public InstrumentationHook(Context mHostContext) {
        super(mHostContext);
        setEnable(true);
    }

    @Override
    protected BaseClassHandle createHookHandle() {
        return null;
    }

    @Override
    public void onInit(ClassLoader classLoader) {
        try {
            Object target = ActivityThreadCompat.currentActivityThread();
            Class ActivityThreadClass = ActivityThreadCompat.activityThreadClass();
            Instrumentation mInstrumentation = (Instrumentation) ReflectFieldUtils.readStaticField(ActivityThreadClass, "mInstrumentation");

            PluginInstrumentation pit = new PluginInstrumentation(mHostContext, mInstrumentation);
            pit.setEnable(isEnable());
            ReflectFieldUtils.writeField(target, "mInstrumentation", pit);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
