package com.talk51.downloader.real.listener;


import com.talk51.downloader.real.DownloadTask;

public interface DownloadListener {
    void onStart(DownloadTask task);

    void onCancel(DownloadTask task);

    void onError(DownloadTask task);

    void onWait(DownloadTask task);

    void onDownloading(DownloadTask task);

    void onDone(DownloadTask task);

    void onRemove(DownloadTask task);
}
