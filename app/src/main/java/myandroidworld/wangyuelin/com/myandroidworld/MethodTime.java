package myandroidworld.wangyuelin.com.myandroidworld;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 统计方法执行的时间
 */
@Aspect
public class MethodTime {

    private CallHelper.CallListener callListener = new CallHelper.CallListener() {
        @Override
        public void onCallDone(CallBean callBean) {

            Log.d("shu", callBean.toString());
        }
    };
    private CallHelper callHelper = new CallHelper(callListener);

    //////////////////////////////////统计方法的执行时间///////////////////////////////////////
    @Before("execution(* com.wangyuelin.uiwidgetmodule.EasyScrollView.*(..))")
    public void preMethod(JoinPoint joinPoint) {
        String signature = joinPoint.getSignature().toString();
        callHelper.recordStart(signature, joinPoint.getArgs());
    }

    @After("execution(* com.wangyuelin.uiwidgetmodule.EasyScrollView.*(..))")
    public void afterMethod(JoinPoint joinPoint) {
        String signature = joinPoint.getSignature().toString();
        callHelper.recordEnd(signature);
    }

}
