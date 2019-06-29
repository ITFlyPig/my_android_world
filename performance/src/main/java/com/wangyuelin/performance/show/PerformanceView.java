package com.wangyuelin.performance.show;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.wangyuelin.myandroidworld.util.ConvertUtils;
import com.wangyuelin.myandroidworld.util.ScreenUtils;
import com.wangyuelin.performance.fps.FpsBean;

import java.util.Collections;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 显示方法调用的柱状图
 * 以后考虑使用SurfaceView实现
 * 横轴：表示调用的时间序列
 * 纵轴：表示方法的包含关系或者调用序列
 */
public class PerformanceView extends View {
    private Paint mPaint;
    private int[] colors;//柱子的颜色池
    public static int methodH;//方法对应柱子的最小高度
    private float scaleW;//横向缩放比例
    private int maxW;//最大的宽度


    private int baseLine;//绘制的基线
    private int methodCutPointSpace = ConvertUtils.dp2px(5);//代码片段之间的间隔
    private long aniTime = 400;//动画的时间
    private long distancePerFrame;
    private int curColorIndex;//颜色的索引
    private boolean isAutoScrollPause = false;//是否自动股滚动的标志
    private GestureDetector mGestureDetector;
    private FlingHelper flingHelper;
    private boolean isFling;
    private int fpsLinew;//fps的线宽
    private int lineStartX;

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
        colors = new int[]{Color.parseColor("#8B008B"), Color.parseColor("#4B0082"), Color.parseColor("#483D8B"),
                Color.parseColor("#0000FF"), Color.parseColor("#191970"), Color.parseColor("#778899"), Color.parseColor("#00CED1")
                , Color.parseColor("#2E8B57"), Color.parseColor("#808000"), Color.parseColor("#DAA520"), Color.parseColor("#FF8C00")
                , Color.parseColor("#8B4513"), Color.parseColor("#FF4500"), Color.parseColor("#696969"), Color.parseColor("#800000")};
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(ConvertUtils.sp2px(7));
//        scaleW = 0.3f;//即一毫秒对应的像素

        startLoop();

        mGestureDetector = new GestureDetector(getContext(), new MyGestureListener());


        setOnTouchListener((v, event) -> {
            mGestureDetector.onTouchEvent(event);
            return true;
        });

        flingHelper = new FlingHelper(getContext(), flingListener);
        fpsLinew = ConvertUtils.dp2px(1);
        lineStartX = ScreenUtils.getScreenWidth() - ConvertUtils.dp2px(10);
        mPaint.setStrokeWidth(fpsLinew);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //检查是否有需要绘制的
        if (!isAutoScrollPause) {
            CallDrawItem waitItem = MethodQueue.getWait();
            if (waitItem != null) {
                MethodQueue.methods.add(0, waitItem);
//                LogUtil.d("tt", "将待绘制的放到绘制列表中，然后更新基线， 之前基线：" + baseLine);
                baseLine -= (waitItem.h + methodCutPointSpace);
                distancePerFrame = getDistancePerFrame(Math.abs(baseLine), aniTime);
//                LogUtil.d("tt", "更新之后的基线：" + baseLine + "  一帧对应的距离：" + distancePerFrame);
            }
            baseLine += distancePerFrame;
        }


        if (isFling) {//Fling计算
            isFling = flingHelper.onDraw();
        }

//        LogUtil.d("tt", "onDraw的基线：" + baseLine);
        if (baseLine < 0) {
            invalidate();
        } else if (baseLine > 0) {
            baseLine = 0;
        }

        System.out.println("onDraw的基线：" + baseLine);
        //获得最大的宽度
        for (CallDrawItem method : MethodQueue.methods) {
            int w = getW(method);
            if (w > maxW) {
                maxW = w;
            }
        }
        //计算缩放的比例
        if (maxW > 0) {
            scaleW = (ScreenUtils.getScreenWidth() - ConvertUtils.dp2px(100)) / (float) maxW;//计算缩放比例是为了填充屏幕宽度，但是留一定距离出来
        }

//        LogUtil.d("计算的缩放比例：" + scaleW + " 屏幕的宽度：" + ScreenUtils.getScreenWidth() + " 最大的宽度：" + maxW);


        //计算尺寸
        for (CallDrawItem method : MethodQueue.methods) {
            CallDrawUtil.caculate(method, scaleW, methodH);
        }


        Iterator<CallDrawItem> it = MethodQueue.methods.iterator();
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
            if (count > 100) {//队列中放100个代码调用片段
                it.remove();
                count--;
            }
        }
        //布局
        int nextX = 0;
        int nextY = baseLine;
        for (CallDrawItem method : MethodQueue.methods) {
            layout(method, nextY, nextX);
            nextY += (method.h + ConvertUtils.dp2px(5));//得加上代码片段之间的间隔
        }


        //绘制
        CallDrawItem pre = null;
        for (CallDrawItem method : MethodQueue.methods) {
            draw(method, canvas);
            drawFps(method, pre, canvas);
            pre = method;
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
        if (item.color == 0) {
            item.color = getRandomColor();
        }
        mPaint.setColor(item.color);
        canvas.drawRect(item.pos, mPaint);
        //绘制名字
//        Log.d("wdd", "drawRightTop绘制文字：" + getName(item) + " 类：" + item.classC);
        item.textPos = drawRightTop(item, canvas);
        //绘制孩子
        if (item.childs != null && item.totalTime > 0) {
            for (int i = 0; i < item.childs.size(); i++) {
                draw(item.childs.get(i), canvas);
            }
        }

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
//        LogUtil.d("方法：" + item.signature + " 测量得到的坐标：left:" + pos.left + " top:" + pos.top + " right:" + pos.right + " bottom:" + pos.bottom);

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
     * 产生随机的颜色
     *
     * @return
     */
    private int getRandomColor() {
        if (curColorIndex >= colors.length) {
            curColorIndex = 0;
        }
        int color = colors[curColorIndex];
        curColorIndex++;
        return color;
    }

    /**
     * 将文字绘制到右上角
     */
    private Rect drawRightTop(CallDrawItem item, Canvas canvas) {
        String methodName = getName(item);
        //方法加上耗时时间
        methodName += "(" + item.totalTime + ")";
        Rect rect = item.pos;
        if (TextUtils.isEmpty(methodName) || rect == null) {
            return null;
        }

        mPaint.setColor(Color.BLACK);
        Rect temp = new Rect();
        mPaint.getTextBounds(methodName, 0, methodName.length(), temp);
        int w = temp.width();
        int h = temp.height();

        float x = rect.right;
        float y = rect.top + h;

        //开始调整自己的文字位置，避免和父文字得位置重叠
        if (item.parent != null && item.parent.textPos != null) {
            Rect parentTextPos = item.parent.textPos;
            if (x + w > parentTextPos.left && y - h < parentTextPos.bottom) {//自己的text和父text有重叠，调整
                //调整到父text的下面
                y = parentTextPos.bottom + h;
            }
        }

        mPaint.setColor(item.color);
        canvas.drawText(methodName, x, y, mPaint);

        //记录文字的位置
        return new Rect((int) x, (int) (y - temp.height()), (int) (x + temp.width()), (int) y);
    }

    /**
     * 获取名称 格式：类.方法
     *
     * @param item
     * @return
     */
    private String getName(CallDrawItem item) {
        if (item == null) {
            return "";
        }
        if (item.className == null) {
            item.className = "";
        }
        if (item.methodName == null) {
            item.methodName = "";
        }
        return item.className + "." + item.methodName;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 获取宽度
     *
     * @param item
     * @return
     */
    public int getW(CallDrawItem item) {
        if (item == null) {
            return 0;
        }
        return (int) item.totalTime;
    }

    /**
     * 据距离和时间或者每一帧应该移动的距离
     *
     * @param distance 距离
     * @param duration 时间
     * @return
     */
    private int getDistancePerFrame(int distance, long duration) {
        if (distance == 0) {
            return 0;
        }
        if (duration == 0) {
            return distance;
        }

        return (int) (distance / (duration / 16.6F));
    }

    private void startLoop() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
//                LogUtil.d("检查是否有需要绘制的");
                if (MethodQueue.waits.size() > 0) {
                    post(() -> invalidate());
                }
            }
        }, 1000, 2000);
    }

    private long lastDwon;

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onDown(MotionEvent e) {
            System.out.println("onDown :" + (System.currentTimeMillis() - lastDwon));
            if (System.currentTimeMillis() - lastDwon < 250) {

            } else {
                isAutoScrollPause = true;
            }

            lastDwon = System.currentTimeMillis();
            flingHelper.cancelFling();
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
//            isAutoScrollPause = true;
            return super.onSingleTapConfirmed(e);
        }

        //继续滑动
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            System.out.println("onDoubleTap");
            isAutoScrollPause = false;
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            System.out.println("onScroll");
            isAutoScrollPause = true;
            baseLine -= distanceY;
            invalidate();
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            System.out.println("onFling");
            isFling = true;
            isAutoScrollPause = true;
            int bottom = getBottom();
            flingHelper.fling(baseLine, -(Math.abs(baseLine) + Math.abs(bottom)), 0, (int) velocityY);
            return super.onFling(e1, e2, velocityX, velocityY);
        }


        /**
         * 获取所有需要绘制的View的最底部位置
         *
         * @return
         */
        private int getBottom() {
            if (MethodQueue.methods.size() == 0) {
                return 0;
            }
            return MethodQueue.methods.get(MethodQueue.methods.size() - 1).pos.bottom;
        }
    }


    private FlingHelper.FlingListener flingListener = new FlingHelper.FlingListener() {
        @Override
        public void onFling(int cur) {
            baseLine = cur;
            invalidate();
        }
    };


    public interface Funcs{
        void clear();//清除显示
    }

    private Funcs funcListener = () -> {
        MethodQueue.methods.clear();
        MethodQueue.waits.clear();
        invalidate();
    };

    /**
     * 对外部提供的功能
     * @return
     */
    public Funcs getFuncs() {
        return funcListener;
    }

    private void drawFps(CallDrawItem curDrawItem, CallDrawItem pre, Canvas canvas) {
        if (curDrawItem == null) {
            return;
        }
        if (isDrawFps(curDrawItem, pre)) {
            mPaint.setColor(Color.RED);
            canvas.drawLine(lineStartX, curDrawItem.pos.top, lineStartX, curDrawItem.pos.bottom + methodCutPointSpace, mPaint);
        }
    }

    private boolean isDrawFps(CallDrawItem cur, CallDrawItem pre) {
        Log.d("fps", "查看这个时间段是否有fps");
        if (cur == null ) {
            return false;
        }
        //判断是否需要绘制fps
        FpsBean fpsBean = getFps(cur, pre);
        if (fpsBean == null) {
            return false;
        }
        return true;
    }

    /**
     * 获取fps队列中的最大时间
     * @return
     */
    private long getFpsMaxTime() {
        if (MethodQueue.fps.size() > 0) {
            return MethodQueue.fps.get(MethodQueue.fps.size() - 1).endTime;
        }
        return 0;
    }

    /**
     * 获得Fps队列中的最小时间
     * @return
     */
    private long getFpsMinTime() {
        if (MethodQueue.fps.size() == 0) {
            return 0;
        }
        return MethodQueue.methods.get(0).startTime;
    }

    /**
     * 据开始时间获得包含对应时间的fps统计bean
     * @return
     */
    private FpsBean getFps(CallDrawItem cur, CallDrawItem pre) {
       for (int i = 0; i < MethodQueue.fps.size(); i++) {
           FpsBean fpsBean = MethodQueue.fps.get(i);

           //判断是否有fps的结束部分包含这个方法片段
           if (pre == null) {
               if (fpsBean.endTime > cur.startTime) {
                   return fpsBean;
               }
           } else {

               if (fpsBean.endTime <= pre.startTime  && fpsBean.endTime > cur.startTime
                       || (fpsBean.startTime < cur.endTIme && fpsBean.endTime > pre.startTime)){
                   return fpsBean;
               }
           }
       }
       return null;

    }

}
