package com.molihua.hlbmerge.controller.videocontroller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.molihuan.utilcode.util.FileUtils;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.controller.videocontroller.component.VideoBottomControlView;
import com.molihua.hlbmerge.controller.videocontroller.component.VideoCompleteView;
import com.molihua.hlbmerge.controller.videocontroller.component.VideoDanmakuView;
import com.molihua.hlbmerge.controller.videocontroller.component.VideoGestureView;
import com.molihua.hlbmerge.controller.videocontroller.component.VideoSettingView;
import com.molihua.hlbmerge.controller.videocontroller.component.VideoTitleView;
import com.molihuan.pathselector.utils.Mtools;

import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.ui.widget.DanmakuView;
import xyz.doikki.videocontroller.component.ErrorView;
import xyz.doikki.videocontroller.component.PrepareView;
import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.GestureVideoController;
import xyz.doikki.videoplayer.controller.IControlComponent;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.util.PlayerUtils;

/**
 * @ClassName: DKVideoController
 * @Author: molihuan
 * @Date: 2022/12/29/22:03
 * @Description:
 */
public class DKVideoController extends GestureVideoController implements View.OnClickListener, IControlComponent {
    //视频路径
    private String videoPath;
    //上下文
    private Context mContext;
    //上下文
    private Activity mActivity;

    //弹幕视图
    private DanmakuView mDanmakuView;


    //屏幕锁按钮
    private ImageView lockImgView;

    private VideoDanmakuView videoDanmakuView;

    private VideoSettingView videoSettingView;

    private VideoBottomControlView bottomControlView;

    public DanmakuView getDanmakuView() {
        return videoDanmakuView.getDanmakuView();
    }

    public DanmakuContext getDanmakuContext() {
        return videoDanmakuView.getDanmakuContext();
    }

    public VideoBottomControlView getBottomControlView() {
        return bottomControlView;
    }

    public boolean isLocalXmlExists() {
        return videoDanmakuView.isLocalXmlExists();
    }

    public VideoSettingView getVideoSettingView() {
        return videoSettingView;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dk_video_controller;
    }

    public DKVideoController(@NonNull Context context, String videoPath) {
        super(context);
        //获取组件
        getComponents();
        //初始化数据
        initData(context, videoPath);
        //初始化视图
        initControlView();
        //设置监听
        setListeners();
    }

    private void getComponents() {
        lockImgView = findViewById(R.id.iv_lock);
    }

    private void initData(Context context, String videoPath) {
        this.videoPath = videoPath;
        mContext = context;
        mActivity = (Activity) context;
    }

    private void initControlView() {
        //添加基础视图
        videoDanmakuView = new VideoDanmakuView(mContext, videoPath, this);
        //获取弹幕视图
        mDanmakuView = getDanmakuView();

        bottomControlView = new VideoBottomControlView(mContext, this);
        VideoCompleteView videoCompleteView = new VideoCompleteView(mContext, mDanmakuView);
        ErrorView errorView = new ErrorView(mContext);
        PrepareView prepareView = new PrepareView(mContext);
        VideoTitleView videoTitleView = new VideoTitleView(mContext, FileUtils.getFileNameNoExtension(videoPath), this);
        VideoGestureView gestureView = new VideoGestureView(mContext, mDanmakuView);
        videoSettingView = new VideoSettingView(mContext, this);

        addControlComponent(
                videoTitleView,
                bottomControlView,
                videoSettingView,
                videoCompleteView,
                errorView,
                prepareView,
                gestureView,
                videoDanmakuView
        );

        //是否在竖屏模式下开始手势控制
        setEnableInNormal(true);
        //设置播放视图自动隐藏超时
        setDismissTimeout(4000);
        //显示控制器
        show();
    }


    private void setListeners() {
        lockImgView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_lock) {
            mControlWrapper.toggleLockState();
        }
    }


    /**
     * 处理返回事件,需要Activity将返回事件传递过来
     *
     * @return 已经处理了就返回true, 没有处理返回false
     */
    @Override
    public boolean onBackPressed() {

        if (isLocked()) {
            show();
            Mtools.toast("请先解锁屏幕");
            return true;
        }

        if (videoSettingView.isExpandableLayout()) {
            videoSettingView.changeExpandableLayout(false);
            return true;
        }

        if (mControlWrapper.isFullScreen()) {
            mControlWrapper.toggleFullScreenByVideoSize(mActivity);
            return true;
        }

        mActivity.finish();

        return false;
    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
//        mControlWrapper = controlWrapper;
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
        if (mControlWrapper == null || mControlWrapper.isFullScreen()) {
            if (isVisible) {
                if (lockImgView.getVisibility() == GONE) {
                    lockImgView.setVisibility(VISIBLE);
                    if (anim != null) {
                        lockImgView.startAnimation(anim);
                    }
                }
            } else {
                lockImgView.setVisibility(GONE);
                if (anim != null) {
                    lockImgView.startAnimation(anim);
                }
            }
        }

    }


    @Override
    public void onPlayStateChanged(int playState) {
        super.onPlayStateChanged(playState);
        switch (playState) {
            //调用release方法会回到此状态
            case VideoView.STATE_IDLE:
                lockImgView.setSelected(false);
                break;
            case VideoView.STATE_PLAYING:
            case VideoView.STATE_PAUSED:
            case VideoView.STATE_PREPARED:
            case VideoView.STATE_ERROR:
            case VideoView.STATE_BUFFERED:
                break;
            case VideoView.STATE_PREPARING:
            case VideoView.STATE_BUFFERING:
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:
                lockImgView.setVisibility(GONE);
                lockImgView.setSelected(false);
                break;
        }
    }


    @Override
    public void onPlayerStateChanged(int playerState) {
        super.onPlayerStateChanged(playerState);
        switch (playerState) {
            case VideoView.PLAYER_NORMAL:
                setLayoutParams(new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                lockImgView.setVisibility(GONE);
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                if (isShowing()) {
                    lockImgView.setVisibility(VISIBLE);
                } else {
                    lockImgView.setVisibility(GONE);
                }
                break;
        }

        if (mActivity != null && hasCutout()) {
            int orientation = mActivity.getRequestedOrientation();
            int dp24 = PlayerUtils.dp2px(getContext(), 24);
            int cutoutHeight = getCutoutHeight();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                RelativeLayout.LayoutParams lblp = (RelativeLayout.LayoutParams) lockImgView.getLayoutParams();
                lblp.setMargins(dp24, 0, dp24, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) lockImgView.getLayoutParams();
                layoutParams.setMargins(dp24 + cutoutHeight, 0, dp24 + cutoutHeight, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) lockImgView.getLayoutParams();
                layoutParams.setMargins(dp24, 0, dp24, 0);
            }
        }
    }


    @Override
    public void setProgress(int duration, int position) {

    }

    /**
     * 锁定状态发生改变监听
     *
     * @param isLocked
     */
    @Override
    public void onLockStateChanged(boolean isLocked) {
        if (isLocked) {
            lockImgView.setSelected(true);
            Mtools.toast("已锁定");
        } else {
            lockImgView.setSelected(false);
            Mtools.toast("已解锁");
        }
    }


}
