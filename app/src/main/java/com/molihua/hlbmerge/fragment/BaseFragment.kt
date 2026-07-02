package com.molihua.hlbmerge.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment

abstract class BaseFragment : DialogFragment() {
    //FragmentView
    var mFragmentView: View? = null
    @JvmField
    protected var mActivity : Activity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mFragmentView == null) {
            //获取Fragment布局
            mFragmentView = inflater.inflate(setFragmentViewId(), container, false)
            //获取组件
            getComponents(mFragmentView)
            //初始化数据
            initData()
            //初始化视图
            initView()
            //设置监听
            setListeners()
        }
        return mFragmentView
    }

    /**
     * 子类的数据初始化必须在这些方法中，否则可能出现空指针异常
     *
     * @param
     */
    @LayoutRes
    abstract fun setFragmentViewId(): Int

    abstract fun getComponents(view: View?)

    open fun initData() {
    }

    open fun initView() {
    }

    open fun setListeners() {
    }


    /**
     * 子类可以重写此方法让fragment先处理返回按钮事件
     *
     * @return true表示Fragment已经处理了Activity可以不用处理了 false反之
     */
    open fun onBackPressed(): Boolean {
        return false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = activity
    }
}