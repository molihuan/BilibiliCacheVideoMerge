package com.molihua.hlbmerge.utils

import android.content.Context
import android.widget.Toast
import com.blankj.molihuan.utilcode.util.TimeUtils
import com.molihua.hlbmerge.dao.ConfigData
import com.molihuan.pathselector.utils.Mtools
import com.xuexiang.xupdate.XUpdate
import com.xuexiang.xupdate.easy.EasyUpdate
import com.xuexiang.xupdate.entity.UpdateError
import com.xuexiang.xupdate.utils.UpdateUtils

object UpdateTools {
    //默认更新url(gitee)

    const val UPDATE_URL_CHANNEL_GITEE: String =
        "https://gitee.com/molihuan/BilibiliCacheVideoMergeAndroid/raw/master/jsonapi/update_release.json"
    //备份更新url(gitlink)

    const val UPDATE_URL_CHANNEL_GITLINK: String =
        "https://www.gitlink.org.cn/api/molihuan/BilibiliCacheVideoMerge/raw/jsonapi%2Fupdate_release.json?ref=master"

    //已经使用gitlink检测更新
    private var usedGitlinkCheckUpdata: Boolean = false

    //已经使用github检测更新
    private const val usedGithubCheckUpdata: Boolean = false

    //点击检测更新次数
    private var clickCheckUpdataTimes: Int = 0

    //解除点击更新限制毫秒时间戳
    private var unlimitClickCheckUpdataMills: Long = 0

    const val TIMESTAMP_DAY: Long = 86400000

    const val TIMESTAMP_WEEK: Long = 604800000

    @JvmField
    val TIMESTAMP_MONTH: Long = "2592000000".toLong()

    /**
     * 限制点击检查更新频率(gitee)
     */
    @JvmStatic
    fun limitClickCheckUpdata(context: Context) {
        if (clickCheckUpdataTimes++ <= 2) {
            checkUpdata(context)
        } else {
            val nowMills = TimeUtils.getNowMills()

            if (unlimitClickCheckUpdataMills == 0L) {
                unlimitClickCheckUpdataMills = nowMills + 300000
                Mtools.toast("为了减小服务器压力,请5分钟后再试 或 自行进入官网(开源地址)下载")
            } else if (nowMills < UpdateTools.unlimitClickCheckUpdataMills) {
                Mtools.toast(
                    String.format(
                        "为了减小服务器压力,请%s分钟后再试 或 自行进入官网(开源地址)下载",
                        (UpdateTools.unlimitClickCheckUpdataMills - nowMills) / 60000
                    )
                )
            } else {
                checkUpdata(context)
                clickCheckUpdataTimes = 0
            }
        }
    }

    @JvmStatic
    fun initXUpdate(context: Context) {
        XUpdate.get()
            .debug(false)
            .isWifiOnly(false) //默认设置只在wifi下检查版本更新
            .isGet(true) //默认设置使用get请求检查版本
            .isAutoMode(false) //默认设置非自动模式，可根据具体使用配置
            .param("versionCode", UpdateUtils.getVersionCode(context)) //设置默认公共请求参数
            .param("appKey", context.packageName)
            .setOnUpdateFailureListener { error ->

                //设置版本更新出错的监听
                when (error.code) {
                    UpdateError.ERROR.CHECK_NO_NEW_VERSION -> Mtools.toast("未发现新版本!")
                    UpdateError.ERROR.CHECK_NO_NETWORK, UpdateError.ERROR.CHECK_NO_WIFI -> {}
                    else -> {
                        Mtools.toast(
                            "更新失败!正在尝试使用备用链接 或 自行进入官网(开源地址)下载",
                            Toast.LENGTH_LONG
                        )
                        //启用备用检测更新
                        checkUpdataByGitlink(context.getApplicationContext())
                    }
                }
            }
            .supportSilentInstall(false)
        //设置是否支持静默安装，默认是true
    }


    /**
     * 周期自动检查更新(gitee)
     * 自动检测更新频率
     */
    @JvmStatic
    fun autoCheckUpdata(context: Context) {
        val nowMills = TimeUtils.getNowMills()
        var updateMills = ConfigData.getUpdateMills()
        val updateFrequency = ConfigData.getUpdateFrequency()

        if (updateFrequency != 3 && nowMills > updateMills) {
            checkUpdata(context)

            when (updateFrequency) {
                0 -> updateMills = nowMills + TIMESTAMP_DAY
                2 -> updateMills = nowMills + TIMESTAMP_MONTH
                1 -> updateMills = nowMills + TIMESTAMP_WEEK
                else -> updateMills = nowMills + TIMESTAMP_WEEK
            }
            //设置下一次更新时间戳
            ConfigData.setUpdateMills(updateMills)
        }
    }

    /**
     * 检查更新(默认gitee)
     */
    @JvmStatic
    fun checkUpdata(context: Context) {
        EasyUpdate.checkUpdate(context, UPDATE_URL_CHANNEL_GITEE)
    }

    /**
     * 备用检查更新(gitlink)
     * 
     * @param context
     */
    @JvmStatic
    fun checkUpdataByGitlink(context: Context) {
        if (!usedGitlinkCheckUpdata) {
            usedGitlinkCheckUpdata = true
            EasyUpdate.checkUpdate(context, UPDATE_URL_CHANNEL_GITLINK)
        }
    }

}