package com.molihua.hlbmerge.controller.videocontroller.component;

import static xyz.doikki.videoplayer.util.PlayerUtils.stringForTime;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import master.flame.danmaku.ui.widget.DanmakuView;
import xyz.doikki.videocontroller.R;
import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.IGestureComponent;
import xyz.doikki.videoplayer.player.VideoView;

/**
 * @ClassName: VideoGestureView
 * @Author: molihuan
 * @Date: 2023/01/01/14:36
 * @Description: 视频页面手势
 */
public class VideoGestureView extends FrameLayout implements IGestureComponent {

    protected DanmakuView danmakuView;

    private ControlWrapper mControlWrapper;

    private final ImageView mIcon;
    private final ProgressBar mProgressPercent;
    private final TextView mTextPercent;

    private final LinearLayout mCenterContainer;

    private long newPosition;

    public VideoGestureView(@NonNull Context context, DanmakuView danmakuView) {
        super(context);
        this.danmakuView = danmakuView;
    }

    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(R.layout.dkplayer_layout_gesture_control_view, this, true);
        mIcon = findViewById(R.id.iv_icon);
        mProgressPercent = findViewById(R.id.pro_percent);
        mTextPercent = findViewById(R.id.tv_percent);
        mCenterContainer = findViewById(R.id.center_container);
    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {

    }

    @Override
    public void onPlayerStateChanged(int playerState) {

    }

    @Override
    public void onStartSlide() {
        mControlWrapper.hide();
        mCenterContainer.setVisibility(VISIBLE);
        mCenterContainer.setAlpha(1f);
    }

    @Override
    public void onStopSlide() {
        mCenterContainer.animate()
                .alpha(0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mCenterContainer.setVisibility(GONE);
                    }
                })
                .start();


    }

    @Override
    public void onPositionChange(int slidePosition, int currentPosition, int duration) {
        mProgressPercent.setVisibility(GONE);
        if (slidePosition > currentPosition) {
            mIcon.setImageResource(R.drawable.dkplayer_ic_action_fast_forward);
        } else {
            mIcon.setImageResource(R.drawable.dkplayer_ic_action_fast_rewind);
        }
        mTextPercent.setText(String.format("%s/%s", stringForTime(slidePosition), stringForTime(duration)));
        newPosition = slidePosition;
        danmakuView.seekTo(newPosition);
    }

    @Override
    public void onBrightnessChange(int percent) {
        mProgressPercent.setVisibility(VISIBLE);
        mIcon.setImageResource(R.drawable.dkplayer_ic_action_brightness);
        mTextPercent.setText(percent + "%");
        mProgressPercent.setProgress(percent);
    }

    @Override
    public void onVolumeChange(int percent) {

        mProgressPercent.setVisibility(VISIBLE);
        if (percent <= 0) {
            mIcon.setImageResource(R.drawable.dkplayer_ic_action_volume_off);
        } else {
            mIcon.setImageResource(R.drawable.dkplayer_ic_action_volume_up);
        }
        mTextPercent.setText(percent + "%");
        mProgressPercent.setProgress(percent);
    }

    @Override
    public void onPlayStateChanged(int playState) {
        if (playState == VideoView.STATE_IDLE
                || playState == VideoView.STATE_START_ABORT
                || playState == VideoView.STATE_PREPARING
                || playState == VideoView.STATE_PREPARED
                || playState == VideoView.STATE_ERROR
                || playState == VideoView.STATE_PLAYBACK_COMPLETED) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }
    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLock) {

    }
}
