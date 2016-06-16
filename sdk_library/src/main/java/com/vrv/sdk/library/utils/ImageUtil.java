package com.vrv.sdk.library.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.vrv.imsdk.api.ConfigApi;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Yang on 15/08/07 007.
 */
public class ImageUtil {

    private static final String TAG = ImageUtil.class.getSimpleName();

    public static void loadViewLocal(Context context, String filePath, ImageView view) {
        Glide.with(context).load(new File(filePath)).into(view);
    }

    /**
     * 加载本地图片
     *
     * @param context
     * @param filePath
     * @param view
     * @param def
     */
    public static void loadViewLocal(final Context context, String filePath, final ImageView view, final int def) {

        File file = new File(filePath);
        if (file.exists()) {
            RequestManager glideRequest = Glide.with(context);
            DrawableTypeRequest drawableTypeRequest = glideRequest.load(file);
            drawableTypeRequest.listener(new RequestListener() {
                @Override
                public boolean onException(Exception e, Object o, Target target, boolean b) {
                    Glide.with(context).load(def).into(view);
                    return true;
                }

                @Override
                public boolean onResourceReady(Object o, Object o2, Target target, boolean b, boolean b1) {
                    return false;
                }
            });
            drawableTypeRequest.into(view);
        }else{
            Glide.with(context).load(def).into(view);
        }
    }
    public static void loadViewLocalIfExists(final Context context, String filePath, final ImageView view, final int def) {

        File file = new File(filePath);
        if (file.exists()) {
            RequestManager glideRequest = Glide.with(context);
            DrawableTypeRequest drawableTypeRequest = glideRequest.load(file);
            drawableTypeRequest.listener(new RequestListener() {
                @Override
                public boolean onException(Exception e, Object o, Target target, boolean b) {
                    Glide.with(context).load(def).into(view);
                    return true;
                }

                @Override
                public boolean onResourceReady(Object o, Object o2, Target target, boolean b, boolean b1) {
                    return false;
                }
            });
            drawableTypeRequest.into(view);
        }
    }

    /**
     * 加载本地图片,是加密过的
     *
     * @param context
     * @param key
     * @param filePath
     * @param view 需要加载到的imgeview
     * @param def 默认显示
     */
    public static void loadViewLocalWithEncrypt(Context context, String key, String filePath, ImageView view, int def) {
        if (TextUtils.isEmpty(filePath)) {
            Glide.with(context).load(def).into(view);
            return;
        }
        if (TextUtils.isEmpty(key) || !filePath.contains("/")) {
            Glide.with(context).load(new File(filePath)).error(def).into(view);
            return;
        }
        String decryptPath = ConfigApi.decryptFile(key,filePath);
        Glide.with(context).load(new File(decryptPath)).error(def).into(view);
    }

    /**
     * 加载本地圆角头像
     *
     * @param context
     * @param filePath
     * @param view
     * @param def
     */
    public static void loadViewLocalHead(final Context context, final String filePath, final ImageView view, final int def) {
        if (TextUtils.isEmpty(filePath)) {
            loadDefaultHead(context, view, def);
        } else {
            File file = new File(filePath);
            if (file.exists()) {
                RequestManager glideRequest = Glide.with(context);
                DrawableTypeRequest drawableTypeRequest = glideRequest.load(file);
                drawableTypeRequest.listener(new RequestListener() {
                    @Override
                    public boolean onException(Exception e, Object o, Target target, boolean b) {
                        loadDefaultHead(context, view, def);
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Object o, Object o2, Target target, boolean b, boolean b1) {
                        return false;
                    }
                });
                drawableTypeRequest.transform(new GlideUtils.GlideCircleTransform(context));
                drawableTypeRequest.into(view);
            } else {
                loadDefaultHead(context, view, def);
            }
        }
    }

    public static void loadDefaultHead(Context context, ImageView view, int def) {
        Glide.with(context).load(def).transform(new GlideUtils.GlideCircleTransform(context)).into(view);
    }

    /**
     * Glide 加载网络图片
     *
     * @param context
     * @param url
     * @param view
     */
    public static void loadViewSyc(Context context, String url, ImageView view) {
        if (TextUtils.isEmpty(url))
            return;
        Glide.with(context).load(url).into(view);
    }

    /**
     * 加载网络图片
     *
     * @param context
     * @param url
     * @param view
     * @param defaultRes 默认图片
     */
    public static void loadViewSyc(Context context, String url, ImageView view, int defaultRes) {
        if (TextUtils.isEmpty(url)) {
            view.setImageResource(defaultRes);
            return;
        }
        Glide.with(context).load(url).error(defaultRes).into(view);
    }

    /**
     * 设置图片圆角
     *
     * @param context
     * @param view
     * @param defaultRes
     */
    public static void setImageRound(Context context, ImageView view, int defaultRes) {
        Glide.with(context).load(defaultRes).transform(new GlideUtils.GlideRoundTransform(context, 4)).into(view);
    }

    /**
     * 设置图片圆角
     *
     * @param context
     * @param url
     * @param view
     * @param defaultRes
     */
    public static void setImageRoundSyc(Context context, String url, ImageView view, int defaultRes) {
        Glide.with(context).load(url).error(defaultRes).transform(new GlideUtils.GlideRoundTransform(context, 4)).into(view);
    }

    /**
     * 设置Icon离线变灰
     *
     * @param icon
     */
    public static void setIconOffline(ImageView icon) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.5f);
        ColorMatrixColorFilter matrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
        icon.setColorFilter(matrixColorFilter);

    }

    /**
     * 设置Icon在线
     *
     * @param icon
     */
    public static void setIconOnline(ImageView icon) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(1);
        ColorMatrixColorFilter matrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
        icon.setColorFilter(matrixColorFilter);
    }

    /**
     *将bitmap 修改成圆形
     * @param bitmap
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = bitmap.getWidth() / 2;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 启动相机拍照
     *
     * @param activity
     * @param requestCode
     */
    public static String takePic(Activity activity, int requestCode) {
        if (!Utils.isSDExist(activity)) {
            return null;
        }
        String photoPath = ConfigApi.getCachePath() + "DICM/linkdood." + System.currentTimeMillis() + ".jpg";
        try {
            File photoFile = FileUtils.openOrCreateFile(photoPath);
            if (photoFile == null || !photoFile.exists())
                return null;
            // 将File对象转换为Uri并启动照相程序
            Uri imageUri = Uri.fromFile(photoFile);
            // "android.media.action.IMAGE_CAPTURE"
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 照相
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 指定图片输出地址
            activity.startActivityForResult(intent, requestCode); // 启动照相
        } catch (Exception e) {
            e.printStackTrace();
        }
        return photoPath;

    }
    /**
     * 将bitmap保存到指定路径
     */
    public static boolean saveBitmapToFile(Bitmap bitmap, String path) {
        if (bitmap == null) {
            return false;
        }
        BufferedOutputStream os = null;
        boolean save = false;
        try {
            File file = FileUtils.openOrCreateFile(path, true);
            os = new BufferedOutputStream(new FileOutputStream(file));
            save = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
        } catch (Exception e) {
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }
        return save;
    }

}
