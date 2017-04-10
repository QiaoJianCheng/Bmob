package com.visionvera.bmob.utils;

/**
 * Created by Qiao on 2016/12/20.
 */

public class TextUtil {
    /**
     * 字符串不为null，并且 trim 后不为 ""
     *
     * @param string
     * @return
     */
    public static boolean isEmpty(String string) {
        return string == null || string.trim().length() == 0;
    }

    /**
     * 字符串相同，任意 null 视为不相同
     *
     * @param string1
     * @param string2
     * @return
     */
    public static boolean equals(String string1, String string2) {
        return string1 != null && string2 != null && string1.equals(string2);
    }

    /**
     * 字符串 trim 后相同，任意 null 视为不相同
     *
     * @param string1
     * @param string2
     * @return
     */
    public static boolean equalsTrim(String string1, String string2) {
        return string1 != null && string2 != null && string1.trim().equals(string2.trim());
    }

    /**
     * 简单校验手机号 13, 15, 17, 18 开头
     *
     * @param number
     * @return
     */
    public static boolean isMobileNumber(String number) {
        return number != null && number.trim().matches("[1][3578]\\d{9}");
    }

    /**
     * 不含非法字符，只含有 A-Z, a-z, 0-9, _
     *
     * @param string
     * @param min    至少几位
     * @param max    最多几位
     * @return
     */
    public static boolean isValidateChars(String string, int min, int max) {
        return string != null && string.matches("[\\w]{" + min + "," + max + "}");
    }

    /**
     * 至少一位，不含非法字符，只含有 A-Z, a-z, 0-9, _
     *
     * @param string
     * @return
     */
    public static boolean isValidateChars(String string) {
        return string != null && string.matches("[\\w]+");
    }

    public static boolean isValidateHost(String ipPort) {
        return ipPort != null && ipPort.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d{1,5}");
    }

}
