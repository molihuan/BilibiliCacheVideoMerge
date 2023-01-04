package com.molihua.hlbmerge.controller.videocontroller.component;

import static xyz.doikki.videoplayer.util.PlayerUtils.stringForTime;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.controller.videocontroller.DKVideoController;
import com.molihua.hlbmerge.dao.ConfigData;
import com.molihuan.pathselector.utils.Mtools;

import master.flame.danmaku.ui.widget.DanmakuView;
import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.IGestureComponent;
import xyz.doikki.videoplayer.player.VideoView;

/**
 * @ClassName: VideoBottomControlView
 * @Author: molihuan
 * @Date: 2023/01/01/22:07
 * @Description: 底部控制面板
 */
public class VideoBottomControlView extends FrameLayout implements IGestureComponent, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private ControlWrapper mControlWrapper;
    //上下文
    private Context mContext;
    //上下文
    private Activity mActivity;

    //底部控制总布局
    private RelativeLayout bottomControlRela;
    //播放按钮
    private ImageView playImgView;
    //弹幕按钮
    private ImageView barrageImgView;
    //当前播放时间
    private TextView currTimeTv;
    //总时间
    private TextView totalTimeTv;
    //进度条
    private SeekBar progressSb;
    //全屏按钮
    private ImageView fullScreenImgView;

    //弹幕视图
    private DanmakuView danmakuView;

    //视频是否已经播放完
    private boolean playCompleted = false;


    public VideoBottomControlView(@NonNull Context context, DKVideoController dkVideoController) {
        super(context);
        //获取组件
        getComponents();
        //初始化数据
        initData(context, dkVideoController);
        //初始化视图
        initMyView();
        //设置监听
        setListeners();
    }

    private void getComponents() {

        LayoutInflater.from(getContext()).inflate(R.layout.fragment_dk_video_bottom_control, this, true);

        bottomControlRela = findViewById(R.id.rela_bottom_control_panel);
        playImgView = findViewById(R.id.iv_play);
        barrageImgView = findViewById(R.id.iv_barrage);
        currTimeTv = findViewById(R.id.curr_time);
        totalTimeTv = findViewById(R.id.total_time);
        progressSb = findViewById(R.id.sb_progress);
        fullScreenImgView = findViewById(R.id.iv_full_screen);
    }

    private void initData(Context context, DKVideoController dkVideoController) {
        danmakuView = dkVideoController.getDanmakuView();
        mContext = context;
        mActivity = (Activity) context;
    }

    private void initMyView() {
        //获取弹幕是否显示配置
        if (!ConfigData.isOpenBarrage()) {
            barrageImgSrc(false);
        }
    }

    private void setListeners() {
        playImgView.setOnClickListener(this);
        barrageImgView.setOnClickListener(this);
        progressSb.setOnSeekBarChangeListener(this);
        fullScreenImgView.setOnClickListener(this);
    }

    @Override
    public void onStartSlide() {

    }

    @Override
    public void onStopSlide() {

    }

    @Override
    public void onPositionChange(int slidePosition, int currentPosition, int duration) {

    }

    @Override
    public void onBrightnessChange(int percent) {

    }

    @Override
    public void onVolumeChange(int percent) {

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

    /**
     * 监听控制视图的显示和隐藏，在这里隐藏控和显示制视图
     * show()和 hide()调用会回调此方法
     *
     * @param isVisible
     * @param anim      动画
     */
    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {
        if (isVisible) {
            bottomControlRela.setVisibility(View.VISIBLE);
        } else {
            bottomControlRela.setVisibility(View.GONE);
        }
        //设置动画
        if (anim != null) {
            bottomControlRela.startAnimation(anim);
        }
    }

    /**
     * 下面是改变按钮图片的方法
     *
     * @param status
     */
    public void playImgSrc(boolean status) {
        if (status) {
            playImgView.setImageResource(R.drawable.ic_video_play);
        } else {
            playImgView.setImageResource(R.drawable.ic_video_pause);
        }
    }

    public void barrageImgSrc(boolean status) {
        if (status) {
            barrageImgView.setImageResource(R.drawable.ic_barrage_open);
        } else {
            barrageImgView.setImageResource(R.drawable.ic_barrage_close);
        }
    }

    public void fullScreenImgSrc(boolean status) {
        if (status) {
            fullScreenImgView.setImageResource(R.drawable.ic_video_view_biggest);
        } else {
            fullScreenImgView.setImageResource(R.drawable.ic_video_view_minimum);
        }
    }

    /**
     * 播放状态改变监听
     *
     * @param playState
     */
    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case VideoView.STATE_PREPARING://准备中
                Mtools.log("准备中--------视频");
                break;
            case VideoView.STATE_PREPARED://准备完成
                Mtools.log("准备完成--------视频");
                break;
            case VideoView.STATE_PLAYING://播放中
                Mtools.log("播放中--------视频");
                playCompleted = false;
                //必须调用才可开心刷新进度回调
                mControlWrapper.startProgress();
                playImgSrc(true);

                if (danmakuView.isPaused()) {
                    danmakuView.resume();
                }

                break;
            case VideoView.STATE_PAUSED://暂停
                Mtools.log("暂停--------视频");
                mControlWrapper.stopProgress();
                playImgSrc(false);

                danmakuView.pause();
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED://播放完成
                Mtools.log("播放完成--------视频");
                playCompleted = true;
                //显示控制视图
                mControlWrapper.show();
                playImgSrc(false);
                danmakuView.hideAndPauseDrawTask();

                progressSb.setProgress(0);
                progressSb.setSecondaryProgress(0);
                break;
            //调用release方法会回到此状态
            case VideoView.STATE_IDLE://闲置
                Mtools.log("闲置--------视频");
                setVisibility(GONE);
                progressSb.setProgress(0);
                progressSb.setSecondaryProgress(0);
                break;
            //下面的回调不常用
            case VideoView.STATE_BUFFERING://缓冲中
                Mtools.log("缓冲中--------视频");
                break;
            case VideoView.STATE_BUFFERED://缓冲完成
                Mtools.log("缓冲完成--------视频");
                break;
            case VideoView.STATE_START_ABORT://开始、播放、中止
                Mtools.log("开始、播放、中止--------视频");
                break;
            case VideoView.STATE_ERROR://错误
                Mtools.log("错误--------视频");
                break;

        }
    }

    /**
     * 普通、全屏、小屏播放器状态监听
     *
     * @param playerState
     */
    @Override
    public void onPlayerStateChanged(int playerState) {
        switch (playerState) {
            case VideoView.PLAYER_NORMAL://普通
                Mtools.log("普通--------播放器");
                fullScreenImgSrc(true);
                break;
            case VideoView.PLAYER_TINY_SCREEN://小屏
                Mtools.log("小屏--------播放器");
                fullScreenImgSrc(true);
                break;
            case VideoView.PLAYER_FULL_SCREEN://全屏
                Mtools.log("全屏--------播放器");
                fullScreenImgSrc(false);
                break;
        }
    }

    /**
     * 播放进度回调
     * 只有调用了startProgress()才会开始回调此方法
     *
     * @param duration 视频总时长
     * @param position 视频当前时长
     */
    @Override
    public void setProgress(int duration, int position) {

        if (duration > 0) {
            progressSb.setEnabled(true);
            int pos = (int) (position * 1.0 / duration * progressSb.getMax());
            progressSb.setProgress(pos);
        } else {
            progressSb.setEnabled(false);
        }

        int percent = mControlWrapper.getBufferedPercentage();
        if (percent >= 95) { //解决缓冲进度不能100%问题
            progressSb.setSecondaryProgress(progressSb.getMax());
        } else {
            progressSb.setSecondaryProgress(percent * 10);
        }

        currTimeTv.setText(stringForTime(position));
        totalTimeTv.setText(stringForTime(duration));

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        onVisibilityChanged(!isLocked, null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_play) {
            if (playCompleted) {
                mControlWrapper.replay(true);
                //danmakuView.restart();
                danmakuView.show();
                danmakuView.seekTo((long) 0);
            } else {
                mControlWrapper.togglePlay();
                danmakuView.toggle();
            }

        } else if (id == R.id.iv_barrage) {
            if (danmakuView.isShown()) {
                danmakuView.hide();
                barrageImgSrc(false);
                ConfigData.setOpenBarrage(false);
            } else {
                danmakuView.show();
                barrageImgSrc(true);
                ConfigData.setOpenBarrage(true);
            }
        } else if (id == R.id.iv_full_screen) {
            mControlWrapper.toggleFullScreenByVideoSize(mActivity);
        }
    }

    /**
     * 进度条组件进度改变监听
     *
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }
        //获取进度
        long duration = mControlWrapper.getDuration();
        long newPosition = (duration * progress) / progressSb.getMax();


        if (!danmakuView.isPaused()) {
            currTimeTv.setText(stringForTime((int) newPosition));
            totalTimeTv.setText(stringForTime((int) duration));
        }


        danmakuView.seekTo(newPosition);
        mControlWrapper.seekTo((int) newPosition);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mControlWrapper.stopProgress();
        mControlWrapper.stopFadeOut();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        long duration = mControlWrapper.getDuration();
        long newPosition = (duration * seekBar.getProgress()) / progressSb.getMax();
        mControlWrapper.seekTo((int) newPosition);
        danmakuView.seekTo(newPosition);
        mControlWrapper.startProgress();
        mControlWrapper.startFadeOut();
    }
}
