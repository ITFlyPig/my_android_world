package com.wangyuelin.performance.fps;


public class FpsBean {
    public long startTime;  //开始统计的时间
    public long endTime;   //结束统计的时间
    public float fps;        //帧率
    public int color;


    public FpsBean() {
    }

    public FpsBean(long startTime, long endTime, float fps) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.fps = fps;
    }

}
