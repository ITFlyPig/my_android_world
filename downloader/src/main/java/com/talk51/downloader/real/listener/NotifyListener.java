package com.talk51.downloader.real.listener;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;


import com.talk51.downloader.real.Talk51DownloadManager;
import com.talk51.downloader.real.DownloadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotifyListener {
    private List<DownloadListener> listeners = new ArrayList<>();

    private Handler uiHandler;

    private long uiCallbackInterval = 500;//更新ui回调的间隔，当然只对下载中有效
    private long dbUpdateInterval = 3000;//数据库更新间隔，只对下载中有效

    private Map<DownloadTask, Long> lastTimeMap;//用于控制更新间隔
    private Map<DownloadTask, Long> lastDBTimeMap;//用于控制更新间隔

    private static class Holder {
        private static NotifyListener notifyListener = new NotifyListener();
    }

    public static NotifyListener getInstance() {
        return Holder.notifyListener;
    }


    private NotifyListener() {
        uiHandler = new Handler(Looper.getMainLooper());
        lastTimeMap = new HashMap<>();
        lastDBTimeMap = new HashMap<>();
    }

    /**
     * 添加回调
     *
     * @param listener
     */
    public void addCallBack(DownloadListener listener) {
        if (listener == null) {
            return;
        }
        listeners.add(listener);

    }

    /**
     * 删除回调
     *
     * @param listener
     */
    public void removeCallBack(DownloadListener listener) {
        if (listener == null) {
            return;
        }
        listeners.remove(listener);

    }


    /**
     * 开始回调
     *
     * @param task
     */
    public void onStart(final DownloadTask task) {
        if (!isUidSame(task.belongUid, Talk51DownloadManager.getInstance().getDbTaskManager().uid)) {
            return;
        }
        DownloadTask old = Talk51DownloadManager.getInstance().getDbTaskManager().get(task.getUrl());
        if (old == null) {
            Talk51DownloadManager.getInstance().getDbTaskManager().insert(task);
            DownloadTask temp = Talk51DownloadManager.getInstance().getDbTaskManager().get(task.getUrl());
            if (temp != null) {
                task.id = temp.id;//更新id
            }
        } else {
            Talk51DownloadManager.getInstance().getDbTaskManager().update(task);
        }


        Talk51DownloadManager.getInstance().getFileDownloader().updateTaskQueue(task);


        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                for (DownloadListener listener : listeners) {
                    listener.onStart(task);
                }
            }
        });
        Log.d("xz", task.getShowFileName() + "=========onStart");

    }

    public void onCancel(final DownloadTask task) {
        if (!isUidSame(task.belongUid, Talk51DownloadManager.getInstance().getDbTaskManager().uid)) {
            return;
        }
        Talk51DownloadManager.getInstance().getDbTaskManager().update(task);//更新数据库
        Talk51DownloadManager.getInstance().getFileDownloader().updateTaskQueue(task);//更新内存中的队列
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                for (DownloadListener listener : listeners) {
                    listener.onCancel(task);
                }
            }
        });
        Log.d("xz", task.getShowFileName() + "=========onCancel");

    }

    public void onError(final DownloadTask task) {
        if (!isUidSame(task.belongUid, Talk51DownloadManager.getInstance().getDbTaskManager().uid)) {
            return;
        }
        Talk51DownloadManager.getInstance().getDbTaskManager().update(task);
        Talk51DownloadManager.getInstance().getFileDownloader().updateTaskQueue(task);
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                for (DownloadListener listener : listeners) {
                    listener.onError(task);
                }
            }
        });
        Log.d("xz", task.getShowFileName() + "=========onError");

    }

    public void onWait(final DownloadTask task) {
        if (!isUidSame(task.belongUid, Talk51DownloadManager.getInstance().getDbTaskManager().uid)) {
            return;
        }
        DownloadTask old = Talk51DownloadManager.getInstance().getDbTaskManager().get(task.getUrl());
        if (old == null) {
            Talk51DownloadManager.getInstance().getDbTaskManager().insert(task);
            DownloadTask temp = Talk51DownloadManager.getInstance().getDbTaskManager().get(task.getUrl());
            if (temp != null) {
                task.id = temp.id;//更新id
            }
        } else {
            Talk51DownloadManager.getInstance().getDbTaskManager().update(task);
        }

        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                for (DownloadListener listener : listeners) {
                    listener.onWait(task);
                }
            }
        });
        Log.d("xz", task.getShowFileName() + "=========onWait");

    }

    public void onDownloading(final DownloadTask task) {
        if (!isUidSame(task.belongUid, Talk51DownloadManager.getInstance().getDbTaskManager().uid)) {
            return;
        }
        //更新数据库下载进度
        Long lastDBTimeL = lastDBTimeMap.get(task);
        long lastDBTime = 0;
        if (lastDBTimeL != null) {
            lastDBTime = lastDBTimeL;
        }

        long cur = System.currentTimeMillis();
        if (cur - lastDBTime > dbUpdateInterval) {//更新数据库
            lastDBTimeMap.put(task, cur);
            Talk51DownloadManager.getInstance().getDbTaskManager().update(task);
        }


        //更新UI
        Long l = lastTimeMap.get(task);
        long lastTime = 0;
        if (l != null) {
            lastTime = l;
        }

        if (cur - lastTime > uiCallbackInterval) {
            lastTimeMap.put(task, cur);
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (DownloadListener listener : listeners) {
                        listener.onDownloading(task);
                    }
                }
            });
            Log.d("xz", task.getShowFileName() + "=========onDownloading========" + (task.currentSize / (float) task.totalSize));

        }


    }

    public void onDone(final DownloadTask task) {
        if (!isUidSame(task.belongUid, Talk51DownloadManager.getInstance().getDbTaskManager().uid)) {
            return;
        }
        Log.d("xz", task.getShowFileName() + "=========onDone");
        task.completeTime = System.currentTimeMillis();
        Talk51DownloadManager.getInstance().getDbTaskManager().update(task);
        Talk51DownloadManager.getInstance().getFileDownloader().updateTaskQueue(task);

        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                for (DownloadListener listener : listeners) {
                    listener.onDone(task);
                }
            }
        });

    }


    public void onRemove(final DownloadTask task) {
        if (!isUidSame(task.belongUid, Talk51DownloadManager.getInstance().getDbTaskManager().uid)) {
            return;
        }
        Log.d("xz", task.getShowFileName() + "=========onRemove");

        Talk51DownloadManager.getInstance().getDbTaskManager().delete(task.getUrl());
        Talk51DownloadManager.getInstance().getFileDownloader().updateTaskQueue(task);
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                for (DownloadListener listener : listeners) {
                    listener.onRemove(task);
                }
            }
        });

    }

    /**
     * 判断uid是否相等
     * @param taskUid
     * @param curUid
     * @return
     */
    private boolean isUidSame(String taskUid, String curUid) {
        return TextUtils.equals(taskUid, curUid);

    }


}
