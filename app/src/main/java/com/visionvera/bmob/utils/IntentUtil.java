package com.visionvera.bmob.utils;

import android.app.Activity;
import android.content.Intent;

import com.visionvera.bmob.R;
import com.visionvera.bmob.activity.MainActivity;
import com.visionvera.bmob.activity.RegisterActivity;

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
        activity.startActivity(new Intent(activity, MainActivity.class));
        enterActivityAnim(activity);
    }

    public static void toRegisterActivity(Activity activity) {
        if (activity == null) return;
        activity.startActivity(new Intent(activity, RegisterActivity.class));
        enterActivityAnim(activity);
    }
}
