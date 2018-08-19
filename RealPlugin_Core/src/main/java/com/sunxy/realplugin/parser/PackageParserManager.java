package com.sunxy.realplugin.parser;

import android.content.Context;
import android.os.Build;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/17 0017.
 */
public class PackageParserManager {

    private static final PackageParserManager ourInstance = new PackageParserManager();

    public static PackageParserManager getInstance() {
        return ourInstance;
    }

    private PackageParserManager() {
    }

    public PackageParser getPluginParser(Context context) throws Exception{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1){
            return new PackageParser27(context);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //sdk大于22的  走这里
            return new PackageParser23(context);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return new PackageParser22(context);//API 21
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new PackageParser21(context);//API 21
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return new PackageParser20(context);//API 17,18,19,20
        }
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
            return new PackageParser16(context); //API 16
        }
        return new PackageParser15(context); //API 9，10
    }

}
