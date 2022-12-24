package com.molihua.hlbmerge.fragment.impl;

import android.view.View;
import android.widget.TextView;

import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.dialog.impl.MergeOptionDialog;
import com.molihua.hlbmerge.fragment.AbstractMainHandleFragment;

/**
 * @ClassName: MainHandleFragment
 * @Author: molihuan
 * @Date: 2022/12/21/19:53
 * @Description:
 */
public class MainHandleFragment extends AbstractMainHandleFragment implements View.OnClickListener {
    private TextView leftTv;
    private TextView centerTv;
    private TextView rightTv;

    @Override
    public int setFragmentViewId() {
        return R.layout.fragment_main_handle;
    }

    @Override
    public void getComponents(View view) {
        leftTv = view.findViewById(R.id.left_tv);
        centerTv = view.findViewById(R.id.center_tv);
        rightTv = view.findViewById(R.id.right_tv);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        leftTv.setText("全选");
        centerTv.setText("合并");
        rightTv.setText("取消");
    }

    @Override
    public void setListeners() {
        leftTv.setOnClickListener(this);
        centerTv.setOnClickListener(this);
        rightTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.left_tv) {

            TextView tv = (TextView) v;
            if ("全选".equals(tv.getText())) {
                abstractMainActivity.selectAllCacheFile(true);
                tv.setText("全不选");
            } else {
                abstractMainActivity.selectAllCacheFile(false);
                tv.setText("全选");
            }

        } else if (id == R.id.center_tv) {
            MergeOptionDialog.showMergeOptionDialog(abstractMainActivity.getSelectedCacheFileList(), mActivity);
        } else if (id == R.id.right_tv) {
            abstractMainActivity.openCloseMultipleMode(false);
        }
    }
}
