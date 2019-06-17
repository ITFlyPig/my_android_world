package com.wangyuelin.performance.show;

import com.wangyuelin.myandroidworld.util.ConvertUtils;
import com.wangyuelin.myandroidworld.util.LogUtil;

public class CallDrawUtil {

    /**
     *  计算每个item对应的柱状图
     *   可以计算得到w和h
     * @param item    要计算的item树
     * @param scaleW  宽度的缩放比例
     * @param methodH 每个item的单位高度
     */
    public static void caculate(CallDrawItem item, float scaleW, int methodH) {
        if (item == null) {
            return;
        }
        LogUtil.d("开始测量方法：" + item.signature);
        item.w = (int) (item.totalTime * scaleW);
        if (item.childs == null || item.childs.size() == 0) {//没有子调用，直接知道高度和宽度
            item.h = methodH;
            LogUtil.d("方法：" + item.signature + " 时间：" + item.totalTime + " 宽度：" + item.w);
            LogUtil.d("没有孩子，直接测量： w:" + item.w + " h:" + item.h);
            return;
        }

        int totalH = 0;
        for (CallDrawItem callDrawItem : item.childs) {
            caculate(callDrawItem, scaleW, methodH);
            totalH += (callDrawItem.h + ConvertUtils.dp2px(3));
        }
        item.h = totalH;

        LogUtil.d("有孩子，测量得到： w:" + item.w + " h:" + item.h);

    }
}
