package com.sunxy.realplugin.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * -- 反射方法
 * <p>
 * Created by sunxy on 2018/8/17 0017.
 */
public class ReflectMethodUtils {

    private static Map<String, Method> sMethodCache = new HashMap<String, Method>();

    /**
     * 创建类
     */
    public static Object invokeConstructor(Class clazz, boolean isPackage, Object...args)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor constructor = clazz.getConstructor(args2Class(isPackage, args));
        return constructor.newInstance(args);
    }

    /**
     * 执行静态方法。
     */
    public static Object invokeStaticMethod(Object object, String name, boolean isPackage, Object...args)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return invokeStaticMethod(object.getClass(), name, isPackage, args);
    }

    /**
     * 执行静态方法。
     */
    public static Object invokeStaticMethod(Class clazz, String name, boolean isPackage, Object...args)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = findMethod(clazz, name, isPackage, args);
        return method.invoke(null, args);
    }

    /**
     * 执行普通非静态方法。
     */
    public static Object invokeMethod(Object object, String name, boolean isPackage, Object...args)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = findMethod(object.getClass(), name, isPackage, args);
        return method.invoke(object, args);
    }

    /**
     * 获取方法。
     */
    public static Method findMethod(Class clazz, String name, boolean isPackage, Object...args) throws NoSuchMethodException {
         String methodName = getMethodName(clazz, name);
        Method method = sMethodCache.get(methodName);
        if (method == null){
            Class<?>[] classes = args2Class(isPackage, args);
            method = ReflectUtils.findMethod(clazz, name, classes);
            if (method != null){
                sMethodCache.put(methodName, method);
            }
        }
        return method;
    }

    /**
     * 获取方法名 类名$方法名
     */
    private static String getMethodName(Class clazz, String name){
        return clazz.getName() + "$" + name;
    }

    /**
     * 获取参数集合的class集合
     */
    public static Class<?>[] args2Class(boolean isPackage, Object...args){
        if (args == null){
            return null;
        }
        int length = args.length;
        Class<?>[] classes = new Class[length];
        for (int i = 0; i < length; i++) {
            Class<?> aClass;
            if (isPackage){
                aClass = args[i].getClass();
            }else{
                if (args[i] instanceof Integer){
                    aClass = int.class;
                }
                else if(args[i] instanceof Float){
                    aClass = float.class;
                }
                else if(args[i] instanceof Double){
                    aClass = double.class;
                }
                else if(args[i] instanceof Long){
                    aClass = long.class;
                }
                else if(args[i] instanceof Short){
                    aClass = short.class;
                }
                else{
                    aClass = args[i].getClass();
                }
            }
            classes[i] = aClass;
        }
        return classes;
    }
}
