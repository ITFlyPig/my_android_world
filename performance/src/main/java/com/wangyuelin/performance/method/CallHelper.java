package com.wangyuelin.performance.method;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class CallHelper {
    private HashMap<Thread, Stack<CallBean>> callMap;
    private CallListener callListener;

    public CallHelper(CallListener callListener) {
        this.callListener = callListener;
        callMap = new HashMap<>();//记录每个线程对应的调用树
    }

    public void setCallListener(CallListener callListener) {
        this.callListener = callListener;
    }

    /**
     * 记录一次方法的开始执行
     * @param signature
     * @param args
     */
    public void recordStart(String signature, Object[] args) {
        if (TextUtils.isEmpty(signature)) {
            return;
        }
        CallBean newCall = new CallBean(signature, -1, System.currentTimeMillis(),
                -1, getCalss(signature), args);
        Thread thread = Thread.currentThread();
        Stack<CallBean> stack = callMap.get(thread);
        if (stack == null) {
            stack = new Stack<>();
            callMap.put(thread, stack);
        }
        if (!stack.isEmpty()) {//表示此次调用时一个方法调用树的子调用
            CallBean parentCall = stack.peek();
            //将这次的调用放进合适的父方法调用位置
            if (parentCall.childs == null) {
                parentCall.childs = new ArrayList<>();
            }

            parentCall.childs.add(newCall);
            newCall.parent = parentCall;
        }
        stack.push(newCall);
    }

    /**
     * 记录一个方法执行的结束
     * @param signature
     */
    public void recordEnd(String signature) {
        if (TextUtils.isEmpty(signature)) {
            return;
        }
        Thread thread = Thread.currentThread();
        Stack<CallBean> stack = callMap.get(thread);
        if (stack == null || stack.isEmpty()) {
            return;
        }
        CallBean curCall = stack.pop();
        if (!TextUtils.equals(curCall.signature, signature)) {//确保是同一个方法
            return;
        }
        curCall.endTIme = System.currentTimeMillis();
        curCall.totalTime = curCall.endTIme - curCall.startTime;

        //一个方法调用树结束了
        if (stack.isEmpty()) {
            if (callListener != null) {
                callListener.onCallDone(curCall);
            }
        }
    }

    /**
     * 监听一次方法调用完成
     */
    public interface CallListener {
        void onCallDone(CallBean callBean);

    }

    /**
     * 据方法的签名解析得到方法所属的类
     * @param signature
     * @return
     */
    private String getCalss(String signature) {
        if (TextUtils.isEmpty(signature)) {
            return "";
        }
        int end = signature.lastIndexOf(".");
        if (end > 0) {
            return signature.substring(0, end);
        }
        return "";
    }

}
