package com.wangyuelin.performance.show;

import com.alibaba.fastjson.JSON;
import com.wangyuelin.myandroidworld.util.LogUtil;
import com.wangyuelin.performance.WebSocketHelper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 待绘制方法的队列
 */
public class MethodQueue {
    public static List<CallDrawItem> methods;//已经绘制的列表
    public static List<CallDrawItem> waits;//等待绘制的列表

    static {
        methods = Collections.synchronizedList(new LinkedList<CallDrawItem>());
        waits = Collections.synchronizedList(new LinkedList<CallDrawItem>());

        WebSocketHelper.getInstance().setWebSocketListener(new WebSocketHelper.SocketListener() {
            @Override
            public void onMessage(String msg) {
                CallDrawItem callBean =  JSON.parseObject(msg, CallDrawItem.class);
                waits.add(callBean);
                LogUtil.d("将Socket下发的数据添加到waits中");
            }
        });

//        makeTest();

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
        String json = "{\"args\":[2],\"childs\":[{\"args\":[2,-1,true],\"childs\":[{\"args\":[],\"classC\":\"MainApplication com.talk51.kid.core.app.MainApplication\",\"endTIme\":1560761009768,\"parent\":{\"$ref\":\"$.childs[0]\"},\"signature\":\"MainApplication com.talk51.kid.core.app.MainApplication.inst()\",\"startTime\":1560761009768,\"totalTime\":10},{\"args\":[],\"childs\":[{\"args\":[],\"classC\":\"MainApplication com.talk51.kid.core.app.MainApplication\",\"endTIme\":1560761009769,\"parent\":{\"$ref\":\"$.childs[0].childs[1]\"},\"signature\":\"MainApplication com.talk51.kid.core.app.MainApplication.inst()\",\"startTime\":1560761009768,\"totalTime\":11},{\"args\":[],\"classC\":\"int com.talk51.kid.core.app.MainApplication\",\"endTIme\":1560761009769,\"parent\":{\"$ref\":\"$.childs[0].childs[1]\"},\"signature\":\"int com.talk51.kid.core.app.MainApplication.getCurrentIndex()\",\"startTime\":1560761009769,\"totalTime\":10}],\"classC\":\"Fragment com.talk51.kid.core.app.HomeActivity\",\"endTIme\":1560761009769,\"parent\":{\"$ref\":\"$.childs[0]\"},\"signature\":\"Fragment com.talk51.kid.core.app.HomeActivity.getCurrentFragment()\",\"startTime\":1560761009768,\"totalTime\":11},{\"args\":[],\"classC\":\"int com.talk51.kid.core.app.MainApplication\",\"endTIme\":1560761009769,\"parent\":{\"$ref\":\"$.childs[0]\"},\"signature\":\"int com.talk51.kid.core.app.MainApplication.getCurrentIndex()\",\"startTime\":1560761009769,\"totalTime\":10},{\"args\":[2],\"classC\":\"void com.talk51.kid.core.app.MainApplication\",\"endTIme\":1560761009770,\"parent\":{\"$ref\":\"$.childs[0]\"},\"signature\":\"void com.talk51.kid.core.app.MainApplication.setCurrentIndex(int)\",\"startTime\":1560761009770,\"totalTime\":10},{\"args\":[2],\"classC\":\"void com.talk51.kid.core.app.HomeActivity\",\"endTIme\":1560761009771,\"parent\":{\"$ref\":\"$.childs[0]\"},\"signature\":\"void com.talk51.kid.core.app.HomeActivity.showTab(int)\",\"startTime\":1560761009770,\"totalTime\":11}],\"classC\":\"void com.talk51.kid.core.app.HomeActivity\",\"endTIme\":1560761009771,\"parent\":{\"$ref\":\"$\"},\"signature\":\"void com.talk51.kid.core.app.HomeActivity.switchTab(int, int, boolean)\",\"startTime\":1560761009768,\"totalTime\":13}],\"classC\":\"void com.talk51.kid.core.app.HomeActivity\",\"endTIme\":1560761009771,\"signature\":\"void com.talk51.kid.core.app.HomeActivity.onCheckedChanged(int)\",\"startTime\":1560761009767,\"totalTime\":14}\n";
        CallDrawItem callBean =  JSON.parseObject(json, CallDrawItem.class);
        methods.add(callBean);
        LogUtil.d("wyl", "转换后的对象：" + callBean.toString());
    }

    public static CallDrawItem getTest() {
        String json = "{\"args\":[null],\"childs\":[{\"args\":[],\"childs\":[{\"args\":[],\"childs\":[{\"args\":[],\"classC\":\"void com.wangyuelin.app.TestActivity\",\"endTIme\":1560500707229,\"parent\":{\"$ref\":\"$.childs[0].childs[0]\"},\"signature\":\"void com.wangyuelin.app.TestActivity.method3()\",\"startTime\":1560500706928,\"totalTime\":301}],\"classC\":\"void com.wangyuelin.app.TestActivity\",\"endTIme\":1560500707229,\"parent\":{\"$ref\":\"$.childs[0]\"},\"signature\":\"void com.wangyuelin.app.TestActivity.method2()\",\"startTime\":1560500705926,\"totalTime\":1303}],\"classC\":\"void com.wangyuelin.app.TestActivity\",\"endTIme\":1560500707229,\"parent\":{\"$ref\":\"$\"},\"signature\":\"void com.wangyuelin.app.TestActivity.method1()\",\"startTime\":1560500703925,\"totalTime\":3304},{\"args\":[],\"classC\":\"void com.wangyuelin.app.TestActivity\",\"endTIme\":1560500707730,\"parent\":{\"$ref\":\"$\"},\"signature\":\"void com.wangyuelin.app.TestActivity.method4()\",\"startTime\":1560500707229,\"totalTime\":501}],\"classC\":\"void com.wangyuelin.app.TestActivity\",\"endTIme\":1560500707731,\"signature\":\"void com.wangyuelin.app.TestActivity.onCreate(Bundle)\",\"startTime\":1560500703892,\"totalTime\":3839}";
        CallDrawItem callBean =  JSON.parseObject(json, CallDrawItem.class);
        return callBean;
    }
}
