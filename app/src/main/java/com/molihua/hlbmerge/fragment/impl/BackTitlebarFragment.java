package com.molihua.hlbmerge.fragment.impl;

import android.view.View;
import android.widget.TextView;

import com.blankj.molihuan.utilcode.util.StringUtils;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.fragment.BaseFragment;
import com.xuexiang.xui.widget.button.shadowbutton.ShadowImageView;
import com.xuexiang.xui.widget.textview.autofit.AutoFitTextView;

/**
 * @ClassName: BackTitlebarFragment
 * @Author: molihuan
 * @Date: 2022/11/26/14:30
 * @Description: 通用返回titlebar
 */
public class BackTitlebarFragment extends BaseFragment implements View.OnClickListener {
    private String title;
    private String subtitle;

    private ShadowImageView btn_back_toolbar;
    private AutoFitTextView main_title_toolbar;
    private AutoFitTextView subtitle_toolbar;

    private TextView rightOptionTv;
    private String rightOptionText;
    private IClickListener rightOptionClickListener;

    public interface IClickListener {
        void onClick(View v);
    }

    public BackTitlebarFragment setRightOption(String rightOptionText, IClickListener rightOptionClickListener) {
        this.rightOptionText = rightOptionText;
        this.rightOptionClickListener = rightOptionClickListener;
        return this;
    }

    public BackTitlebarFragment(String title) {
        this.title = title;
    }

    public BackTitlebarFragment(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }


    @Override
    public int setFragmentViewId() {
        return R.layout.fragment_back_titlebar;
    }

    @Override
    public void getComponents(View view) {
        btn_back_toolbar = view.findViewById(R.id.btn_back_toolbar);
        main_title_toolbar = view.findViewById(R.id.main_title_toolbar);
        subtitle_toolbar = view.findViewById(R.id.subtitle_toolbar);
        rightOptionTv = view.findViewById(R.id.tv_right_option);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        if (StringUtils.isTrimEmpty(title)) {
            main_title_toolbar.setVisibility(View.INVISIBLE);
        } else {
            main_title_toolbar.setText(title);
        }

        if (StringUtils.isTrimEmpty(subtitle)) {
            subtitle_toolbar.setVisibility(View.GONE);
        } else {
            subtitle_toolbar.setText(subtitle);
        }

        if (rightOptionText != null) {
            rightOptionTv.setVisibility(View.VISIBLE);
            rightOptionTv.setText(rightOptionText);
            rightOptionTv.setOnClickListener(this);
        }
    }

    @Override
    public void setListeners() {
        btn_back_toolbar.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back_toolbar) {
            mActivity.onBackPressed();
        } else if (id == R.id.tv_right_option) {
            rightOptionClickListener.onClick(v);
        }

    }


}