package com.wangyuelin.performance.method;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.wangyuelin.performance.ThreadHelper;

public class MethodCall {
    private static CallHelper.CallListener callListener = callBean -> ThreadHelper.getInstance().submit(() -> {
        String callJson = JSON.toJSONString(callBean);
//                    WebSocketHelper.getInstance().send(callJson);
        Log.d("wyl", "反馈的Josn" + callJson);
    });
    private static CallHelper callHelper = new CallHelper(callListener);

    //////////////////////////下面是AOP的方法//////////////////////////////

    /**
     * 方法的开始会调用
     * @param signature
     * @param args
     */
    public static void onStart(String signature, Object[] args) {
        callHelper.recordStart(signature, args);
    }

    /**
     * 没有参数的情况
     * @param signature
     */
    public static void onStart(String signature) {
        callHelper.recordStart(signature, null);
    }

    /**
     * 方法的结束会调用
     * @param signature
     */
    public static void onEnd(String signature) {
        callHelper.recordEnd(signature);

    }
}
