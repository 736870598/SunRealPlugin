package com.sunxy.realplugin.hook.base;

import android.content.Context;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/20 0020.
 */
public abstract class BaseHook {

    protected Context mHostContext;
    protected Object realObj;
    protected BaseClassHandle classHandle;
    private boolean isEnable = false;

    public BaseHook(Context mHostContext) {
        this.mHostContext = mHostContext;
    }

    public  boolean isEnable(){
        return isEnable;
    }

    public void setEnable(boolean enable){
        isEnable = enable;
    }

    public void setRealObj(Object obj){
        this.realObj = obj;
        classHandle = createHookHandle();
    }

    /**
     * 策略模式
     */
    protected abstract BaseClassHandle createHookHandle();

    public abstract void onInit(ClassLoader classLoader);


}
