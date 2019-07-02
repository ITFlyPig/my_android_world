package com.talk51.downloader.real.download;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 获取下载实现
 */
public class DownloadClient {
    public OkHttpClient client;

    private static class Holder {
        private static DownloadClient client = new DownloadClient();

    }

    public static DownloadClient getInstance() {
        return Holder.client;
    }

    private DownloadClient() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build();

    }

}
