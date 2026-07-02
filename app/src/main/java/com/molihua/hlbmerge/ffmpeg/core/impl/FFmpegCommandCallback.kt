package com.molihua.hlbmerge.ffmpeg.core.impl

import com.coder.ffmpeg.call.IFFmpegCallBack
import com.molihua.hlbmerge.ffmpeg.core.BaseFFmpegCallback
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog

class FFmpegCommandCallback : BaseFFmpegCallback, IFFmpegCallBack {
    constructor(dialog: MaterialDialog?) {
        this.dialog = dialog
    }

    override fun onStart() {
    }

    override fun onComplete() {
        super.onFinish()
    }

    override fun onError(i: Int, s: String?) {
        super.onError("errorCode:" + i + "-----" + s)
    }
}