package com.molihua.hlbmerge.ffmpeg.core.impl

import com.coder.ffmpeg.call.IFFmpegCallBack
import com.coder.ffmpeg.jni.FFmpegCommand.cancel
import com.coder.ffmpeg.jni.FFmpegCommand.runCmd
import com.molihua.hlbmerge.ffmpeg.core.BaseFFmpegCallback
import com.molihua.hlbmerge.ffmpeg.core.BaseFFmpegCore
import com.molihuan.pathselector.utils.Mtools
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog

class FFmpegCommandCore : BaseFFmpegCore() {
    override fun setDebug(isDebug: Boolean) {
        setDebug(isDebug)
    }

    override fun getFFmpegCallback(dialog: MaterialDialog): BaseFFmpegCallback {
        return FFmpegCommandCallback(dialog)
    }

    override fun runCommand(cmdStr: String, ffmpegCallback: BaseFFmpegCallback): Int {
        val cmd: Array<String?> =
            cmdStr.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        Mtools.log("核心:" + javaClass.getSimpleName() + "\n命令:" + cmdStr)

        //执行命令
        return runCmd(cmd, ffmpegCallback as IFFmpegCallBack?)!!
    }

    override fun exitRunCommand() {
        cancel()
    }
}