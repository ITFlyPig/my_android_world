package myandroidworld.wangyuelin.com.myandroidworld;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.wangyuelin.myandroidworld.util.FileIOUtils;
import com.wangyuelin.uiwidgetmodule.EasyScrollView;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MainActivity extends Activity {
    private EasyScrollView easyScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        easyScrollView = findViewById(R.id.easy_scroll_view);

        ObjectAnimator objectAnimator;

        Context context = null;
        SharedPreferences sp = context.getSharedPreferences("test", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("b", false);
        editor.putString("name", "wang");
        editor.commit();//同步提交
        editor.apply();//异步提交
        CountDownLatch latch = new CountDownLatch(4);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        latch.countDown();
        CyclicBarrier barrier = new CyclicBarrier(2);
        barrier.await();

        Semaphore semaphore = new Semaphore(9);
        semaphore.acquire(1);
        semaphore.release(1);
        ReentrantLock lock = new ReentrantLock();

        ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
        Lock w = rwl.writeLock();
        w.lock();
        try {
            //......
        } finally {
            w.unlock();
        }

        Executor executor;
        Executors executors;
        ExecutorService threadPoolExecutor = Executors.newCachedThreadPool();

    }



}
