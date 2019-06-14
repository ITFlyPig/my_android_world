package com.wangyuelin.performance.show;

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
        CallDrawItem bean = new CallDrawItem("methos1", 49L, 1L, 50L, "class1", null);
        CallDrawItem bean11 = new CallDrawItem("methos11", 1L, 9L, 10L, "class11", null);
        CallDrawItem bean111 = new CallDrawItem("methos111", 1L, 6L, 7L, "class111", null);
        CallDrawItem bean12 = new CallDrawItem("methos12", 11L, 6L, 18L, "class11", null);
        bean.childs = new ArrayList<>();
        bean.childs.add(bean11);
        bean.childs.add(bean12);
        bean11.childs = new ArrayList<>();
        bean11.childs.add(bean111);

        methods.add(bean);
    }
}
