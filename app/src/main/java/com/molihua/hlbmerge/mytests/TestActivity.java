package com.molihua.hlbmerge.mytests;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.molihua.hlbmerge.R;

import io.microshow.rxffmpeg.player.RxFFmpegPlayerView;

public class TestActivity extends AppCompatActivity {
    private RxFFmpegPlayerView mPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        getComponents();//获取组件
        setListeners();//设置监听
        initData();//初始化数据
    }

    private void getComponents() {

    }

    private void setListeners() {
    }

    private void initData() {
    }


}