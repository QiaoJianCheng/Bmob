package com.visionvera.bmob.utils;

import android.widget.Toast;

import com.visionvera.bmob.global.App;

/**
 * Created by Qiao on 2016/12/16.
 */

public class ToastUtil {
    private static Toast toast;

    /**
     * 强大的吐司，能够连续弹的吐司
     *
     * @param text
     */
    public static void showToast(String text) {
        if (toast == null) {
            toast = Toast.makeText(App.getContext(), text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.show();
    }

    public static void networkFailure(String msg) {
        showToast(msg);
    }
}
