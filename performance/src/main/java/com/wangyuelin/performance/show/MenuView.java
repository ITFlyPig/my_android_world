package com.wangyuelin.performance.show;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangyuelin.performance.R;

public class MenuView extends RelativeLayout implements View.OnClickListener {
    private TextView tvClear;
    private PerformanceView.Funcs funcs;


    public MenuView(Context context) {
        this(context, null);
    }

    public MenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setFuncs(PerformanceView.Funcs funcs) {
        this.funcs = funcs;
    }

    private void init() {
        View.inflate(getContext(), R.layout.v_menu, this);
        tvClear = findViewById(R.id.tv_clear);
        tvClear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_clear) {
            if (funcs != null) {
                funcs.clear();
            }
        }

    }
}
