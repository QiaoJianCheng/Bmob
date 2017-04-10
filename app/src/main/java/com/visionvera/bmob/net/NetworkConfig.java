package com.visionvera.bmob.net;

import com.visionvera.bmob.utils.SharedPrefUtil;

/**
 * Created by Qiao on 2017/3/3.
 */

public class NetworkConfig {
    private static final String KEY_HOST = "KEY_HOST";
    //        static String HOST_DEFAULT = "192.168.10.44:8080"; // 测试部
    private static String HOST_DEFAULT = "https://api.bmob.cn/";

    public static void saveHost(String host) {
        SharedPrefUtil.putString(KEY_HOST, host);
        NetworkClient.reset();
    }

    public static String getHost() {
        return SharedPrefUtil.getString(KEY_HOST, HOST_DEFAULT);
    }

    public static String getBaseUrl() {
        return getHost();
    }
}
