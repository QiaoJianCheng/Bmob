package com.visionvera.bmob.global;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.crash.handler.CrashHandler;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;
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
        Fresco.initialize(getApplicationContext());
        CrashHandler.getInstance().init(getApplicationContext(), BuildConfig.DEBUG, getPackageName(), ResUtil.getString(R.string.app_name), BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME);
        initHotFix();
    }

    private void initHotFix() {
        SophixManager.getInstance().setContext(this)
                .setAppVersion(BuildConfig.VERSION_NAME)
                .setAesKey(null)
                .setEnableDebug(BuildConfig.DEBUG)
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        // 补丁加载回调通知
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            // 表明补丁加载成功
                            LogUtil.d(TAG, "CODE_LOAD_SUCCESS");
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                            // 建议: 用户可以监听进入后台事件, 然后应用自杀
                            LogUtil.d(TAG, "CODE_LOAD_RELAUNCH");
                        } else if (code == PatchStatus.CODE_LOAD_FAIL) {
                            // 内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载
                            SophixManager.getInstance().cleanPatches();
                            LogUtil.d(TAG, "CODE_LOAD_FAIL");
                        } else {
                            // 其它错误信息, 查看PatchStatus类说明
                            LogUtil.d(TAG, "CODE ELSE: " + info);
                        }
                    }
                }).initialize();
        SophixManager.getInstance().queryAndLoadNewPatch();
    }

}