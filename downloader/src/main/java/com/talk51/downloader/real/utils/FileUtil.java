package com.talk51.downloader.real.utils;

import android.text.TextUtils;

import java.io.File;

public class FileUtil {
    /**
     * 删除文件
     * @param path
     */
    public static void delFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
    }

    /**
     * 删除文件
     * @param f
     */
    public static void delFile(File f) {
        if (f == null) {
            return;
        }
        if (f.exists()) {
            f.delete();
        }
    }


}
