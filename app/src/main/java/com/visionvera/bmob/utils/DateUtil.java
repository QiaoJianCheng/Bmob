package com.visionvera.bmob.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Qiao on 2017/4/13.
 */

public class DateUtil {
    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm:ss.SS", Locale.CHINA);

    public static String getTimeString(long time) {
        TimeZone utc = TimeZone.getTimeZone("UTC");
        sDateFormat.setTimeZone(utc);
        Calendar calendar = Calendar.getInstance(utc);
        calendar.setTimeInMillis(time);
        return sDateFormat.format(calendar.getTime());
    }
}
