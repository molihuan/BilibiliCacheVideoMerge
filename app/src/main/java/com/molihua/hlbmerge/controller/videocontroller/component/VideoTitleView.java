package com.molihua.hlbmerge.controller.videocontroller.component;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.controller.videocontroller.DKVideoController;
import com.xuexiang.xui.widget.textview.MarqueeTextView;

import java.util.ArrayList;
import java.util.List;

import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.IControlComponent;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.util.PlayerUtils;

/**
 * @ClassName: VideoTitleView
 * @Author: molihuan
 * @Date: 2023/01/01/15:01
 * @Description:
 */
public class VideoTitleView extends FrameLayout implements IControlComponent {
    private ControlWrapper mControlWrapper;

    private final RelativeLayout mTitleContainer;
    private final MarqueeTextView mTitle;
    private final TextView mSysTime;//系统当前时间
    private final ImageView batteryLevel;//电量
    private final ImageView settingIv;//设置按钮

    private final BatteryReceiver mBatteryReceiver;
    private boolean mIsRegister;//是否注册BatteryReceiver

    private String videoName;
    private DKVideoController dkVideoController;


    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(R.layout.fragment_dk_video_title, this, true);
        mTitleContainer = findViewById(R.id.rela_titlebar);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dkVideoController.onBackPressed();
            }
        });
        mTitle = findViewById(R.id.title);
        
        mSysTime = findViewById(R.id.sys_time);
        //电量
        batteryLevel = findViewById(R.id.iv_battery);
        mBatteryReceiver = new BatteryReceiver(batteryLevel);

        settingIv = findViewById(R.id.iv_setting);
        settingIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dkVideoController.getVideoSettingView().expandableLayoutToggle();
            }
        });

    }

    public VideoTitleView(@NonNull Context context, String videoName, DKVideoController dkVideoController) {
        super(context);
        //初始化数据
        initData(context, videoName, dkVideoController);
        //初始化视图
        initMyView();
        //设置监听
        setListeners();
    }

    private void initData(Context context, String videoName, DKVideoController dkVideoController) {
        this.videoName = videoName;
        this.dkVideoController = dkVideoController;
    }

    private void initMyView() {
        setTitle(videoName);
    }

    private void setListeners() {
    }

    public void setTitle(String title) {
        //大于25个字就跑马灯
        if (title.length() > 25) {
            List<String> titles = new ArrayList<>();
            titles.add(title);
            mTitle.startSimpleRoll(titles);
        } else {
            mTitle.setText(title);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mIsRegister) {
            getContext().unregisterReceiver(mBatteryReceiver);
            mIsRegister = false;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mIsRegister) {
            getContext().registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            mIsRegister = true;
        }
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
        //只在全屏时才有效
        //if (mControlWrapper != null && !mControlWrapper.isFullScreen()) return;
        if (isVisible) {

            if (getVisibility() == GONE) {

                //不是全屏时
                if (mControlWrapper == null || !mControlWrapper.isFullScreen()) {
                    mSysTime.setVisibility(GONE);
                    batteryLevel.setVisibility(GONE);
                } else {
                    mSysTime.setVisibility(VISIBLE);
                    batteryLevel.setVisibility(VISIBLE);
                    mSysTime.setText(PlayerUtils.getCurrentSystemTime());
                }

                setVisibility(VISIBLE);
                if (anim != null) {
                    startAnimation(anim);
                }
            }
        } else {
            if (getVisibility() == VISIBLE) {
                setVisibility(GONE);
                if (anim != null) {
                    startAnimation(anim);
                }
            }
        }
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case VideoView.STATE_IDLE:
            case VideoView.STATE_START_ABORT:
            case VideoView.STATE_PREPARING:
            case VideoView.STATE_PREPARED:
            case VideoView.STATE_ERROR:
            case VideoView.STATE_PLAYBACK_COMPLETED:
                setVisibility(VISIBLE);
                break;
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        if (playerState == VideoView.PLAYER_FULL_SCREEN) {
            if (mControlWrapper.isShowing() && !mControlWrapper.isLocked()) {
                setVisibility(VISIBLE);
                mSysTime.setText(PlayerUtils.getCurrentSystemTime());
            }
            mTitle.setSelected(true);
        } else {
            setVisibility(GONE);
            mTitle.setSelected(false);
        }

        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity != null && mControlWrapper.hasCutout()) {
            int orientation = activity.getRequestedOrientation();
            int cutoutHeight = mControlWrapper.getCutoutHeight();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mTitleContainer.setPadding(0, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                mTitleContainer.setPadding(cutoutHeight, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                mTitleContainer.setPadding(0, 0, cutoutHeight, 0);
            }
        }
    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        if (isLocked) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
            mSysTime.setText(PlayerUtils.getCurrentSystemTime());
        }
    }

    private static class BatteryReceiver extends BroadcastReceiver {
        private final ImageView pow;

        public BatteryReceiver(ImageView pow) {
            this.pow = pow;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras == null) return;
            int current = extras.getInt("level");// 获得当前电量
            int total = extras.getInt("scale");// 获得总电量
            int percent = current * 100 / total;
            pow.getDrawable().setLevel(percent);
        }
    }
}
