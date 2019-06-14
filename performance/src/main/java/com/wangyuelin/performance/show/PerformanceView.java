package com.wangyuelin.performance.show;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.wangyuelin.myandroidworld.util.ConvertUtils;
import com.wangyuelin.myandroidworld.util.LogUtil;
import com.wangyuelin.myandroidworld.util.ScreenUtils;

import java.util.Iterator;
import java.util.Random;

/**
 * 显示方法调用的柱状图
 * 以后考虑使用SurfaceView实现
 */
public class PerformanceView extends View {
    private Paint mPaint;
    private int[] colors;//柱子的颜色池
    private int methodH;//方法对应柱子的最小高度
    private float scaleW;//横向缩放比例


    public PerformanceView(Context context) {
        this(context, null);
    }

    public PerformanceView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PerformanceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        methodH = ConvertUtils.dp2px(30);
        colors = new int[]{Color.parseColor("#ADD8E6"), Color.parseColor("#00BFFF"), Color.parseColor("#87CEEB"),
                Color.parseColor("#87CEFA"), Color.parseColor("#4682B4")};
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(ConvertUtils.sp2px(12));
        scaleW = ConvertUtils.dp2px(10);//即一毫秒对应的像素
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (CallDrawItem method : MethodQueue.methods) {
            caculate(method);
        }
        int nextX = 0;
        int nextY = 0;

        Iterator<CallDrawItem> it = MethodQueue.methods.iterator();
        while (it.hasNext()) {
            CallDrawItem next = it.next();
            if (nextY >= ScreenUtils.getScreenHeight()) {
                MethodQueue.methods.remove(next);//超出屏幕的边界，直接删除
            }
            layout(next, nextY, nextX);
            nextY += (next.h + 10);//得加上代码片段之间的间隔
        }
        //绘制
        for (CallDrawItem method : MethodQueue.methods) {
            draw(method, canvas);
        }

    }


    /**
     * 绘制柱状图
     *
     * @param item
     * @param canvas
     */
    private void draw(CallDrawItem item, Canvas canvas) {
        if (item == null) {
            return;
        }

        //绘制自己
        mPaint.setColor(getRandomColor());
        canvas.drawRect(item.pos, mPaint);
        //绘制名字
        drawRightTop(item.signature, item.pos, canvas);

        //绘制孩子
        if (item.childs != null) {
            for (CallDrawItem child : item.childs) {
                draw(child, canvas);
            }
        }

    }


    /**
     * 计算每个item对应的柱状图
     * 可以计算得到w和h
     */
    public void caculate(CallDrawItem item) {
        if (item == null) {
            return;
        }
        LogUtil.d("开始测量方法：" + item.signature);
        if (item.childs == null || item.childs.size() == 0) {//没有子调用，直接知道高度和宽度
            item.h = methodH;
            item.w = (int) ((item.endTIme - item.startTime) * scaleW);
            LogUtil.d("没有孩子，直接测量： w:" + item.w + " h:" + item.h);
            return;
        }

        int totalW = 0;
        int totalH = 0;
        for (CallDrawItem callDrawItem : item.childs) {
            caculate(callDrawItem);
            totalW += callDrawItem.w;
            totalH += callDrawItem.h;
        }
        item.w = totalW;
        item.h = totalH;

        LogUtil.d("有孩子，测量得到： w:" + item.w + " h:" + item.h);

    }

    /**
     * 更新每个元素的绘制位置
     *
     * @param item
     */
    private void layout(CallDrawItem item, int y, int x) {
        if (item == null) {
            return;
        }

        //更新自己的位置
        Rect pos = new Rect(x, y, x + item.w, y + item.h);
        item.pos = pos;
        LogUtil.d("方法：" + item.signature + " 测量得到的坐标：left:" + pos.left + " top:" + pos.top + " right:" + pos.right + " bottom:" + pos.bottom);

        //更新孩子的位置
        if (item.childs != null && item.childs.size() > 0) {
            int nextX = x;
            int nextY = y;
            for (CallDrawItem child : item.childs) {
                layout(child, nextY, nextX);
                nextX += child.w;
                nextY += child.h;
            }
        }
    }

    /**
     * 产生随机数
     *
     * @param min
     * @param max
     * @return
     */
    private int random(int min, int max) {
        return new Random().nextInt(max - min) + min;
    }

    /**
     * 产生随机的颜色
     *
     * @return
     */
    private int getRandomColor() {
        return colors[random(0, colors.length)];
    }

    /**
     * 将文字绘制到右上角
     * @param methodName
     */
    private void drawRightTop(String methodName, Rect rect, Canvas canvas) {
        if (TextUtils.isEmpty(methodName) || rect == null) {
            return;
        }
        mPaint.setColor(Color.BLACK);
        Rect temp = new Rect();
        mPaint.getTextBounds(methodName, 0, methodName.length(), temp);
        int w = temp.width();
        int h = temp.height();

        canvas.drawText(methodName, rect.right - w, rect.top + h, mPaint);


    }

}
