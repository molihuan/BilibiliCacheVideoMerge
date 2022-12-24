package com.molihua.hlbmerge.fragment;

import android.content.Context;

import androidx.annotation.NonNull;

import com.molihua.hlbmerge.activity.AbstractMainActivity;

/**
 * @ClassName: AbstractMainFragment
 * @Author: molihuan
 * @Date: 2022/12/20/17:14
 * @Description:
 */
public abstract class AbstractMainFragment extends BaseFragment {
    protected AbstractMainActivity abstractMainActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (mActivity instanceof AbstractMainActivity) {
            abstractMainActivity = (AbstractMainActivity) mActivity;
        }
    }
}
