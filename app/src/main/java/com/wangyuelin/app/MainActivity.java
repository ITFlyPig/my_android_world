package com.wangyuelin.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wangyuelin.myandroidworld.util.LogUtil;
import com.wangyuelin.performance.MethodCall;
import com.wangyuelin.performance.WebSocketHelper;
import com.wangyuelin.performance.show.MethodQueue;

import java.util.Timer;
import java.util.TimerTask;


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

//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                String json = "{\"classC\":\"com.talk51.ushare.ShareManager.initShareParams(android.content\",\"endTIme\":1561485952401,\"signature\":\"com.talk51.ushare.ShareManager.initShareParams(android.content.Context)\",\"startTime\":1561485952351,\"totalTime\":50}";
//                WebSocketHelper.getInstance().send(json);
//            }
//        }, 1000, 4000);




    }

    private boolean test(boolean v1, byte v2, char v3, short v4, int v5, long v6, float v7, double v8, String name,  byte[] bytes) {
        MethodCall.onStart("methodname", new Object[]{v1, v2, v3, v4, v5, v6, v7, v8, name, bytes});
        System.out.println("method:  test");
        MethodCall.onEnd("methodname");
        return true;
    }

}
