package com.molihua.hlbmerge.utils;

import android.content.Context;

import com.molihua.hlbmerge.dao.ConfigData;
import com.molihua.hlbmerge.dialog.impl.StatementDialog;
import com.umeng.commonsdk.UMConfigure;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

/**
 * @ClassName: UMTools
 * @Author: molihuan
 * @Date: 2023/01/03/14:55
 * @Description: 友盟工具类
 */
public class UMTools {

    public final static String APP_KEY = "63b302eed64e6861390b324f";
    public static String APP_CHANNEL = "release_molihuan";
    //debug渠道
    public final static String CHANNEL_DEBUG = "debug_molihuan";
    //release渠道
    public final static String CHANNEL_RELEASE = "release_molihuan";

    /**
     * 是否开启debug
     *
     * @param isDebug
     */
    public static void setDebug(boolean isDebug) {
        UMConfigure.setLogEnabled(isDebug);

    }

    public static void setChannel(String channel) {
        APP_CHANNEL = channel;
    }

    /**
     * 友盟预初始化
     *
     * @param context
     */
    public static void preInit(Context context) {
        // SDK预初始化函数不会采集设备信息，也不会向友盟后台上报数据。
        UMConfigure.preInit(context, APP_KEY, APP_CHANNEL);
    }

    /**
     * 友盟初始化(带弹窗)
     *
     * @param context
     */
    public static void init(Context context) {
        //是否同意用户协议
        if (ConfigData.isAgreeTerm()) {
            //友盟初始化
            //初始化组件化基础库, 所有友盟业务SDK都必须调用此初始化接口。
            UMConfigure.init(context, APP_KEY, APP_CHANNEL, UMConfigure.DEVICE_TYPE_PHONE, "");
        } else {
            StatementDialog.showStatementDialog(context, new StatementDialog.IButtonCallback() {
                @Override
                public void onClick(MaterialDialog dialog, DialogAction which) {
                    UMConfigure.init(context, APP_KEY, APP_CHANNEL, UMConfigure.DEVICE_TYPE_PHONE, "");
                }
            });
        }
    }


}
