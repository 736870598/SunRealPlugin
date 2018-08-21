package com.sunxy.realplugin.utils;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/17 0017.
 */
public class FileUtils {

    public static String getPluginApkPath(Context context, String packageName){
        File pluginFile = getPluginFile(context, packageName);
        deleteFile(pluginFile);
        File pluginApkFile = new File(pluginFile, "apk/base.apk");
        return enforceFileExists(pluginApkFile);
    }

    public static String getPluginDataPath(Context context, String packageName){
        File pluginFile = getPluginFile(context, packageName);
        File pluginDataFile = new File(pluginFile + "/data");
        return enforceDirExists(pluginDataFile);
    }

    public static String getPluginNativeLibraryPath(Context context, String packageName){
        File pluginFile = getPluginFile(context, packageName);
        File pluginDataFile = new File(pluginFile + "/lib");
        return enforceDirExists(pluginDataFile);
    }
    public static String getPluginDalvikCacheDir(Context context, String packageName){
        File pluginFile = getPluginFile(context, packageName);
        File pluginDataFile = new File(pluginFile + "/dalvik-cache");
        return enforceDirExists(pluginDataFile);
    }

    public static File getPluginFile(Context context, String packageName){
        String pluginPath = getPluginPath(context);
        return new File(pluginPath + "/" + packageName);
    }

    public static String getPluginPath(Context context){
        File pluginFile = context.getExternalFilesDir("plugin");
        return enforceDirExists(pluginFile);
    }

    private static String enforceDirExists(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }


    private static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFile(files[i]);
            }
        }
        file.delete();
    }

    public static void copyFile(String src, String dst)   {
        BufferedInputStream in = null;
        BufferedOutputStream ou = null;
        try {
            in = new BufferedInputStream(new FileInputStream(src));
            ou = new BufferedOutputStream(new FileOutputStream(dst));
            byte[] buffer = new byte[8192];
            int read = 0;
            while ((read = in.read(buffer)) != -1) {
                ou.write(buffer, 0, read);
            }
        }catch (Exception e){

        }
        finally{
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }

            if (ou != null) {
                try {
                    ou.close();
                } catch (Exception e) {
                }
            }
        }
    }


    private static String enforceFileExists(File file)  {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }
}
