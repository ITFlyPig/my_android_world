package com.wangyuelin.performance.show;

import com.alibaba.fastjson.JSON;
import com.wangyuelin.myandroidworld.util.LogUtil;
import com.wangyuelin.performance.CallBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 待绘制方法的队列
 */
public class MethodQueue {
    public static List<CallDrawItem> methods;

    static {
        methods = Collections.synchronizedList(new LinkedList<CallDrawItem>());

        makeTest();
    }

    public static void makeTest() {
        String json = "{\"args\":[null],\"childs\":[{\"args\":[],\"childs\":[{\"args\":[],\"childs\":[{\"args\":[],\"classC\":\"void com.wangyuelin.app.TestActivity\",\"endTIme\":1560500707229,\"parent\":{\"$ref\":\"$.childs[0].childs[0]\"},\"signature\":\"void com.wangyuelin.app.TestActivity.method3()\",\"startTime\":1560500706928,\"totalTime\":301}],\"classC\":\"void com.wangyuelin.app.TestActivity\",\"endTIme\":1560500707229,\"parent\":{\"$ref\":\"$.childs[0]\"},\"signature\":\"void com.wangyuelin.app.TestActivity.method2()\",\"startTime\":1560500705926,\"totalTime\":1303}],\"classC\":\"void com.wangyuelin.app.TestActivity\",\"endTIme\":1560500707229,\"parent\":{\"$ref\":\"$\"},\"signature\":\"void com.wangyuelin.app.TestActivity.method1()\",\"startTime\":1560500703925,\"totalTime\":3304},{\"args\":[],\"classC\":\"void com.wangyuelin.app.TestActivity\",\"endTIme\":1560500707730,\"parent\":{\"$ref\":\"$\"},\"signature\":\"void com.wangyuelin.app.TestActivity.method4()\",\"startTime\":1560500707229,\"totalTime\":501}],\"classC\":\"void com.wangyuelin.app.TestActivity\",\"endTIme\":1560500707731,\"signature\":\"void com.wangyuelin.app.TestActivity.onCreate(Bundle)\",\"startTime\":1560500703892,\"totalTime\":3839}";
        CallDrawItem callBean =  JSON.parseObject(json, CallDrawItem.class);
        methods.add(callBean);
        LogUtil.d("wyl", "转换后的对象：" + callBean.toString());
    }
}