package com.sunxy.realplugin.hook;

import android.content.Context;
import android.nfc.cardemulation.OffHostApduService;

import com.sunxy.realplugin.hook.base.BaseHook;
import com.sunxy.realplugin.hook.hookImpl.IActivityManagerHook;
import com.sunxy.realplugin.hook.hookImpl.IHandleHook;
import com.sunxy.realplugin.hook.hookImpl.IPackageManagerHook;
import com.sunxy.realplugin.utils.ReflectMethodUtils;
import com.sunxy.realplugin.utils.ReflectUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/20 0020.
 */
public class HookFactory {

    private static final HookFactory ourInstance = new HookFactory();

    public static HookFactory getInstance(){
        return ourInstance;
    }

    private HookFactory() {}

    public void installHook(BaseHook baseHook, ClassLoader classLoader){
        baseHook.onInit(classLoader);
    }

    public final void installHook(Context context, ClassLoader classLoader){
        if (isPluginService()){
            //插件进程
        }else{
            //宿主进程
            installHook(new IActivityManagerHook(context), classLoader);
            installHook(new IPackageManagerHook(context), classLoader);
            installHook(new IHandleHook(context), classLoader);
        }
    }


    private boolean isPluginService(){
        String processName = getProcessName();
        if (processName != null){
            return processName.contains(":Plugin");
        }
        return false;
    }
    /**
     * 获取进程名。
     */
    private String getProcessName(){
        try {
            Class<?> ActivityThread = Class.forName("android.app.ActivityThread");
            Object am = ReflectMethodUtils.invokeStaticMethod(ActivityThread, "currentActivityThread", true);
            Method getProcessName = ReflectUtils.findMethod(ActivityThread, "getProcessName");
            return (String) getProcessName.invoke(am);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

}
