package com.sunxy.sunrealplugin;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.sunxy.realplugin.core.PluginManager;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void installApk(View v){
       String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/sunxy_file/plugin/pc.apk";
        int result = PluginManager.getInstance().installPackage(apkPath, 0);
        Log.v("sunxiaoyu", "result : " + result);
//       startActivity(new Intent(this, SecondActivity.class));
    }

    public void jump(View view){
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.sunxy.pluginc",
                "com.sunxy.pluginc.MainActivity"));
        startActivity(intent);

    }
}
