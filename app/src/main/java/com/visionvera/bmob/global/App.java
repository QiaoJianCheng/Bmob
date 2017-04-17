package com.visionvera.bmob.global;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;

import com.crash.handler.CrashHandler;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.visionvera.bmob.BuildConfig;
import com.visionvera.bmob.R;
import com.visionvera.bmob.utils.LogUtil;
import com.visionvera.bmob.utils.ResUtil;

import java.util.List;

/**
 * Created by Qiao on 2016/12/15.
 */

public class App extends Application {
    public static final String TAG = "APP";
    private static App mApp;
    public Handler mHandler = new Handler();

    public void post(Runnable r) {
        mHandler.post(r);
    }

    public void postDelay(Runnable r, long delay) {
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
        LogUtil.d("App onCreate");
        Fresco.initialize(getApplicationContext());
        CrashHandler.getInstance().init(getApplicationContext(), BuildConfig.DEBUG, getPackageName(), ResUtil.getString(R.string.app_name), BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME);
    }

    public boolean isAppOnForeground() {
        if (true) return false;
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
//        if (appProcesses == null) return false;
//        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
//            if (appProcess.processName.equals(getPackageName())
//                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                return true;
//            }
//        }
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(100);
        LogUtil.d(TAG, "===============================================================================");
        for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTasks) {
            String top = runningTaskInfo.topActivity.getClassName();
            String base = runningTaskInfo.baseActivity.getClassName();
            int actNumber = runningTaskInfo.numActivities;
            int runningNum = runningTaskInfo.numRunning;
            LogUtil.d(TAG, "top=" + top + " base=" + base + " actNum=" + actNumber + " runningNum=" + runningNum);
        }
        LogUtil.d(TAG, "===============================================================================");

        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
            String[] pkgList = runningAppProcessInfo.pkgList;
            for (String pkg : pkgList) {
                LogUtil.d(TAG, pkg);
            }
        }
        LogUtil.d(TAG, "===============================================================================");
        return false;
    }

    public void MoveToFront() {
        if (Build.MANUFACTURER.toLowerCase().contains("samsung")) {
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            while (!isAppOnForeground()) {
//                activityManager.moveTaskToFront(getTaskId(), 0);
            }
        } else {
            Intent intent = new Intent(this, this.getClass());
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
    }

}