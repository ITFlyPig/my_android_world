package com.wangyuelin.performance.fps;

import android.app.ActivityManager;
import android.content.Context;
import android.view.Choreographer;

import com.alibaba.fastjson.JSON;
import com.wangyuelin.performance.ThreadHelper;
import com.wangyuelin.performance.socket.WebSocketHelper;

/**
 * 统计帧率
 */
public class PerformanceUtil {
    private static long mLastFrameTime;
    private static long mFrameCount;
    private static volatile boolean isStopFps;
    private static Choreographer.FrameCallback callback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
//            if (mLastFrameTime == 0) {
//                mLastFrameTime = frameTimeNanos;
//            }
//            float diff = (frameTimeNanos - mLastFrameTime) / 1000000.0f;//得到毫秒，正常是 16.66 ms
//            if (diff > 500) {
//                float fps = (((float) (mFrameCount * 1000L)) / diff);
//                mFrameCount = 0;
//                mLastFrameTime = 0;
//                FpsBean fpsBean = new FpsBean(mLastFrameTime, frameTimeNanos, fps);
//                ThreadHelper.getInstance().submit(() -> {
//                    String json = JSON.toJSONString(fpsBean);
//                    WebSocketHelper.getInstance().send(json);
//                });
//            } else {
//                ++mFrameCount;
//            }
//            if (!isStopFps) {
//                Choreographer.getInstance().postFrameCallback(this);
//            }

        }
    };

    /**
     * 开始Fps检测
     */
    public static void startFps() {
        Choreographer.getInstance().postFrameCallback(callback);
    }

    /**
     * 结束Fps的检测
     */
    public static void stopFps() {
        isStopFps = true;
    }


    /**
     * 获得栈顶的Activity
     * @param context
     * @return
     */
    public static String getStackTopActivity(Context context) {
        if (context == null) {
            return "";
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return "";
        }
        return am.getRunningTasks(1).get(0).topActivity.getClassName();
    }
}
