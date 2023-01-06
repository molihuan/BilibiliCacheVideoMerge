package com.molihua.hlbmerge.fragment.impl;

import android.view.View;
import android.widget.Button;

import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.fragment.AbstractMainFfmpegFragment;
import com.molihuan.pathselector.utils.Mtools;
import com.xuexiang.xui.widget.edittext.ClearEditText;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;

/**
 * @ClassName: MainFfmpegFragment
 * @Author: molihuan
 * @Date: 2022/12/21/19:53
 * @Description:
 */
public class MainToolsFragment extends AbstractMainFfmpegFragment implements View.OnClickListener {
    private MultiLineEditText ffmpegCmdMlet;
    private Button runFfmpegCmdBtn;
    private ClearEditText avbvCet;
    private Button barrageBtn;
    private Button picBtn;


    @Override
    public int setFragmentViewId() {
        return R.layout.fragment_main_tools;
    }

    @Override
    public void getComponents(View view) {
        ffmpegCmdMlet = view.findViewById(R.id.mlet_ffmpeg_cmd);
        runFfmpegCmdBtn = view.findViewById(R.id.btn_run_ffmpeg_cmd);
        avbvCet = view.findViewById(R.id.cet_avbv);
        barrageBtn = view.findViewById(R.id.btn_barrage_download);
        picBtn = view.findViewById(R.id.btn_pic_download);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void setListeners() {
        runFfmpegCmdBtn.setOnClickListener(this);
        barrageBtn.setOnClickListener(this);
        picBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_run_ffmpeg_cmd) {

        } else if (id == R.id.btn_barrage_download) {

        } else if (id == R.id.btn_pic_download) {

        }
        Mtools.toast("还在开发中...");
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }


}
