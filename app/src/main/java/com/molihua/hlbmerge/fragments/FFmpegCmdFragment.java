package com.molihua.hlbmerge.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.utils.HttpTools;
import com.molihua.hlbmerge.utils.RxFfmpegTools;
import com.xuexiang.xui.widget.edittext.ClearEditText;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;

/**
 * 执行FFmpeg  Fragment
 */
public class FFmpegCmdFragment extends Fragment implements View.OnClickListener {

    private View view;
    private MultiLineEditText multiLineEditText_ffmpegCmd;
    private Button superButton_ffmpegCmd_run;
    private ClearEditText edit_avbv;
    private Button btn_avbv_download;
    private Button btn_pic_download;

    public FFmpegCmdFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view==null){
            view = inflater.inflate(R.layout.fragment_f_fmpeg_cmd, container, false);
            getComponents();//获取组件
            setListeners();//设置监听
            initData();//初始化数据
        }
        return view;
    }

    private void initData() {
        multiLineEditText_ffmpegCmd.setHintText("请输入ffmpeg命令");
    }

    private void setListeners() {
        superButton_ffmpegCmd_run.setOnClickListener(this);
        btn_avbv_download.setOnClickListener(this);
        btn_pic_download.setOnClickListener(this);
    }

    private void getComponents() {
        multiLineEditText_ffmpegCmd=view.findViewById(R.id.multiLineEditText_ffmpegCmd);
        superButton_ffmpegCmd_run=view.findViewById(R.id.superButton_ffmpegCmd_run);
        edit_avbv=view.findViewById(R.id.edit_avbv);
        btn_avbv_download=view.findViewById(R.id.btn_avbv_download);
        btn_pic_download=view.findViewById(R.id.btn_pic_download);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.superButton_ffmpegCmd_run :
                String contentText = multiLineEditText_ffmpegCmd.getContentText();
                try {
                    RxFfmpegTools.execStatement(contentText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ToastUtils.make().show("暂时还没设置回调,所以不知道执行成功了没,以后加");
                break;
            case R.id.btn_avbv_download :
                //String bvav="BV1XY4y157g8";
                String bvav=edit_avbv.getText().toString();
                LogUtils.e(bvav);
                HttpTools.downloadFileFromCidByAV((AppCompatActivity)getContext(),bvav,HttpTools.FILETYPE_XML);
                break;
            case R.id.btn_pic_download :
                //String bvav="BV1XY4y157g8";
                bvav = edit_avbv.getText().toString();
                HttpTools.downloadFileFromCidByAV((AppCompatActivity)getContext(),bvav,HttpTools.FILETYPE_PIC);
                break;
        }
    }
}