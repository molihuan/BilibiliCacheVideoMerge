package com.molihua.hlbmerge.fragment.impl;

import android.view.View;
import android.widget.Button;

import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.entity.CacheSrc;
import com.molihua.hlbmerge.fragment.AbstractMainFfmpegFragment;
import com.molihuan.pathselector.utils.Mtools;
import com.xuexiang.xui.widget.edittext.ClearEditText;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;

//import butterknife.BindView;
//import butterknife.OnClick;

/**
 * @ClassName: MainFfmpegFragment
 * @Author: molihuan
 * @Date: 2022/12/21/19:53
 * @Description:
 */
public class MainToolsFragment extends AbstractMainFfmpegFragment implements View.OnClickListener {
    //    @BindView(R.id.mlet_ffmpeg_cmd)
    MultiLineEditText ffmpegCmdMlet;
    //    @BindView(R.id.btn_run_ffmpeg_cmd)
    Button runFfmpegCmdBtn;
    //    @BindView(R.id.cet_avbv)
    ClearEditText avbvCet;
    //    @BindView(R.id.btn_barrage_download)
    Button barrageBtn;
    //    @BindView(R.id.btn_pic_download)
    Button picBtn;

    //    @OnClick(R.id.btn_run_ffmpeg_cmd)
    public void clickRunFfmpegCmdBtn() {
        Mtools.toast("333333");
    }


    @Override
    public int setFragmentViewId() {
        return R.layout.fragment_main_tools;
    }

    @Override
    public void getComponents(View view) {
        ffmpegCmdMlet = view.findViewById(R.id.mlet_ffmpeg_cmd);
        runFfmpegCmdBtn = view.findViewById(R.id.btn_run_ffmpeg_cmd);
        barrageBtn = view.findViewById(R.id.btn_barrage_download);
        picBtn = view.findViewById(R.id.btn_pic_download);

    }

    @Override
    public void setListeners() {
        runFfmpegCmdBtn.setOnClickListener(this);
        barrageBtn.setOnClickListener(this);
        picBtn.setOnClickListener(this);
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

    CacheSrc<String> src = new CacheSrc<>();

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_run_ffmpeg_cmd) {

        } else if (id == R.id.btn_barrage_download) {


        } else if (id == R.id.btn_pic_download) {
//            ConfigData.clearTempData();
//            ffmpegCmdMlet.setContentText("666");

        }

        Mtools.toast("开发中....");
    }
}
