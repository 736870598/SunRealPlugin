package com.sunxy.realplugin.hook.handleImpl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.sunxy.realplugin.core.Env;
import com.sunxy.realplugin.core.PluginCoreProcessManager;
import com.sunxy.realplugin.core.PluginManager;
import com.sunxy.realplugin.hook.HookFactory;
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
            Log.v("sunxiaoyu", "mh handleMessage " );

            if (PluginManager.getInstance().isPluginService() && !PluginManager.getInstance().isConnected()){
                //这里必须要这么做。如果当前进程是插件进程，并且，还没有绑定上插件管理服务，我们则把消息延迟一段时间再处理。
                //这样虽然会降低启动速度，但是可以解决在没绑定服务就启动，会导致的一系列时序问题。
                Log.v("sunxiaoyu", "mh sendMessageDelayed " );
                mH.sendMessageDelayed(Message.obtain(msg), 5);
                return true;
            }

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
            Intent realIntent = proxyIntent.getParcelableExtra(Env.EXTRA_TARGET_INTENT);
            if(realIntent != null){

                //将obj里的applicationInfo的包名改成插件的包名，
                //因为ActivityThread中通过包名去获取对应的LoadedApk，
                //再通过LoadedApk中的classLoader加载相应的类。
                Field activityInfoField = ReflectUtils.findField(obj, "activityInfo");
                ActivityInfo activityInfo = (ActivityInfo) activityInfoField.get(obj);
                activityInfo.applicationInfo.packageName =  realIntent.getPackage() == null ?
                        realIntent.getComponent().getPackageName() : realIntent.getPackage();

                //这时候去加载loadedApk
                PluginCoreProcessManager.preLoadApk(context, realIntent.getComponent());

                ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(proxyIntent, 0);
                realIntent.putExtra(Env.EXTRA_STUB_INFO, resolveInfo.activityInfo);
                realIntent.putExtra(Env.EXTRA_TARGET_INFO, PluginManager.getInstance().resolveActivityInfo(realIntent, 0));

                //把正在要跳转的intent放回去
                intentField.set(obj, realIntent);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}
