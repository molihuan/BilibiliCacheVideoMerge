package com.molihua.hlbmerge;

import android.app.Application;

import com.molihua.hlbmerge.dao.ConfigData;
import com.molihuan.pathselector.PathSelector;
import com.molihuan.pathselector.configs.PathSelectorConfig;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xui.XUI;

import io.microshow.rxffmpeg.RxFFmpegInvoke;

/**
 * @ClassName: App
 * @Author: molihuan
 * @Date: 2022/12/15/22:18
 * @Description:
 */
public class App extends Application {
    @Override
    public void onCreate() {
        //腾讯的键值对存储mmkv初始化
        MMKV.initialize(this);
        //配置初始化
        ConfigData.init();
        //初始化XUI
        XUI.init(this);
        XUI.debug(false);
        //ffmpeg debug
        RxFFmpegInvoke.getInstance().setDebug(false);
        //路径选择器debug
        PathSelector.setDebug(true);
        //取消自动申请权限
        PathSelectorConfig.setAutoGetPermission(false);

        super.onCreate();
    }
}
