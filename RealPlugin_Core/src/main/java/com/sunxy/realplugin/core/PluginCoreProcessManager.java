package com.sunxy.realplugin.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.sunxy.realplugin.utils.FileUtils;
import com.sunxy.realplugin.utils.ReflectFieldUtils;
import com.sunxy.realplugin.utils.ReflectMethodUtils;
import com.sunxy.realplugin.utils.ReflectUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

import dalvik.system.DexClassLoader;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/21 0021.
 */
public class PluginCoreProcessManager {

    private static Map<String, Object> sPluginLoadedApkCache = new WeakHashMap<>();

    public static void preLoadApk(Context hostContext, ComponentName componentName){
        if (sPluginLoadedApkCache.get(componentName.getPackageName()) != null){
            return;
        }
        try {

            //1. 得到activityThread对象
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = ReflectUtils.findMethod(activityThreadClass, "currentActivityThread");
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);


            //2. 获取activityThread对象中的mPackages对象
            //  final ArrayMap<String, WeakReference<LoadedApk>> mPackages = new ArrayMap<>();
            Field mPackagesField = ReflectUtils.findField(activityThreadClass, "mPackages");
            Map mPackages = (Map) mPackagesField.get(currentActivityThread);

            //3. 将apk加载成loadedApk，添加到mPackages中
            // 。
            // 3.1 拿到ActivityThread中的getPackageInfoNoCheck方法。反射时优先拿public的方法。
            // public final LoadedApk getPackageInfoNoCheck(ApplicationInfo ai, CompatibilityInfo compatInfo)
            Class<?> compatibilityInfoClass = Class.forName("android.content.res.CompatibilityInfo");
            Method getPackageInfoNoCheckMethod = ReflectUtils.findMethod(activityThreadClass, "getPackageInfoNoCheck",
                    ApplicationInfo.class, compatibilityInfoClass);

            // 3.2 获取3.1方法中需要的CompatibilityInfo对象
            Field defaultCompatibilityInfoField = ReflectUtils.findField(compatibilityInfoClass,
                    "DEFAULT_COMPATIBILITY_INFO");
            Object defaultCompatibilityInfo = defaultCompatibilityInfoField.get(null);

            // 3.3 获取3.1方法中需要的 ApplicationInfo 对象
            ApplicationInfo applicationInfo = PluginManager.getInstance().getApplicationInfo(componentName, 0);

            // 3.4 生成loadApk文件
            Object loadedApk = getPackageInfoNoCheckMethod.invoke(currentActivityThread, applicationInfo, defaultCompatibilityInfo);

            //4. 加工处理下loadedApk   最终目的  是要替换ClassLoader  不是替换LoaderApk
            Field mClassLoaderField = ReflectUtils.findField(loadedApk, "mClassLoader");
            DexClassLoader newClassLoader = new DexClassLoader(
                    applicationInfo.publicSourceDir,
                    FileUtils.getPluginDalvikCacheDir(hostContext, componentName.getPackageName()),
                    FileUtils.getPluginNativeLibraryPath(hostContext, componentName.getPackageName()),
                    hostContext.getClassLoader());
            mClassLoaderField.set(loadedApk, newClassLoader);

            //5. 把loadedApk放到map中
            WeakReference weakReference = new WeakReference<>(loadedApk);
            mPackages.put(applicationInfo.packageName, weakReference);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
