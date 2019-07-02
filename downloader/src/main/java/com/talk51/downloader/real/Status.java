package com.talk51.downloader.real;

public interface Status {
    int NONE = 0;       //表示没有状态，比如还没有添加url对应的任务就查询
    int DONE = 1;       //下载完成
    int WAITING = 2;    //等待下载
    int DOWNLOADING = 3;//下载中
    int PAUSE = 4;      //暂停
    int ERROE = 5;      //出错
    int REMOVED = 6;    //任务移除
    int SHOULD_DOWNLOAD = 7;//应该下载
}
