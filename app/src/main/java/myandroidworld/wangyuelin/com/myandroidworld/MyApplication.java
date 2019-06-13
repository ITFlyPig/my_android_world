package myandroidworld.wangyuelin.com.myandroidworld;

import com.wangyuelin.common.BaseApplication;

public class MyApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        WebSocketHelper.getInstance().connect("sid");
    }
}
