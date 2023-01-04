package com.molihua.hlbmerge.utils;

import androidx.fragment.app.FragmentTransaction;

import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.fragments.MoreChooseFragment;

public class FragmentTools {
    private static MoreChooseFragment moreChooseFragment;
    //显示多选按钮
    public static MoreChooseFragment showMoreChooseBtn(FragmentTransaction fragmentTransaction){

        if (moreChooseFragment ==null){
            moreChooseFragment = new MoreChooseFragment();//实例化fragment
            fragmentTransaction.add(R.id.show_hidden_bar, moreChooseFragment);//添加fragment
        }

        fragmentTransaction.show(moreChooseFragment);//显示fragment


        fragmentTransaction.commitAllowingStateLoss();//提交事务
        return moreChooseFragment;

    }
    //隐藏多选按钮
    public static MoreChooseFragment HiddenMoreChooseBtn(FragmentTransaction fragmentTransaction){

        if (moreChooseFragment ==null){
            moreChooseFragment = new MoreChooseFragment();//实例化fragment
            fragmentTransaction.add(R.id.show_hidden_bar, moreChooseFragment);//添加fragment
        }

        fragmentTransaction.hide(moreChooseFragment);//隐藏fragment

        fragmentTransaction.commitAllowingStateLoss();//提交事务
        return moreChooseFragment;

    }
}
