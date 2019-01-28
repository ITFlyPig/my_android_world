package myandroidworld.wangyuelin.com.myandroidworld;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wangyuelin.uiwidgetmodule.EasyScrollView;

public class MainActivity extends AppCompatActivity {
    private EasyScrollView easyScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        easyScrollView = findViewById(R.id.easy_scroll_view);
    }
}
