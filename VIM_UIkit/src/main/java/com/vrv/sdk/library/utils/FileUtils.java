package com.vrv.sdk.library.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.vrv.sdk.library.ui.activity.PhotosPreviewActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 文件工具类
 */
public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    public static boolean isExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return file.exists();
    }

    public static boolean mkDirs(String dir) {
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            return dirFile.mkdirs();
        }
        return true;
    }

    public static boolean createFile(String filePath) {
        return createFile(false, filePath);
    }

    public static boolean createFile(boolean isForce, String filePath) {
        File file = new File(filePath);
        try {
            if (file.exists() && isForce) {
                file.delete();
            }
            if (!file.exists()) {
                return file.createNewFile();
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 打开或创建文件
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static File openOrCreateFile(String filePath) throws IOException {
        return openOrCreateFile(filePath, false);
    }

    public static File openOrCreateFile(String filePath, boolean isForce) throws IOException {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        String dir = filePath.substring(0, filePath.lastIndexOf(File.separator));
        if (!mkDirs(dir)) {
            Log.i(TAG, "目录不存在，创建失败：" + dir);
            return null;
        }
        if (!createFile(isForce, filePath)) {
            return null;
        }
        return new File(filePath);
    }

    public static void openFile(Context context, String filePath, String fileName) {
        Intent intent = openFileIntent(context, filePath);
        if (intent != null) {
            context.startActivity(openFileIntent(context, filePath));
        } else {
            //文件打开失败
            ToastUtil.showShort(context, String.format("无法打开%s文件", fileName));
        }
    }

    private static Intent openFileIntent(Context context, String filePath) {
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory())
            return null;
        if (!file.getName().contains(".")) {
            return getTextFileIntent(filePath, false);
        }
        /* 取得扩展名 */
        String end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length())
                .toLowerCase();
        /* 依扩展名的类型决定MimeType */
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf") || end.equals("ogg")
                || end.equals("wav") || end.equals("ra") || end.equals("ram") || end.equals("amr")) {
            return getAudioFileIntent(filePath);
        } else if (end.equals("3gp") || end.equals("mp4") || end.equals("mpg") || end.equals("3gpp")
                || end.equals("rm") || end.equals("mpeg") || end.equals("mwv") || end.equals("avi")
                || end.equals("mov")) {
            return getVideoFileIntent(filePath);
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("bmp")
                || end.equals("jpeg")) {
            return getImageFileIntent(context, filePath);
        } else if (end.equals("apk")) {
            return getApkFileIntent(filePath);
        } else if (end.equals("ppt")) {
            return getPptFileIntent(filePath);
        } else if (end.equals("xls") || end.equals("xlsx")) {
            return getExcelFileIntent(filePath);
        } else if (end.equals("doc") || end.equals("rtf") || end.equals("docx")) {
            return getWordFileIntent(filePath);
        } else if (end.equals("pdf")) {
            return getPdfFileIntent(filePath);
        } else if (end.equals("chm")) {
            return getChmFileIntent(filePath);
        } else if (end.equals("txt") || end.equals("log")) {
            return getTextFileIntent(filePath, false);
        } else {
            return getAllIntent(filePath);
        }
    }

    //Android获取一个用于打开APK文件的intent
    public static Intent getApkFileIntent(String param) {

        Intent intent = new Intent();
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }

    //Android获取一个用于打开VIDEO文件的intent
    public static Intent getVideoFileIntent(String param) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    //Android获取一个用于打开AUDIO文件的intent
    public static Intent getAudioFileIntent(String param) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    //Android获取一个用于打开Html文件的intent
    public static Intent getHtmlFileIntent(String param) {

        Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content")
                .encodedPath(param).build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    //Android获取一个用于打开图片文件的intent
    /*public static Intent getImageFileIntent(String param) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "image*//*");
        return intent;
    }*/


    //Android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(Context context, String param) {
        ArrayList<String> pathList = new ArrayList<>();
        pathList.add(param);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return PhotosPreviewActivity.startIntent(context, pathList);
    }

    //Android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent(String param) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory("android.intent.category.DEFAULT");
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    //Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(String param) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    //Android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(String param) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory("android.intent.category.DEFAULT");
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    //Android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(String param) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory("android.intent.category.DEFAULT");
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    //Android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(String param, boolean paramBoolean) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory("android.intent.category.DEFAULT");
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean) {
            Uri uri1 = Uri.parse(param);
            intent.setDataAndType(uri1, "text/plain");
        } else {
            Uri uri2 = Uri.fromFile(new File(param));
            intent.setDataAndType(uri2, "text/plain");
        }
        return intent;
    }

    //Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(String param) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory("android.intent.category.DEFAULT");
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    //Android获取一个用于打开APK文件的intent
    public static Intent getAllIntent(String param) {
        Intent intent = new Intent();
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "*/*");
        return intent;
    }

}
