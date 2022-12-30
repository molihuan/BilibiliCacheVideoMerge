package com.molihua.hlbmerge.controller.videocontroller;

import static xyz.doikki.videoplayer.util.PlayerUtils.stringForTime;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.molihuan.utilcode.util.FileUtils;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.dao.ConfigData;
import com.molihua.hlbmerge.utils.BiliDanmukuParserTools;
import com.molihuan.pathselector.utils.Mtools;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.parser.android.AndroidFileSource;
import master.flame.danmaku.ui.widget.DanmakuView;
import xyz.doikki.videocontroller.component.GestureView;
import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.GestureVideoController;
import xyz.doikki.videoplayer.controller.IControlComponent;
import xyz.doikki.videoplayer.player.VideoView;

/**
 * @ClassName: DKVideoController
 * @Author: molihuan
 * @Date: 2022/12/29/22:03
 * @Description:
 */
public class DKVideoController extends GestureVideoController implements View.OnClickListener, IControlComponent, SeekBar.OnSeekBarChangeListener {
    //视频路径
    private String videoPath;
    //上下文
    private Context mContext;
    //上下文
    private Activity mActivity;

    //标题栏总布局
    private RelativeLayout titlebarRela;
    //返回按钮
    private ImageView backImgView;
    //视频名称TextView
    private TextView videoNameTv;
    //弹幕视图
    private DanmakuView danmakuView;
    //底部控制总布局
    private RelativeLayout bottomControlRela;
    //播放按钮
    private ImageView playImgView;
    //弹幕按钮
    private ImageView barrageImgView;
    //时间TextView
    private TextView timeTv;
    //进度条
    private SeekBar progressSb;
    //声音按钮
    private ImageView soundImgView;
    //全屏按钮
    private ImageView fullScreenImgView;
    //屏幕锁按钮
    private ImageView lockImgView;


    //视频是否已经播放完
    private boolean playCompleted = false;

    //弹幕内容
    private DanmakuContext danmakuContext;

    public DanmakuView getDanmakuView() {
        return danmakuView;
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
        titlebarRela = findViewById(R.id.rela_titlebar);
        backImgView = findViewById(R.id.iv_back);
        videoNameTv = findViewById(R.id.tv_video_name_titlebar);
        danmakuView = findViewById(R.id.danmaku_show_area);
        bottomControlRela = findViewById(R.id.rela_bottom_control_panel);
        playImgView = findViewById(R.id.iv_play);
        barrageImgView = findViewById(R.id.iv_barrage);
        timeTv = findViewById(R.id.tv_time);
        progressSb = findViewById(R.id.sb_progress);
        soundImgView = findViewById(R.id.iv_sound);
        fullScreenImgView = findViewById(R.id.iv_full_screen);
        lockImgView = findViewById(R.id.iv_lock);
    }

    private void initData(Context context, String videoPath) {
        this.videoPath = videoPath;
        mContext = context;
        mActivity = (Activity) context;
    }

    private void initControlView() {
        if (videoNameTv != null && videoPath != null) {
            videoNameTv.setText(FileUtils.getFileNameNoExtension(videoPath));
            //设置setSelected才可以实现跑马灯效果
            videoNameTv.setSelected(true);
        }
        //添加手势视图
        addControlComponent(new GestureView(mContext));
        //初始化弹幕
        initDanmakuView();
        //是否在竖屏模式下开始手势控制
        setEnableInNormal(true);
        //设置播放视图自动隐藏超时
        setDismissTimeout(4000);
        //显示控制器
        show();
    }

    /**
     * 初始化弹幕
     */
    private void initDanmakuView() {
        //获取弹幕是否显示配置
        if (!ConfigData.isOpenBarrage()) {
            danmakuView.hide();
            barrageImgSrc(false);
        }
        //创建弹幕内容
        danmakuContext = DanmakuContext.create();

        if (danmakuView != null) {
            String localXmlPath = videoPath.replace(FileUtils.getFileExtension(videoPath), "xml");
            if (!FileUtils.isFileExists(localXmlPath)) {
                return;
            }
            //String localXmlPath="https://comment.bilibili.com/367013145.xml";
            IDataSource danmakuFileSource = new AndroidFileSource(localXmlPath);
            //自定义弹幕解析工具
            BaseDanmakuParser danmakuParser = new BiliDanmukuParserTools(mContext);
            danmakuParser.load(danmakuFileSource);
            //设置弹幕绘制回调
            danmakuView.setCallback(new DrawHandler.Callback() {
                @Override
                public void prepared() {
                    Mtools.log("弹幕准备完成--------弹幕");
                    danmakuView.start();
                }

                @Override
                public void updateTimer(DanmakuTimer timer) {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {

                }

                @Override
                public void drawingFinished() {
                    Mtools.log("弹幕全部绘制完成--------弹幕");
                    //先暂停如果停止了就启动不了弹幕了
                    danmakuView.hideAndPauseDrawTask();
                }
            });

            //进行弹幕准备
            danmakuView.prepare(danmakuParser, danmakuContext);
            //显示FPS
            //danmakuView.showFPS(true);
            //开启绘制缓存
            danmakuView.enableDanmakuDrawingCache(true);
        }

    }

    private void setListeners() {
        backImgView.setOnClickListener(this);
        videoNameTv.setOnClickListener(this);
        playImgView.setOnClickListener(this);
        barrageImgView.setOnClickListener(this);
        progressSb.setOnSeekBarChangeListener(this);
        soundImgView.setOnClickListener(this);
        fullScreenImgView.setOnClickListener(this);
        lockImgView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            onBackPressed();
        } else if (id == R.id.tv_video_name_titlebar) {

        } else if (id == R.id.iv_play) {

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
        } else if (id == R.id.iv_sound) {
            //是否静音
            if (mControlWrapper.isMute()) {
                mControlWrapper.setMute(false);
                soundImgView.setImageResource(R.drawable.ic_volume_open);
            } else {
                mControlWrapper.setMute(true);
                soundImgView.setImageResource(R.drawable.ic_volume_close);
            }

        } else if (id == R.id.iv_full_screen) {
            mControlWrapper.toggleFullScreenByVideoSize(mActivity);
        } else if (id == R.id.iv_lock) {
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

        if (mControlWrapper.isFullScreen()) {
            mControlWrapper.toggleFullScreenByVideoSize(mActivity);
            return true;
        }

        mActivity.finish();

        return false;
    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        //mControlWrapper = controlWrapper;
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

            if (mControlWrapper != null && mControlWrapper.isFullScreen()) {
                if (lockImgView.getVisibility() == GONE) {
                    lockImgView.setVisibility(VISIBLE);
                }
            }

            titlebarRela.setVisibility(View.VISIBLE);
            bottomControlRela.setVisibility(View.VISIBLE);
        } else {
            lockImgView.setVisibility(GONE);
            titlebarRela.setVisibility(View.GONE);
            bottomControlRela.setVisibility(View.GONE);
        }
        //设置动画
        if (anim != null) {
            lockImgView.startAnimation(anim);
            titlebarRela.startAnimation(anim);
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
                startProgress();
                playImgSrc(true);

                if (danmakuView.isPaused()) {
                    danmakuView.resume();
                }

                break;
            case VideoView.STATE_PAUSED://暂停
                Mtools.log("暂停--------视频");
                stopProgress();
                playImgSrc(false);

                danmakuView.pause();
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED://播放完成
                Mtools.log("播放完成--------视频");
                playCompleted = true;
                //显示控制视图
                show();
                playImgSrc(false);
                danmakuView.hideAndPauseDrawTask();
                break;
            //调用release方法会回到此状态
            case VideoView.STATE_IDLE://闲置
                Mtools.log("闲置--------视频");
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

        super.onPlayStateChanged(playState);
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

        super.onPlayerStateChanged(playerState);
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

        timeTv.setText(stringForTime(position) + "/" + stringForTime(duration));

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
            timeTv.setText(stringForTime((int) newPosition) + "/" + stringForTime((int) duration));
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
