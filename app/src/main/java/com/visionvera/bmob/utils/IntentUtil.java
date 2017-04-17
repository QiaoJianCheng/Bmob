package com.visionvera.bmob.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.view.View;

import com.visionvera.bmob.R;
import com.visionvera.bmob.activity.AppDetailActivity;
import com.visionvera.bmob.activity.ColorFilterActivity;
import com.visionvera.bmob.activity.HomeActivity;
import com.visionvera.bmob.activity.LoginActivity;
import com.visionvera.bmob.activity.RegisterActivity;
import com.visionvera.bmob.activity.SdcardActivity;
import com.visionvera.bmob.activity.SensorActivity;

/**
 * Created by Qiao on 2016/12/16.
 */

public class IntentUtil {
    /**
     * /**
     * 进入 Activity 左移动画，新的 Activity 从右边出来，在 onBackPressed , finish , 或 onDestroy 方法中调用
     *
     * @param activity 当前 activity
     */
    public static void enterActivityAnim(Activity activity) {
        if (activity == null) {
            return;
        }
        activity.overridePendingTransition(R.anim.next_right_in, R.anim.current_left_out);
    }

    /**
     * 退出 Activity 右移动画，上一个 Activity 从左边出来，在进入 activity 时调用
     *
     * @param activity 当前 activity
     */
    public static void exitActivityAnim(Activity activity) {
        if (activity == null) {
            return;
        }
        activity.overridePendingTransition(R.anim.previus_left_in, R.anim.current_right_out);
    }

    public static void toMainActivity(Activity activity) {
        if (activity == null) return;
        Intent intent = new Intent(activity, HomeActivity.class);
        activity.startActivity(intent);
        enterActivityAnim(activity);
    }

    public static void toLoginActivity(Activity activity, View view) {
        if (activity == null) return;
        activity.startActivity(new Intent(activity, LoginActivity.class), ActivityOptions.makeSceneTransitionAnimation(activity, view, "image").toBundle());
        activity.overridePendingTransition(R.anim.next_alpha_in, R.anim.current_alpha_out);
    }

    public static void toRegisterActivity(Activity activity) {
        if (activity == null) return;
        activity.startActivity(new Intent(activity, RegisterActivity.class));
        enterActivityAnim(activity);
    }

    public static void toAppDetailActivity(Activity activity, String appId, String appName) {
        if (activity == null) return;
        Intent intent = new Intent(activity, AppDetailActivity.class);
        intent.putExtra(AppDetailActivity.INTENT_APP_ID, appId);
        intent.putExtra(AppDetailActivity.INTENT_APP_NAME, appName);
        activity.startActivity(intent);
        enterActivityAnim(activity);
    }

    public static void toSensorActivity(Activity activity) {
        if (activity == null) return;
        activity.startActivity(new Intent(activity, SensorActivity.class));
        enterActivityAnim(activity);
    }

    public static void toSdcardActivity(Activity activity) {
        if (activity == null) return;
        activity.startActivity(new Intent(activity, SdcardActivity.class));
        enterActivityAnim(activity);
    }

    public static void toColorFilterActivity(Activity activity) {
        if (activity == null) return;
        activity.startActivity(new Intent(activity, ColorFilterActivity.class));
        enterActivityAnim(activity);
    }

}
