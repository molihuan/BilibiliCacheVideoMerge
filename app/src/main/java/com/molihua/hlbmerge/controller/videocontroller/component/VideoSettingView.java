package com.molihua.hlbmerge.controller.videocontroller.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.controller.videocontroller.DKVideoController;
import com.molihua.hlbmerge.dao.ConfigData;
import com.xuexiang.xui.widget.layout.ExpandableLayout;
import com.xuexiang.xui.widget.picker.XSeekBar;

import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.IGestureComponent;

/**
 * @ClassName: VideoSettingView
 * @Author: molihuan
 * @Date: 2023/01/02/13:44
 * @Description:
 */
public class VideoSettingView extends FrameLayout implements IGestureComponent, XSeekBar.OnSeekBarListener, View.OnClickListener {

    private ControlWrapper mControlWrapper;
    private Context mContext;
    private DKVideoController dkVideoController;

    //弹幕内容
    private DanmakuContext danmakuContext;

    private ExpandableLayout expandableLayout;

    private XSeekBar danmakuSizeXSB;
    private XSeekBar danmakuAlphaXSB;
    private XSeekBar danmakuSpeedXSB;
    private XSeekBar danmakuShowAreaXSB;

    private int danmakuSize;
    private int danmakuAlpha;
    private int danmakuSpeed;

    private View emptyView;

    public VideoSettingView(@NonNull Context context, DKVideoController dkVideoController) {
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
        LayoutInflater.from(getContext()).inflate(R.layout.fragment_dk_video_setting, this, true);
        expandableLayout = findViewById(R.id.el_setting);

        danmakuSizeXSB = findViewById(R.id.xsb_danmaku_size);
        danmakuAlphaXSB = findViewById(R.id.xsb_danmaku_alpha);
        danmakuSpeedXSB = findViewById(R.id.xsb_danmaku_speed);
        danmakuShowAreaXSB = findViewById(R.id.xsb_danmaku_show_area);

        emptyView = findViewById(R.id.empty_view);
    }

    private void initData(Context context, DKVideoController dkVideoController) {
        mContext = context;
        this.dkVideoController = dkVideoController;
        danmakuContext = dkVideoController.getDanmakuContext();
        //默认值应该从配置中读取
        danmakuSize = ConfigData.getDanmakuSize();
        danmakuAlpha = ConfigData.getDanmakuAlpha();
        danmakuSpeed = ConfigData.getDanmakuSpeed();


        danmakuSizeXSB.setDefaultValue(danmakuSize);
        danmakuSizeXSB.setMin(30);
        danmakuSizeXSB.setMax(250);

        danmakuAlphaXSB.setDefaultValue(danmakuAlpha);
        danmakuAlphaXSB.setMin(0);
        danmakuAlphaXSB.setMax(100);

        danmakuSpeedXSB.setDefaultValue(danmakuSpeed);
        danmakuSpeedXSB.setMin(1);
        danmakuSpeedXSB.setMax(200);


    }

    private void initMyView() {
        setVisibility(GONE);
    }

    private void setListeners() {
        danmakuSizeXSB.setOnSeekBarListener(this);
        danmakuAlphaXSB.setOnSeekBarListener(this);
        danmakuSpeedXSB.setOnSeekBarListener(this);
        danmakuShowAreaXSB.setOnSeekBarListener(this);
        emptyView.setOnClickListener(this);
    }

    @Override
    public void onValueChanged(XSeekBar seekBar, int newValue) {
        int id = seekBar.getId();
        if (id == R.id.xsb_danmaku_size) {
            danmakuContext.setScaleTextSize((float) newValue / 100);
            danmakuSize = newValue;
        } else if (id == R.id.xsb_danmaku_alpha) {
            danmakuContext.setDanmakuTransparency((float) newValue / 100);//设置不透明度
            danmakuAlpha = newValue;
        } else if (id == R.id.xsb_danmaku_speed) {
            danmakuContext.setScrollSpeedFactor((float) (200 - newValue) / 100);
            danmakuSpeed = newValue;
        } else if (id == R.id.xsb_danmaku_show_area) {

        }
    }

    /**
     * 弹出、关闭视图
     */
    public void expandableLayoutToggle() {
        if (expandableLayout.isExpanded()) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }
        expandableLayout.toggle();
    }

    public void changeExpandableLayout(boolean isExpand) {
        if (isExpand) {
            setVisibility(VISIBLE);
            expandableLayout.expand();
        } else {
            expandableLayout.collapse();
            setVisibility(GONE);
        }
    }

    public boolean isExpandableLayout() {
        return expandableLayout.isExpanded();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.empty_view) {
            if (expandableLayout.isExpanded()) {
                changeExpandableLayout(false);
            }
        }
    }

    public int getDanmakuSize() {
        return danmakuSize;
    }

    public int getDanmakuAlpha() {
        return danmakuAlpha;
    }

    public int getDanmakuSpeed() {
        return danmakuSpeed;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //销毁视图----在这里存储配置
        ConfigData.setDanmakuSize(danmakuSize);
        ConfigData.setDanmakuAlpha(danmakuAlpha);
        ConfigData.setDanmakuSpeed(danmakuSpeed);

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
