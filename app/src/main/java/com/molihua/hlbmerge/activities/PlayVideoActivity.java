package com.molihua.hlbmerge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.controllers.DKVideoController;
import com.molihua.hlbmerge.utils.HttpTools;
import com.molihua.hlbmerge.utils.PathTools;

import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;
import xyz.doikki.videoplayer.player.VideoView;

public class PlayVideoActivity extends AppCompatActivity implements View.OnClickListener {

    private VideoView play_video_view;//播放器组件
    private DanmakuView play_DanmakuView;//弹幕组件
    private DanmakuContext mDanmakuContext;
    private BaseDanmakuParser mParser;
    private DKVideoController controller;
    private Button btn_copypath;
    private Button btn_updataxml;
//    private Button btn_avbv_download;
//    private Button btn_pic_download;
    //private ClearEditText edit_avbv;

    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        getComponent();
        setListener();
        initData();
    }

    private void initData() {

        Intent intent = getIntent();
        videoPath = intent.getStringExtra("videoPath");//获取播放视频的路径
        ScreenUtils.setFullScreen(this);//全屏模式

        play_video_view.setUrl(videoPath); //设置视频地址
        //play_video_view.setUrl("https://media.w3.org/2010/05/sintel/trailer.mp4"); //设置视频地址
        //play_video_view.setUrl("http://api.bilibili.com/x/player/playurl?bvid=BV1Hq4y1x7Z4&cid=367013145"); //设置视频地址

        controller = new DKVideoController(this, videoPath);
        //根据屏幕方向自动进入/退出全屏
        //controller.setEnableOrientation(true);
        play_video_view.setVideoController(controller); //设置控制器
        play_video_view.start(); //开始播放，不调用则不自动播放
        play_DanmakuView = controller.getDanmakuView();
        btn_copypath.setText("路径:" + videoPath);

    }


    private void setListener() {
        btn_copypath.setOnClickListener(this);
        btn_updataxml.setOnClickListener(this);

    }

    private void getComponent() {
        play_video_view = findViewById(R.id.play_video_view);
        btn_copypath = findViewById(R.id.btn_copypath);
        btn_updataxml = findViewById(R.id.btn_updataxml);

        //edit_avbv=findViewById(R.id.edit_avbv);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_copypath:
                ClipboardUtils.copyText(videoPath);
                ToastUtils.make().show("文件路径已复制到剪贴板");
                break;
            case R.id.btn_updataxml:
                String url = controller.getCidByXml();
                String dowmloadFilePath = PathTools.getFileAbsolutePathNoExtension(videoPath) + ".xml";
                HttpTools.downloadFile(PlayVideoActivity.this, url, dowmloadFilePath);
                break;

        }
    }


    /**
     * 恢复
     */
    @Override
    public void onResume() {
        super.onResume();
        //恢复播放
        play_video_view.resume();
        if (play_DanmakuView != null && play_DanmakuView.isPrepared() && play_DanmakuView.isPaused()) {
            play_DanmakuView.resume();
        }
    }

    /**
     * 暂停
     */
    @Override
    public void onPause() {
        super.onPause();
        //暂停视频
        play_video_view.pause();

        if (play_DanmakuView != null && play_DanmakuView.isPrepared()) {
            play_DanmakuView.pause();
        }
    }

    /**
     * 销毁
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //销毁播放器
        play_video_view.release();

        if (play_DanmakuView != null) {
            // dont forget release!
            play_DanmakuView.release();
            play_DanmakuView = null;
        }
    }

    @Override
    public void onBackPressed() {

        //退出全屏
        if (play_video_view.isFullScreen()) {
            controller.screenTypeBtnPress();//退出全屏
            if (!ScreenUtils.isPortrait()) {//不是竖屏则设置为竖屏
                ScreenUtils.setPortrait(this);
            }
            return;
        }
        //销毁播放器
        play_video_view.release();
        if (play_DanmakuView != null) {
            //销毁弹幕
            play_DanmakuView.release();
            play_DanmakuView = null;
        }
        super.onBackPressed();
    }


}