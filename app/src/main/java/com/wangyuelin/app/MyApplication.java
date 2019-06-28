package com.wangyuelin.app;

import com.wangyuelin.common.BaseApplication;
import com.wangyuelin.performance.ThreadHelper;
import com.wangyuelin.performance.socket.WebSocketHelper;

public class MyApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        ThreadHelper.getInstance().submit(() -> WebSocketHelper.getInstance().connect("o"));
    }
}
