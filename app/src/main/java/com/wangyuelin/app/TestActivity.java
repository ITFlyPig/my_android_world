package com.wangyuelin.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.wangyuelin.performance.show.MenuView;
import com.wangyuelin.performance.show.PerformanceView;


public class TestActivity extends AppCompatActivity {
    private PerformanceView performanceView;
    private MenuView menuView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        performanceView = findViewById(R.id.v_performance);
        menuView = findViewById(R.id.v_menu);
        menuView.setFuncs(performanceView.getFuncs());

    }

    private void method1() {
        System.out.println("method1");
        method2();
    }

    private void method2() {
        System.out.println("method1");
        method3();
    }

    private void method3() {
        System.out.println("method1");
    }

    private void method4() {
        System.out.println("method1");
    }


}
