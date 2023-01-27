package com.molihua.hlbmerge.utils;

import android.content.Context;

import com.blankj.molihuan.utilcode.util.DeviceUtils;
import com.molihua.hlbmerge.activity.AbstractMainActivity;
import com.molihua.hlbmerge.dao.ConfigData;
import com.molihua.hlbmerge.dialog.impl.StatementDialog;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

/**
 * @ClassName: UMTools
 * @Author: molihuan
 * @Date: 2023/01/03/14:55
 * @Description: 友盟工具类
 */
public class InitTool {

    public final static String APP_KEY = "63b302eed64e6861390b324f";
    public final static String BUGLY_APP_KEY = "ac467503ed";

    public static String APP_CHANNEL = "release_molihuan";


    public static void setChannel(String channel) {
        APP_CHANNEL = channel;
    }

    /**
     * 友盟和bugly初始化
     *
     * @param context
     */
    public static void init(Context context, boolean buglyDebug, boolean umDebug) {

        if (!GeneralTools.isApkDebug(context)) {
            buglyDebug = false;
            umDebug = false;
        }
        //友盟初始化
        //初始化组件化基础库, 所有友盟业务SDK都必须调用此初始化接口。
        UMConfigure.setLogEnabled(umDebug);
        UMConfigure.init(context, APP_KEY, APP_CHANNEL, UMConfigure.DEVICE_TYPE_PHONE, "");
        //bugly初始化
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setDeviceID(DeviceUtils.getUniqueDeviceId())
                .setDeviceModel(DeviceUtils.getModel())
                .setAppChannel(APP_CHANNEL)
                .setEnableUserInfo(true);
        CrashReport.initCrashReport(context, BUGLY_APP_KEY, buglyDebug, strategy);
        CrashReport.setUserId(DeviceUtils.getUniqueDeviceId());
    }

    /**
     * 友盟预初始化
     *
     * @param context
     */
    public static void preInit(Context context, boolean buglyDebug, boolean umDebug) {
        // SDK预初始化函数不会采集设备信息，也不会向友盟后台上报数据。
        UMConfigure.preInit(context, APP_KEY, APP_CHANNEL);
        if (ConfigData.isAgreeTerm()) {
            init(context, buglyDebug, umDebug);
        }
    }

    /**
     * 友盟初始化(带弹窗)
     *
     * @param context
     */
    public static void initWithDialog(AbstractMainActivity context, boolean buglyDebug, boolean umDebug) {
        //是否同意用户协议
        if (!ConfigData.isAgreeTerm()) {
            StatementDialog.showStatementDialog(context, new StatementDialog.IButtonCallback() {
                @Override
                public void onClick(MaterialDialog dialog, DialogAction which) {
                    init(context, buglyDebug, umDebug);
                    //获取存储权限
                    GeneralTools.initPermissionsOfStorage(context);
                    UriTool.grantedUriPermission(ConfigData.getCacheFilePath(), context);
                }
            });
        } else {
            //获取存储权限
            GeneralTools.initPermissionsOfStorage(context);
        }
    }


}
