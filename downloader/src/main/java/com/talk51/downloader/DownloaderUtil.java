package com.talk51.downloader;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.net.URLEncoder;

/**
 * 下载的工具类
 */
public class DownloaderUtil {

    /**
     * 获取保存下载文件的文件夹，路径为/data/data/Package Name/files/uid_download
     * 主要是分用户存
     * @param uid
     * @return
     */
    public static String getSaveDir(Context context, String uid) {
        if (context == null) {
            return null;
        }
        if (TextUtils.isEmpty(uid)) {
            uid = "51talk";
        }

        uid += "_download";

        File file = context.getFilesDir();
        if (file == null) {
            return null;
        }
        String savePath = file.getAbsolutePath() + File.separator + uid;
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdir();//创建文件夹
        }
        return savePath;

    }


    /**
     * 据Url计算保存文件的名称，不能直接截取名字，因为可能存在：名字相同，但是上级目录不同
     * @param url
     * @return
     */
    public static String getSaveName(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        return URLEncoder.encode(url);
    }

    /**
     * 据Url获取文件名称
     * @param url
     * @return
     */
    public static String getFileName(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        int start = url.lastIndexOf("/") + 1;
        int end = url.length();
        if (start < end && start >= 0) {
            return url.substring(start, end);
        }
        return "";

    }

    /**
     * 返回文件的类型
     * @param url
     * @return
     */
    public static String getFileType(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        int start = url.lastIndexOf(".");
        int end = url.length();
        if (start < end && start >= 0) {
            return url.substring(start, end);
        }
        return null;
    }


}
