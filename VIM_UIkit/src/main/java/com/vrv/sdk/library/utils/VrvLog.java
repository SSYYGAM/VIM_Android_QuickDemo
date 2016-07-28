package com.vrv.sdk.library.utils;

import android.util.Log;

/**
 * 统一日志类
 */
public class VrvLog {

    private static final String TAG = "VrvLog";
    private static final boolean DEBUG = true;

    public static void i(String msg) {
        if (DEBUG) {
            Log.i(TAG, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void d(String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void e(String msg) {
        if (DEBUG) {
            Log.e(TAG, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        }
    }
}

