package com.sunxy.realplugin.hook.handleImpl;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.sunxy.realplugin.compat.ActivityThreadCompat;
import com.sunxy.realplugin.core.Env;
import com.sunxy.realplugin.core.PluginManager;
import com.sunxy.realplugin.utils.ReflectFieldUtils;
import com.sunxy.realplugin.utils.ReflectUtils;

import java.lang.reflect.Field;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/22 0022.
 */
public class PluginInstrumentation extends Instrumentation {

    protected Instrumentation mTarget;
    private final Context mHostContext;
    private boolean enable = true;

    public PluginInstrumentation(Context hostContext, Instrumentation target) {
        mTarget = target;
        mHostContext = hostContext;
    }


    public void setEnable(boolean enable){
        this.enable = enable;
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        if (enable){
            fixContextPackageManager(activity);
            try {
                onActivityCreated(activity);
            } catch (Exception e) {
            }

            try {
                fixBaseContextImplOpsPackage(activity.getBaseContext());
            } catch (Exception e) {
            }

            try {
                fixBaseContextImplContentResolverOpsPackage(activity.getBaseContext());
            } catch (Exception e) {
            }
        }
        super.callActivityOnCreate(activity, icicle);
    }

    private void fixContextPackageManager(Context context){
        try {
            Object currentActivityThread = ActivityThreadCompat.currentActivityThread();
            Object newPm = ReflectFieldUtils.readField(currentActivityThread, "sPackageManager");
            PackageManager pm = context.getPackageManager();
            Object mPM = ReflectFieldUtils.readField(pm, "mPM");
            if (mPM != newPm){
                ReflectFieldUtils.writeField(pm, "mPM", newPm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onActivityCreated(Activity activity) throws Exception{
        Intent targetIntent = activity.getIntent();
        if (targetIntent != null){
            ActivityInfo targetInfo = targetIntent.getParcelableExtra(Env.EXTRA_TARGET_INFO);
            ActivityInfo stubInfo = targetIntent.getParcelableExtra(Env.EXTRA_STUB_INFO);
            if (targetInfo != null && stubInfo != null){
                PluginManager.getInstance().onActivityCreated(stubInfo, targetInfo);
            }
        }

    }

    private void fixBaseContextImplOpsPackage(Context context) throws NoSuchFieldException, IllegalAccessException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 && context != null && !TextUtils.equals(context.getPackageName(), mHostContext.getPackageName())) {
            Context baseContext = context;
            Class clazz = baseContext.getClass();
            Field mOpPackageName = ReflectUtils.findField(clazz, "mOpPackageName");
            if (mOpPackageName != null) {
                Object valueObj = mOpPackageName.get(baseContext);
                if (valueObj instanceof String) {
                    String opPackageName = ((String) valueObj);
                    if (!TextUtils.equals(opPackageName, mHostContext.getPackageName())) {
                        mOpPackageName.set(baseContext, mHostContext.getPackageName());
                    }
                }
            }
        }
    }

    private void fixBaseContextImplContentResolverOpsPackage(Context context) throws NoSuchFieldException, IllegalAccessException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 && context != null && !TextUtils.equals(context.getPackageName(), mHostContext.getPackageName())) {
            Context baseContext = context;
            Class clazz = baseContext.getClass();
            Field mContentResolver = ReflectUtils.findField(clazz, "mContentResolver");
            if (mContentResolver != null) {
                Object valueObj = mContentResolver.get(baseContext);
                if (valueObj instanceof ContentResolver) {
                    ContentResolver contentResolver = ((ContentResolver) valueObj);
                    Field mPackageName = ReflectUtils.findField(ContentResolver.class, "mPackageName");
                    Object mPackageNameValueObj = mPackageName.get(contentResolver);
                    if (mPackageNameValueObj != null && mPackageNameValueObj instanceof String) {
                        String packageName = ((String) mPackageNameValueObj);
                        if (!TextUtils.equals(packageName, mHostContext.getPackageName())) {
                            mPackageName.set(contentResolver, mHostContext.getPackageName());
                        }
                    }

                }
            }
        }
    }
}
