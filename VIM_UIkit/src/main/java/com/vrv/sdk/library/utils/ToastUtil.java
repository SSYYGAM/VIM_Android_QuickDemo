package com.vrv.sdk.library.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    public static void showShort(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(Context context, int resID) {
        String msg = context.getString(resID);
        showShort(context, msg);
    }
}