package com.molihua.hlbmerge;


import android.app.Application;
import android.widget.Toast;

import com.molihua.hlbmerge.dao.ConfigData;
import com.molihua.hlbmerge.utils.LConstants;
import com.molihua.hlbmerge.utils.UMTools;
import com.molihua.hlbmerge.utils.UpdataTools;
import com.molihuan.pathselector.PathSelector;
import com.molihuan.pathselector.configs.PathSelectorConfig;
import com.molihuan.pathselector.utils.Mtools;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xui.XUI;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;
import com.xuexiang.xupdate.utils.UpdateUtils;

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
        ConfigData.initFFmpegCore();
        //初始化XUI
        XUI.init(this);
        XUI.debug(false);
        //路径选择器debug
        PathSelector.setDebug(true);
        //取消自动申请权限
        PathSelectorConfig.setAutoGetPermission(false);
        //XUpdate初始化
        initXUpdate();
        //友盟预初始化
        UMTools.setDebug(false);
        UMTools.setChannel(UMTools.CHANNEL_RELEASE);
        UMTools.preInit(this);
        //ffmpeg核心debug
        ConfigData.ffmpegCore.setDebug(false);
        super.onCreate();
    }

    private void initXUpdate() {
        XUpdate.get()
                .debug(false)
                .isWifiOnly(true)                                               //默认设置只在wifi下检查版本更新
                .isGet(true)                                                    //默认设置使用get请求检查版本
                .isAutoMode(false)                                              //默认设置非自动模式，可根据具体使用配置
                .param("versionCode", UpdateUtils.getVersionCode(this))         //设置默认公共请求参数
                .param("appKey", getPackageName())
                .setOnUpdateFailureListener(new OnUpdateFailureListener() {     //设置版本更新出错的监听
                    @Override
                    public void onFailure(UpdateError error) {
                        switch (error.getCode()) {
                            case UpdateError.ERROR.CHECK_NO_NEW_VERSION:
                                Mtools.toast("未发现新版本!");
                                break;
                            case UpdateError.ERROR.CHECK_NO_NETWORK:
                            case UpdateError.ERROR.CHECK_NO_WIFI:
                                break;
                            default:
                                Mtools.toast("更新失败!正在尝试使用备用链接 或 自行进入下载:" + LConstants.PROJECT_ADDRESS, Toast.LENGTH_LONG);
                                //启用备用检测更新
                                UpdataTools.checkUpdataByGitlink(getApplicationContext());
                        }

                    }
                })
                .supportSilentInstall(true)                                     //设置是否支持静默安装，默认是true
        ;
    }


}
