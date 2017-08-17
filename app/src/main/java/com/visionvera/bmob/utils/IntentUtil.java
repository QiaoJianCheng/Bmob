package com.visionvera.bmob.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.visionvera.bmob.R;
import com.visionvera.bmob.activity.HomeActivity;
import com.visionvera.bmob.activity.PublishActivity;
import com.visionvera.bmob.activity.app.CrashDetailActivity;
import com.visionvera.bmob.activity.app.CrashListlActivity;
import com.visionvera.bmob.activity.plan.ColorFilterActivity;
import com.visionvera.bmob.activity.plan.SdcardActivity;
import com.visionvera.bmob.activity.plan.SensorActivity;
import com.visionvera.bmob.activity.plan.camera.CameraActivity;
import com.visionvera.bmob.activity.plan.codec.CodecActivity;
import com.visionvera.bmob.activity.user.LoginActivity;
import com.visionvera.bmob.activity.user.MoodActivity;
import com.visionvera.bmob.activity.user.RegisterActivity;
import com.visionvera.bmob.model.CrashesBean;

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

    public static void toHomeActivity(Activity activity) {
        if (activity == null) return;
        Intent intent = new Intent(activity, HomeActivity.class);
        activity.startActivity(intent);
        enterActivityAnim(activity);
    }

    public static void toLoginActivity(Activity activity, View view) {
        if (activity == null) return;
//        activity.startActivity(new Intent(activity, LoginActivity.class), ActivityOptions.makeSceneTransitionAnimation(activity, view, "image").toBundle());
        activity.startActivity(new Intent(activity, LoginActivity.class));
        enterActivityAnim(activity);
    }

    public static void toRegisterActivity(Activity activity) {
        if (activity == null) return;
        activity.startActivity(new Intent(activity, RegisterActivity.class));
        enterActivityAnim(activity);
    }

    public static void toAppDetailActivity(Activity activity, String appId, String appName) {
        if (activity == null) return;
        Intent intent = new Intent(activity, CrashListlActivity.class);
        intent.putExtra(CrashListlActivity.INTENT_APP_ID, appId);
        intent.putExtra(CrashListlActivity.INTENT_APP_NAME, appName);
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

    public static void toCrashDetailActivity(Activity activity, String title, CrashesBean.CrashBean bean) {
        if (activity == null) return;
        Intent intent = new Intent(activity, CrashDetailActivity.class);
        intent.putExtra(CrashDetailActivity.INTENT_TITLE, title);
        intent.putExtra(CrashDetailActivity.INTENT_DETAIL, bean);
        activity.startActivity(intent);
        enterActivityAnim(activity);
    }

    public static void toCameraActivity(Activity activity) {
        if (activity == null) return;
        activity.startActivity(new Intent(activity, CameraActivity.class));
        enterActivityAnim(activity);
    }

    public static void toCodecActivity(Activity activity) {
        if (activity == null) return;
        activity.startActivity(new Intent(activity, CodecActivity.class));
        enterActivityAnim(activity);
    }

    public static void toMoodActivity(Activity activity, String name, String objectId) {
        if (activity == null) return;
        Intent intent = new Intent(activity, MoodActivity.class);
        intent.putExtra(MoodActivity.INTENT_NAME, name);
        intent.putExtra(MoodActivity.INTENT_ID, objectId);
        activity.startActivity(intent);
        enterActivityAnim(activity);
    }

    public static void toPublishActivity(Activity activity) {
        if (activity == null) return;
        activity.startActivity(new Intent(activity, PublishActivity.class));
        enterActivityAnim(activity);
    }

}
