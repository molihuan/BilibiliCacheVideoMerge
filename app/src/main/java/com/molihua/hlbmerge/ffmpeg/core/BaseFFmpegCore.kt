package com.molihua.hlbmerge.ffmpeg.core;

import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

/**
 * @ClassName: FfmpegCoreConfig
 * @Author: molihuan
 * @Date: 2023/01/20/23:15
 * @Description: ffmpeg核心接口
 */
abstract class BaseFFmpegCore {
    abstract fun setDebug(isDebug: Boolean)

    abstract fun getFFmpegCallback(dialog: MaterialDialog): BaseFFmpegCallback

    /**
     * 同步执行命令
     *
     * @param cmdStr
     * @param ffmpegCallback
     * @return
     */
    abstract fun runCommand(cmdStr: String, ffmpegCallback: BaseFFmpegCallback): Int

    /**
     * 退出命令执行
     */
    abstract fun exitRunCommand()
}
