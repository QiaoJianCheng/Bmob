package com.visionvera.bmob.utils;

import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.visionvera.bmob.R;
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
    public static void showToast(final String text) {
        showToast(text, true);
    }

    public static void warnToast(final String text) {
        showToast(text, true);
    }

    private static void showToast(final String text, final boolean warning) {
        App.post(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = new Toast(App.getContext());
                }
                TextView textView = new TextView(App.getContext());
                textView.setText(text);
                textView.setTextColor(ResUtil.getColor(R.color.colorWhite));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResUtil.getDimen(R.dimen.textSizeSmall));
                textView.setBackgroundResource(warning ? R.drawable.shape_toast_warning : R.drawable.shape_toast_normal);
                toast.setView(textView);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
