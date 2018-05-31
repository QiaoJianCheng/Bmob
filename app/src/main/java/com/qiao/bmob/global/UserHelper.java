package com.qiao.bmob.global;

import com.qiao.bmob.model.UsersBean;
import com.qiao.bmob.utils.SharedPrefUtil;

/**
 * Created by Qiao on 2017/3/17.
 */

public class UserHelper {
    private static final String KEY_USER_ID = "KEY_USER_ID";
    private static final String KEY_USER_NAME = "KEY_USER_NAME";
    private static final String KEY_USER_TOKEN = "KEY_USER_TOKEN";
    private static final String KEY_PHONE = "KEY_PHONE";
    private static final String KEY_GENDER = "KEY_GENDER";
    private static final String KEY_SIGNATURE = "KEY_SIGNATURE";
    private static final String KEY_IS_LOGIN = "KEY_IS_LOGIN";
    private static final String KEY_PASSWORD = "KEY_PASSWORD";

    public static void saveUserId(String userId) {
        SharedPrefUtil.putString(KEY_USER_ID, userId);
    }

    public static String getUserId() {
        return SharedPrefUtil.getString(KEY_USER_ID);
    }

    public static void saveUserName(String username) {
        SharedPrefUtil.putString(KEY_USER_NAME, username);
    }

    public static String getUserName() {
        return SharedPrefUtil.getString(KEY_USER_NAME);
    }

    public static void savePhone(String phone) {
        SharedPrefUtil.putString(KEY_PHONE, phone);
    }

    public static String getPhone() {
        return SharedPrefUtil.getString(KEY_PHONE);
    }

    public static void saveGender(int gender) {
        SharedPrefUtil.putInt(KEY_GENDER, gender);
    }

    public static int getGender() {
        return SharedPrefUtil.getInt(KEY_GENDER, Constants.GENDER_MALE);
    }

    public static void saveSignature(String signature) {
        SharedPrefUtil.putString(KEY_SIGNATURE, signature);
    }

    public static String getSignature() {
        return SharedPrefUtil.getString(KEY_SIGNATURE);
    }

    public static void saveUserToken(String sessionToken) {
        SharedPrefUtil.putString(KEY_USER_TOKEN, sessionToken);
    }

    public static String getUserToken() {
        return SharedPrefUtil.getString(KEY_USER_TOKEN);
    }

    public static void saveIsLogin(boolean isLogin) {
        SharedPrefUtil.putBoolean(KEY_IS_LOGIN, isLogin);
    }

    public static boolean isLogin() {
        return SharedPrefUtil.getBoolean(KEY_IS_LOGIN, false);
    }


    public static String getPassword() {
        return SharedPrefUtil.getString(KEY_PASSWORD);
    }

    public static void savePassword(String password) {
        SharedPrefUtil.putString(KEY_PASSWORD, password);
    }

    public static void saveUser(UsersBean.UserBean userBean) {
        if (userBean == null) return;
        saveUserId(userBean.objectId);
        saveUserName(userBean.username);
        saveGender(userBean.gender);
        savePhone(userBean.mobilePhoneNumber);
        saveSignature(userBean.signature);
        saveUserToken(userBean.sessionToken);
    }

}
