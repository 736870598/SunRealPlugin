package com.sunxy.realplugin.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * - 反射属性
 * <p>
 * Created by sunxy on 2018/8/17 0017.
 */
public class ReflectFieldUtils {

    private static Map<String, Field> sFieldCache = new HashMap<>();

    /**
     * 设置Field
     * 返回旧的值
     */
    public static Object writeField(Object object, String name, Object newValue)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = findField(object.getClass(), name);
        Object oldValue = field.get(object);
        field.set(object, newValue);
        return oldValue;
    }

    /**
     * 获取Field
     */
    public static Object readField(Object object, String name)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = findField(object.getClass(), name);
        return field.get(object);
    }

    /**
     * 找Field
     */
    private static Field findField(Class<?> clazz, String name)
            throws NoSuchFieldException {
        String fieldName = getFieldName(clazz, name);
        Field field = sFieldCache.get(fieldName);
        if (field == null){
            field = ReflectUtils.findField(clazz, name);
            sFieldCache.put(fieldName, field);
        }
        return field;
    }

    /**
     * 组装Field的名字
     */
    private static String getFieldName(Class<?> clazz, String name){
        return clazz.getName() + "_" + name;
    }
}
