package com.vrv.sdk.library.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.MaterialDialog;
import com.vrv.sdk.library.R;
import com.vrv.sdk.library.listener.OnDateTimeChangeListener;
import com.vrv.sdk.library.common.view.DateTimeView;

import java.util.Calendar;

public class DialogUtil {

    /**
     * 普通的进度提示框
     *
     * @param context
     * @param outSideTouchable
     */
    public static PopupWindow createProgressbarPop(Context context, boolean outSideTouchable) {
        View view = LayoutInflater.from(context).inflate(R.layout.vim_pop_progressbar, null);
        PopupWindow pop = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setOutsideTouchable(outSideTouchable);
        pop.setBackgroundDrawable(new ColorDrawable(0));
        pop.setFocusable(true);
//        pop.showAtLocation(v, Gravity.CENTER, 0, 0);
        return pop;
    }

    //progressDialog
    public static ProgressDialog createProgressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context, R.style.Vim_LoadDialogStyle);
        View view = View.inflate(context, R.layout.vim_pop_progressbar, null);
        dialog.setContentView(view);
        return dialog;
    }

    //请稍候progressDialog
    public static MaterialDialog buildProgressDialog(Context context) {
        return new MaterialDialog.Builder(context)
                .content(R.string.vim_wait)
                .progress(true, 0)
                .build();
    }

    public static MaterialDialog buildDialog(Context context) {
        return new MaterialDialog.Builder(context)
                .build();
    }

    public static MaterialDialog buildProgressDialogNoMsg(Context context) {
        return new MaterialDialog.Builder(context)
                .progress(true, 0)
                .build();
    }

    public static Dialog buildLoginDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.vim_pop_progressbar, null);
        Dialog dialog = new Dialog(context, R.style.Vim_LoadDialogStyle);
        dialog.setContentView(view);
        return dialog;
    }

    public static MaterialDialog buildTipsDialog(Context context, String msg) {
        return new MaterialDialog.Builder(context)
                .content(msg)
                .positiveText("ok")
                .build();
    }

    public static MaterialDialog buildInputDialog(Context context, String title, String hint, String preFill, MaterialDialog.InputCallback inputCallback, MaterialDialog.ButtonCallback buttonCallback) {
        return buildInputDialog(context, title, hint, preFill, 0, inputCallback, buttonCallback);
    }

    public static MaterialDialog buildInputDialog(Context context, String title, String hint, String preFill, int maxLength, MaterialDialog.InputCallback inputCallback, MaterialDialog.ButtonCallback buttonCallback) {
        if (inputCallback == null) {
            inputCallback = new MaterialDialog.InputCallback() {
                @Override
                public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {

                }
            };
        }
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(title);
        builder.inputType(InputType.TYPE_CLASS_TEXT /*| InputType.TYPE_TEXT_VARIATION_PASSWORD*/);
        builder.input(hint, preFill, inputCallback);
        if (maxLength > 0) {
            builder.inputMaxLength(maxLength);
        }
        builder.positiveText("ok");
        builder.callback(buttonCallback);
        return builder.build();
    }

    //操作选项dialog
    public static MaterialDialog buildOperateDialog(Context context, CharSequence[] items, MaterialDialog.ListCallback callback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.items(items);
        builder.itemsCallback(callback);
        MaterialDialog dialog = builder.build();
        return dialog;
    }

    //单选dialog
    public static MaterialDialog buildSingleChoiceDialog(Context context, String title, CharSequence[] items, int selectIndex, MaterialDialog.ListCallbackSingleChoice callback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.title(title);
        }
        builder.items(items);
        builder.itemsCallbackSingleChoice(selectIndex, callback);
        return builder.build();
    }

    //单选dialog
    public static MaterialDialog buildSingleChoiceDialog(Context context, String title, int itemsId, int selectIndex, MaterialDialog.ListCallbackSingleChoice callback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.title(title);
        }
        builder.items(itemsId);
        builder.itemsCallbackSingleChoice(selectIndex, callback);
        return builder.build();
    }

    public static MaterialDialog buildDateDialog(Context context, long time, OnDateTimeChangeListener dateTimeChangeListener) {
        return buildDateTimeDialog(context, time, dateTimeChangeListener, false);
    }

    public static MaterialDialog buildTimeDialog(Context context, long time, OnDateTimeChangeListener dateTimeChangeListener) {
        return buildDateTimeDialog(context, time, dateTimeChangeListener, true);
    }

    public static MaterialDialog buildDateTimeDialog(Context context, long time, final OnDateTimeChangeListener dateTimeChangeListener, boolean showTime) {
        final MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        View customView = null;
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        final MaterialDialog.ButtonCallback buttonCallback = new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                dateTimeChangeListener.dateTimeChangeListener(calendar.getTimeInMillis());
            }
        };
        DatePicker.OnDateChangedListener dateChangedListener = new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth);
            }
        };
        if (showTime) {
            TimePicker.OnTimeChangedListener timeChangedListener = new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    calendar.add(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.add(Calendar.MINUTE, minute);
                }
            };
            customView = new DateTimeView(context, time, dateChangedListener, timeChangedListener);
        } else {
            customView = new DateTimeView(context, time, dateChangedListener);
        }
        builder.customView(customView, true);
        builder.positiveText("ok");
        builder.callback(buttonCallback);
        return builder.build();
    }

    /**
     * 默认取消按钮不做特殊操作
     *
     * @param context
     * @param title
     * @param content
     * @param buttonCallback
     */
    public static void buildSelectDialog(Context context, String title, String content, MaterialDialog.SingleButtonCallback buttonCallback) {
        buildSelectDialog(context, title, content, buttonCallback, null);
    }

    public static void buildSelectDialog(Context context, String title, String content, MaterialDialog.SingleButtonCallback buttonCallback, MaterialDialog.SingleButtonCallback cancelButtonCallback) {
        new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .positiveText("确定")
                .onPositive(buttonCallback)
                .negativeText("取消")
                .onNegative(cancelButtonCallback)
                .show();
    }
}
