package com.talk51.downloader;

import com.talk51.downloader.real.db.DownloadStateTask;

import java.util.List;


/**
 * 下载接口
 */
public interface Downloader {

    /**
     * 开始下载
     * @param url
     */
    void start(String url, String name, String fileType, String extra);

    /**
     * 设置下载回调
     * @param listener
     */
    void addListener(DownloadStateTask.Listener listener);

    /**
     * 删除回调
     * @param listener
     */
    void removeListener(DownloadStateTask.Listener listener);


    /**
     * 暂停下载
     * @param url
     */
    void pause(String url);


    /**
     * 删除下载，本地数据库中也要删除
     * @param url
     */
    void remove(String url);

    /**
     * 查询下载状态
     * @param url
     * @return
     */
    DownloadStateTask getTask(String url);


    /**
     * 开始所有任务
     */
    void startAll();

    /**
     * 暂停所有任务
     */
    void pauseAll();

    /**
     * 删除所有任务
     */
    void removeAll();


    /**
     * 获取全部的任务
     * @return
     */
    List<DownloadStateTask> getAllTasks();

    /**
     * 下载的文件类型
     * @param url
     * @return
     */
    String getFileType(String url);

    /**
     * 设置配置
     */
    void setConfig(DownloadConf conf);


}
