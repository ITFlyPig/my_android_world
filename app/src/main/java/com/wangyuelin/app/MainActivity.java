package com.wangyuelin.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wangyuelin.performance.MethodCall;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                startActivity(intent);

            }
        });

        test("wang", 25);
    }

    private boolean test(String name, int age) {
        MethodCall.onStart("methodname", null);
        System.out.println("name:" + name + " age:" + age);
        MethodCall.onEnd("methodname");
        return true;
    }

    private void test2() {
        System.out.println("插入的代码");

        test("wang", 0);
    }
}
