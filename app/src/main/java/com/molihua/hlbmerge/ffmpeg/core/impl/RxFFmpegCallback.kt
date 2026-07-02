package com.molihua.hlbmerge.ffmpeg.core.impl

import com.molihua.hlbmerge.ffmpeg.core.BaseFFmpegCallback
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import io.microshow.rxffmpeg.RxFFmpegInvoke.IFFmpegListener

class RxFFmpegCallback : BaseFFmpegCallback, IFFmpegListener {
    constructor(dialog: MaterialDialog?) {
        this.dialog = dialog
    }
}