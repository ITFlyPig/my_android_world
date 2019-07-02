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

        test();

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
        User user = new User();
        user.name = "王跃林";
        user.age = 26;
        user.id = 0;

        ObjectCache.getInstance().save("wang", user);

        User u = ObjectCache.getInstance().getObejct("wang");
        LogUtil.d("wyl", "获取到的缓存的对象：" + u);



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
