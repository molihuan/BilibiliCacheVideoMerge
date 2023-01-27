package com.molihua.hlbmerge;


import android.app.Application;

import com.molihua.hlbmerge.dao.ConfigData;
import com.molihua.hlbmerge.utils.InitTool;
import com.molihua.hlbmerge.utils.UpdataTools;
import com.molihuan.pathselector.PathSelector;
import com.molihuan.pathselector.configs.PathSelectorConfig;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xui.XUI;

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
        //友盟预初始化
        InitTool.preInit(this, true, false);
        //ffmpeg核心初始化
        ConfigData.initFFmpegCore();
        //初始化XUI
        XUI.init(this);
        XUI.debug(false);
        //路径选择器debug
        PathSelector.setDebug(true);
        //取消自动申请权限
        PathSelectorConfig.setAutoGetPermission(false);
        //XUpdate初始化
        UpdataTools.initXUpdate(this);

        //ffmpeg核心debug
        ConfigData.ffmpegCore.setDebug(false);
        super.onCreate();
    }


}
