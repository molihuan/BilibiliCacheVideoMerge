package com.molihua.hlbmerge.dao;

import com.blankj.molihuan.utilcode.util.AppUtils;
import com.molihuan.pathselector.utils.MConstants;
import com.tencent.mmkv.MMKV;

import java.io.File;

/**
 * @ClassName: ConfigData
 * @Author: molihuan
 * @Date: 2022/12/22/13:24
 * @Description:
 */
public class ConfigData {
    //国内包名
    public final static String PKGNAME_BILIBILI_INTERNAL = "tv.danmaku.bili";
    //国际包名
    public final static String PKGNAME_BILIBILI_ABROAD = "com.bilibili.app.in";
    //平板包名
    public final static String PKGNAME_BILIBILI_IPAD = "tv.danmaku.bilibilihd";
    //概念包名
    public final static String PKGNAME_BILIBILI_CONCEPT = "com.bilibili.app.blue";

    //国内默认缓存下载路径
    public final static String TYPE_CACHE_FILE_PATH_INTERNAL = File.separator + PKGNAME_BILIBILI_INTERNAL + File.separator + "download";
    //国际默认缓存下载路径
    public final static String TYPE_CACHE_FILE_PATH_ABROAD = File.separator + PKGNAME_BILIBILI_ABROAD + File.separator + "download";
    //平板默认缓存下载路径
    public final static String TYPE_CACHE_FILE_PATH_IPAD = File.separator + PKGNAME_BILIBILI_IPAD + File.separator + "download";
    //概念默认缓存下载路径
    public final static String TYPE_CACHE_FILE_PATH_CONCEPT = File.separator + PKGNAME_BILIBILI_CONCEPT + File.separator + "download";

    //完成文件路径
    public final static String TYPE_OUTPUT_FILE_PATH_COMPLETE = "/bilibili缓存合并/complete";
    //临时文件路径
    public final static String TYPE_OUTPUT_FILE_PATH_TEMP = MConstants.DEFAULT_ROOTPATH + "/bilibili缓存合并/temp";

    private final static MMKV kv = MMKV.defaultMMKV();

    //缓存文件路径
    private String cacheFilePath;
    //输出文件路径
    private String outputFilePath;
    //是否导出弹幕
    private boolean exportDanmaku;
    //导出类型，0有声音视频，1无声音视频，2仅仅音频
    private int exportType;
    //是否同意条款
    private boolean agreeTerms;
    //是否开启弹幕
    private boolean openBarrage;

    /**
     * 每当需要新增配置就
     * 新增一个判断
     */
    public static void init() {
        //不存在配置版本号为0则
        if (!kv.containsKey("configDataVersion0")) {
            kv.encode("configDataVersion0", true);
            setCacheFilePathByInstalledBili();
            setOutputFilePath(MConstants.DEFAULT_ROOTPATH + TYPE_OUTPUT_FILE_PATH_COMPLETE);
            setExportDanmaku(false);
            setExportType(0);
            setAgreeTerms(false);
            setOpenBarrage(false);
        }
        //新增配置1
//        if (!kv.containsKey("configDataVersion1")) {
//            kv.encode("configDataVersion1", true);
//        }

    }

    /**
     * 根据安装的bilibili版本设置对应的缓存路径
     */
    public static void setCacheFilePathByInstalledBili() {
        String cacheFilePath;

        if (AppUtils.isAppInstalled(PKGNAME_BILIBILI_INTERNAL)) {
            cacheFilePath = MConstants.PATH_ANRROID_DATA + TYPE_CACHE_FILE_PATH_INTERNAL;
        } else if (AppUtils.isAppInstalled(PKGNAME_BILIBILI_ABROAD)) {
            cacheFilePath = MConstants.PATH_ANRROID_DATA + TYPE_CACHE_FILE_PATH_ABROAD;
        } else if (AppUtils.isAppInstalled(PKGNAME_BILIBILI_IPAD)) {
            cacheFilePath = MConstants.PATH_ANRROID_DATA + TYPE_CACHE_FILE_PATH_IPAD;
        } else if (AppUtils.isAppInstalled(PKGNAME_BILIBILI_CONCEPT)) {
            cacheFilePath = MConstants.PATH_ANRROID_DATA + TYPE_CACHE_FILE_PATH_CONCEPT;
        } else {
            cacheFilePath = MConstants.PATH_ANRROID_DATA + TYPE_CACHE_FILE_PATH_INTERNAL;
        }

        kv.encode("cacheFilePath", cacheFilePath);

    }


    public static String getCacheFilePath() {
        return kv.decodeString("cacheFilePath");
    }

    public static boolean setCacheFilePath(String cacheFilePath) {
        return kv.encode("cacheFilePath", cacheFilePath);
    }

    public static String getOutputFilePath() {
        return kv.decodeString("outputFilePath");
    }

    public static boolean setOutputFilePath(String outputFilePath) {
        return kv.encode("outputFilePath", outputFilePath);
    }

    public static boolean isExportDanmaku() {
        return kv.decodeBool("exportDanmaku");
    }

    public static boolean setExportDanmaku(boolean exportDanmaku) {
        return kv.encode("exportDanmaku", exportDanmaku);
    }

    public static int getExportType() {
        return kv.decodeInt("exportType");
    }

    public static boolean setExportType(int exportType) {
        return kv.encode("exportType", exportType);
    }

    public static boolean isAgreeTerms() {
        return kv.decodeBool("agreeTerms");
    }

    public static boolean setAgreeTerms(boolean agreeTerms) {
        return kv.encode("agreeTerms", agreeTerms);
    }

    public static boolean isOpenBarrage() {
        return kv.decodeBool("openBarrage");
    }

    public static boolean setOpenBarrage(boolean openBarrage) {
        return kv.encode("openBarrage", openBarrage);
    }
}
