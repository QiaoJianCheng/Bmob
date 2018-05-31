package com.qiao.bmob.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Qiao on 2017/4/13.
 */

public class DateFormatUtil {
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static SimpleDateFormat mDurationFormat = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
    private static SimpleDateFormat mLocalDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private static TimeZone GMT8 = TimeZone.getTimeZone("GMT+08:00");

    public static String getFormattedDuration(long duration) {
        mDurationFormat.setTimeZone(UTC);
        Calendar calendar = Calendar.getInstance(UTC);
        calendar.setTimeInMillis(duration);
        return mDurationFormat.format(calendar.getTime());
    }

    public static long currentTimestamp() {
        Calendar calendar = Calendar.getInstance(UTC);
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.getTimeInMillis();
    }

    public static String formatLocalDate(String standardDate) {
        try {
            mLocalDateFormat.setTimeZone(GMT8);
            Date date = mLocalDateFormat.parse(standardDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            Calendar nowCalendar = Calendar.getInstance();
            nowCalendar.setTimeInMillis(System.currentTimeMillis());
            int nowYear = nowCalendar.get(Calendar.YEAR);
            int nowDayOfYear = nowCalendar.get(Calendar.DAY_OF_YEAR);
            int nowHour = nowCalendar.get(Calendar.HOUR_OF_DAY);
            int nowMinute = nowCalendar.get(Calendar.MINUTE);

            if (year < nowYear) {
                return String.format(Locale.CHINA, "%d 年前", nowYear - year);
            }
            if (nowDayOfYear - dayOfYear > 31) {
                return String.format(Locale.CHINA, "%d 个月前", (nowDayOfYear - dayOfYear) / 31);
            }
            if (nowDayOfYear - dayOfYear > 2) {
                return String.format(Locale.CHINA, "%d 天前", nowDayOfYear - dayOfYear);
            }
            if (nowDayOfYear - dayOfYear > 1) {
                return String.format(Locale.CHINA, "前天 %s:%s", hour < 10 ? "0" + hour : hour, minute < 10 ? "0" + minute : minute);
            }
            if (nowDayOfYear - dayOfYear > 0) {
                return String.format(Locale.CHINA, "昨天 %s:%s", hour < 10 ? "0" + hour : hour, minute < 10 ? "0" + minute : minute);
            }
            if (nowHour - hour > 0) {
                return String.format(Locale.CHINA, "今天 %s:%s", hour < 10 ? "0" + hour : hour, minute < 10 ? "0" + minute : minute);
            }
            if (nowMinute - minute > 0) {
                return String.format(Locale.CHINA, "%d 分钟前", nowMinute - minute);
            }
            return "刚刚";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
