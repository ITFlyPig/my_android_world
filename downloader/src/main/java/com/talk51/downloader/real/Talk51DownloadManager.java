package com.talk51.downloader.real;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.talk51.downloader.real.db.DBTaskManager;
import com.talk51.downloader.real.download.FileDownloader;
import com.talk51.downloader.real.listener.DownloadListener;
import com.talk51.downloader.real.listener.NotifyListener;

import java.util.List;

public class Talk51DownloadManager {
    private FileDownloader fileDownloader;//文件下载
    private NotifyListener notifyListener;//通知管理
    private DBTaskManager dbTaskManager;  //数据库管理

    private Talk51DownloadManager() {
        fileDownloader = new FileDownloader(5);//实际的下载实现
        notifyListener = NotifyListener.getInstance();
    }


    private static class Holder {
        private static Talk51DownloadManager downloadManager = new Talk51DownloadManager();

    }

    public static Talk51DownloadManager getInstance() {
        return Holder.downloadManager;
    }

    public DBTaskManager getDbTaskManager() {
        return dbTaskManager;
    }

    public FileDownloader getFileDownloader() {
        return fileDownloader;
    }

    /**
     * 创建或者更新数据库
     *
     * @param context
     * @param uid
     */
    public void updateDB(Context context, String uid) {
        if (dbTaskManager == null) {
            fileDownloader.clearTasks();

            dbTaskManager = new DBTaskManager(context, uid);
            Log.d("updateDB", "dbTaskManager为空，创建数据库：" + uid);

            fileDownloader.setTasks(getTasksFromDB());//查询存在数据库的任务
        } else {
            if (!TextUtils.equals(uid, dbTaskManager.uid)) {
                fileDownloader.clearTasks();
                dbTaskManager = new DBTaskManager(context, uid);
                Log.d("updateDB", "dbTaskManager不为空，创建数据库：" + uid);

                fileDownloader.setTasks(getTasksFromDB());
            }

        }

    }

    /**
     * 当切换数据库的时候
     */
    private void onChangeDB() {
        fileDownloader.getTasks().clear();

    }


    /**
     * 从数据库获取
     * @return
     */
    private List<DownloadTask> getTasksFromDB() {
        List<DownloadTask> tasks = dbTaskManager.getAll();
        if (tasks == null) {
            return tasks;
        }
        for (DownloadTask task : tasks) {
            if (task.state != Status.DONE) {
                task.state = Status.PAUSE;
            }
        }
        return tasks;

    }

    /**
     * 开始任务
     *
     * @param task
     */
    public void start(DownloadTask task) {
        if (task == null) {
            return;
        }
        task.belongUid = dbTaskManager.uid;
        fileDownloader.start(task);
    }

    /**
     * 删除任务
     *
     * @param task
     */
    public void remove(DownloadTask task) {
        fileDownloader.remove(task);
    }

    public void removeAll(List<DownloadTask> removeTasks) {
        fileDownloader.removeAll(removeTasks);
    }

    /**
     * 停止任务
     *
     * @param task
     */
    public void stop(DownloadTask task) {
        fileDownloader.stop(task);
    }


    /**
     * 添加回调
     *
     * @param listener
     */
    public void addCallBack(DownloadListener listener) {
        notifyListener.addCallBack(listener);

    }

    /**
     * 删除回调
     *
     * @param listener
     */
    public void removeCallBack(DownloadListener listener) {
        notifyListener.removeCallBack(listener);

    }

    /**
     * 获取所有的下载任务
     *
     * @return
     */
    public List<DownloadTask> getAllTasks() {
        return fileDownloader.getTasks();
    }

    public DownloadTask getTask(String url) {
        return fileDownloader.getTask(url);
    }



}
