package com.sunxy.realplugin.hook.handleImpl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.Log;

import com.sunxy.realplugin.core.Env;
import com.sunxy.realplugin.core.PluginManager;
import com.sunxy.realplugin.hook.base.BaseClassHandle;
import com.sunxy.realplugin.hook.base.BaseMethodHandle;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * -- hook ActivityThread中的IActivityManager，在startActivity交由ASM之前
 * 把真实的跳转意图藏起来。
 * <p>
 * Created by sunxy on 2018/8/20 0020.
 */
public class IActivityManagerClassHandle extends BaseClassHandle {

    public IActivityManagerClassHandle(Context mHostContext) {
        super(mHostContext);
    }

    @Override
    protected void init(Map<String, BaseMethodHandle> hookMethodHandles) {
        hookMethodHandles.put("startActivity", new StartActivity(mHostContext));
    }

    private static class StartActivity extends BaseMethodHandle{

        public StartActivity(Context context) {
            super(context);
        }

        private boolean isFirst = true;

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Exception {
            int intentIndexOfArgs = findFirstIntentIndexInArgs(args);
            if (intentIndexOfArgs > -1){
                Intent realIntent = (Intent) args[intentIndexOfArgs];
                if (!mHostContext.getApplicationInfo().packageName.equals( realIntent.getComponent().getPackageName())){
                    // 跳转的不是本apk的，需要设置
                    ComponentName componentName = selectProxyActivity(realIntent);
                    if (componentName != null){
                        Intent proxyIntent = new Intent();
                        proxyIntent.setComponent(componentName);
//                        if (isFirst){
//                            isFirst = false;
//                        }else{
                            proxyIntent.putExtra(Env.EXTRA_TARGET_INTENT, realIntent);
//                        }
                        args[intentIndexOfArgs] = proxyIntent;
                    }
                }
            }
            return false;
        }

        private ComponentName selectProxyActivity(Intent intent){
            if (intent != null){
                ActivityInfo proxyInfo = PluginManager.getInstance().selectProxyActivity(intent);
                if (proxyInfo != null){
                    return new ComponentName(proxyInfo.packageName, proxyInfo.name);
                }
            }
            return null;
        }

        private int findFirstIntentIndexInArgs(Object[] args){
            if (args != null && args.length > 0){
                int i = 0;
                for (Object arg : args) {
                    if (arg != null && arg instanceof Intent){
                        return i;
                    }
                    i++;
                }
            }
            return -1;
        }
    }
}
