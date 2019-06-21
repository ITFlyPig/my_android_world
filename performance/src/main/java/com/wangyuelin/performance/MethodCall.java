package com.wangyuelin.performance;

public class MethodCall {
    public static void onStart(String signature, Object[] args) {
    }

    public static void onEnd(String signature) {
        System.out.println("方法被调用：" + signature);

    }
}
