package com.wangyuelin.aop;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.wangyuelin.myandroidworld.util.LogUtil;
import com.wangyuelin.performance.CallBean;
import com.wangyuelin.performance.CallHelper;
import com.wangyuelin.performance.ThreadHelper;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 统计方法执行的时间
 */
@Aspect
public class MethodTime {

    private CallHelper.CallListener callListener = new CallHelper.CallListener() {
        @Override
        public void onCallDone(final CallBean callBean) {
            ThreadHelper.getInstance().submit(new Runnable() {
                @Override
                public void run() {
                    String callJson = JSON.toJSONString(callBean);
//                    WebSocketHelper.getInstance().send(callJson);
                    Log.d("wyl", "反馈的Josn" + callJson);
                }
            });

        }
    };
    private CallHelper callHelper = new CallHelper(callListener);

    //////////////////////////////////统计方法的执行时间///////////////////////////////////////
//    @Before("execution(* com.wangyuelin..*.*(..))")
    public void preMethod(JoinPoint joinPoint) {
        String signature = joinPoint.getSignature().toString();
        callHelper.recordStart(signature, joinPoint.getArgs());
    }

//    @After("execution(* com.wangyuelin..*.*(..))")
    public void afterMethod(JoinPoint joinPoint) {
        String signature = joinPoint.getSignature().toString();
        callHelper.recordEnd(signature);
    }

    @Around("execution( * com.wangyuelin.app..*.*(..))")
    public void aroundAspect(ProceedingJoinPoint point) {
        LogUtil.d("开始执行方法前");
        try {
            point.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        LogUtil.d("执行方法后");
    }



}
