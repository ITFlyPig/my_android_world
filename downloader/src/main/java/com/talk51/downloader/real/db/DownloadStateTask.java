package com.talk51.downloader.real.db;


import android.text.TextUtils;

import com.talk51.downloader.real.DownloadTask;

import java.io.File;

/**
 * 封装下载信息
 */
public class DownloadStateTask {

    /**
     * 使用protected保护包外不让修改
     */
    public int id;
    public String tag;        //资源的唯一标识
    public String url;        //资源的url
    public long totalSize;     //资源的大小
    public long currentSize;   //当前下载数量
    public String path;        //文件的保存路径
    public int state;          //下载的状态
    public String saveName;   //文件的保存名称，给用户友好显示使用
    public String fileName;   //文件名称
    public String msg;        //错误提示信息
    public long date;         //创建时间
    public String fileType;   //文件的类型
    public String extra;      //扩展使用


    /**
     * 封装下载状态
     */
    public interface State {
        int NONE = 0;       //表示没有状态，比如还没有添加url对应的任务就查询
        int DONE = 1;       //下载完成
        int WAITING = 2;    //等待下载
        int DOWNLOADING = 3;//下载中
        int PAUSE = 4;      //暂停
        int ERROE = 5;      //出错
        int REMOVED = 6;    //任务移除

    }


    /**
     * 下载进度的监听，不使用接口，省得实现全部方法，只需要实现感兴趣的
     */
    public interface Listener {
        void onStart(DownloadStateTask task);//开始


        void onDownloading(DownloadStateTask task);    //正在下载

        void onError(DownloadStateTask task);          //下载失败

        void onFinish(DownloadStateTask task, File f); //下载完成

        void onWait(DownloadStateTask task);           //等待下载,这个回调不一定会走比如当已达到最大下载数量，那么新添加的任务会等待下载

        void onPause(DownloadStateTask task);          //任务暂停

        void onRemove(DownloadStateTask task);         //任务删除

    }



    public static DownloadStateTask build(DownloadTask task) {
        if (task == null) {
            return null;
        }
        DownloadStateTask showTask = new DownloadStateTask();
        showTask.state = task.state;
        showTask.fileType = task.fileType;
        showTask.extra = task.extra;
        showTask.currentSize = task.currentSize;
        showTask.date = task.date;
        showTask.fileName = task.getFileName();
        showTask.path = task.getSaveDir() + File.separator+ task.getFileName();
        showTask.saveName = task.getShowFileName();
        showTask.totalSize = task.totalSize;
        showTask.url = task.getUrl();
        showTask.id = task.id;
        return showTask;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (obj instanceof DownloadStateTask) {
            final DownloadStateTask task = (DownloadStateTask) obj;
            if (TextUtils.equals(this.url, task.url)) return true;//通过url判断task是否相同
        }
        return false;
    }

}
