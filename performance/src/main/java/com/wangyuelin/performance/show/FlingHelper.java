package com.wangyuelin.performance.show;

import android.content.Context;
import android.widget.OverScroller;

/**
 * 获取Fling产生的数值
 */
public class FlingHelper{
    private OverScroller overScroller;
    private FlingListener flingListener;
    private Context context;

    public FlingHelper(Context context, FlingListener flingListener) {
        this.flingListener = flingListener;
        this.context = context;
    }

    public void cancelFling() {
        if (overScroller != null) {
            overScroller.forceFinished(true);
        }
    }


    /**
     * 开始fling
     * @param start   开始的位置
     * @param min     最小的位置
     * @param max     最大的位置
     * @param velocity 开始速度
     */
    public void fling(int start, int min, int max,  int velocity) {
        if (overScroller != null) {
            overScroller.forceFinished(true);
        }
        overScroller = new OverScroller(context);
        overScroller.fling(0, start, 0, velocity, 0, 0, min, max);
    }

    /**
     * 每次绘制的时候会调用这个方法
     * @return 决定是否还需要继续计算
     */
    public boolean onDraw() {
        if (overScroller == null) {
            return false;
        }
        if (overScroller.isFinished()) {
            return false;
        }
        if (overScroller.computeScrollOffset()) {
            int cur = overScroller.getCurrY();
            if (flingListener != null) {
                flingListener.onFling(cur);
            }
            return true;
        }
        return false;

    }

    /**
     * Fling监听
     */
    public interface FlingListener{
        void onFling(int cur);
    }
}
