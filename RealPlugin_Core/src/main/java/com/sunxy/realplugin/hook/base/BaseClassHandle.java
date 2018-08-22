package com.sunxy.realplugin.hook.base;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/20 0020.
 */
public abstract class BaseClassHandle {

    protected Map<String, BaseMethodHandle> hookMethodHandles = new HashMap<>();
    protected Context mHostContext;

    public BaseClassHandle(Context mHostContext){
        this.mHostContext = mHostContext;
        init(hookMethodHandles);
    }

    protected abstract void init(Map<String, BaseMethodHandle> hookMethodHandles);

    public BaseMethodHandle getHookMethodHandle(String methodName){
        if (methodName != null){
            return hookMethodHandles.get(methodName);
        }
        return null;
    }


}
