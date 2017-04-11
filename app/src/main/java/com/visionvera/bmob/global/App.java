package com.visionvera.bmob.global;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.visionvera.bmob.BuildConfig;
import com.visionvera.bmob.utils.LogUtil;

/**
 * Created by Qiao on 2016/12/15.
 */

public class App extends Application {
    public static Handler mHandler = new Handler();
    private static App mApp;

    public static void runOnUiThread(Runnable r) {
        mHandler.post(r);
    }

    public static Context getContext() {
        return mApp.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;

        LogUtil.init(BuildConfig.DEBUG);
        LogUtil.d("App onCreate");
        Fresco.initialize(getApplicationContext());
        CrashHandler.getInstance().init(getApplicationContext(), BuildConfig.DEBUG, getPackageName(), BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME);
    }
}
