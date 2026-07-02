package com.molihua.hlbmerge.utils

import android.content.Context
import com.blankj.molihuan.utilcode.util.DeviceUtils
import com.molihua.hlbmerge.activity.impl.MainActivity
import com.molihua.hlbmerge.dao.ConfigData
import com.molihua.hlbmerge.dialog.impl.StatementDialog
import com.molihua.hlbmerge.utils.GeneralTools.initPermissionsOfStorage
import com.molihua.hlbmerge.utils.GeneralTools.isApkDebug
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.bugly.crashreport.CrashReport.UserStrategy
import com.umeng.commonsdk.UMConfigure
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog

object InitTool {
    private const val APP_KEY: String = "63b302eed64e6861390b324f"
    private const val BUGLY_APP_KEY: String = "ac467503ed"
    private var APP_CHANNEL: String = "release_molihuan"

    fun setChannel(channel: String) {
        APP_CHANNEL = channel
    }

    /**
     * 友盟和bugly初始化
     * 
     * @param context
     */
    @JvmStatic
    fun init(context: Context, buglyDebug: Boolean, umDebug: Boolean) {
        var buglyDebug = buglyDebug
        var umDebug = umDebug
        if (!isApkDebug(context)) {
            buglyDebug = false
            umDebug = false
        }
        //友盟初始化
        //初始化组件化基础库, 所有友盟业务SDK都必须调用此初始化接口。
        UMConfigure.setLogEnabled(umDebug)
        UMConfigure.init(
            context,
            InitTool.APP_KEY,
            InitTool.APP_CHANNEL,
            UMConfigure.DEVICE_TYPE_PHONE,
            ""
        )
        //bugly初始化
        val strategy = UserStrategy(context)
        strategy.setDeviceID(DeviceUtils.getUniqueDeviceId())
            .setDeviceModel(DeviceUtils.getModel())
            .setAppChannel(InitTool.APP_CHANNEL)
            .setEnableUserInfo(true)
        CrashReport.initCrashReport(context, InitTool.BUGLY_APP_KEY, buglyDebug, strategy)
        CrashReport.setUserId(DeviceUtils.getUniqueDeviceId())
    }

    /**
     * 友盟预初始化
     * 
     * @param context
     */
    @JvmStatic
    fun preInit(context: Context, buglyDebug: Boolean, umDebug: Boolean) {
        // SDK预初始化函数不会采集设备信息，也不会向友盟后台上报数据。
        UMConfigure.preInit(context, InitTool.APP_KEY, InitTool.APP_CHANNEL)
        if (ConfigData.isAgreeTerm()) {
            InitTool.init(context, buglyDebug, umDebug)
        }
    }


    /**
     * 友盟初始化(带弹窗)
     * 
     * @param context
     */
    @JvmStatic
    fun initWithDialog(context: MainActivity, buglyDebug: Boolean, umDebug: Boolean) {
        //是否同意用户协议
        if (!ConfigData.isAgreeTerm()) {
            StatementDialog.showStatementDialog(context, object : StatementDialog.IButtonCallback {
                override fun onClick(dialog: MaterialDialog, which: DialogAction) {
                    init(context, buglyDebug, umDebug)
                    //获取存储权限
                    initPermissionsOfStorage(context)
                    UriTool.grantedUriPermission(ConfigData.getCacheFilePath(), context)
                }
            })
        } else {
            //获取存储权限
            initPermissionsOfStorage(context)
        }
    }
}