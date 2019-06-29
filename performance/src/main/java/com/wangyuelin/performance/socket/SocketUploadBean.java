package com.wangyuelin.performance.socket;

import com.wangyuelin.performance.fps.FpsBean;
import com.wangyuelin.performance.method.CallBean;
import com.wangyuelin.performance.show.CallDrawItem;

public class SocketUploadBean {
    private CallDrawItem methodCall; //方法调用相关
    private FpsBean fps;//帧率相关的

    public SocketUploadBean() {
    }

    public SocketUploadBean(CallDrawItem methodCall, FpsBean fps) {
        this.methodCall = methodCall;
        this.fps = fps;
    }

    public CallDrawItem getMethodCall() {
        return methodCall;
    }

    public FpsBean getFps() {
        return fps;
    }
}
