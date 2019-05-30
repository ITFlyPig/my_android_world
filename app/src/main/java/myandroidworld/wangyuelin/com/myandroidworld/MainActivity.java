package myandroidworld.wangyuelin.com.myandroidworld;

import android.app.Activity;
import android.os.Bundle;

import com.wangyuelin.uiwidgetmodule.EasyScrollView;

public class MainActivity extends Activity {
    private EasyScrollView easyScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        easyScrollView = findViewById(R.id.easy_scroll_view);
    }
}
