package com.qiao.bmob.utils;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.qiao.bmob.global.App;

/**
 * Created by Qiao on 2016/12/16.
 */

public class ResUtil {
    public static Resources getResources() {
        return App.getContext().getResources();
    }

    public static String getString(int resId) {
        return getResources().getString(resId);
    }

    public static int getColor(int resId) {
        return getResources().getColor(resId);
    }

    public static float getDimen(int resId) {
        return getResources().getDimension(resId);
    }

    public static Drawable getDrawable(int resId) {
        return getResources().getDrawable(resId);
    }
}
