package com.wangyuelin.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;

import com.wangyuelin.db.demo.User;
import com.wangyuelin.myandroidworld.util.LogUtil;
import com.wangyuelin.myandroidworld.util.ObjectCache;
import com.wangyuelin.performance.show.MenuView;
import com.wangyuelin.performance.show.PerformanceView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


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

    private void test() {
        ObjectCache.getInstance();
        User user = new User();
        user.name = "王跃林";
        user.age = 26;
        user.id = 0;

        long sonStart = System.nanoTime();
        SharedPreferences sharedPreferences = getSharedPreferences("www", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("bb", true);
        editor.commit();
        LogUtil.d("wyl", "SP保存Boolean花费时间：" + (System.nanoTime() - sonStart));

        long fbStart = System.nanoTime();
        ObjectCache.getInstance().save("bb", true);
        LogUtil.d("wyl", "File保存Boolean花费时间：" + (System.nanoTime() - fbStart));


        long spStart = System.nanoTime();
        try {
            for (int i =0; i < 1000; i++) {
                saveUser(this, "www", "user" + i, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.d("wyl", "SP保存User花费时间：" + (System.nanoTime() - spStart));

        long fStart = System.nanoTime();
        for (int i =0; i < 1000; i++) {
            ObjectCache.getInstance().save("www" + i, user);
        }

        LogUtil.d("wyl", "File保存User花费时间：" + (System.nanoTime() - fStart));

        long sprStart = System.nanoTime();
        LogUtil.d("wyl", "SP读取User花费时间：" + getUser(this, "www", "user") + ":===" + (System.nanoTime() - sprStart));

        long frStart = System.nanoTime();
        LogUtil.d("wyl", "SP读取User花费时间：" + ObjectCache.getInstance().getObejct("www")+ ":==="  + (System.nanoTime() - frStart));





    }

    public static void saveUser(Context context, String preferenceName, String key, User user) throws Exception {
        if(user instanceof Serializable) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceName, context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(user);//把对象写到流里
                String temp = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
                editor.putString(key, temp);
                editor.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            throw new Exception("User must implements Serializable");
        }
    }

    public static User getUser(Context context, String preferenceName,String key) {
        SharedPreferences sharedPreferences=context.getSharedPreferences(preferenceName,context.MODE_PRIVATE);
        String temp = sharedPreferences.getString(key, "");
        ByteArrayInputStream bais =  new ByteArrayInputStream(Base64.decode(temp.getBytes(), Base64.DEFAULT));
        User user = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            user = (User) ois.readObject();
        } catch (IOException e) {
        }catch(ClassNotFoundException e1) {

        }
        return user;
    }



}
