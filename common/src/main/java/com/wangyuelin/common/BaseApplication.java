package com.wangyuelin.common;

import android.app.Application;

/**
 * 基础库的Application，可以提供给所有的Module和App
 */
public class BaseApplication extends Application {

    private static BaseApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static BaseApplication getApplication(){
        return application;
    }
}
