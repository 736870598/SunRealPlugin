package com.sunxy.realplugin.hook.handleImpl;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;

import com.sunxy.realplugin.core.PluginCoreProcessManager;
import com.sunxy.realplugin.utils.ReflectUtils;

import java.lang.reflect.Field;

/**
 * -- ActivityThread中的mh，处理ams检测完的startActivity事件。
 * <p>
 * Created by sunxy on 2018/8/20 0020.
 */
public class ActivityMH implements Handler.Callback {

    private Handler mH;
    private Context context;

    public ActivityMH(Context context, Handler mH){
        this.context = context;
        this.mH = mH;
    }


    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == 100){
            handleLaunchActivity(msg);
        }
        mH.handleMessage(msg);
        return true;
    }

    /**
     * 还原ActivityClientRecord中的intent。
     */
    private void handleLaunchActivity(Message msg) {
        Object obj = msg.obj;  //ActivityClientRecord

        try {
            Field intentField = ReflectUtils.findField(obj, "intent");
            Intent proxyIntent = (Intent) intentField.get(obj); //代理意图，需要替换
            Intent realIntent = proxyIntent.getParcelableExtra("realIntent");
            if(realIntent != null){
                intentField.set(obj, realIntent);

                //将obj里的applicationInfo的包名改成插件的包名，
                //因为ActivityThread中通过包名去获取对应的LoadedApk，
                //再通过LoadedApk中的classLoader加载相应的类。
                Field activityInfoField = ReflectUtils.findField(obj, "activityInfo");
                ActivityInfo activityInfo = (ActivityInfo) activityInfoField.get(obj);
                activityInfo.applicationInfo.packageName =  realIntent.getPackage() == null ?
                        realIntent.getComponent().getPackageName() : realIntent.getPackage();

                //这时候去加载loadedApk
                PluginCoreProcessManager.preLoadApk(context, realIntent.getComponent());

            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
