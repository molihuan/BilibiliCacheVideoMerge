package com.molihua.hlbmerge.utils;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.activities.SettingsActivity;
import com.molihua.hlbmerge.dialogs.StatementDialog;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

/*
        kv.encode("bool", true);
        boolean bValue = kv.decodeBool("bool");
        kv.encode("int", Integer.MIN_VALUE);
        int iValue = kv.decodeInt("int");
        kv.encode("string", "Hello from mmkv");
        String str = kv.decodeString("string");
*/


public class MLHInitConfig {
    private static MMKV  kv = MMKV.defaultMMKV();
    private static int ExportType;
    private static boolean isExportXml;
    private static boolean openBarrage;

    private static String BiliDownPath;

    public static final int TYPE_EXPORT_VIDEO_AUDIO=0;
    public static final int TYPE_EXPORT_VIDEO=1;
    public static final int TYPE_EXPORT_AUDIO=2;




    public static void initConfig(Context context){
        if (!kv.decodeBool("isSecond")) {
            //初始化配置写在这里
            kv.encode("isExportXml",false);//是否导出弹幕
            kv.encode("ExportType",TYPE_EXPORT_VIDEO_AUDIO);//导出类型，0有声音视频，1无声音视频，2仅仅音频

            kv.encode("isAgreeTerms",false);//是否同意条款
            kv.encode("openBarrage",true);//是否开启弹幕
            kv.encode("userCustomPath","");//用户自选缓存路径
            if (VersionTools.isAndroid11()){
                kv.encode("BiliDownPath",PathTools.getOutputTempPath() + "/tv.danmaku.bili/download");//数据路径
            }else {
                kv.encode("BiliDownPath",PathTools.getADPath() + "/tv.danmaku.bili/download");//数据路径
            }

            kv.encode("userCustomCompletePath",PathTools.getOutputPath());//用户自选完成路径

            kv.encode("isSecond",true);//是第二次不是第一次
            LogUtils.e("初始化");
        }
        //后加的属性也要配置更好的用户体验
        if (!kv.containsKey("BiliDownPath")){
            kv.encode("BiliDownPath",PathTools.getADPath() + "/tv.danmaku.bili/download");//数据路径

            new MaterialDialog.Builder(context)
                    .iconRes(R.drawable.xui_ic_default_tip_btn)
                    .title("提示")
                    .content("因为重新适配了bilibili版本,所以如果无法显示的话则需要进入软件设置重新选择版本(国内or国外)")
                    .positiveText("朕去改")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (VersionTools.isAndroid11()){
                                kv.encode("BiliDownPath",PathTools.getOutputTempPath() + "/tv.danmaku.bili/download");//数据路径
                            }else {
                                kv.encode("BiliDownPath",PathTools.getADPath() + "/tv.danmaku.bili/download");//数据路径
                            }

                            Intent intent = new Intent(context, SettingsActivity.class);
                            context.startActivity(intent);
                        }
                    })
                    .negativeText("朕已阅")
                    .show();
        }

        if (!kv.containsKey("userCustomPath")){
            kv.encode("userCustomPath","");//用户自选缓存路径
        }

        if (!kv.containsKey("userCustomCompletePath")){
            kv.encode("userCustomCompletePath",PathTools.getOutputPath());//用户自选合并路径
        }


        if (!kv.decodeBool("isAgreeTerms")){//不同意条款
            StatementDialog.showStatementDialog(context);//声明弹窗
        }

    }

    public static MMKV getKv() {
        return kv;
    }



    public static String getUserCustomCompletePath() {
        return kv.decodeString("userCustomCompletePath");
    }
    public static String getUserCustomPath() {
        return kv.decodeString("userCustomPath");
    }
    public static void setUserCustomCompletePath(String path) {
        kv.encode("userCustomCompletePath",path);//用户自选完成路径
    }
    public static void setUserCustomPath(String userCustomPath) {
        kv.encode("userCustomPath",userCustomPath);//用户自选缓存路径
    }
    public static boolean isNullUserCustomPath() {
        return StringUtils.isEmpty(getUserCustomPath());
    }


    public static String getBiliDownPath() {
        return kv.decodeString("BiliDownPath");
    }



    public static void setBiliDownPath(String biliDownPath) {
        kv.encode("BiliDownPath",biliDownPath);//数据路径
    }

    public static boolean isOpenBarrage() {
        return kv.decodeBool("openBarrage",false);//是否导出弹幕
    }

    public static void setOpenBarrage(boolean openBarrage) {
        kv.encode("openBarrage",openBarrage);//是否开启弹幕
    }



    public static int getExportType() {
        return kv.decodeInt("ExportType",0);//导出类型，0有声音视频，1无声音视频，2仅仅音频
    }

    public static void setExportType(int exportType) {
        ExportType = exportType;
        kv.encode("ExportType",exportType);//导出类型，0有声音视频，1无声音视频，2仅仅音频
    }

    public static boolean isIsExportXml() {
        return kv.decodeBool("isExportXml",false);//是否导出弹幕
    }

    public static void setIsExportXml(boolean isExportXml) {
        MLHInitConfig.isExportXml = isExportXml;
        kv.encode("isExportXml",isExportXml);//是否导出弹幕
    }
}
