package com.sunxy.realplugin.hook.base;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/20 0020.
 */
public class BaseMethodHandle {

    protected Context mHostContext;
    protected Object userMyResult;

    public void setUserMyResult(Object userMyResult){
        this.userMyResult = userMyResult;
    }

    public BaseMethodHandle(Context context){
        this.mHostContext = context;
    }

    public Object doHootInner(Object receiver, Method method, Object[] args){
        userMyResult = null;
        try {
            boolean suc = beforeInvoke(receiver, method, args);
            Log.v("sunxiaoyu", "suc : " + args[2].toString());
            if (!suc){
                userMyResult = method.invoke(receiver, args);
            }
            afterInvoke(receiver, method, args);
        }catch (Exception e){
            e.printStackTrace();
        }
        return userMyResult;

    }

    protected  boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Exception{
        return false;
    }

    protected void afterInvoke(Object receiver, Method method, Object[] args) throws Exception {}
}
