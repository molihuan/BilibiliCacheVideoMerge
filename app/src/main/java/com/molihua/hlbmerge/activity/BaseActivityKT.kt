package com.molihua.hlbmerge.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.umeng.analytics.MobclickAgent
import com.xuexiang.xui.XUI

abstract class BaseActivityKT<T : ViewDataBinding> : AppCompatActivity() {
    protected lateinit var mBinding: T


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //初始化主题
        XUI.initTheme(this);
        //获取ViewDataBinding,有了ViewDataBinding就可以操作视图了
        mBinding = DataBindingUtil.setContentView(this, setContentViewId())
        initViewModel()
        initData()
        setListeners()
        //设置数据观察
        mBinding.lifecycleOwner = this
    }

    //获取视图id
    abstract fun setContentViewId(): Int

    //初始化ViewModel,推荐使用本类getViewModel快速获取
    open fun initViewModel() {

    }

    //初始化数据
    open fun initData() {

    }

    //设置监听
    open fun setListeners() {

    }

    /**
     * 将ViewModel与当前Activity绑定
     * 子类快速获取ViewModel
     */
    fun <T : ViewModel> getViewModelByProvider(viewModelClass: Class<T>): T {
        return ViewModelProvider(this)[viewModelClass]
    }

    override fun onStart() {
        super.onStart()
        //Mtools.log(javaClass.simpleName + "---------onStart");
    }

    override fun onResume() {
        super.onResume()
        //Mtools.log(javaClass.simpleName + "---------onResume");
    }

    override fun onPause() {
        super.onPause()
        //Mtools.log(javaClass.simpleName + "---------onPause");
    }

    override fun onStop() {
        super.onStop()
        //Mtools.log(javaClass.simpleName + "---------onStop");
    }

    override fun onRestart() {
        super.onRestart()
        //Mtools.log(javaClass.simpleName + "---------onRestart");
    }

    override fun onDestroy() {
        //友盟保存数据
        MobclickAgent.onKillProcess(this)
        //Mtools.log(javaClass.simpleName + "---------onDestroy");
        super.onDestroy()
    }


}