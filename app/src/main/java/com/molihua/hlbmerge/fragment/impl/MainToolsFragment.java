package com.molihua.hlbmerge.fragment.impl;

import android.view.View;
import android.widget.Button;

import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.fragment.AbstractMainFfmpegFragment;
import com.molihuan.pathselector.utils.Mtools;
import com.xuexiang.xui.widget.edittext.ClearEditText;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ClassName: MainFfmpegFragment
 * @Author: molihuan
 * @Date: 2022/12/21/19:53
 * @Description:
 */
public class MainToolsFragment extends AbstractMainFfmpegFragment {
    @BindView(R.id.mlet_ffmpeg_cmd)
    MultiLineEditText ffmpegCmdMlet;
    @BindView(R.id.btn_run_ffmpeg_cmd)
    Button runFfmpegCmdBtn;
    @BindView(R.id.cet_avbv)
    ClearEditText avbvCet;
    @BindView(R.id.btn_barrage_download)
    Button barrageBtn;
    @BindView(R.id.btn_pic_download)
    Button picBtn;

    @OnClick(R.id.btn_run_ffmpeg_cmd)
    public void clickRunFfmpegCmdBtn() {
        Mtools.toast("333333");
    }


    @Override
    public int setFragmentViewId() {
        return R.layout.fragment_main_tools;
    }

    @Override
    public void getComponents(View view) {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {

    }


    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }


}
