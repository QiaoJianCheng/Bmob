package com.visionvera.bmob.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.visionvera.bmob.global.App;

/**
 * Created by Qiao on 2016/12/16.
 */

public class SharedPrefUtil {
    private static SharedPreferences mSp;

    private static SharedPreferences getSharedPreferences() {
        if (mSp == null) {
            mSp = App.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return mSp;
    }

    public static void putBoolean(String key, boolean value) {
        getSharedPreferences().edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return getSharedPreferences().getBoolean(key, defValue);
    }

    public static void putString(String key, String value) {
        getSharedPreferences().edit().putString(key, value).apply();
    }

    public static String getString(String key, String defValue) {
        return getSharedPreferences().getString(key, defValue);
    }

    public static String getString(String key) {
        return getSharedPreferences().getString(key, null);
    }

    public static void putInt(String key, int value) {
        getSharedPreferences().edit().putInt(key, value).apply();
    }

    public static int getInt(String key, int defValue) {
        return getSharedPreferences().getInt(key, defValue);
    }

    public static void remove(String key) {
        getSharedPreferences().edit().remove(key).apply();
    }
}
