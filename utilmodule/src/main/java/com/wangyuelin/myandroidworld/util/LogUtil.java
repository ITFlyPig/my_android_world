/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wangyuelin.myandroidworld.util;

import android.util.Log;

import com.wangyuelin.myandroidworld.BuildConfig;

/**
 *
 *
 * 日志的工具类
 */
public class LogUtil {
    private static boolean isLogEnable = BuildConfig.DEBUG;//只有debug包才打印日志

    private static String tag = "LogUtil";

    public static void v(String msg) {
        v(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isLogEnable) Log.v(tag, msg);
    }

    public static void d(String msg) {
        d(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (isLogEnable) Log.d(tag, msg);
    }

    public static void i(String msg) {
        i(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (isLogEnable) Log.i(tag, msg);
    }

    public static void w(String msg) {
        w(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (isLogEnable) Log.w(tag, msg);
    }

    public static void e(String msg) {
        e(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isLogEnable) Log.e(tag, msg);
    }

    public static void printStackTrace(Throwable t) {
        if (isLogEnable && t != null) t.printStackTrace();
    }
}
