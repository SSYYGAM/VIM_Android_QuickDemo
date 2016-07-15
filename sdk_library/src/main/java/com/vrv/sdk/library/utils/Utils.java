package com.vrv.sdk.library.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.vrv.sdk.library.R;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

/**
 * Created by Yang on 2015/8/20 020.
 */
public class Utils {

    /**
     * 加载头像，
     *
     * @param context
     * @param path
     * @param headView
     */
    public static void loadHead(Context context, String path, ImageView headView, int def) {
        if (TextUtils.isEmpty(path) || !path.contains("/")) {
            ImageUtil.loadDefaultHead(context.getApplicationContext(), headView, def);
            return;
        }
        ImageUtil.loadViewLocalHead(context.getApplicationContext(), path, headView, def);
    }

    /**
     * 检查外部存储SD是否存在
     *
     * @return
     */
    public static boolean isSDExist(Context context) {
        String SDState = Environment.getExternalStorageState();
        if (!SDState.equals(Environment.MEDIA_MOUNTED)) {
            ToastUtil.showShort(context, R.string.vim_lost_SD);
            return false;
        }
        return true;
    }

    /**
     * 弹出键盘
     *
     * @param context
     */
    public static void showSoftInput(Context context, View view) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view == null) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            imm.showSoftInput(view, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 隐藏键盘
     *
     * @param context
     * @param view
     */
    public static void hideSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view == null) {
            imm.toggleSoftInput(0, InputMethodManager.RESULT_HIDDEN);
        } else {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
        }
    }

    /**
     * 判断输入法是否显示
     *
     * @param context
     * @return
     */
    public static boolean getSoftInputState(Activity context) {
        return context.getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static Drawable getDrawable(Context context, int drawRes) {
        return ContextCompat.getDrawable(context, drawRes);
    }

    /**
     * recyclerView divider
     *
     * @param context
     * @return
     */
    public static RecyclerView.ItemDecoration buildDividerItemDecoration(Context context) {
        return buildDividerItemDecoration(context, 0);
    }

    public static RecyclerView.ItemDecoration buildDividerItemDecoration(Context context, int colorID) {
        HorizontalDividerItemDecoration.Builder builder = new HorizontalDividerItemDecoration.Builder(context);
        if (colorID != 0) {
            builder.drawable(new ColorDrawable(Color.TRANSPARENT));
        } else {
            builder.drawable(new ColorDrawable(context.getResources().getColor(R.color.vim_divider)));
        }
        builder.size(2);
        return builder.build();
    }
}
