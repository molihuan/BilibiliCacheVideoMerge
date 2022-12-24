package com.molihua.hlbmerge.dao;

import com.molihuan.pathselector.utils.MConstants;
import com.tencent.mmkv.MMKV;

/**
 * @ClassName: ConfigData
 * @Author: molihuan
 * @Date: 2022/12/22/13:24
 * @Description:
 */
public class ConfigData {
    //国内
    public final static String TYPE_CACHE_FILE_PATH_INTERNAL = "/tv.danmaku.bili/download";
    //国际
    public final static String TYPE_CACHE_FILE_PATH_ABROAD = "/com.bilibili.app.in/download";
    //平板
    public final static String TYPE_CACHE_FILE_PATH_IPAD = "/tv.danmaku.bilibilihd/download";
    //概念
    public final static String TYPE_CACHE_FILE_PATH_CONCEPT = "/com.bilibili.app.blue/download";
    //完成文件路径
    public final static String TYPE_OUTPUT_FILE_PATH_COMPLETE = "/bilibili缓存合并/complete";
    //临时文件路径
    public final static String TYPE_OUTPUT_FILE_PATH_TEMP = MConstants.DEFAULT_ROOTPATH + "/bilibili缓存合并/temp";

    private final static MMKV kv = MMKV.defaultMMKV();
    //缓存文件路径
    private String cacheFilePath;
    //输出文件路径
    private String outputFilePath;
    //第一次使用
    private boolean firstUse;
    //是否导出弹幕
    private boolean exportDanmaku;
    //导出类型，0有声音视频，1无声音视频，2仅仅音频
    private int exportType;
    //是否同意条款
    private boolean agreeTerms;
    //是否开启弹幕
    private boolean openBarrage;

    public static void init() {
        //第一次
        if (!kv.containsKey("firstUse")) {
            kv.encode("firstUse", false);
            kv.encode("cacheFilePath", MConstants.PATH_ANRROID_DATA + TYPE_CACHE_FILE_PATH_INTERNAL);
            kv.encode("outputFilePath", MConstants.DEFAULT_ROOTPATH + TYPE_OUTPUT_FILE_PATH_COMPLETE);
            kv.encode("exportDanmaku", false);
            kv.encode("exportType", 0);
            kv.encode("agreeTerms", false);
            kv.encode("openBarrage", false);
        }
    }

    public static String getCacheFilePath() {
        return kv.decodeString("cacheFilePath");
    }

    public static void setCacheFilePath(String cacheFilePath) {
        kv.encode("cacheFilePath", cacheFilePath);
    }

    public static String getOutputFilePath() {
        return kv.decodeString("outputFilePath");
    }

    public static void setOutputFilePath(String outputFilePath) {
        kv.encode("outputFilePath", outputFilePath);
    }

    public static boolean isFirstUse() {
        return kv.decodeBool("firstUse");
    }

    public static void setFirstUse(boolean firstUse) {
        kv.encode("firstUse", firstUse);
    }

    public static boolean isExportDanmaku() {
        return kv.decodeBool("exportDanmaku");
    }

    public static void setExportDanmaku(boolean exportDanmaku) {
        kv.encode("exportDanmaku", exportDanmaku);
    }

    public static int getExportType() {
        return kv.decodeInt("exportType");
    }

    public static void setExportType(int exportType) {
        kv.encode("exportType", exportType);
    }

    public static boolean isAgreeTerms() {
        return kv.decodeBool("agreeTerms");
    }

    public static void setAgreeTerms(boolean agreeTerms) {
        kv.encode("agreeTerms", agreeTerms);
    }

    public static boolean isOpenBarrage() {
        return kv.decodeBool("openBarrage");
    }

    public static void setOpenBarrage(boolean openBarrage) {
        kv.encode("openBarrage", openBarrage);
    }
}
