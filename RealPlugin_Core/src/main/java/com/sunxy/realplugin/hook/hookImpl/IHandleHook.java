package com.sunxy.realplugin.hook.hookImpl;

import android.content.Context;
import android.os.Handler;

import com.sunxy.realplugin.compat.ActivityThreadCompat;
import com.sunxy.realplugin.hook.base.BaseClassHandle;
import com.sunxy.realplugin.hook.base.BaseHook;
import com.sunxy.realplugin.hook.handleImpl.ActivityMH;
import com.sunxy.realplugin.utils.ReflectFieldUtils;
import com.sunxy.realplugin.utils.ReflectMethodUtils;
import com.sunxy.realplugin.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/21 0021.
 */
public class IHandleHook extends BaseHook {


    public IHandleHook(Context context) {
        super(context);
    }

    @Override
    protected BaseClassHandle createHookHandle() {
        return null;
    }

    @Override
    public void onInit(ClassLoader classLoader) {
        try {
            Handler mH = ActivityThreadCompat.mH();
            Field mCallbackField = ReflectUtils.findField(mH, "mCallback");

            mCallbackField.set(mH, new ActivityMH(mHostContext, mH));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
