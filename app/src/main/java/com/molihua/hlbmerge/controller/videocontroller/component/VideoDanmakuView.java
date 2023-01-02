package com.molihua.hlbmerge.controller.videocontroller.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.molihuan.utilcode.util.FileUtils;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.controller.videocontroller.DKVideoController;
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
import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.IGestureComponent;

/**
 * @ClassName: VideoDanmakuView
 * @Author: molihuan
 * @Date: 2023/01/01/21:30
 * @Description:
 */
public class VideoDanmakuView extends FrameLayout implements IGestureComponent {
    //弹幕视图
    private DanmakuView danmakuView;

    //弹幕内容
    private DanmakuContext danmakuContext;

    private ControlWrapper mControlWrapper;
    private String videoPath;
    private Context mContext;
    private DKVideoController dkVideoController;

    public DanmakuView getDanmakuView() {
        return danmakuView;
    }

    public DanmakuContext getDanmakuContext() {
        return danmakuContext;
    }

    public VideoDanmakuView(@NonNull Context context, String videoPath, DKVideoController dkVideoController) {
        super(context);
        //获取组件
        getComponents();
        //初始化数据
        initData(context, videoPath, dkVideoController);
        //初始化视图
        initMyView();
        //设置监听
        setListeners();
    }

    private void getComponents() {
        LayoutInflater.from(getContext()).inflate(R.layout.fragment_dk_video_danmaku, this, true);
        danmakuView = findViewById(R.id.danmaku_show_area);
    }

    private void initData(Context context, String videoPath, DKVideoController dkVideoController) {
        this.videoPath = videoPath;
        mContext = context;
        this.dkVideoController = dkVideoController;
    }

    private void initMyView() {
        //初始化弹幕
        initDanmakuView();
    }

    /**
     * 初始化弹幕
     */
    private void initDanmakuView() {
        //获取弹幕是否显示配置
        if (!ConfigData.isOpenBarrage()) {
            danmakuView.hide();
        }
        //初始化弹幕配置
        initDanmakuConfig();

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

    private void initDanmakuConfig() {
        //创建弹幕配置
        danmakuContext = DanmakuContext.create();

//         滚动弹幕最大显示5行
//        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
//        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5);
//        danmakuContext.setMaximumLines(maxLinesPair)//设置滚动弹幕最大显示行

        //默认值应该从配置中读取
        int danmakuSize = ConfigData.getDanmakuSize();
        int danmakuAlpha = ConfigData.getDanmakuAlpha();
        int danmakuSpeed = ConfigData.getDanmakuSpeed();

        //设置基本弹幕配置
        danmakuContext
                .setSpecialDanmakuVisibility(true)//显示特殊弹幕
                .setScaleTextSize((float) danmakuSize / 100)//设置弹幕大小
                .setDanmakuTransparency((float) danmakuAlpha / 100)//设置透明度
                .setScrollSpeedFactor((float) (200 - danmakuSpeed) / 100)//设置弹幕滚动速度系数
        ;


    }

    private void setListeners() {
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

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {

    }

    @Override
    public void onPlayStateChanged(int playState) {

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
