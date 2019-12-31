package com.wangyuelin.base;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gyf.immersionbar.ImmersionBar;
import com.gyf.immersionbar.components.ImmersionOwner;
import com.gyf.immersionbar.components.ImmersionProxy;
import com.wangyuelin.base.helper.TitleHelper;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * BaseFagment提供的功能：
 * 1、沉浸式
 * immersionBarEnabled：是否开启沉浸式
 *
 * 2、预置的Titlebar
 * isUseTitle：是否使用预置的标题
 * getTitle:返回标题的文字
 *
 * 3、生命周期回调：以下方法按顺序执行
 * getLayoutId:布局id
 *  initData();//初始化数据
 * initView();//初始化View，和数据绑定
 * setListener();//设置监听
 *
 * 4、使用ButterKnife
 * isUseButterKnife：是否使用ButterKnife
 *
 */
public abstract class BaseFagment extends Fragment implements ImmersionOwner {
    /**
     * ImmersionBar代理类
     */
    private ImmersionProxy  mImmersionProxy = new ImmersionProxy(this);;
    private TitleHelper titleHelper;
    private Unbinder unbinder;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (immersionBarEnabled()) {
            mImmersionProxy.setUserVisibleHint(isVisibleToUser);
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (immersionBarEnabled()) {
            mImmersionProxy.onCreate(savedInstanceState);
        }
        if (isUseTitle()) {
            titleHelper = new TitleHelper();
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (immersionBarEnabled()) {
            mImmersionProxy.onActivityCreated(savedInstanceState);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (immersionBarEnabled()) {
            mImmersionProxy.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (immersionBarEnabled()) {
            mImmersionProxy.onPause();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (immersionBarEnabled()) {
            mImmersionProxy.onDestroy();
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (immersionBarEnabled()) {
            mImmersionProxy.onHiddenChanged(hidden);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (immersionBarEnabled()) {
            mImmersionProxy.onConfigurationChanged(newConfig);
        }

    }

    /**
     * 懒加载，在view初始化完成之前执行
     * On lazy after view.
     */
    @Override
    public void onLazyBeforeView() {
    }

    /**
     * 懒加载，在view初始化完成之后执行
     * On lazy before view.
     */
    @Override
    public void onLazyAfterView() {
    }

    /**
     * Fragment用户可见时候调用
     * On visible.
     */
    @Override
    public void onVisible() {
    }

    /**
     * Fragment用户不可见时候调用
     * On invisible.
     */
    @Override
    public void onInvisible() {
    }

    /**
     * 是否可以实现沉浸式，当为true的时候才可以执行initImmersionBar方法
     * Immersion bar enabled boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean immersionBarEnabled() {
        return true;
    }

    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this)
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.colorPrimary)
                .init();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = null;
        if (isUseTitle()) {
            v = titleHelper.onCreateView(getLayoutId(), this);
            titleHelper.getIvBack().setVisibility(View.INVISIBLE);
            titleHelper.getTvTitle().setText(getTitle());
        } else {
            v = inflater.inflate(getLayoutId(), container, false);
        }

        if (isUseButterKnife()) {
            unbinder = ButterKnife.bind(this, v);
        }

        initData();//初始化数据
        initView();//初始化View，和数据绑定
        setListener();//设置监听
        return v;
    }

    /**
     * 子类设置布局Id
     *
     * @return the layout id
     */
    protected abstract int getLayoutId();

    protected void initData() { }

    protected void initView() { }

    protected void setListener() { }

    /**
     * 标题
     */
    protected String getTitle() {
      return "";
    }

    /**
     * 是否使用标题
     * @return
     */
    protected boolean isUseTitle() {
        return false;
    }

    /**
     * 是否使用ButterKnife
     * @return
     */
    protected boolean isUseButterKnife() {
        return true;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }


    protected TitleHelper getTitleHelper() {
        return titleHelper;
    }
}
