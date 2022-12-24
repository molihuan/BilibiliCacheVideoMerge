package com.molihua.hlbmerge.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;


/**
 * @ClassName: BaseFragment
 * @Author: molihuan
 * @Date: 2022/11/22/14:29
 * @Description:
 */
public abstract class BaseFragment extends DialogFragment {
    //FragmentView
    public View mFragmentView;
    //依附的Activity
    public Activity mActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mFragmentView == null) {
            //获取Fragment布局
            mFragmentView = inflater.inflate(setFragmentViewId(), container, false);
            //获取组件
            getComponents(mFragmentView);
            //初始化数据
            initData();
            //初始化视图
            initView();
            //设置监听
            setListeners();
        }
        return mFragmentView;
    }

    /**
     * 子类的数据初始化必须在这些方法中，否则可能出现空指针异常
     *
     * @param
     */

    public abstract @LayoutRes
    int setFragmentViewId();

    public abstract void getComponents(View view);

    public void initData() {

    }

    public void initView() {

    }

    public void setListeners() {

    }


    /**
     * 子类可以重写此方法让fragment先处理返回按钮事件
     *
     * @return true表示Fragment已经处理了Activity可以不用处理了 false反之
     */
    public boolean onBackPressed() {
        return false;
    }


    /**
     * 当Activity和Fragment产生关系时调用
     *
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //获取与fragment产生关系的Activity
        if (mActivity == null) {
            mActivity = getActivity();
        }
    }

}
