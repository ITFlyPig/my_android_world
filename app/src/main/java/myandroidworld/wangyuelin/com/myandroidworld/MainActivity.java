package myandroidworld.wangyuelin.com.myandroidworld;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.wangyuelin.uiwidgetmodule.EasyScrollView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WebSocketHelper webSocketHelper = new WebSocketHelper();
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webSocketHelper.connect();
            }
        });
    }
}
