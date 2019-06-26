package com.wangyuelin.performance.show;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.wangyuelin.myandroidworld.util.ConvertUtils;
import com.wangyuelin.myandroidworld.util.LogUtil;
import com.wangyuelin.myandroidworld.util.ScreenUtils;

import java.util.Iterator;
import java.util.Random;
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
    private long aniTime = 4000;//动画的时间
    private long distancePerFrame;
    private int curColorIndex;//颜色的索引
    private boolean isPause = false;//是否暂停

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
                Color.parseColor("#87CEFA"), Color.parseColor("#4682B4"), Color.parseColor("#4B0082"), Color.parseColor("#20B2AA"),
                Color.parseColor("#228B22"), Color.parseColor("#EEE8AA"), Color.parseColor("#FFDEAD"), Color.parseColor("#FF4500")};
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(ConvertUtils.sp2px(7));
        scaleW = 0.3f;//即一毫秒对应的像素

//        final Handler handler = new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                LogUtil.d("开始添加绘制");
//                MethodQueue.waits.add(MethodQueue.getTest());
//                LogUtil.d("添加完成");
//                invalidate();
//            }
//        };
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                handler.sendEmptyMessage(100);
//            }
//        }, 300);
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                handler.sendEmptyMessage(100);
//            }
//        }, 100);
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                handler.sendEmptyMessage(100);
//            }
//        }, 700);

        startLoop();
        handlePause();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //检查是否有需要绘制的
        if (!isPause) {
            CallDrawItem waitItem = MethodQueue.getWait();
            if (waitItem != null) {
                MethodQueue.methods.add(0, waitItem);
                LogUtil.d("tt","将待绘制的放到绘制列表中，然后更新基线， 之前基线：" + baseLine );
                baseLine -= (waitItem.h + methodCutPointSpace);
                distancePerFrame = getDistancePerFrame(Math.abs(baseLine), aniTime);
                LogUtil.d("tt","更新之后的基线：" + baseLine + "  一帧对应的距离：" + distancePerFrame);
            }
            baseLine += distancePerFrame;
        }

        LogUtil.d("tt","onDraw的基线：" + baseLine);
        if (baseLine < 0) {
            invalidate();
        } else if (baseLine > 0) {
            baseLine = 0;
        }

        //获得最大的宽度
        for (CallDrawItem method : MethodQueue.methods) {
            int w = getW(method);
            if (w > maxW) {
                maxW = w;
            }
        }
        //计算缩放的比例
        if (maxW > 0) {
            scaleW = (ScreenUtils.getScreenWidth() - ConvertUtils.dp2px(50))/ (float)maxW;

            //测试
            scaleW = 5;
        }

        LogUtil.d("计算的缩放比例：" + scaleW + " 屏幕的宽度：" + ScreenUtils.getScreenWidth() + " 最大的宽度：" + maxW);


        //计算尺寸
        for (CallDrawItem method : MethodQueue.methods) {
            CallDrawUtil.caculate(method, scaleW, methodH);
        }

        //布局
        int nextX = 0;
        int nextY = baseLine;
        Iterator<CallDrawItem> it = MethodQueue.methods.iterator();
        while (it.hasNext()) {
            CallDrawItem next = it.next();
            if (nextY >= ScreenUtils.getScreenHeight()) {
                MethodQueue.methods.remove(next);//超出屏幕的边界，直接删除
            }
            layout(next, nextY, nextX);
            nextY += (next.h + ConvertUtils.dp2px(5));//得加上代码片段之间的间隔
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
        if (item.color == 0) {
            item.color = getRandomColor();
        }
        mPaint.setColor(item.color);
        canvas.drawRect(item.pos, mPaint);
        //绘制名字
        drawRightTop(getName(item.signature), item.pos, canvas);

        //绘制孩子
        if (item.childs != null) {
            for (CallDrawItem child : item.childs) {
                draw(child, canvas);
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
        if (curColorIndex >= colors.length) {
            curColorIndex = 0;
        }
        int color = colors[curColorIndex];
        curColorIndex++;
        return color;
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
        float x = 0;
        float y = 0;
        if (rect.width() < w) {//绘制到外面
            y = rect.top + h;
            x = rect.left;

        } else {
            x = rect.right - w;
            y = rect.top + h;
        }

        canvas.drawText(methodName, x, y, mPaint);
    }

    /**
     * 据签名获得方法的名称
     * @param signature
     * @return
     */
    private String getName(String signature) {
        if (TextUtils.isEmpty(signature)) {
            return "";
        }
        int index = signature.lastIndexOf(".");
        if (index <= 0) {
            return "";
        }
        return signature.substring(index + 1);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 获取宽度
     * @param item
     * @return
     */
    public int getW(CallDrawItem item) {
        if (item == null) {
            return 0;
        }
        return  (int) item.totalTime;
    }

    /**
     * 据距离和时间或者每一帧应该移动的距离
     * @param distance  距离
     * @param duration  时间
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
               LogUtil.d("检查是否有需要绘制的");
               if (MethodQueue.waits.size() > 0) {
                   post(() -> invalidate());
               }
           }
       }, 1000, 2000);
    }

    /**
     * 处理暂停
     */
    private void handlePause() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    isPause = true;
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP){
                    isPause = false;
                }
                return false;
            }
        });
    }
}
