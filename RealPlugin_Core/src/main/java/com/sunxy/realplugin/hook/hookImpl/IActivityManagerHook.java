package com.sunxy.realplugin.hook.hookImpl;

import android.content.Context;
import android.os.Build;

import com.sunxy.realplugin.hook.base.BaseClassHandle;
import com.sunxy.realplugin.hook.base.BaseProxyHook;
import com.sunxy.realplugin.hook.handleImpl.IActivityManagerClassHandle;
import com.sunxy.realplugin.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * -- hook IActivityManager
 * <p>
 * Created by sunxy on 2018/8/20 0020.
 */
public class IActivityManagerHook extends BaseProxyHook {


    public IActivityManagerHook(Context context) {
        super(context);
        setEnable(true);
    }

    @Override
    protected BaseClassHandle createHookHandle() {
        return new IActivityManagerClassHandle(mHostContext);
    }

    @Override
    public void onInit(ClassLoader classLoader) {
        try {
            Object IActivityManager;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                Class<?> forName = Class.forName("android.app.ActivityManager");
                Field defaultField =  ReflectUtils.findField(forName, "IActivityManagerSingleton");
                IActivityManager = defaultField.get(null);
            }else{
                Class<?> forName = Class.forName("android.app.ActivityManagerNative");
                Field defaultField = ReflectUtils.findField(forName, "gDefault");
                //gDefault的变量值（IActivityManager）
                IActivityManager = defaultField.get(null);
            }

            //反射Singleton
            Class<?> forName2 = Class.forName("android.util.Singleton");
            Field instanceField = ReflectUtils.findField(forName2, "mInstance");
            //系统的iActivityManager对象
            Object iActivityManagerObject = instanceField.get(IActivityManager);
            setRealObj(iActivityManagerObject);
            //钩子
            Class<?> IActivityManagerIntercept = Class.forName("android.app.IActivityManager");
            Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{IActivityManagerIntercept}, this);
            //动态代理
            instanceField.set(IActivityManager, proxy);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }
}
