package com.sunxy.realplugin.parser;

import android.content.Context;
import android.util.DisplayMetrics;

import com.sunxy.realplugin.utils.ReflectMethodUtils;

import java.io.File;

/**
 * -- 20版本的packageParser
 * <p>
 * Created by sunxy on 2018/8/17 0017.
 */
public class PackageParser20 extends PackageParser21{

    public PackageParser20(Context mContext) throws Exception {
        super(mContext);
    }

    @Override
    public void parsePackage(File file, int flags) throws Exception {
        super.parsePackage(file, flags);
        DisplayMetrics metrics = new DisplayMetrics();
        metrics.setToDefaults();
        String destCodePath = file.getPath();
        mPackageParser = ReflectMethodUtils.invokeConstructor(sPackageParserClass, true, destCodePath);
        mPackage = ReflectMethodUtils.invokeMethod(mPackageParser,
                "parsePackage", false
                , file, destCodePath, metrics, flags);
    }
}
