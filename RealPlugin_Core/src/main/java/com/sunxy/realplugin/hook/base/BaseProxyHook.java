package com.sunxy.realplugin.hook.base;

import android.content.Context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/20 0020.
 */
public abstract class BaseProxyHook extends BaseHook implements InvocationHandler {

    public BaseProxyHook(Context mHostContext) {
        super(mHostContext);
    }

    @Override
    public abstract void onInit(ClassLoader classLoader);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 分发总站     startActivity（宿主 OneActivity）
        if (isEnable()){
            BaseMethodHandle baseMethodHandle = classHandle.getHookMethodHandle(method.getName());
            if (baseMethodHandle != null){
                return baseMethodHandle.doHootInner(realObj, method, args);
            }
        }
        return method.invoke(realObj, args);
    }
}
