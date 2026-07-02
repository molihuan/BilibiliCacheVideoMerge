package com.molihua.hlbmerge.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding

import com.umeng.analytics.MobclickAgent;
import com.xuexiang.xui.XUI;


/**
 * @ClassName: AbstractActivity
 * @Author: molihuan
 * @Date: 2022/11/22/13:07
 * @Description:
 */
abstract class BaseActivity<T: ViewBinding> : AppCompatActivity() {
    protected lateinit var binding: T

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        //初始化主题
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        //设置布局资源
        binding = getContentViewBinding()
        setContentView(binding.root);
        //获取组件
        getComponents();
        //初始化数据
        initData();
        //初始化视图
        initView();
        //设置监听
        setListeners();

    }

    abstract fun getContentViewBinding(): T

    abstract fun getComponents();

    abstract fun initData();

    open fun initView() {

    }

    open fun setListeners() {
    }


    @Override
    override fun onDestroy() {
        //友盟保存数据
        MobclickAgent.onKillProcess(this);
        super.onDestroy();
    }


}
