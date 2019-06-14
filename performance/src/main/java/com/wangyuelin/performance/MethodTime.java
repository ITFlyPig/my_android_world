package com.wangyuelin.performance;

import android.util.Log;

import com.alibaba.fastjson.JSON;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

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
    @Before("execution(* com.wangyuelin.uiwidgetmodule.**.*(..))")
    public void preMethod(JoinPoint joinPoint) {
        String signature = joinPoint.getSignature().toString();
        callHelper.recordStart(signature, joinPoint.getArgs());
    }

    @After("execution(* com.wangyuelin.uiwidgetmodule.**.*(..))")
    public void afterMethod(JoinPoint joinPoint) {
        String signature = joinPoint.getSignature().toString();
        callHelper.recordEnd(signature);
    }

}
