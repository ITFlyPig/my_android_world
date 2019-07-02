package com.talk51.downloader.real.download;

import com.talk51.downloader.real.DownloadTask;

import java.util.List;

public class FileDownloader {
    /**
     * 委托给DownloadQueue实现具体逻辑
     */
    private DownloadQueue downloadQueue;

    public FileDownloader(int maxParalle) {
        downloadQueue = new DownloadQueue(maxParalle);
    }



    /**
     * 开始下载
     * @param task
     */
    public void start(final DownloadTask task) {
//        downloadQueue.threadPool.execute(new Runnable() {
//            @Override
//            public void run() {
//                downloadQueue.startTask(task);
//            }
//        });
        downloadQueue.startTask(task);
    }

    /**
     * 停止
     * @param task
     */
    public void stop(final DownloadTask task) {
//        downloadQueue.threadPool.execute(new Runnable() {
//            @Override
//            public void run() {
//                downloadQueue.stop(task);
//            }
//        });

        downloadQueue.stop(task);

    }

    /**
     * 删除
     * @param task
     */
    public void remove(final DownloadTask task) {
//        downloadQueue.threadPool.execute(new Runnable() {
//            @Override
//            public void run() {
//                downloadQueue.remove(task);
//            }
//        });
        downloadQueue.remove(task);

    }


    public void updateTaskQueue(DownloadTask task) {
        downloadQueue.updateTaskQueue(task);
    }

    public void removeAll(List<DownloadTask>  removeTasks) {
        downloadQueue.removeAll(removeTasks);
    }

    public void setTasks(List<DownloadTask> tasks) {
        downloadQueue.setTasks(tasks);
    }

    public List<DownloadTask> getTasks(){
        return downloadQueue.getTasks();
    }

    public DownloadTask getTask(String url) {
        return downloadQueue.getTask(url);
    }

    /**
     * 清除所有存在的下载任务，但是不删除数据库中的数据
     */
    public void clearTasks() {
        downloadQueue.clearData();
    }


}
