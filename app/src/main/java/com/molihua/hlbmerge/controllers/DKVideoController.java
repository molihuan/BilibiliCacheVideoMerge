package com.molihua.hlbmerge.controllers;

import static xyz.doikki.videoplayer.util.PlayerUtils.stringForTime;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.activities.PlayVideoActivity;
import com.molihua.hlbmerge.utils.BiliDanmukuParserTools;
import com.molihua.hlbmerge.utils.MLHInitConfig;
import com.molihua.hlbmerge.utils.PathTools;

import java.util.HashMap;

import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.ui.widget.DanmakuView;
import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.GestureVideoController;
import xyz.doikki.videoplayer.controller.IGestureComponent;
import xyz.doikki.videoplayer.player.VideoView;

/**
 * 自定义视频播放控制器
 * 添加开启关闭弹幕
 */

public class DKVideoController extends GestureVideoController implements View.OnClickListener, IGestureComponent, SeekBar.OnSeekBarChangeListener {
    private TextView mTimeView;//具体时间
    private SeekBar mProgressView;//时间进度
    private ProgressBar mProgressBar;//加载进度等待
    private RelativeLayout mBottomPanel;//控制RelativeLayout布局
    private RelativeLayout controller_view;//总布局
    private RelativeLayout video_show_title_bar;//titlebar布局
    private ImageView playBtn;//播放按钮
    private ImageView iv_back;//播放按钮
    private ImageView iv_barrage;//弹幕按钮
    private FrameLayout repeatPlay;//重播按钮
    private ImageView muteImage;//静音图标
    private ImageView mFullScreenIv;//全屏按钮
    private Context context;
    private DanmakuView danmakuView;//弹幕视图
    private boolean mIsDragging = false;
    public int mPosition;
    private String videoPath;
    private TextView video_title_name;

    private boolean mIsShowBottomProgress = true;
    private BaseDanmakuParser mParser;//弹幕解析器
    private DanmakuContext mDanmakuContext;
    private boolean openBarrage;

    public DanmakuView getDanmakuView() {
        return danmakuView;
    }

    public DKVideoController(@NonNull Context context, DanmakuView danmakuView, String videoPath) {
        super(context);
        this.context = context;
        //this.danmakuView=danmakuView;
        this.videoPath = videoPath;

    }

    public DKVideoController(@NonNull Context context, String videoPath) {
        super(context);
        this.context = context;
        //this.danmakuView=danmakuView;
        this.videoPath = videoPath;
        initDanmakuView();//必须在此初始化
        initTitle();
    }

    private void initTitle() {
        video_title_name.setText(FileUtils.getFileNameNoExtension(videoPath));//设置标题
        video_title_name.setSelected(true);//超长跑马灯
    }

    private void initDanmakuView() {
        // DanmakuView
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        mDanmakuContext = DanmakuContext.create();
//        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
//                .setDuplicateMergingEnabled(false)
//                .setScrollSpeedFactor(1.2f)
//                .setScaleTextSize(1.2f)
//                //.setCacheStuffer(new SpannedCacheStuffer(), mCacheStufferAdapter) // 图文混排使用SpannedCacheStuffer
//                //.setCacheStuffer(new BackgroundCacheStuffer())  // 绘制背景使用BackgroundCacheStuffer
//                .setMaximumLines(maxLinesPair)
//                .preventOverlapping(overlappingEnablePair)
//                .setDanmakuMargin(40);
        if (danmakuView != null) {

            String localXmlPath = PathTools.getFileAbsolutePathNoExtension(videoPath) + ".xml";
            if (!FileUtils.isFileExists(localXmlPath)) {
                return;
            }

            //String localXmlPath="https://comment.bilibili.com/367013145.xml";
            mParser = createParser(localXmlPath);
            danmakuView.setCallback(new master.flame.danmaku.controller.DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {

                }

                //全部画完
                @Override
                public void drawingFinished() {
                    //LogUtils.e("drawingFinished已经画完");
                    danmakuView.pause();//先暂停如果停止了就启动不了弹幕了
                }

                //一些画完
                @Override
                public void danmakuShown(BaseDanmaku danmaku) {
                    //LogUtils.e("danmakuShown已经画完");
                }

                //准备完毕
                @Override
                public void prepared() {
                    danmakuView.start();
                }
            });
            danmakuView.prepare(mParser, mDanmakuContext);//进行弹幕准备
            //danmakuView.showFPS(true);//显示FPS
            danmakuView.enableDanmakuDrawingCache(true);//开启绘制缓存
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.my_dk_player_controller;
    }


    @Override
    public void initView() {
        super.initView();
        getComponents();//获取组件
        setListeners();//设置监听
        initData();//初始化数据
    }

    private void initData() {
        if (!MLHInitConfig.isOpenBarrage()) {
            danmakuView.hide();
            iv_barrage.setImageResource(R.drawable.ic_barrage_close);
        }

        //是否在竖屏模式下开始手势控制
        //setEnableInNormal(true);
        //设置播放视图自动隐藏超时
        setDismissTimeout(4000);
        show();//显示控制器

    }


    /**
     * 创建解析器对象，解析xml弹幕路径
     *
     * @param xmlFilePath
     * @return
     */
    private BaseDanmakuParser createParser(String xmlFilePath) {
        if (xmlFilePath == null || xmlFilePath.equals("")) {//如果没有路径就新建一个返回
            return new BaseDanmakuParser() {
                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }
        //B站是xml格式
        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);
        //loader = MyBiliDanmakuLoader.instance();
        try {
            loader.load(xmlFilePath);//传入xml弹幕文件路径
        } catch (Exception e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParserTools(context);//自定义弹幕
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);//载入
        return parser;
    }


    public String getCidByXml() {
        String xmlChatId = ((BiliDanmukuParserTools) mParser).getChatId();//获取xml文件中的ChatId
        return "https://comment.bilibili.com/" + xmlChatId + ".xml";
    }


    private void setListeners() {
        playBtn.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_barrage.setOnClickListener(this);
        mFullScreenIv.setOnClickListener(this);
        repeatPlay.setOnClickListener(this);
        muteImage.setOnClickListener(this);
        mProgressView.setOnSeekBarChangeListener(this);
    }

    private void getComponents() {
        mBottomPanel = findViewById(R.id.bottomPanel);
        video_show_title_bar = findViewById(R.id.video_show_title_bar);
        controller_view = findViewById(R.id.controller_view);
        mProgressView = findViewById(R.id.progress_view);
        mTimeView = findViewById(R.id.time_view);
        mProgressBar = findViewById(R.id.progressBar);
        playBtn = findViewById(R.id.iv_play);//播放按钮
        iv_back = findViewById(R.id.iv_back);//返回按钮
        repeatPlay = findViewById(R.id.repeatPlay);//重播按钮
        mFullScreenIv = findViewById(R.id.iv_fullscreen);//全屏按钮
        muteImage = findViewById(R.id.iv_mute);//静音按钮
        iv_barrage = findViewById(R.id.iv_barrage);//弹幕按钮
        danmakuView = findViewById(R.id.play_DanmakuView);//弹幕按钮
        video_title_name = findViewById(R.id.video_title_name);//title

    }

    //点击弹幕按钮时会调用
    public void barrageBtnPress() {
        if (danmakuView.isShown()) {
            //显示则隐藏
            MLHInitConfig.setOpenBarrage(false);
            danmakuView.hide();
            iv_barrage.setImageResource(R.drawable.ic_barrage_close);
        } else {
            MLHInitConfig.setOpenBarrage(true);
            danmakuView.show();
            iv_barrage.setImageResource(R.drawable.ic_barrage_open);
        }
    }

    //点击全屏按钮
    public void screenTypeBtnPress() {
        mControlWrapper.toggleFullScreenByVideoSize((Activity) context);//根据视频大小全屏
        if (!ScreenUtils.isPortrait()) {//不是竖屏则设置为竖屏
            ScreenUtils.setPortrait((Activity) context);
        }
    }


    //点击播放按钮
    public void playBtnPress() {
        if (mControlWrapper.isPlaying()) {
            danmakuView.pause();

        } else {
            danmakuView.resume();
        }
        togglePlay();
    }

    //点击静音按钮
    public void muteBtnPress() {


        //ToastUtils.make().show();

        if (mControlWrapper.isMute()) {//是否静音
            mControlWrapper.setMute(false);
            muteImage.setImageResource(R.drawable.ic_volume_open);
        } else {
            mControlWrapper.setMute(true);
            muteImage.setImageResource(R.drawable.ic_volume_close);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.repeatPlay://重播按钮
                mControlWrapper.replay(true);//重新播放
                danmakuView.seekTo((long) 0);
                break;
            case R.id.iv_fullscreen://全屏按钮
                screenTypeBtnPress();
                break;
            case R.id.iv_mute://静音
                muteBtnPress();
                break;
            case R.id.iv_play://播放按钮
                playBtnPress();
                break;
            case R.id.iv_barrage://点击弹幕按钮
                barrageBtnPress();
                break;
            case R.id.iv_back://返回按钮
                ((PlayVideoActivity) context).onBackPressed();
                break;

        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }
        long duration = mControlWrapper.getDuration();//获取进度
        long newPosition = (duration * progress) / mProgressView.getMax();
        if (mIsDragging) {
            if (mTimeView != null) {
                mTimeView.setText(stringForTime((int) newPosition) + "/" + stringForTime((int) duration));
            }
        }

        danmakuView.seekTo(newPosition);
        mControlWrapper.seekTo((int) newPosition);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mIsDragging = true;
        mControlWrapper.stopProgress();
        mControlWrapper.stopFadeOut();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mIsDragging = false;
        long duration = mControlWrapper.getDuration();
        long newPosition = (duration * seekBar.getProgress()) / mProgressView.getMax();
        mControlWrapper.seekTo((int) newPosition);
        danmakuView.seekTo(newPosition);
        mControlWrapper.startProgress();
        mControlWrapper.startFadeOut();
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
     * 子类重写此方法监听控制面板和title的显示和隐藏
     *
     * @param isVisible 是否可见
     * @param anim      显示/隐藏动画
     */
    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {
        if (isVisible) {
            mBottomPanel.setVisibility(View.VISIBLE);
            video_show_title_bar.setVisibility(View.VISIBLE);
            if (anim != null) {
                mBottomPanel.startAnimation(anim);
                video_show_title_bar.startAnimation(anim);
            }

        } else {
            mBottomPanel.setVisibility(View.GONE);
            video_show_title_bar.setVisibility(View.GONE);
            if (anim != null) {
                mBottomPanel.startAnimation(anim);
                video_show_title_bar.startAnimation(anim);
            }

        }


    }


    /**
     * 播放器状态
     * 普通播放器10
     * 全屏播放器11
     * 小屏播放器12
     *
     * @param playerState
     */
    @Override
    public void onPlayerStateChanged(int playerState) {
        //ToastUtils.make().show("播放器状态："+playerState);
        ScreenUtils.setFullScreen((Activity) context);//全屏模式
        switch (playerState) {
            case VideoView.PLAYER_NORMAL://普通播放器10
                mFullScreenIv.setImageResource(R.drawable.ic_video_view_biggest);
                break;
            case VideoView.PLAYER_TINY_SCREEN://小屏播放器12
                mFullScreenIv.setImageResource(R.drawable.ic_video_view_biggest);
                break;
            case VideoView.PLAYER_FULL_SCREEN://全屏播放器11
                mFullScreenIv.setImageResource(R.drawable.ic_video_view_minimum);

                break;
        }
        super.onPlayerStateChanged(playerState);
    }

    /**
     * 播放状态改变
     *
     * @param playState
     */
    @Override
    public void onPlayStateChanged(int playState) {
        //ToastUtils.make().show("播放状态："+playState);

        switch (playState) {
            //调用release方法会回到此状态
            case VideoView.STATE_IDLE://闲置
                mProgressBar.setVisibility(View.GONE);
                repeatPlay.setVisibility(View.GONE);
                break;
            case VideoView.STATE_PLAYING://播放中3
                startProgress();//必须在此调用
                if (danmakuView.isPaused()) {
                    danmakuView.resume();
                }
                playBtn.setImageResource(R.drawable.ic_video_play);//设置为播放图片
                mProgressBar.setVisibility(View.GONE);
                repeatPlay.setVisibility(View.GONE);
                break;
            case VideoView.STATE_PAUSED://暂停4
                playBtn.setImageResource(R.drawable.ic_video_pause);
                danmakuView.pause();
                break;
            case VideoView.STATE_PREPARED://准备完毕
                break;
            case VideoView.STATE_ERROR://错误
                break;
            case VideoView.STATE_BUFFERED://缓冲完毕
                break;
            case VideoView.STATE_PREPARING://准备中
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case VideoView.STATE_BUFFERING://缓冲中
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED://播放完成
                mProgressBar.setVisibility(View.GONE);
                repeatPlay.setVisibility(View.VISIBLE);
                playBtn.setImageResource(R.drawable.ic_video_pause);
                //mLockButton.setVisibility(GONE);
                //mLockButton.setSelected(false);
                break;
        }

        super.onPlayStateChanged(playState);
    }

    /**
     * 根据进度改变ui
     *
     * @param duration 总时间
     * @param position 当前时间
     */
    @Override
    public void setProgress(int duration, int position) {


        if (mProgressView != null) {
            if (duration > 0) {
                mProgressView.setEnabled(true);
                int pos = (int) (position * 1.0 / duration * mProgressView.getMax());
                mProgressView.setProgress(pos);
                mProgressBar.setProgress(pos);
            } else {
                mProgressView.setEnabled(false);
            }
            int percent = mControlWrapper.getBufferedPercentage();
            if (percent >= 95) { //解决缓冲进度不能100%问题
                mProgressView.setSecondaryProgress(mProgressView.getMax());
                mProgressBar.setSecondaryProgress(mProgressBar.getMax());
            } else {
                mProgressView.setSecondaryProgress(percent * 10);
                mProgressBar.setSecondaryProgress(percent * 10);
            }
        }
        if (mTimeView != null)
            mTimeView.setText(stringForTime(position) + "/" + stringForTime(duration));
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {

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
}
