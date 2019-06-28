package com.wangyuelin.performance.socket;

import com.wangyuelin.performance.fps.FpsBean;
import com.wangyuelin.performance.method.CallBean;

public class SocketUploadBean {
    private CallBean methodCall; //方法调用相关
    private FpsBean fps;//帧率相关的

    public SocketUploadBean() {
    }

    public SocketUploadBean(CallBean methodCall, FpsBean fps) {
        this.methodCall = methodCall;
        this.fps = fps;
    }

    public CallBean getMethodCall() {
        return methodCall;
    }

    public FpsBean getFps() {
        return fps;
    }
}
