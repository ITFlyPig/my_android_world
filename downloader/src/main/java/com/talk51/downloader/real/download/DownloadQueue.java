package com.talk51.downloader.real.download;

import android.text.TextUtils;
import android.util.Log;


import com.talk51.downloader.real.DownloadTask;
import com.talk51.downloader.real.Status;
import com.talk51.downloader.real.listener.NotifyListener;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 下载队列和下载状态的管理
 */
public class DownloadQueue {

    public Executor threadPool = Executors.newCachedThreadPool();


    private LinkedList<DownloadTask> tasks = new LinkedList<>();
    private LinkedList<DownloadTask> running = new LinkedList<>();
//    private LinkedList<DownloadTask> done = new LinkedList<>();
    private ReentrantLock lock = new ReentrantLock();

    private int maxParalle;
    private volatile boolean stop;


    public DownloadQueue(int maxParalle) {
        this.maxParalle = maxParalle;

    }


    /**
     * 检查是否有可下载的任务
     */
    private void checkTask() {
        lock.lock();
        Log.d("gg", Thread.currentThread().getName() + "checkTask 获取锁");
        try {

            Iterator<DownloadTask> it = running.iterator();
            while (it.hasNext()) {
                DownloadTask task = it.next();
                if (task.state == Status.SHOULD_DOWNLOAD) {
                    task.state = Status.DOWNLOADING;
                    task.stop = false;
                    //开始真正的下载
                    threadPool.execute(new DTask(task));
                    //通知Listener开始下载
                    NotifyListener.getInstance().onStart(task);
                }
            }

//            if (running.size() > maxParalle || tasks.size() == 0) {//挂起
//                return;
//            }
//            //从队列中取出任务执行
//            if (running.size() <= maxParalle) {
//                Iterator<DownloadTask> it = tasks.iterator();
//                while (it.hasNext()) {
//                    DownloadTask task = it.next();
//                    Log.d("mm", task.getShowFileName() + "是否是SHOULD_DOWNLOAD状态：" + (task.state == Status.SHOULD_DOWNLOAD));
//                    if (task.state == Status.SHOULD_DOWNLOAD) {
//                        task.state = Status.DOWNLOADING;
//                        task.stop = false;
//                        if (!running.contains(task)) {
//                            running.add(task);
//                        }
//
//                    }
//
//                }
//            }
        } finally {
            lock.unlock();
            Log.d("gg", Thread.currentThread().getName() + "checkTask 释放锁");
        }

    }


    /**
     * 添加任务
     *
     * @param task
     */
    public void startTask(DownloadTask task) {
        lock.lock();
        Log.d("gg", Thread.currentThread().getName() + "startTask 获取锁");
        try {
            if (task == null) {
                return;
            }
            boolean contains = tasks.contains(task);
            if (!contains) {//之前不存在
                task.date = System.currentTimeMillis();
            }

            task.onCancelListener = onCancelListener;
            if (running.contains(task)) {//已经在下载队列

            } else if (!contains || (contains && task.state != Status.DONE)) {//没在下载队列，且没有下载完成
                if (!contains) {
                    tasks.add(task);
                }
                File f = new File(task.getSaveDir(), task.getFileName());//纠正为Done状态但是没有文件的情况
                if (task.totalSize > 0 && f.length() == task.totalSize) {
                    task.state = Status.DONE;
                } else {
                    task.state = Status.WAITING;
                }

                if (task.state != Status.DONE) {
                    if (running.size() < maxParalle) {//置为应该下载
                        task.state = Status.SHOULD_DOWNLOAD;
                        running.add(task);
                    } else {
                        task.state = Status.WAITING;//等待下载
                        NotifyListener.getInstance().onWait(task);
                    }

                } else {
                    NotifyListener.getInstance().onDone(task);//下载完成
                }

            }

            checkTask();

        } finally {
            lock.unlock();
            Log.d("gg", Thread.currentThread().getName() + "startTask 释放锁");
        }


    }


    /**
     * 暂停执行
     */
    public void stop(DownloadTask task) {
        lock.lock();
        try {
            if (task == null) {
                return;
            }
            if (running.contains(task)) {//正在下载的
                task.stop = true;
            } else if (tasks.contains(task) && task.state == Status.WAITING) {
                task.state = Status.PAUSE;
                NotifyListener.getInstance().onCancel(task);

            }

        } finally {
            lock.unlock();
        }


    }

    /**
     * 据url查找存在的任务
     *
     * @param url
     * @return
     */
    public DownloadTask findTask(String url) {
        lock.lock();
        try {
            if (TextUtils.isEmpty(url)) {
                return null;
            }
            for (DownloadTask task : tasks) {
                if (TextUtils.equals(task.getUrl(), url)) {
                    return task;
                }
            }

        } finally {
            lock.unlock();
        }

        return null;
    }

    /**
     * 删除任务
     *
     * @param task
     */
    private void onCancelTask(DownloadTask task) {
        lock.lock();
        try {
            Iterator<DownloadTask> it = running.iterator();
            while (it.hasNext()) {
                DownloadTask t = it.next();
                if (TextUtils.equals(t.getUrl(), task.getUrl())) {
                    it.remove();
                }
            }

        } finally {
            lock.unlock();
        }

    }

    private DownloadTask.OnCancelListener onCancelListener = new DownloadTask.OnCancelListener() {
        @Override
        public void onCancel(DownloadTask task) {
            if (task == null) {
                return;
            }
            onCancelTask(task);

        }
    };

    /**
     * 删除任务
     */
    public void remove(DownloadTask task) {
        lock.lock();
        try {
            if (task == null) {
                return;
            }
            //从队列中删除
            task.state = Status.REMOVED;
            if (task.call != null) {
                task.call.cancel();
            }
            running.remove(task);
            tasks.remove(task);
            //删除文件
            File f = new File(task.getSaveDir(), task.getFileName());
            if (f.exists()) {
                f.delete();
            }
            NotifyListener.getInstance().onRemove(task);

        } finally {
            lock.unlock();
        }
    }

    /**
     * 全部删除
     * @param removeTasks
     */
    public void removeAll(List<DownloadTask> removeTasks) {
        lock.lock();
        try {
            if (removeTasks == null) {
                return;
            }
            //置状态
            for (DownloadTask removeTask : removeTasks) {
                removeTask.state = Status.REMOVED;
            }

            //真正的开始删除
            for (DownloadTask  removeTaskL: removeTasks) {
                remove(removeTaskL);
            }

            //从队列中删除

        } finally {
            lock.unlock();
        }

    }



    /**
     * 据状态更新task所属的队列
     *
     * @param task
     */
    public void updateTaskQueue(DownloadTask task) {
        lock.lock();
        try {
            if (task == null) {
                return;
            }
            if (task.state == Status.DONE) {//移到完成队列
                running.remove(task);
            } else if (task.state == Status.DOWNLOADING) {//tasks和running都得有
                if (!tasks.contains(task)) {
                    tasks.add(task);
                }
                if (!running.contains(task)) {
                    running.add(task);
                }
            } else if (task.state == Status.PAUSE || task.state == Status.WAITING || task.state == Status.ERROE || task.state == Status.SHOULD_DOWNLOAD) {
                if (!tasks.contains(task)) {
                    tasks.add(task);
                }
                if (running.contains(task)) {
                    running.remove(task);
                }
            } else if (task.state == Status.PAUSE) {
                running.remove(task);
            }

            waitStatusToShouldDownload();
            checkTask();//每队列调整一次就检查一次，是否有可执行的


        } finally {
            lock.unlock();

        }

    }

    /**
     * 将队列中的wait状态转为可下载状态
     */
    public void waitStatusToShouldDownload() {
        for (DownloadTask task : tasks) {
            if (running.size() < maxParalle && task.state == Status.WAITING) {
                task.state = Status.SHOULD_DOWNLOAD;
                running.add(task);
            }
        }

    }

    public void setTasks(List<DownloadTask> tasks) {
        if (tasks == null || tasks.size() == 0) {
            this.tasks.clear();
        } else {
            this.tasks.addAll(tasks);
        }

    }

    public LinkedList<DownloadTask> getTasks() {
        return tasks;
    }

    /**
     * 据Url获取任务
     * @param url
     * @return
     */
    public DownloadTask getTask(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        Iterator<DownloadTask> it = tasks.iterator();
        while (it.hasNext()) {
            DownloadTask task = it.next();
            if (TextUtils.equals(task.getUrl(), url)) {
                return task;
            }
        }
        return null;
    }

    /**
     * 清空数据
     * 退出已有的所有下载任务
     */
    public void clearData() {
        lock.lock();
        try {
            for (DownloadTask task : tasks) {
                task.stop = true;
            }
            for (DownloadTask  task: running) {
                task.stop = true;
            }
            tasks.clear();
            running.clear();


        } finally {
            lock.unlock();
        }
    }
}
