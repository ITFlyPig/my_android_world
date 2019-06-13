package myandroidworld.wangyuelin.com.myandroidworld;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        method1();
        method4();
    }

    private void method1(){
        System.out.println("method1");
        method2();
    }

    private void method2(){
        System.out.println("method1");
        method3();
    }
    private void method3(){
        System.out.println("method1");
    }
    private void method4(){
        System.out.println("method1");
    }
}
