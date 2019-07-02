package com.talk51.downloader;

import android.text.TextUtils;

import com.talk51.downloader.real.db.DownloadStateTask;
import com.talk51.downloader.real.DownloadTask;
import com.talk51.downloader.real.Status;
import com.talk51.downloader.real.Talk51DownloadManager;
import com.talk51.downloader.real.listener.DownloadListener;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class RealDownloader implements Downloader {

    private DownloadConf mConf;//下载配置类
    private List<DownloadStateTask.Listener> listeners;

    public DownloadConf getmConf() {
        return mConf;
    }


    private static class Holder {
        public static RealDownloader downloader = new RealDownloader();
    }

    private RealDownloader() {
        listeners = new LinkedList<>();
        Talk51DownloadManager.getInstance().addCallBack(listener);//注册回调

    }


    /**
     * 单例获取
     *
     * @return
     */
    public static Downloader getInstace() {
        return Holder.downloader;
    }

    @Override
    public void start(String url, String name, String fileType, String extra) {
        if (TextUtils.isEmpty(url) || mConf == null) {
            return;
        }
        DownloadTask task = Talk51DownloadManager.getInstance().getTask(url);

        if (task == null) {
            String fileName = DownloaderUtil.getFileName(url);
            task = new DownloadTask.Builder()
                    .setSaveDir(mConf.mSaveDir)
                    .setFileName(fileName)
                    .setShowFileName(name)
                    .setUrl(url)
                    .setFileType(fileType)
                    .setExtra(extra)
                    .build();
        }
        Talk51DownloadManager.getInstance().start(task);

    }

    @Override
    public void addListener(DownloadStateTask.Listener listener) {
        //添加到list里面
        if (listener != null) {
            listeners.add(listener);
        }

    }

    @Override
    public void removeListener(DownloadStateTask.Listener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }

    }


    @Override
    public void pause(String url) {
        Talk51DownloadManager.getInstance().stop(Talk51DownloadManager.getInstance().getTask(url));
    }

    @Override
    public void remove(String url) {
        Talk51DownloadManager.getInstance().remove(Talk51DownloadManager.getInstance().getTask(url));


    }


    @Override
    public DownloadStateTask getTask(String url) {
        return DownloadStateTask.build(Talk51DownloadManager.getInstance().getTask(url));
    }

    @Override
    public void startAll() {
        List<DownloadTask> tasks = Talk51DownloadManager.getInstance().getAllTasks();
        if (tasks == null) {
            return;
        }
        for (DownloadTask task : tasks) {
            if (task.state != Status.DONE) {
                Talk51DownloadManager.getInstance().start(task);
            }

        }


    }

    @Override
    public void pauseAll() {
        List<DownloadTask> tasks = Talk51DownloadManager.getInstance().getAllTasks();
        if (tasks == null) {
            return;
        }
        for (DownloadTask task : tasks) {
            if (task.state != Status.DONE) {
                Talk51DownloadManager.getInstance().stop(task);
            }

        }
    }

    @Override
    public void removeAll() {
        List<DownloadTask> tasks = Talk51DownloadManager.getInstance().getAllTasks();
        if (tasks == null) {
            return;
        }
        List<DownloadTask> copyList = new ArrayList<>();//只删除已下载的任务
        for (DownloadTask task : tasks) {
            if (task.state == Status.DONE) {
                copyList.add(task);
            }
        }

        Talk51DownloadManager.getInstance().removeAll(copyList);

    }

    @Override
    public List<DownloadStateTask> getAllTasks() {
        List<DownloadTask> tasks = Talk51DownloadManager.getInstance().getAllTasks();
        if (tasks == null) {
            return null;
        }
        ArrayList<DownloadStateTask> respList = new ArrayList<>();
        for (DownloadTask task : tasks) {
            respList.add(DownloadStateTask.build(task));
        }

        return respList;
    }

    @Override
    public String getFileType(String url) {
        return DownloaderUtil.getFileType(url);
    }

    @Override
    public void setConfig(DownloadConf conf) {
        this.mConf = conf;
    }



    private DownloadListener listener = new DownloadListener() {
        @Override
        public void onStart(DownloadTask task) {
            for (DownloadStateTask.Listener listener : listeners) {
                listener.onStart(DownloadStateTask.build(task));
            }

        }

        @Override
        public void onCancel(DownloadTask task) {
            for (DownloadStateTask.Listener listener : listeners) {
                listener.onPause(DownloadStateTask.build(task));
            }

        }

        @Override
        public void onError(DownloadTask task) {
            for (DownloadStateTask.Listener listener : listeners) {
                listener.onError(DownloadStateTask.build(task));
            }

        }

        @Override
        public void onWait(DownloadTask task) {
            for (DownloadStateTask.Listener listener : listeners) {
                listener.onWait(DownloadStateTask.build(task));
            }

        }

        @Override
        public void onDownloading(DownloadTask task) {
            for (DownloadStateTask.Listener listener : listeners) {
                listener.onDownloading(DownloadStateTask.build(task));
            }

        }

        @Override
        public void onDone(DownloadTask task) {
            for (DownloadStateTask.Listener listener : listeners) {
                listener.onFinish(DownloadStateTask.build(task), new File(task.getSaveDir(), task.getFileName()));
            }

        }

        @Override
        public void onRemove(DownloadTask task) {
            for (DownloadStateTask.Listener listener : listeners) {
                listener.onRemove(DownloadStateTask.build(task));
            }

        }
    };
}
