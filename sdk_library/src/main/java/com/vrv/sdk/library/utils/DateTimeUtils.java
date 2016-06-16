package com.vrv.sdk.library.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateUtils;
import android.text.format.Time;

import com.vrv.sdk.library.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期时间格式化工具类
 * Created by Yang on 2015/8/13 013.
 */
public class DateTimeUtils {

    private static final String TAG = DateTimeUtils.class.getSimpleName();

    public static String format2YMD(long when) {
        SimpleDateFormat mouth = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return mouth.format(when);
    }

    public static String formatFullTime(Context context, long when) {
        return formatTime(context, when, true);
    }

    public static String formatBriefTime(Context context, long when) {
        return formatTime(context, when, false);
    }

    //    static String[] TimeInday = {"凌晨 ", "上午 ", "下午 ", "晚上 "};
    public static String formatTime(Context context, long when, boolean isFull) {
        String[] TimeInday = {context.getString(R.string.vim_day1), context.getString(R.string.vim_day2),
                context.getString(R.string.vim_day3), context.getString(R.string.vim_day4)};
        SimpleDateFormat format = new SimpleDateFormat(" HH:mm", Locale.getDefault());
        StringBuilder sb = new StringBuilder();
        Calendar oldDay = Calendar.getInstance();
        oldDay.setTimeInMillis(when);
        if (!DateUtils.isToday(when)) {
            long now = System.currentTimeMillis();
            Calendar curDay = Calendar.getInstance();
            curDay.setTimeInMillis(now);
            if (oldDay.get(Calendar.WEEK_OF_YEAR) == curDay.get(Calendar.WEEK_OF_YEAR)) {
                SimpleDateFormat week = new SimpleDateFormat("EE ", Locale.getDefault());
                sb.append(week.format(oldDay.getTime()));
            } else if (oldDay.get(Calendar.YEAR) == curDay.get(Calendar.YEAR)) {
                SimpleDateFormat mouth = new SimpleDateFormat(context.getString(R.string.vim_m_dd),
                        Locale.getDefault());
                sb.append(mouth.format(oldDay.getTime()));
            } else {
                SimpleDateFormat year = new SimpleDateFormat(context.getString(R.string.vim_yyyy_m_dd_),
                        Locale.getDefault());
                sb.append(year.format(oldDay.getTime()));
            }
            if (isFull) {
                sb.append(TimeInday[oldDay.get(Calendar.HOUR_OF_DAY) / 6]);
                sb.append(format.format(oldDay.getTime()));
            }
        } else {
            sb.append(TimeInday[oldDay.get(Calendar.HOUR_OF_DAY) / 6]);
            sb.append(format.format(oldDay.getTime()));
        }
        return sb.toString();
    }

    public static String setConversationTime(long when) {
        return formatTimeNew(when, false, false);
    }

    public static String setChatTime(long when) {
        return formatTimeNew(when, false, true);
    }

    /**
     * @param when   需要处理的时间
     * @param isFull 是否显示全格式 ？
     * @return
     */
    public static String formatTimeNew(long when, boolean isFull, boolean showTime) {
        if (isFull) {
            return timeStamp2Date(when, 7);
        } else {
            int TodayDiff = getTodayDiff(when);
            if (showTime) {
                if (TodayDiff == 0) {
                    return timeStamp2Date(when, 3);
                } else if (TodayDiff == 1) {
                    return "昨天" + " " + timeStamp2Date(when, 3);
                } else if (TodayDiff == 2) {
                    return "前天" + " " + timeStamp2Date(when, 3);
                } else {
                    return timeStamp2Date(when, 8);
                }
            } else {
                if (TodayDiff == 0) {
                    return timeStamp2Date(when, 3);
                } else if (TodayDiff == 1) {
                    return "昨天";
                } else if (TodayDiff == 2) {
                    return "前天";
                } else {
                    return timeStamp2Date(when, 9);
                }
            }
        }
    }


    public static String formatDateWeek(Context context, long when) {
        SimpleDateFormat mouth = new SimpleDateFormat(context.getString(R.string.vim_m_dd_ee),
                Locale.getDefault());
        return mouth.format(when);
    }

    public static String formatMonth(Context context, long when) {
        SimpleDateFormat mouth = new SimpleDateFormat(context.getString(R.string.vim_yyyy_m_),
                Locale.getDefault());
        return mouth.format(when);
    }

    /**
     * 转换为 yyyy-MM-dd HH:mm
     *
     * @param when
     * @return
     */
    public static String formatYMDHMS(long when) {
        SimpleDateFormat mouth = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return mouth.format(when);
    }

    public static String formatDateTime(long when, String format) {
        SimpleDateFormat mouth = new SimpleDateFormat(format, Locale.getDefault());
        return mouth.format(when);
    }

    /**
     * 日期转换为long
     *
     * @param date
     * @return
     */
    public static long date2ms(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date d = format.parse(date);
            return d.getTime();
        } catch (ParseException e) {
            // VrvLog.e(TAG, "date2ms parse failed!! \n date == " + date);
        }

        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date d = format.parse(date);
            return d.getTime();
        } catch (ParseException e) {
        }
        return -1;
    }

    public static long string2date(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = format.parse(str);
            /*date.setHours(0);
            date.setMinutes(0);
            date.setSeconds(0);*/
            return date.getTime();
        } catch (ParseException e) {
        }

        return 0;
    }

    public static String ms2String(long when) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(new Date(when));
    }

    /*public static long YMD2Date(YMD ymd) {
        Calendar cal = Calendar.getInstance();
        cal.set(ymd.getYear(), (ymd.getMonth() - 1), ymd.getDay());
        return cal.getTimeInMillis();
    }


    public static YMD Date2YMD(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        YMD ymd = new YMD();
        ymd.setYear((short) cal.get(Calendar.YEAR));
        ymd.setMonth((byte) (cal.get(Calendar.MONTH) + 1));
        ymd.setDay((byte) (cal.get(Calendar.DAY_OF_MONTH)));
        return ymd;
    }

    public static long YMD2Long(YMD ymd) {
        long date = ymd.year * 10000 + ymd.month * 100 + ymd.day;
        return date;
    }

    public static YMD Long2YMD(long date) {
        YMD ymd = new YMD();
        ymd.setYear((short) (date / 10000 % 10000));
        ymd.setMonth((byte) (date / 100 % 100));
        ymd.setDay((byte) (date % 100));
        return ymd;
    }*/


    /*
     * 获取当前系统时间字符串
	 */
    public static String getCurrentTimeString() {
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR); // 获取年份
        int month = ca.get(Calendar.MONTH); // 获取月份
        int day = ca.get(Calendar.DATE); // 获取日
        int hour = ca.get(Calendar.HOUR); // 获取小时
        int minute = ca.get(Calendar.MINUTE); // 获取分
        int second = ca.get(Calendar.SECOND); // 获取秒
        int millisecond = ca.get(Calendar.MILLISECOND); // 获取毫秒
        String date = "" + year + (month + 1) + day + hour + minute + second + millisecond;
        return date;
    }

    /**
     * 获取当前年份
     *
     * @return
     */
    public static int getCurrentYear() {
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        return year;
    }

    /**
     * 获取当前月份
     *
     * @return
     */
    public static int getCurrentMonth() {
        Calendar ca = Calendar.getInstance();
        int month = ca.get(Calendar.MONTH) + 1;
        return month;
    }

    /*
     * 转换long型为标准日期串
     */
    @SuppressLint("SimpleDateFormat")
    public static String getFormatDate(long date) {
        String dateStr = "";
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (0 == date) return dateStr;
        dateStr = sDateFormat.format(new Date(date));
        return dateStr;
    }

    /*
     * 获取过去pastCount天的日期串
     */
    @SuppressLint("SimpleDateFormat")
    public static String getPastDate(int pastCount) {
        Date date = new Date();// 取时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, pastCount);// 把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    /**
     * 将时间类型字符串转为long型
     * 时间格式yyyy-MM-dd hh:mm:ss
     *
     * @return
     */
    public static long getLongDate(String sDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date ret = sdf.parse(sDate);
            return ret.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取当前时间字符串类型
     * 时间格式yyyy-MM-dd hh:mm:ss
     *
     * @return
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ret = sdf.format(new Date());
        return ret;
    }

    /**
     * long 日期的格式化
     *
     * @param date
     * @return
     */
    public static String longParseDate(String pattern, long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    /**
     * 格式日期转换为Long（毫秒）
     * <nl>
     * </br><b> 日期必须和要转换的格式相同
     * <nl>
     * </br>例如：2014-05-26 02:15:33 -----yyyy-MM-DD hh:mm:ss
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static long dateParseLong(String dateStr, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
        }
        return date.getTime();
    }

    /**
     * 日期按格式转换 </br>注意: <b>dateStr 必须和 beforePattern 格式相同
     *
     * @param dateStr
     * @param beforePattern
     * @param afterPattern
     */
    public static String dateFormat(String dateStr, String beforePattern,
                                    String afterPattern) {
        long date = dateParseLong(dateStr, beforePattern);
        String afterDateStr = longParseDate(afterPattern, date);
        return afterDateStr;
    }

    /**
     * 获取年月日分秒
     *
     * @return
     */
    public static int[] getYMDHMS(long time) {
        int[] ymdhms = new int[6];
        Calendar ca = Calendar.getInstance();
        if (time >= 1000) {
            ca.setTimeInMillis(time);
        }
        ymdhms[0] = ca.get(Calendar.YEAR);
        ymdhms[1] = ca.get(Calendar.MONTH) + 1;
        ymdhms[2] = ca.get(Calendar.DAY_OF_MONTH);
        ymdhms[3] = ca.get(Calendar.HOUR_OF_DAY);
        ymdhms[4] = ca.get(Calendar.MINUTE);
        ymdhms[5] = ca.get(Calendar.SECOND);
        return ymdhms;
    }

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

    /**
     * @return 0为 相等  1为 昨天  依次类推
     */
    public static int getTodayDiff(long when) {
        Time time = new Time();
        time.set(when);

        int thenYear = time.year;
        int thenMonth = time.month;
        int thenMonthDay = time.monthDay;

        time.set(System.currentTimeMillis());
        if ((thenYear == time.year) && (thenMonth == time.month)) {
            return time.monthDay - thenMonthDay;
        }
        return -1;
    }

}
