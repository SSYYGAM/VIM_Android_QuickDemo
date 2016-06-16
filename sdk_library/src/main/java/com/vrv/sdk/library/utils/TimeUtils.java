package com.vrv.sdk.library.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zxj on 2016/1/28.
 */
public class TimeUtils {
    /**
     * 时间戳转换成日期格式字符串
     *
     * @param seconds 精确到秒的字符串
     * @param type
     * @return
     */
    public static String timeStamp2Date(long seconds, int type) {
        String s = seconds + "";
        if (s.length() < 11)
            seconds = seconds * 1000;
        String format = null;
        switch (type) {
            case 1:
                format = "yyyy-MM-dd\nHH:mm:ss";
                break;
            case 2:
                format = "HH:mm:ss";
                break;
            case 3:
                format = "HH:mm";
                break;
            case 4:
                format = "yyyy/MM/dd";
                break;
            case 5:
                format = "MM-dd HH:mm";
                break;
            case 6:
                format = "mm:ss";
                break;
            case 7:
                format = "yyyy/MM/dd HH:mm:ss";
                break;
            case 8:
                format = "MM/dd HH:mm";
                break;
            case 9:
                format = "MM/dd";
                break;
            default:
                format = "yyyy-MM-dd HH:mm:ss";
                break;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(seconds));
    }
}
