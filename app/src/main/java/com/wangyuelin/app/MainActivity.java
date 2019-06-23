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
        MethodCall.onStart("methodname", new Object[]{name, age});
        System.out.println("name:" + name + " age:" + age);
        return true;
    }

}
