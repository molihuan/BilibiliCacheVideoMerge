package com.molihua.hlbmerge.utils;

import android.content.Context;
import android.widget.Toast;

import com.blankj.molihuan.utilcode.util.TimeUtils;
import com.molihua.hlbmerge.dao.ConfigData;
import com.molihuan.pathselector.utils.Mtools;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.easy.EasyUpdate;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;
import com.xuexiang.xupdate.utils.UpdateUtils;

/**
 * @ClassName: UpdataTools
 * @Author: molihuan
 * @Date: 2023/01/03/15:47
 * @Description:
 */
public class UpdataTools {
    //**************************    更新url    ****************************
    //默认更新url(gitee)
    public static final String UPDATE_URL_CHANNEL_GITEE = "https://gitee.com/molihuan/BilibiliCacheVideoMergeAndroid/raw/master/jsonapi/update_release.json";
    //备份更新url(gitlink)
    public static final String UPDATE_URL_CHANNEL_GITLINK = "https://www.gitlink.org.cn/api/molihuan/BilibiliCacheVideoMerge/raw?filepath=jsonapi/update_release.json&ref=master";
    //备份更新url(github)
    public static final String UPDATE_URL_CHANNEL_GITHUB = "https://raw.githubusercontent.com/molihuan/BilibiliCacheVideoMerge/master/jsonapi/update_release.json";
    //已经使用gitlink检测更新
    private static boolean usedGitlinkCheckUpdata = false;
    //已经使用github检测更新
    private static boolean usedGithubCheckUpdata = false;

    //点击检测更新次数
    private static int clickCheckUpdataTimes = 0;

    //解除点击更新限制毫秒时间戳
    private static long unlimitClickCheckUpdataMills = 0;

    public final static long TIMESTAMP_DAY = 86400000;
    public final static long TIMESTAMP_WEEK = 604800000;
    public final static long TIMESTAMP_MONTH = Long.parseLong("2592000000");

    /**
     * 限制点击检查更新频率(gitee)
     */
    public static void limitClickCheckUpdata(Context context) {
        if (clickCheckUpdataTimes++ <= 2) {
            checkUpdata(context);
        } else {
            long nowMills = TimeUtils.getNowMills();

            if (unlimitClickCheckUpdataMills == 0) {
                unlimitClickCheckUpdataMills = nowMills + 300000;
                Mtools.toast("为了减小服务器压力,请5分钟后再试 或 自行进入下载:" + LConstants.PROJECT_ADDRESS);
            } else if (nowMills < unlimitClickCheckUpdataMills) {
                Mtools.toast(String.format("为了减小服务器压力,请%s分钟后再试 或 自行进入下载:" + LConstants.PROJECT_ADDRESS, (unlimitClickCheckUpdataMills - nowMills) / 60000));
            } else {
                checkUpdata(context);
                clickCheckUpdataTimes = 0;
            }

        }

    }

    public static void initXUpdate(Context context) {
        XUpdate.get()
                .debug(false)
                .isWifiOnly(true)                                               //默认设置只在wifi下检查版本更新
                .isGet(true)                                                    //默认设置使用get请求检查版本
                .isAutoMode(false)                                              //默认设置非自动模式，可根据具体使用配置
                .param("versionCode", UpdateUtils.getVersionCode(context))         //设置默认公共请求参数
                .param("appKey", context.getPackageName())
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
                                UpdataTools.checkUpdataByGitlink(context.getApplicationContext());
                        }

                    }
                })
                .supportSilentInstall(true)                                     //设置是否支持静默安装，默认是true
        ;
    }

    /**
     * 周期自动检查更新(gitee)
     * 自动检测更新频率
     */
    public static void autoCheckUpdata(Context context) {
        long nowMills = TimeUtils.getNowMills();
        long updateMills = ConfigData.getUpdateMills();
        int updateFrequency = ConfigData.getUpdateFrequency();

        if (updateFrequency != 3 && nowMills > updateMills) {
            checkUpdata(context);

            switch (updateFrequency) {
                case 0://一天
                    updateMills = nowMills + TIMESTAMP_DAY;
                    break;
                case 2://一月
                    updateMills = nowMills + TIMESTAMP_MONTH;
                    break;
                case 1://一周
                default:
                    updateMills = nowMills + TIMESTAMP_WEEK;
            }
            //设置下一次更新时间戳
            ConfigData.setUpdateMills(updateMills);

        }


    }

    //public static final String XUPDATE_DEMO_DOWNLOAD_URL = "https://xuexiangjys.oss-cn-shanghai.aliyuncs.com/apk/xupdate_demo_1.0.2.apk";

    /**
     * 检查更新(gitee)
     */
    public static void checkUpdata(Context context) {
        EasyUpdate.checkUpdate(context, UPDATE_URL_CHANNEL_GITEE);

//测试是否可以下载apk文件
//        UpdateManager.Builder builder = EasyUpdate.create(context, UPDATE_URL_CHANNEL_GITEE);
//        builder.build()
//                .download("https://www.gitlink.org.cn/api/molihuan/BilibiliCacheVideoMerge/raw?filepath=app/release/app-release.apk", new OnFileDownloadListener() {
//                    @Override
//                    public void onStart() {
//
//                    }
//
//                    @Override
//                    public void onProgress(float progress, long total) {
//
//                    }
//
//                    @Override
//                    public boolean onCompleted(File file) {
//                        Mtools.toast("apk下载完毕，文件路径：" + file.getPath());
//                        LogUtils.e(file.getPath());
//                        return false;
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//
//                    }
//                });

    }

    /**
     * 备用检查更新(gitlink)
     *
     * @param context
     */
    public static void checkUpdataByGitlink(Context context) {
        if (!usedGitlinkCheckUpdata) {
            usedGitlinkCheckUpdata = true;
            EasyUpdate.checkUpdate(context, UPDATE_URL_CHANNEL_GITLINK);
        }
    }

    /**
     * 备用检查更新(github)
     *
     * @param context
     */
    public static void checkUpdataByGithub(Context context) {
        if (!usedGithubCheckUpdata) {
            usedGithubCheckUpdata = true;
            EasyUpdate.checkUpdate(context, UPDATE_URL_CHANNEL_GITHUB);
        }
    }
}
