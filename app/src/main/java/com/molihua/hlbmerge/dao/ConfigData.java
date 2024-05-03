package com.molihua.hlbmerge.dao;

import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.blankj.molihuan.utilcode.util.AppUtils;
import com.blankj.molihuan.utilcode.util.ReflectUtils;
import com.blankj.molihuan.utilcode.util.TimeUtils;
import com.molihua.hlbmerge.BuildConfig;
import com.molihua.hlbmerge.ffmpeg.core.BaseFFmpegCore;
import com.molihua.hlbmerge.utils.UpdataTools;
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
    public final static String TYPE_OUTPUT_FILE_PATH_COMPLETE = "/bilibili视频合并/complete";
    //临时文件路径
    public final static String TYPE_OUTPUT_FILE_PATH_TEMP = MConstants.DEFAULT_ROOTPATH + "/bilibili视频合并/temp";
    //输出压缩文件路径
    public final static String TYPE_OUTPUT_FILE_PATH_ZIP = MConstants.DEFAULT_ROOTPATH + "/bilibili视频合并/zip";
    //ffmpeg核心类型
    public final static int FFMPEG_CORE_TYPE_All = -1;

    public final static int FFMPEG_CORE_TYPE_RXFFMPEG = 0;

    public final static int FFMPEG_CORE_TYPE_FFMPEGCOMMAND = 1;
    //临时配置前缀
    public final static String TEMP_DATA_PERFIX = "TEMP_DATA_PERFIX_";

    private final static MMKV kv = MMKV.defaultMMKV();

    //ffmpeg核心
    public static BaseFFmpegCore ffmpegCore;

    //缓存文件路径
    private String cacheFilePath;
    //输出文件路径
    private String outputFilePath;
    //是否导出弹幕
    private boolean exportDanmaku;
    //导出类型，0有声音视频，1无声音视频，2仅仅音频
    private int exportType;
    //是否同意条款
    private boolean agreeTerm;
    //是否开启弹幕
    private boolean openBarrage;
    //弹幕大小
    private int danmakuSize;
    //弹幕不透明度
    private int danmakuAlpha;
    //弹幕速度
    private int danmakuSpeed;

    //更新日期毫秒时间戳(当前时间戳只有在这个时间戳后才会自动检测更新)
    private long updateMills;
    //自动检测更新频率(0:一天    1:一周    2:一月   3:永不)
    private int updateFrequency;
    //播放视频时是否循环播放
    private boolean videoReply;
    //ffmpeg命令模板
    private String ffmpegCmdTemplate;
    //ffmpeg核心类型
    private int ffmpegCoreType;
    //单一输出目录,不再每个视频都创建目录
    private boolean singleOutputDir;

    private long clearTempDataMills;


    /**
     * 每当需要新增配置就
     * 新增一个判断
     */
    public static void init() {
        //不存在配置版本号为0则
        if (!kv.containsKey("configDataVersion")) {
            kv.encode("configDataVersion", true);
            setCacheFilePathByInstalledBili();
            setOutputFilePath(MConstants.DEFAULT_ROOTPATH + TYPE_OUTPUT_FILE_PATH_COMPLETE);
            setExportDanmaku(false);
            setExportType(0);
            setAgreeTerm(false);
            setOpenBarrage(true);
        }
        //新增配置1
//        if (!kv.containsKey("ffmpegVersion")) {
//            kv.encode("ffmpegVersion", 0);
//        }


        if (!kv.containsKey("danmakuSize")) {
            setDanmakuSize(100);
            setDanmakuAlpha(100);
            setDanmakuSpeed(100);
        }

        if (!kv.containsKey("updateMills")) {
            setUpdateMills(TimeUtils.getNowMills());
            setUpdateFrequency(1);
        }

        if (!kv.containsKey("videoReply")) {
            setVideoReply(false);
        }

        if (!kv.containsKey("ffmpegCmdTemplate")) {
            //-vcodec copy ：视频只拷贝，不编解码
            //-acodec copy : 音频只拷贝，不编解码
            //-c copy      : 只拷贝，不编解码
            setFfmpegCmdTemplate("ffmpeg -i %s -i %s -metadata title='%s' -c copy %s");
        }

        //检查是否为最新的ffmpeg模板
        checkLatestFfmpegTemplate();

        if (!kv.containsKey("ffmpegCoreType")) {
            setFfmpegCoreType(FFMPEG_CORE_TYPE_RXFFMPEG);
        }

        if (!kv.containsKey("singleOutputDir")) {
            setSingleOutputDir(false);
        }

        if (!containsKey("clearTempDataMills")) {
            setClearTempDataMills(TimeUtils.getNowMills() + UpdataTools.TIMESTAMP_WEEK);
        }

        //必须放最后
        autoClearTempData();


    }

    private static void checkLatestFfmpegTemplate() {
        String template = getFfmpegCmdTemplate();
        if (!template.contains("-metadata title='%s'")) {
            setFfmpegCmdTemplate("ffmpeg -i %s -i %s -metadata title='%s' -c copy %s");
        }
    }

    /**
     * 初始化ffmpeg核心
     */
    public static void initFFmpegCore() {

        switch (BuildConfig.FFMPEG_CORE_TYPE) {
            //-1则是全核心,需要从ky中读取设置的核心，其他的则为单核心无法选择
            case FFMPEG_CORE_TYPE_All:
                //ffmpegCore = new RxFFmpegCore();
                switch (getFfmpegCoreType()) {
                    case FFMPEG_CORE_TYPE_FFMPEGCOMMAND:
                        ffmpegCore = ReflectUtils.reflect("com.molihua.hlbmerge.ffmpeg.core.impl.FFmpegCommandCore").newInstance().get();
                        break;
                    case FFMPEG_CORE_TYPE_RXFFMPEG:
                    default:
                        ffmpegCore = ReflectUtils.reflect("com.molihua.hlbmerge.ffmpeg.core.impl.RxFFmpegCore").newInstance().get();
                }
                break;
            case FFMPEG_CORE_TYPE_FFMPEGCOMMAND:
                //ffmpegCore = new FFmpegCommandCore();
                ffmpegCore = ReflectUtils.reflect("com.molihua.hlbmerge.ffmpeg.core.impl.FFmpegCommandCore").newInstance().get();
                break;
            case FFMPEG_CORE_TYPE_RXFFMPEG:
            default:
                //ffmpegCore = new RxFFmpegCore();
                ffmpegCore = ReflectUtils.reflect("com.molihua.hlbmerge.ffmpeg.core.impl.RxFFmpegCore").newInstance().get();
        }
    }

    /**
     * 自动清理临时数据(一周)
     */
    public static void autoClearTempData() {
        long nowMills = TimeUtils.getNowMills();
        long clearMills = ConfigData.getClearTempDataMills();

        if (nowMills > clearMills) {
            clearTempData();
            clearMills = nowMills + UpdataTools.TIMESTAMP_WEEK;
            //设置下一次清理时间戳
            ConfigData.setClearTempDataMills(clearMills);
        }

    }

    public static void clearTempData() {
//        Mtools.log();
        String[] keys = kv.allKeys();
        for (int i = keys.length - 1; i >= 0; i--) {
            if (keys[i].startsWith(TEMP_DATA_PERFIX)) {
                kv.removeValueForKey(keys[i]);
            }
        }
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

    public static boolean containsKey(String key) {
        return kv.containsKey(key);
    }


    /**
     * 获取实例
     *
     * @param key
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T extends Parcelable> T getInstance(String key, Class<T> tClass) {
        return kv.decodeParcelable(key, tClass);
    }

    public static boolean saveInstance(String key, @Nullable Parcelable value) {
        return kv.encode(key, value);
    }

    public static boolean saveInstance(String key, @Nullable Parcelable value, int expireDurationInSecond) {
        return kv.encode(key, value, expireDurationInSecond);
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

    public static boolean isAgreeTerm() {
        return kv.decodeBool("agreeTerm");
    }

    public static boolean setAgreeTerm(boolean agreeTerm) {
        return kv.encode("agreeTerm", agreeTerm);
    }

    public static boolean isOpenBarrage() {
        return kv.decodeBool("openBarrage");
    }

    public static boolean setOpenBarrage(boolean openBarrage) {
        return kv.encode("openBarrage", openBarrage);
    }


    public static int getDanmakuSize() {
        return kv.decodeInt("danmakuSize");
    }

    public static boolean setDanmakuSize(int danmakuSize) {
        return kv.encode("danmakuSize", danmakuSize);
    }

    public static int getDanmakuAlpha() {
        return kv.decodeInt("danmakuAlpha");
    }

    public static boolean setDanmakuAlpha(int danmakuAlpha) {
        return kv.encode("danmakuAlpha", danmakuAlpha);
    }

    public static int getDanmakuSpeed() {
        return kv.decodeInt("danmakuSpeed");
    }

    public static boolean setDanmakuSpeed(int danmakuSpeed) {
        return kv.encode("danmakuSpeed", danmakuSpeed);
    }


    public static long getUpdateMills() {
        return kv.decodeLong("updateMills");
    }

    public static boolean setUpdateMills(long updateMills) {
        return kv.encode("updateMills", updateMills);
    }

    public static long getClearTempDataMills() {
        return kv.decodeLong("clearTempDataMills");
    }

    public static boolean setClearTempDataMills(long clearTempDataMills) {
        return kv.encode("clearTempDataMills", clearTempDataMills);
    }


    public static int getUpdateFrequency() {
        return kv.decodeInt("updateFrequency");
    }

    public static boolean setUpdateFrequency(int updateFrequency) {
        return kv.encode("updateFrequency", updateFrequency);
    }

    public static boolean isVideoReply() {
        return kv.decodeBool("videoReply");
    }

    public static boolean setVideoReply(boolean videoReply) {
        return kv.encode("videoReply", videoReply);
    }

    public static String getFfmpegCmdTemplate() {
        return kv.decodeString("ffmpegCmdTemplate");
    }

    public static boolean setFfmpegCmdTemplate(String ffmpegCmdTemplate) {
        return kv.encode("ffmpegCmdTemplate", ffmpegCmdTemplate);
    }

    public static int getFfmpegCoreType() {
        return kv.decodeInt("ffmpegCoreType");
    }

    public static boolean setFfmpegCoreType(int ffmpegCoreType) {
        return kv.encode("ffmpegCoreType", ffmpegCoreType);
    }

    public static boolean isSingleOutputDir() {
        return kv.decodeBool("singleOutputDir");
    }

    public static boolean setSingleOutputDir(boolean singleOutputDir) {
        return kv.encode("singleOutputDir", singleOutputDir);
    }
}
