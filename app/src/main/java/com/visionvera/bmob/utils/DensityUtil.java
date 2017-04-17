package com.visionvera.bmob.utils;

import android.content.res.Resources;

/**
 * @author Qiao
 */
public class DensityUtil {

    /* 根据手机的分辨率从 dp 的单位 转成为 px(像素) */
    public static int dip2px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getDensity() {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (scale + 0.5f);
    }

    public static int sp2px(int spValue) {
        final float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (int) ((spValue - 0.5f) * fontScale);
    }

    /* 获取手机的状态栏的高度 */
    public static int getStatusBarHeight() {
        return Resources.getSystem().getDimensionPixelSize(
                Resources.getSystem().getIdentifier("status_bar_height",
                        "dimen", "android"));
    }

    /* 获取手机的导航栏的高度 */
    public static int getNavigationBarHeight() {
        return Resources.getSystem().getDimensionPixelSize(
                Resources.getSystem().getIdentifier("navigation_bar_height",
                        "dimen", "android"));
    }

    /* 获取手机屏幕的宽度 */
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /* 获取手机屏幕的高度 */
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
