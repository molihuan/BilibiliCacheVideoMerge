package com.molihua.hlbmerge.activity.impl;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.blankj.molihuan.utilcode.util.ClipboardUtils;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.activity.BaseActivity;
import com.molihua.hlbmerge.controller.videocontroller.DKVideoController;
import com.molihua.hlbmerge.utils.FileTool;
import com.molihua.hlbmerge.utils.GeneralTools;
import com.molihua.hlbmerge.utils.LConstants;
import com.molihuan.pathselector.utils.Mtools;

import java.io.File;

import master.flame.danmaku.ui.widget.DanmakuView;
import xyz.doikki.videoplayer.player.VideoView;

/**
 * @ClassName: PlayVideoActivity
 * @Author: molihuan
 * @Date: 2022/12/28/0:48
 * @Description: 播放器
 */
public class PlayVideoActivity extends BaseActivity implements View.OnClickListener {
    //复制路径按钮
    private Button btn_copypath;
    //更新弹幕按钮
    private Button btn_updataxml;
    private Button btn_jump_source_vedio;
    //分享按钮
    private Button shareBtn;

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
        btn_jump_source_vedio = findViewById(R.id.btn_jump_source_vedio);
        btn_updataxml = findViewById(R.id.btn_updataxml);
        videoView = findViewById(R.id.play_video_view);
        shareBtn = findViewById(R.id.btn_share);
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
        btn_jump_source_vedio.setOnClickListener(this);
        btn_updataxml.setOnClickListener(this);
        shareBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_copypath:
                ClipboardUtils.copyText(videoPath);
                Mtools.toast("文件路径已复制到剪贴板");
                break;
            case R.id.btn_jump_source_vedio:
                String bvid = FileTool.getVedioMetadataTitle(videoPath);
                if (bvid == null) {
                    Mtools.toast("无法获取bvid,请重新导出,如果重新导出也无法获取则缓存文件不完整导致。");
                    return;
                }
                GeneralTools.jumpBrowser(this, LConstants.URL_BILIBILI_VIDEO_PRE + bvid);
                break;
            case R.id.btn_updataxml:
                Mtools.toast("还在开发中...");
                break;
            case R.id.btn_share:
                FileTool.shareFile(this, new File(videoPath));
                break;
            default:


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
