package com.wangyuelin.performance.show;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wangyuelin.myandroidworld.util.LogUtil;
import com.wangyuelin.performance.fps.FpsBean;
import com.wangyuelin.performance.method.CallBean;
import com.wangyuelin.performance.socket.SocketUploadBean;
import com.wangyuelin.performance.socket.WebSocketHelper;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 待绘制方法的队列
 */
public class MethodQueue {
    public static List<CallDrawItem> methods;//已经绘制的列表
    public static List<CallDrawItem> waits;//等待绘制的列表
    public static List<FpsBean> fps;//Fps列表

    static {
        methods = Collections.synchronizedList(new LinkedList<>());
        waits = Collections.synchronizedList(new LinkedList<>());
        fps = Collections.synchronizedList(new LinkedList<>());

        WebSocketHelper.getInstance().setWebSocketListener(new WebSocketHelper.SocketListener() {
            @Override
            public void onMessage(String msg) {
                SocketUploadBean uploadBean = JSON.parseObject(msg, SocketUploadBean.class);
                if (uploadBean == null) {
                    return;
                }
                if (uploadBean.getFps() != null) {//fps数据
                    FpsBean fpsBean = uploadBean.getFps();
                    if (fpsBean.fps < 50) {//50以下的算掉帧
                        if (fps.size() > 1000) {
                            //每次删除200个
                            Iterator<FpsBean> it = fps.iterator();
                            int count = 0;
                            while (it.hasNext() && count < 200) {
                                it.next();
                                it.remove();
                                count++;
                            }
                        }
                        fps.add(fpsBean);
                    }

                }

                if (uploadBean.getMethodCall() != null) {//方法统计数据
//                    CallDrawItem callBean = uploadBean.getMethodCall();
//                    if (callBean.totalTime > 0) {//过滤掉执行时间为0的代码块，因为0的执行时间不影响性能
//                        waits.add(callBean);
//                    }
                }
            }
        });

        makeTest();
    }

    /**
     * 计算好高度的
     * @return
     */
    public static CallDrawItem getWait() {
        if (waits.size() > 0) {
            CallDrawItem item = waits.remove(0);
            CallDrawUtil.caculate(item, 1, PerformanceView.methodH);
            return item;
        }
        return null;

    }

    public static void makeTest() {
        String fps1 = "{\"fps\":{\"endTime\":1561705312970,\"fps\":40.39143,\"startTime\":1561705312949}}";//跨两个
        fps.add(parseFps(fps1));
//        String fps2 = "{\"fps\":{\"endTime\":1561705312949,\"fps\":40.06433,\"startTime\":1561705312929}}";//夸一个
//        fps.add(parseFps(fps2));
//        String fps3 = "{\"fps\":{\"endTime\":1561705312972,\"fps\":40.87429,\"startTime\":1561705312970}}";//夸一个
//        fps.add(parseFps(fps3));

        String method1 = "{\"methodCall\":{\"classC\":\"com.talk51.kid.view.AutoScrollViewBanner$TGPageAdapter\",\"className\":\"AutoScrollViewBanner$TGPageAdapter\",\"endTIme\":1561705312933,\"methodName\":\"getCount\",\"signature\":\"com.talk51.kid.view.AutoScrollViewBanner$TGPageAdapter.getCount()\",\"startTime\":1561705312930,\"totalTime\":3}}";
        String method2 = "{\"methodCall\":{\"classC\":\"com.talk51.kid.view.AutoScrollViewBanner$TGPageAdapter\",\"className\":\"AutoScrollViewBanner$TGPageAdapter\",\"endTIme\":1561705312948,\"methodName\":\"getCount\",\"signature\":\"com.talk51.kid.view.AutoScrollViewBanner$TGPageAdapter.getCount()\",\"startTime\":1561705312938,\"totalTime\":10}}";
        String method3 = "{\"methodCall\":{\"classC\":\"com.talk51.kid.view.AutoScrollViewBanner$TGPageAdapter\",\"className\":\"AutoScrollViewBanner$TGPageAdapter\",\"endTIme\":1561705312970,\"methodName\":\"getCount\",\"signature\":\"com.talk51.kid.view.AutoScrollViewBanner$TGPageAdapter.getCount()\",\"startTime\":1561705312950,\"totalTime\":20}}";
        String method4 = "{\"methodCall\":{\"classC\":\"com.talk51.kid.view.AutoScrollViewBanner$TGPageAdapter\",\"className\":\"AutoScrollViewBanner$TGPageAdapter\",\"endTIme\":1561705312972,\"methodName\":\"getCount\",\"signature\":\"com.talk51.kid.view.AutoScrollViewBanner$TGPageAdapter.getCount()\",\"startTime\":1561705312970,\"totalTime\":2}}";

        methods.add(parseMethodCall(method1));
        methods.add(parseMethodCall(method2));
        methods.add(parseMethodCall(method3));
        methods.add(parseMethodCall(method4));
        Collections.reverse(methods);


    }

    public static CallDrawItem getTest() {
        String json = "{\"args\":[null],\"childs\":[{\"args\":[],\"childs\":[{\"args\":[],\"childs\":[{\"args\":[],\"classC\":\"void com.wangyuelin.app.TestActivity\",\"endTIme\":1560500707229,\"parent\":{\"$ref\":\"$.childs[0].childs[0]\"},\"signature\":\"void com.wangyuelin.app.TestActivity.method3()\",\"startTime\":1560500706928,\"totalTime\":301}],\"classC\":\"void com.wangyuelin.app.TestActivity\",\"endTIme\":1560500707229,\"parent\":{\"$ref\":\"$.childs[0]\"},\"signature\":\"void com.wangyuelin.app.TestActivity.method2()\",\"startTime\":1560500705926,\"totalTime\":1303}],\"classC\":\"void com.wangyuelin.app.TestActivity\",\"endTIme\":1560500707229,\"parent\":{\"$ref\":\"$\"},\"signature\":\"void com.wangyuelin.app.TestActivity.method1()\",\"startTime\":1560500703925,\"totalTime\":3304},{\"args\":[],\"classC\":\"void com.wangyuelin.app.TestActivity\",\"endTIme\":1560500707730,\"parent\":{\"$ref\":\"$\"},\"signature\":\"void com.wangyuelin.app.TestActivity.method4()\",\"startTime\":1560500707229,\"totalTime\":501}],\"classC\":\"void com.wangyuelin.app.TestActivity\",\"endTIme\":1560500707731,\"signature\":\"void com.wangyuelin.app.TestActivity.onCreate(Bundle)\",\"startTime\":1560500703892,\"totalTime\":3839}";
        CallDrawItem callBean =  JSON.parseObject(json, CallDrawItem.class);
        return callBean;
    }

    private static FpsBean parseFps(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        return JSON.parseObject(jsonObject.get("fps").toString(), FpsBean.class);
    }

    private static CallDrawItem parseMethodCall(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        return JSON.parseObject(jsonObject.get("methodCall").toString(), CallDrawItem.class);
    }
}
