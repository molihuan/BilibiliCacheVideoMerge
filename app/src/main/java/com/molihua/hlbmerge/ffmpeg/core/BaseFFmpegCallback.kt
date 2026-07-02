package com.molihua.hlbmerge.ffmpeg.core

import com.blankj.molihuan.utilcode.util.FileUtils
import com.molihua.hlbmerge.dao.ConfigData
import com.xuexiang.xtask.XTask
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog

open class BaseFFmpegCallback {
    @JvmField
    protected var dialog: MaterialDialog? = null
    var successNum: Int = 0
    var failNum: Int = 0

    //回调事件模板
    fun onFinish() {
        dialog!!.incrementProgress(1)
        successNum++
        FileUtils.deleteAllInDir(ConfigData.TYPE_OUTPUT_FILE_PATH_TEMP)
    }

    fun onProgress(progress: Int, progressTime: Long) {
        XTask.postToMain(object : Runnable {
            override fun run() {
                dialog!!.setContent("进度:" + progress + "\n" + progressTime.toDouble() / 1000000)
            }
        })
    }

    fun onCancel() {
        dialog!!.cancel()
    }

    fun onError(message: String?) {
        dialog!!.incrementProgress(1)
        failNum++
    }

}