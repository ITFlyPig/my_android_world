package com.wangyuelin.performance.show;

import android.graphics.Rect;

import com.wangyuelin.performance.CallBean;

import java.util.List;

public class CallDrawItem  {
    public String signature;//方法的签名
    public long totalTime;//花费的时间
    public long startTime;//开始调用时的时间
    public long endTIme;//结束时的事件
    public String classC;//所属的Class
    public Object[] args;//方法的参数
    public List<CallDrawItem> childs;//被调用的子方法
    public CallBean parent;//父调用

    public int w;//柱状图的宽度
    public int h;//柱状图的高度
    public Rect pos;//绘制的柱状图的位置

    public CallDrawItem(String signature, long totalTime, long startTime, long endTIme, String classC, Object[] args) {
        this.signature = signature;
        this.totalTime = totalTime;
        this.startTime = startTime;
        this.endTIme = endTIme;
        this.classC = classC;
        this.args = args;
    }
}
