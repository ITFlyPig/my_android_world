package com.wangyuelin.performance.show;

import android.animation.TimeInterpolator;

/**
 * Fling类型的插值器
 */
public class FlingInterpolator implements TimeInterpolator {
    @Override
    public float getInterpolation(float input) {
        return 0;
    }
}
