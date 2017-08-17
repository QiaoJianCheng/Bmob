package com.visionvera.bmob.global;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.crash.handler.CrashHandler;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.visionvera.bmob.BuildConfig;
import com.visionvera.bmob.R;
import com.visionvera.bmob.utils.LogUtil;
import com.visionvera.bmob.utils.ResUtil;

/**
 * Created by Qiao on 2016/12/15.
 */

public class App extends Application {
    public static final String TAG = "APP";
    private static App mApp;
    public static Handler mHandler = new Handler();

    public static void post(Runnable r) {
        mHandler.post(r);
    }

    public static void postDelay(Runnable r, long delay) {
        mHandler.postDelayed(r, delay);
    }

    public static Context getContext() {
        return mApp.getApplicationContext();
    }

    public static App getInstance() {
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;

        LogUtil.init(BuildConfig.DEBUG);
        Fresco.initialize(getApplicationContext());
        CrashHandler.getInstance().init(getApplicationContext(), BuildConfig.DEBUG, getPackageName(), ResUtil.getString(R.string.app_name), BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME);
    }
}