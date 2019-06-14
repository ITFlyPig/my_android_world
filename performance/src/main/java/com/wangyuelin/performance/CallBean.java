package com.wangyuelin.performance;

import android.text.TextUtils;

import java.util.List;

/**
 * 记录一次方法调用的相关信息
 */
public class CallBean {
    public String signature;//方法的签名
    public long totalTime;//花费的时间
    public long startTime;//开始调用时的时间
    public long endTIme;//结束时的事件
    public String classC;//所属的Class
    public Object[] args;//方法的参数
    public List<CallBean> childs;//被调用的子方法
    public CallBean parent;//父调用

    public CallBean() {
    }

    public CallBean(String signature, long totalTime, long startTime, long endTIme, String classC, Object[] args) {
        this.signature = signature;
        this.totalTime = totalTime;
        this.startTime = startTime;
        this.endTIme = endTIme;
        this.classC = classC;
        this.args = args;
    }

    @Override
    public String toString() {
        String res = "方法：" + signature + "|| 耗时：" + totalTime + " ms" +
                "";
        String chidStr = "";
        if (childs != null) {
            int count = 0;
            String temp = "";
            for (CallBean child : childs) {
                if (TextUtils.isEmpty(temp)) {
                    temp += "[";
                }
                temp += child.toString() + ";";
            }
            if (!TextUtils.isEmpty(temp)) {
                temp += "]";
            }

            chidStr += " 孩子 " + count + " = " + temp + ";";
        }
        return res + chidStr;
    }
}
