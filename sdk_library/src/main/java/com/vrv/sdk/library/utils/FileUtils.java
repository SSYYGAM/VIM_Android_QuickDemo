package com.vrv.sdk.library.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * 文件工具类
 * Created by Yang on 2015/8/27 027.
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

}
