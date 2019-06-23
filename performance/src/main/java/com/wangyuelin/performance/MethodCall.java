package com.wangyuelin.performance;

public class MethodCall {
    public static void onStart(String signature, Object[] args) {
        System.out.println("onStart" + signature);
    }

    public static void onEnd(String signature) {
        System.out.println("onEnd：" + signature);

    }
}
