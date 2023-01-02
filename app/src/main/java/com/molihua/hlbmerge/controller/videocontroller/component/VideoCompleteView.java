package com.molihua.hlbmerge.controller.videocontroller.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.molihua.hlbmerge.R;

import master.flame.danmaku.ui.widget.DanmakuView;
import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.IControlComponent;
import xyz.doikki.videoplayer.player.VideoView;

/**
 * @ClassName: VideoCompleteView
 * @Author: molihuan
 * @Date: 2023/01/01/13:45
 * @Description: 视频播放完成View
 */
public class VideoCompleteView extends FrameLayout implements IControlComponent {

    protected DanmakuView danmakuView;

    private ControlWrapper mControlWrapper;


    public VideoCompleteView(@NonNull Context context, DanmakuView danmakuView) {
        super(context);
        this.danmakuView = danmakuView;
    }

    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(R.layout.fragment_dk_video_complete, this, true);
        findViewById(R.id.iv_replay).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mControlWrapper.replay(true);
                danmakuView.show();
                danmakuView.seekTo((long) 0);
            }
        });
//        setClickable(true);
    }


    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
    }

    @Nullable
    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {

    }

    @Override
    public void onPlayStateChanged(int playState) {
        if (playState == VideoView.STATE_PLAYBACK_COMPLETED) {
            setVisibility(VISIBLE);
            bringToFront();
        } else {
            setVisibility(GONE);
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {

    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {

    }
}
