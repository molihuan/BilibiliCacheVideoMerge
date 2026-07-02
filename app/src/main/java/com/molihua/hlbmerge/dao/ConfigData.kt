package com.molihua.hlbmerge.dao

import android.os.Parcelable
import com.blankj.molihuan.utilcode.util.AppUtils
import com.blankj.molihuan.utilcode.util.ReflectUtils
import com.blankj.molihuan.utilcode.util.TimeUtils
import com.molihua.hlbmerge.BuildConfig
import com.molihua.hlbmerge.ffmpeg.core.BaseFFmpegCore
import com.molihua.hlbmerge.utils.UpdateTools
import com.molihuan.pathselector.utils.MConstants
import com.tencent.mmkv.MMKV
import java.io.File

object ConfigData {
    //国内包名
    const val PKGNAME_BILIBILI_INTERNAL: String = "tv.danmaku.bili"

    //国际包名
    const val PKGNAME_BILIBILI_ABROAD: String = "com.bilibili.app.in"

    //平板包名
    const val PKGNAME_BILIBILI_IPAD: String = "tv.danmaku.bilibilihd"

    //概念包名
    const val PKGNAME_BILIBILI_CONCEPT: String = "com.bilibili.app.blue"

    //国内默认缓存下载路径
    val TYPE_CACHE_FILE_PATH_INTERNAL: String =
        File.separator + PKGNAME_BILIBILI_INTERNAL + File.separator + "download"

    //国际默认缓存下载路径
    val TYPE_CACHE_FILE_PATH_ABROAD: String =
        File.separator + PKGNAME_BILIBILI_ABROAD + File.separator + "download"

    //平板默认缓存下载路径
    val TYPE_CACHE_FILE_PATH_IPAD: String =
        File.separator + PKGNAME_BILIBILI_IPAD + File.separator + "download"

    //概念默认缓存下载路径
    val TYPE_CACHE_FILE_PATH_CONCEPT: String =
        File.separator + PKGNAME_BILIBILI_CONCEPT + File.separator + "download"

    //完成文件路径
    const val TYPE_OUTPUT_FILE_PATH_COMPLETE: String = "/bilibili视频合并/complete"

    //临时文件路径
    @JvmField
    val TYPE_OUTPUT_FILE_PATH_TEMP: String = MConstants.DEFAULT_ROOTPATH + "/bilibili视频合并/temp"

    //输出压缩文件路径
    @JvmField
    val TYPE_OUTPUT_FILE_PATH_ZIP: String = MConstants.DEFAULT_ROOTPATH + "/bilibili视频合并/zip"

    //ffmpeg核心类型
    val FFMPEG_CORE_TYPE_All: Int = -1

    const val FFMPEG_CORE_TYPE_RXFFMPEG: Int = 0

    const val FFMPEG_CORE_TYPE_FFMPEGCOMMAND: Int = 1

    //临时配置前缀
    const val TEMP_DATA_PERFIX: String = "TEMP_DATA_PERFIX_"

    private val kv: MMKV = MMKV.defaultMMKV()

    //ffmpeg核心
    @JvmField
    var ffmpegCore: BaseFFmpegCore? = null


    /**
     * 每当需要新增配置就
     * 新增一个判断
     */
    @JvmStatic
    fun init() {
        //不存在配置版本号为0则
        if (!kv.containsKey("configDataVersion")) {
            kv.encode("configDataVersion", true)
            setCacheFilePathByInstalledBili()
            setOutputFilePath(MConstants.DEFAULT_ROOTPATH + TYPE_OUTPUT_FILE_PATH_COMPLETE)
            setExportDanmaku(false)
            setExportType(0)
            setAgreeTerm(false)
            setOpenBarrage(true)
        }


        //新增配置1
//        if (!kv.containsKey("ffmpegVersion")) {
//            kv.encode("ffmpegVersion", 0);
//        }
        if (!kv.containsKey("danmakuSize")) {
            setDanmakuSize(100)
            setDanmakuAlpha(100)
            setDanmakuSpeed(100)
        }

        if (!kv.containsKey("updateMills")) {
            setUpdateMills(TimeUtils.getNowMills())
            setUpdateFrequency(1)
        }

        if (!kv.containsKey("videoReply")) {
            setVideoReply(false)
        }

        if (!kv.containsKey("ffmpegCmdTemplate")) {
            //-vcodec copy ：视频只拷贝，不编解码
            //-acodec copy : 音频只拷贝，不编解码
            //-c copy      : 只拷贝，不编解码
            setFfmpegCmdTemplate("ffmpeg -i %s -i %s -metadata title='%s' -c copy %s")
        }

        //检查是否为最新的ffmpeg模板
        ConfigData.checkLatestFfmpegTemplate()

        if (!kv.containsKey("ffmpegCoreType")) {
            setFfmpegCoreType(FFMPEG_CORE_TYPE_RXFFMPEG)
        }

        if (!kv.containsKey("singleOutputDir")) {
            setSingleOutputDir(false)
        }

        if (!containsKey("clearTempDataMills")) {
            setClearTempDataMills(TimeUtils.getNowMills() + UpdateTools.TIMESTAMP_WEEK)
        }

        //必须放最后
        autoClearTempData()
    }


    private fun checkLatestFfmpegTemplate() {
        val template = getFfmpegCmdTemplate()
        if (!template.contains("-metadata title='%s'")) {
            setFfmpegCmdTemplate("ffmpeg -i %s -i %s -metadata title='%s' -c copy %s")
        }
    }

    /**
     * 初始化ffmpeg核心
     */
    @JvmStatic
    fun initFFmpegCore() {
        when (BuildConfig.FFMPEG_CORE_TYPE) {
            FFMPEG_CORE_TYPE_All ->                 //ffmpegCore = new RxFFmpegCore();
                when (getFfmpegCoreType()) {
                    FFMPEG_CORE_TYPE_FFMPEGCOMMAND -> ConfigData.ffmpegCore =
                        ReflectUtils.reflect("com.molihua.hlbmerge.ffmpeg.core.impl.FFmpegCommandCore")
                            .newInstance().get()

                    FFMPEG_CORE_TYPE_RXFFMPEG -> ConfigData.ffmpegCore =
                        ReflectUtils.reflect("com.molihua.hlbmerge.ffmpeg.core.impl.RxFFmpegCore")
                            .newInstance().get()

                    else -> ffmpegCore =
                        ReflectUtils.reflect("com.molihua.hlbmerge.ffmpeg.core.impl.RxFFmpegCore")
                            .newInstance().get()
                }

            FFMPEG_CORE_TYPE_FFMPEGCOMMAND ->                 //ffmpegCore = new FFmpegCommandCore();
                ffmpegCore =
                    ReflectUtils.reflect("com.molihua.hlbmerge.ffmpeg.core.impl.FFmpegCommandCore")
                        .newInstance().get()

            FFMPEG_CORE_TYPE_RXFFMPEG ->                 //ffmpegCore = new RxFFmpegCore();
                ffmpegCore =
                    ReflectUtils.reflect("com.molihua.hlbmerge.ffmpeg.core.impl.RxFFmpegCore")
                        .newInstance().get()

            else ->
                ffmpegCore =
                    ReflectUtils.reflect("com.molihua.hlbmerge.ffmpeg.core.impl.RxFFmpegCore")
                        .newInstance().get()
        }
    }

    /**
     * 自动清理临时数据(一周)
     */
    @JvmStatic
    fun autoClearTempData() {
        val nowMills = TimeUtils.getNowMills()
        var clearMills = getClearTempDataMills()

        if (nowMills > clearMills) {
            clearTempData()
            clearMills = nowMills + UpdateTools.TIMESTAMP_WEEK
            //设置下一次清理时间戳
            setClearTempDataMills(clearMills)
        }
    }

    @JvmStatic
    fun clearTempData() {
//        Mtools.log();
        val keys = kv.allKeys()
        for (i in keys!!.indices.reversed()) {
            if (keys[i]!!.startsWith(ConfigData.TEMP_DATA_PERFIX)) {
                kv.removeValueForKey(keys[i])
            }
        }
    }

    /**
     * 根据安装的bilibili版本设置对应的缓存路径
     */
    @JvmStatic
    fun setCacheFilePathByInstalledBili() {
        val cacheFilePath: String?

        if (AppUtils.isAppInstalled(ConfigData.PKGNAME_BILIBILI_INTERNAL)) {
            cacheFilePath = MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_INTERNAL
        } else if (AppUtils.isAppInstalled(ConfigData.PKGNAME_BILIBILI_ABROAD)) {
            cacheFilePath = MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_ABROAD
        } else if (AppUtils.isAppInstalled(ConfigData.PKGNAME_BILIBILI_IPAD)) {
            cacheFilePath = MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_IPAD
        } else if (AppUtils.isAppInstalled(ConfigData.PKGNAME_BILIBILI_CONCEPT)) {
            cacheFilePath = MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_CONCEPT
        } else {
            cacheFilePath = MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_INTERNAL
        }

        kv.encode("cacheFilePath", cacheFilePath)
    }

    @JvmStatic
    fun containsKey(key: String?): Boolean {
        return kv.containsKey(key)
    }


    /**
     * 获取实例
     * 
     * @param key
     * @param tClass
     * @param <T>
     * @return
    </T> */
    @JvmStatic
    fun <T : Parcelable?> getInstance(key: String?, tClass: Class<T>): T? {
        return kv.decodeParcelable<T?>(key, tClass)
    }

    @JvmStatic
    fun saveInstance(key: String?, value: Parcelable?): Boolean {
        return kv.encode(key, value)
    }

    @JvmStatic
    fun saveInstance(key: String?, value: Parcelable?, expireDurationInSecond: Int): Boolean {
        return kv.encode(key, value, expireDurationInSecond)
    }


    @JvmStatic
    fun getCacheFilePath(): String? {
        return kv.decodeString("cacheFilePath")
    }

    @JvmStatic
    fun setCacheFilePath(cacheFilePath: String?): Boolean {
        return kv.encode("cacheFilePath", cacheFilePath)
    }

    @JvmStatic
    fun getOutputFilePath(): String? {
        return kv.decodeString("outputFilePath")
    }

    @JvmStatic
    fun setOutputFilePath(outputFilePath: String?): Boolean {
        return kv.encode("outputFilePath", outputFilePath)
    }

    @JvmStatic
    fun isExportDanmaku(): Boolean {
        return kv.decodeBool("exportDanmaku")
    }

    @JvmStatic
    fun setExportDanmaku(exportDanmaku: Boolean): Boolean {
        return kv.encode("exportDanmaku", exportDanmaku)
    }

    @JvmStatic
    fun getExportType(): Int {
        return kv.decodeInt("exportType")
    }

    @JvmStatic
    fun setExportType(exportType: Int): Boolean {
        return kv.encode("exportType", exportType)
    }

    @JvmStatic
    fun isAgreeTerm(): Boolean {
        return kv.decodeBool("agreeTerm")
    }

    @JvmStatic
    fun setAgreeTerm(agreeTerm: Boolean): Boolean {
        return kv.encode("agreeTerm", agreeTerm)
    }

    @JvmStatic
    fun isOpenBarrage(): Boolean {
        return kv.decodeBool("openBarrage")
    }

    @JvmStatic
    fun setOpenBarrage(openBarrage: Boolean): Boolean {
        return kv.encode("openBarrage", openBarrage)
    }


    @JvmStatic
    fun getDanmakuSize(): Int {
        return kv.decodeInt("danmakuSize")
    }

    @JvmStatic
    fun setDanmakuSize(danmakuSize: Int): Boolean {
        return kv.encode("danmakuSize", danmakuSize)
    }

    @JvmStatic
    fun getDanmakuAlpha(): Int {
        return kv.decodeInt("danmakuAlpha")
    }

    @JvmStatic
    fun setDanmakuAlpha(danmakuAlpha: Int): Boolean {
        return kv.encode("danmakuAlpha", danmakuAlpha)
    }

    @JvmStatic
    fun getDanmakuSpeed(): Int {
        return kv.decodeInt("danmakuSpeed")
    }

    @JvmStatic
    fun setDanmakuSpeed(danmakuSpeed: Int): Boolean {
        return kv.encode("danmakuSpeed", danmakuSpeed)
    }


    @JvmStatic
    fun getUpdateMills(): Long {
        return kv.decodeLong("updateMills")
    }

    @JvmStatic
    fun setUpdateMills(updateMills: Long): Boolean {
        return kv.encode("updateMills", updateMills)
    }

    @JvmStatic
    fun getClearTempDataMills(): Long {
        return kv.decodeLong("clearTempDataMills")
    }

    @JvmStatic
    fun setClearTempDataMills(clearTempDataMills: Long): Boolean {
        return kv.encode("clearTempDataMills", clearTempDataMills)
    }


    @JvmStatic
    fun getUpdateFrequency(): Int {
        return kv.decodeInt("updateFrequency")
    }

    @JvmStatic
    fun setUpdateFrequency(updateFrequency: Int): Boolean {
        return kv.encode("updateFrequency", updateFrequency)
    }

    @JvmStatic
    fun isVideoReply(): Boolean {
        return kv.decodeBool("videoReply")
    }

    @JvmStatic
    fun setVideoReply(videoReply: Boolean): Boolean {
        return kv.encode("videoReply", videoReply)
    }

    @JvmStatic
    fun getFfmpegCmdTemplate(): String {
        return kv.decodeString("ffmpegCmdTemplate")!!
    }

    @JvmStatic
    fun setFfmpegCmdTemplate(ffmpegCmdTemplate: String?): Boolean {
        return kv.encode("ffmpegCmdTemplate", ffmpegCmdTemplate)
    }

    @JvmStatic
    fun getFfmpegCoreType(): Int {
        return kv.decodeInt("ffmpegCoreType")
    }

    @JvmStatic
    fun setFfmpegCoreType(ffmpegCoreType: Int): Boolean {
        return kv.encode("ffmpegCoreType", ffmpegCoreType)
    }

    @JvmStatic
    fun isSingleOutputDir(): Boolean {
        return kv.decodeBool("singleOutputDir")
    }

    @JvmStatic
    fun setSingleOutputDir(singleOutputDir: Boolean): Boolean {
        return kv.encode("singleOutputDir", singleOutputDir)
    }
}