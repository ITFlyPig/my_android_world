package com.talk51.downloader.real.download;

import android.text.TextUtils;
import android.util.Log;


import com.talk51.downloader.real.DownloadTask;
import com.talk51.downloader.real.Status;
import com.talk51.downloader.real.exception.CancelException;
import com.talk51.downloader.real.listener.NotifyListener;
import com.talk51.downloader.real.utils.IOUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 真正的文件下载地方
 */
public class DTask implements Runnable {
    private DownloadTask downloadTask;
    private int retryTime;
    private int maxRetryTime = 4;

    public DTask(DownloadTask downloadTask) {
        this.downloadTask = downloadTask;
    }

    @Override
    public void run() {
        if (downloadTask == null) {
            return;
        }

        while (retryTime < maxRetryTime ) {//控制重试次数
            Log.d("DTask", downloadTask.getShowFileName() + "开始重试：" + retryTime);

            InputStream is = null;
            FileOutputStream os = null;
            try {
                if (downloadTask.totalSize == 0) {
                    //获取下载的大小
                    long totalSize = getTotalSize(downloadTask.getUrl());
                    if (totalSize < 0) {
                        downloadTask.state = Status.ERROE;//文件下载出错
                        //通知Listener文件下载出错
                        NotifyListener.getInstance().onError(downloadTask);
                        return;
                    } else {
                        downloadTask.totalSize = totalSize;
                    }
                }

                checkCancleTask();

                //确保目录存在
                File dir = new File(downloadTask.getSaveDir());
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                //检查磁盘上是否已经下载
                File f = new File(downloadTask.getSaveDir(), downloadTask.getFileName());
                if (!f.exists()) {
                    f.createNewFile();
                }
                long doneSize = f.length();
                if (doneSize == downloadTask.totalSize) {//已下载完成
                    downloadTask.state = Status.DONE;
                    NotifyListener.getInstance().onDone(downloadTask);
                    return;
                }

                //据下载的文件更新下载进度
                downloadTask.currentSize = doneSize;



                Request request = new Request.Builder()
                        //确定下载的范围,添加此头,则服务器就可以跳过已经下载好的部分
                        .addHeader("RANGE", "bytes=" + downloadTask.currentSize + "-" + downloadTask.totalSize)
                        .url(downloadTask.getUrl())
                        .build();
                Call call = DownloadClient.getInstance().client.newCall(request);

                Response response = call.execute();

                if (response == null || !response.isSuccessful() || response.body() == null) {
                    throw new IOException("网络请求返回response错误");
                }
                downloadTask.call = call;


                is = response.body().byteStream();
                os = new FileOutputStream(f, true);
                byte[] buffer = new byte[2048];//缓冲数组2kB
                int len;
                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                    downloadTask.currentSize += len;
                    //通知Listener下载进度改变
                    NotifyListener.getInstance().onDownloading(downloadTask);
                    checkCancleTask();
                    //连接成功的时候，重置重试次数
                    Log.d("DTask", downloadTask.getShowFileName() + "读取数据");
                    if (retryTime > 0) {
                        retryTime = 0;
                    }
                }

                if (downloadTask.currentSize >= downloadTask.totalSize) {//下载完成
                    //通知Listener下载完成
                    downloadTask.state = Status.DONE;
                    NotifyListener.getInstance().onDone(downloadTask);

                }

            } catch (IOException e) {
                e.printStackTrace();
                if (downloadTask.state == Status.REMOVED ) {//删除
                    downloadTask.call = null;
                    downloadTask.state = Status.REMOVED;
                    //通知Listener文件下载出错
                    NotifyListener.getInstance().onRemove(downloadTask);
                } else if (retryTime >= (maxRetryTime - 1)){//下载报错
                    downloadTask.call = null;
                    downloadTask.state = Status.ERROE;
                    //通知Listener文件下载出错
                    NotifyListener.getInstance().onError(downloadTask);

                }
                Log.d("DTask", downloadTask.getShowFileName() + "IO异常：" + e.getLocalizedMessage());

            } catch (CancelException e) {
                e.printStackTrace();
                //通知Listener文件下载暂停
                downloadTask.state = Status.PAUSE;
                NotifyListener.getInstance().onCancel(downloadTask);
                Log.d("DTask", downloadTask.getShowFileName() + "CancelException异常：" + e.getLocalizedMessage());
            } finally {
                IOUtil.closeAll(is, os);

                if (downloadTask.state == Status.DONE || downloadTask.state == Status.PAUSE || downloadTask.state == Status.REMOVED) {
                    return;
                } else {
                    retryTime++;
                }
            }




        }



    }

    /**
     * 获取文件的大小
     *
     * @param url
     * @return
     */
    private long getTotalSize(String url) {
        if (TextUtils.isEmpty(url)) {
            return -1;
        }
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = DownloadClient.getInstance().client.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                long contentLength = response.body().contentLength();
                response.close();
                return contentLength == 0 ? -1 : contentLength;
            }

        } catch (IOException e) {
            //通知文件下载出错
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 检测取消任务
     */
    private void checkCancleTask() throws CancelException {
        if (downloadTask.stop) {
            downloadTask.stop = false;
            if (downloadTask.call != null) {
                downloadTask.call.cancel();//取消任务
            }
            downloadTask.state = Status.PAUSE;

            if (downloadTask.onCancelListener != null) {
                downloadTask.onCancelListener.onCancel(downloadTask);
            }

            //通知Listener取消任务
            throw new CancelException("取消任务");
        }

    }
}
