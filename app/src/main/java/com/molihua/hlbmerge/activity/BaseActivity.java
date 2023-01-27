package com.molihua.hlbmerge.activity;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

import com.umeng.analytics.MobclickAgent;
import com.xuexiang.xui.XUI;


/**
 * @ClassName: AbstractActivity
 * @Author: molihuan
 * @Date: 2022/11/22/13:07
 * @Description:
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //初始化主题
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        //设置布局资源
        setContentView(setContentViewID());
        //获取组件
        getComponents();
        //初始化数据
        initData();
        //初始化视图
        initView();
        //设置监听
        setListeners();

    }

    public abstract @LayoutRes
    int setContentViewID();

    public abstract void getComponents();

    public abstract void initData();

    public void initView() {

    }

    public void setListeners() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Mtools.log(getClass().getSimpleName() + "---------onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Mtools.log(getClass().getSimpleName() + "---------onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Mtools.log(getClass().getSimpleName() + "---------onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Mtools.log(getClass().getSimpleName() + "---------onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Mtools.log(getClass().getSimpleName() + "---------onRestart");
    }

    @Override
    protected void onDestroy() {
        //友盟保存数据
        MobclickAgent.onKillProcess(this);
        //Mtools.log(getClass().getSimpleName() + "---------onDestroy");
        super.onDestroy();
    }


}
