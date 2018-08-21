package com.sunxy.realplugin.hook.handleImpl;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.sunxy.realplugin.hook.base.BaseClassHandle;
import com.sunxy.realplugin.hook.base.BaseMethodHandle;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/21 0021.
 */
public class IPackageManagerClassHandle extends BaseClassHandle{

    public IPackageManagerClassHandle(Context mHostContext) {
        super(mHostContext);
    }

    @Override
    protected void init(Map<String, BaseMethodHandle> hookMethodHandles) {
        hookMethodHandles.put("getPackageInfo", new GetPackageInfo(mHostContext));
    }

    private static class GetPackageInfo extends BaseMethodHandle{

        PackageInfo packageInfo = new PackageInfo();

        public GetPackageInfo(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Exception {
            setUserMyResult(packageInfo);
            return true;
        }
    }
}
