package com.wangyuelin.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.wangyuelin.base.helper.TitleHelper;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * BaseActivity提供的功能：
 * 1、生命周期回调：以下方法按顺序调用
 * getLayoutId：设置布局Id
 * initImmersionBar：初始化沉浸式，有默认的初始化
 * initData：初始化数据
 * initView：view与数据绑定
 * setListener：设置监听
 *
 * 2、isUseButterKnife：是否使用ButterKnife
 * 3、setTitle：设置标题
 * 4、onBackClick：当返回按钮被点击的默认处理
 * 5、isUseTitleView：是否使用默认提供的Title帮助
 *
 *
 */

public abstract class BaseActivity  extends AppCompatActivity implements View.OnClickListener {
    protected Activity mActivity;
    private TitleHelper titleHelper;
    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        if (isUseTitleView()) {
            titleHelper = new TitleHelper();
            titleHelper.setContentView(getLayoutId(), this);
            titleHelper.getIvBack().setOnClickListener(this);
        } else {
            setContentView(getLayoutId());
        }

        //绑定控件
        if (isUseButterKnife()) {
             unbinder = ButterKnife.bind(this);
        }
        //初始化沉浸式
        initImmersionBar();
        //初始化数据
        initData();
        //view与数据绑定
        initView();
        //设置监听
        setListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    /**
     * 子类设置布局Id
     *
     * @return the layout id
     */
    protected abstract int getLayoutId();

    /**
     * 初始化沉浸式
     * Init immersion bar.
     */
    protected void initImmersionBar() {
        //设置共同沉浸式样式
        ImmersionBar.with(this)
                .navigationBarColor(R.color.colorPrimary)
                .statusBarColor(R.color.white)
                .fitsSystemWindows(true)
                .statusBarDarkFont(true, 0.2f)
                .init();
    }

    protected void initData() {
    }

    protected void initView() {
    }

    protected void setListener() {
    }

    /**
     * 是否使用ButterKnife
     * @return
     */
    protected boolean isUseButterKnife(){
        return true;
    }

    /**
     * 是否使用title的View
     * @return
     */
    protected boolean isUseTitleView() {
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            onBackClick();
        }
    }

    /**
     * 设置标题
     * @param title
     */
    protected void setTitle(String title) {
        if (titleHelper == null) {
            return;
        }
        titleHelper.getTvTitle().setText(title);

    }

    /**
     * 当返回按钮被点击
     */
    protected void onBackClick() {
        finish();
    }


    protected TitleHelper getTitleHelper() {
        return titleHelper;
    }
}
