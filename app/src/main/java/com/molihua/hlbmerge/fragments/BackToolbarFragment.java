package com.molihua.hlbmerge.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.molihua.hlbmerge.R;
import com.xuexiang.xui.widget.button.shadowbutton.ShadowImageView;
import com.xuexiang.xui.widget.textview.autofit.AutoFitTextView;

/**
 * 通用返回toolbar  fragment
 */
public class BackToolbarFragment extends Fragment implements View.OnClickListener {
    private String title = "";
    private String subtitle = "";
    private View view;
    private ShadowImageView btn_back_toolbar;
    private Activity activity;
    private static AutoFitTextView main_title_toolbar;
    private static AutoFitTextView subtitle_toolbar;


    public BackToolbarFragment() {
    }

    public BackToolbarFragment(String title) {
        this.title = title;
    }

    public BackToolbarFragment(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_back_toolbar, container, false);
            getComponents();//获取组件
            setListeners();//设置监听
            initData();//初始化数据
        }

        return view;

    }

    private void initData() {
        main_title_toolbar.setText(title);

        if (subtitle.equals("")) {//如果副标题为空则移除它
            ((RelativeLayout) subtitle_toolbar.getParent()).removeView(subtitle_toolbar);
        } else {
            subtitle_toolbar.setText(subtitle);
        }
    }

    private void setListeners() {
        btn_back_toolbar.setOnClickListener(this);
    }

    private void getComponents() {
        btn_back_toolbar = view.findViewById(R.id.btn_back_toolbar);
        main_title_toolbar = view.findViewById(R.id.main_title_toolbar);
        subtitle_toolbar = view.findViewById(R.id.subtitle_toolbar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back_toolbar:
                if (activity == null) {
                    activity = getActivity();
                }
                activity.onBackPressed();
                break;
        }
    }


}