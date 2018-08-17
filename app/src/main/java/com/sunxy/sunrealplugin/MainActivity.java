package com.sunxy.sunrealplugin;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sunxy.realplugin.core.PluginManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void installApk(View v){
        String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/sunxy_file/plugin/pb.apk";
        int result = PluginManager.getInstance().installPackage(apkPath, 0);

    }
}
