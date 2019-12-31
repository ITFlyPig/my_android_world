package com.wangyuelin.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wangyuelin.base.BaseActivity;
import com.wangyuelin.performance.method.MethodCall;


public class MainActivity extends BaseActivity {



    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        findViewById(R.id.tv).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TestActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected boolean isUseTitleView() {
        return false;
    }

    private boolean test(boolean v1, byte v2, char v3, short v4, int v5, long v6, float v7, double v8, String name, byte[] bytes) {
        MethodCall.onStart("methodname", new Object[]{v1, v2, v3, v4, v5, v6, v7, v8, name, bytes});
        System.out.println("method:  test");
        MethodCall.onEnd("methodname");
        return true;
    }

}
