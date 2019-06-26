package com.wangyuelin.uiwidgetmodule;

import android.animation.ValueAnimator;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.wangyuelin.myandroidworld.util.Utils;
import com.wangyuelin.performance.MethodCall;

import java.util.ArrayList;
import java.util.List;

/**
 * 原生的ScrollView支持水平的或者垂直的，这个不仅支持水平和垂直，还支持任意方向的
 * 支持的滑动模式"
 * 1、水平滑动
 * 2、垂直滑动
 * 3、任意方向滑动
 * <p>
 * ScrollView只支持整体的滑动，这个可以支持子View中某一个直接子View的滑动
 * 注意：是直接子View
 */
public class EasyScrollView extends RelativeLayout {
    private static final String TAG = EasyScrollView.class.getName();


    private ViewDragHelper mDragHelper;
    private List<View> mDragViews; //可拖拽的View的集合
    private int mScrollMode = ScrollMode.ALL_MODE;//滑动的模式
    private ViewPositionListener mPositionChangeListener;

    public EasyScrollView(Context context) {
        this(context, null);
    }

    public EasyScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDragHelper = ViewDragHelper.create(this, 1.0f, new DragCallback());

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDragViews = getDragViews();
        if (mDragViews.size() == 0) {
            Log.e(TAG, "没有可拖动的View");
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    private class DragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(@NonNull View view, int i) {
            if (mDragViews == null) {
                return false;
            }
            for (View dragView : mDragViews) {
                if (dragView == view) {
                    return true;
                }
            }
            return false;
        }


        /**
         * 水平滑动
         *
         * @param child
         * @param left
         * @param dx
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {

            if (mScrollMode == ScrollMode.V_MODE) {
                left -= dx;//不动
            } else if (mScrollMode == ScrollMode.H_MODE || mScrollMode == ScrollMode.ALL_MODE) {
                int childW = child.getWidth();
                int parentW = getWidth();
                if (childW <= parentW) {//不超出父边界
                    if (left < 0) {//限制左边
                        left = 0;
                    } else if (left + childW > parentW) {//限制右边
                        left = parentW - childW;
                    }
                } else {//可以超出边界
                    if (dx < 0) {//向左移动，限制右边
                        int right = left + childW;
                        if (right < parentW) {
                            left = parentW - childW;
                        }

                    } else {//向右移动，限制左边
                        if (left > 0) {
                            left = 0;
                        }
                    }

                }
            }

            return left;
        }

        /**
         * 垂直滑动
         *
         * @param child
         * @param top
         * @param dy
         * @return
         */
        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            if (mScrollMode == ScrollMode.H_MODE) {
                top -= dy;
            } else if (mScrollMode == ScrollMode.V_MODE || mScrollMode == ScrollMode.ALL_MODE) {
                int childH = child.getHeight();
                int parentH = getHeight();
                if (childH <= parentH) {//不超出父边界
                    if (top < 0) {//限制上边
                        top = 0;
                    } else if (top + childH > parentH) {//限制下边
                        top = parentH - childH;
                    }
                } else {//可以超出边界
                    if (dy < 0) {//向上移动，限制下边
                        int bottom = top + childH;
                        if (bottom < parentH) {
                            top = parentH - childH;
                        }

                    } else {//向下移动，限制上边
                        if (top > 0) {
                            top = 0;
                        }
                    }

                }

            }
            return top;
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            int minLeft = 0;
            int maxLeft = 0;
            int minTop = 0;
            int maxTop = 0;


            int childW = releasedChild.getWidth();
            int parentW = getWidth();
            int childH = releasedChild.getHeight();
            int parentH = getHeight();

            if (mScrollMode == ScrollMode.V_MODE) {//禁止水平滑动
                minLeft = maxLeft = releasedChild.getLeft();
                if (childH <= parentH) {
                    minTop = 0;
                    maxTop = parentH - childH;
                } else {
                    minTop = parentH - childH;
                    maxTop = 0;
                }

            } else if (mScrollMode == ScrollMode.H_MODE) {//禁止垂直滑动
                minTop = maxTop = releasedChild.getTop();
                if (childW <= parentW) {
                    minLeft = 0;
                    maxLeft = parentW - childW;
                } else {
                    minLeft = parentW - childW;
                    maxLeft = 0;
                }

            } else {
                if (childW <= parentW) {
                    minLeft = 0;
                    maxLeft = parentW - childW;
                } else {
                    minLeft = parentW - childW;
                    maxLeft = 0;
                }
                if (childH <= parentH) {
                    minTop = 0;
                    maxTop = parentH - childH;
                } else {
                    minTop = parentH - childH;
                    maxTop = 0;
                }
            }


            //在这里实现Fling
            mDragHelper.flingCapturedView(minLeft, minTop, maxLeft, maxTop);
            invalidate();

        }


        /**
         * 在这里可以根据当前View的改变而改变别的View
         *
         * @param changedView
         * @param left
         * @param top
         * @param dx
         * @param dy
         */
        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            if (mPositionChangeListener != null) {
                mPositionChangeListener.onViewPositionChanged(changedView, left, top, dx, dy);
            }
        }
    }


    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    /**
     * 获取可以拖动的View，默认是所有子View，重写这个方法提供自己想拖动的View，必须是这个的直接子View
     *
     * @return
     */
    protected List<View> getDragViews() {
        int size = getChildCount();
        List<View> views = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            views.add(getChildAt(i));
        }
        return views;

    }

    public interface ScrollMode {
        int H_MODE = 1;//水平滑动
        int V_MODE = 2;//垂直滑动
        int ALL_MODE = 3;//任意方向滑动
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //自己测量，目的：正常情况下，父View的大小不会小于子View的大小，自己测量为了子View的大小不做限制，父View的大小可以小于子View的大小。
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mScrollMode == ScrollMode.H_MODE) {//水平方向的子View测量不做限制
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.UNSPECIFIED);
        } else if (mScrollMode == ScrollMode.V_MODE) {//垂直方向的子View测量不做显示
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED);
        } else {//都不做限制
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.UNSPECIFIED);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED);
        }

        ViewGroup.LayoutParams params = getLayoutParams();

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);//利用RelativeLayout的计算规则

        setMeasuredDimension(getSelfMeasureSpec(width, params.width), getSelfMeasureSpec(height, params.height));


    }


    /**
     * @param size          父View建议的尺寸
     * @param childDimesion 代码写的尺寸
     * @return
     */
    public int getSelfMeasureSpec(int size, int childDimesion) {
        int spec;
        if (childDimesion > 0) {
            spec = MeasureSpec.makeMeasureSpec(childDimesion, MeasureSpec.EXACTLY);
        } else {
            spec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);

        }
        return spec;

    }

    /**
     * View位置改变的回调，监听这个View就
     */
    public interface ViewPositionListener {
        void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy);
    }

    /**
     * 设置可拖拽的View
     *
     * @param mDragViews
     */
    public void setDragViews(List<View> mDragViews) {
        ValueAnimator animator = ValueAnimator.ofInt(100);
        animator.start();
        this.mDragViews = mDragViews;
    }

    /**
     * 设置滑动的模式：水平、垂直或者任意方向
     *
     * @param mScrollMode
     */
    public void setScrollMode(int mScrollMode) {
        this.mScrollMode = mScrollMode;
    }

    /**
     * 位置改变的回调
     *
     * @param mPositionChangeListener
     */
    public void setPositionChangeListener(ViewPositionListener mPositionChangeListener) {
        this.mPositionChangeListener = mPositionChangeListener;
    }


    private void test(String name, int age) {
        MethodCall.onStart("test", new Object[]{name, age});
        System.out.println("test");
    }
}

