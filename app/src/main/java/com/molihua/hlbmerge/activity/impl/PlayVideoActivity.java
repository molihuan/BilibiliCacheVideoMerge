package com.molihua.hlbmerge.activity.impl;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.blankj.molihuan.utilcode.util.ClipboardUtils;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.activity.BaseActivity;
import com.molihua.hlbmerge.controller.videocontroller.DKVideoController;
import com.molihuan.pathselector.utils.Mtools;

import master.flame.danmaku.ui.widget.DanmakuView;
import xyz.doikki.videoplayer.player.VideoView;

/**
 * @ClassName: PlayVideoActivity
 * @Author: molihuan
 * @Date: 2022/12/28/0:48
 * @Description:
 */
public class PlayVideoActivity extends BaseActivity implements View.OnClickListener {
    //复制路径按钮
    private Button btn_copypath;
    //更新弹幕按钮
    private Button btn_updataxml;

    //DK播放器视图
    private VideoView videoView;
    //DK控制器
    private DKVideoController videoController;

    //视频路径
    private String videoPath;

    public DanmakuView getDanmakuView() {
        return videoController.getDanmakuView();
    }

    @Override
    public int setContentViewID() {
        return R.layout.activity_play_video;
    }

    @Override
    public void getComponents() {
        btn_copypath = findViewById(R.id.btn_copypath);
        btn_updataxml = findViewById(R.id.btn_updataxml);
        videoView = findViewById(R.id.play_video_view);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        //获取播放视频的路径
        videoPath = intent.getStringExtra("videoPath");
        //全屏模式
        //ScreenUtils.setFullScreen(this);
        //设置视频地址
        videoView.setUrl(videoPath);
        //初始化视频控制器
        videoController = new DKVideoController(this, videoPath);

        //设置控制器
        videoView.setVideoController(videoController);

        //开始播放
        videoView.start();

    }

    @Override
    public void initView() {
        btn_copypath.setText("路径:" + videoPath);
    }

    @Override
    public void setListeners() {
        btn_copypath.setOnClickListener(this);
        btn_updataxml.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_copypath:
                ClipboardUtils.copyText(videoPath);
                Mtools.toast("文件路径已复制到剪贴板");
                break;
            case R.id.btn_updataxml:
                break;

        }
    }

    @Override
    public void onBackPressed() {
        //先让videoView处理返回事件
        if (videoController != null && videoController.onBackPressed()) {
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        //恢复播放
        if (videoView != null) {
            videoView.resume();
        }

        getDanmakuView().resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //暂停视频
        if (videoView != null) {
            videoView.pause();
        }

        getDanmakuView().pause();
    }

    @Override
    public void onDestroy() {
        //销毁播放器
        releaseVideoViewDanmakuView();
        super.onDestroy();
    }

    /**
     * 销毁播放器
     */
    protected void releaseVideoViewDanmakuView() {
        if (videoView != null) {
            videoView.release();
            videoView = null;
        }
        DanmakuView danmakuView = getDanmakuView();
        if (danmakuView != null) {
            danmakuView.release();
        }
        Mtools.log("释放videoView和danmakuView");
    }

}
