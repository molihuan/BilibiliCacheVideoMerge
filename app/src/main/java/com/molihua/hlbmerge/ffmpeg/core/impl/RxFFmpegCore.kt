package com.molihua.hlbmerge.ffmpeg.core.impl

import com.molihua.hlbmerge.ffmpeg.core.BaseFFmpegCallback
import com.molihua.hlbmerge.ffmpeg.core.BaseFFmpegCore
import com.molihuan.pathselector.utils.Mtools
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import io.microshow.rxffmpeg.RxFFmpegInvoke
import io.microshow.rxffmpeg.RxFFmpegInvoke.IFFmpegListener

class RxFFmpegCore : BaseFFmpegCore() {
    override fun setDebug(isDebug: Boolean) {
        RxFFmpegInvoke.getInstance().setDebug(isDebug)
    }

    override fun getFFmpegCallback(dialog: MaterialDialog): BaseFFmpegCallback {
        return RxFFmpegCallback(dialog)
    }

    override fun runCommand(cmdStr: String, ffmpegCallback: BaseFFmpegCallback): Int {
        val cmd: Array<String?> =
            cmdStr.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        Mtools.log("核心:" + javaClass.getSimpleName() + "\n命令:" + cmdStr)

        //        XTask.postToMain(new Runnable() {
//            @Override
//            public void run() {
//                Mtools.toast(cmdStr, Toast.LENGTH_LONG);
//            }
//        });

        //执行命令
        return RxFFmpegInvoke.getInstance()
            .runCommand(cmd, ffmpegCallback as IFFmpegListener?)
    }

    override fun exitRunCommand() {
        RxFFmpegInvoke.getInstance().exit()
    }
}