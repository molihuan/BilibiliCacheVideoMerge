package com.molihua.hlbmerge.ffmpeg.core;

import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

/**
 * @ClassName: FfmpegCoreConfig
 * @Author: molihuan
 * @Date: 2023/01/20/23:15
 * @Description: ffmpeg核心接口
 */
public abstract class BaseFFmpegCore {
    public abstract void setDebug(boolean isDebug);

    public abstract BaseFFmpegCallback getFFmpegCallback(MaterialDialog dialog);

    /**
     * 同步执行命令
     *
     * @param cmdStr
     * @param ffmpegCallback
     * @return
     */
    public abstract int runCommand(String cmdStr, BaseFFmpegCallback ffmpegCallback);

    /**
     * 退出命令执行
     */
    public abstract void exitRunCommand();
}
