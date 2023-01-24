package com.molihua.hlbmerge.ffmpeg.core.impl;

import com.molihua.hlbmerge.ffmpeg.core.BaseFFmpegCallback;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import io.microshow.rxffmpeg.RxFFmpegInvoke;

/**
 * @ClassName: RxFFmpegCallback
 * @Author: molihuan
 * @Date: 2022/12/24/18:05
 * @Description: ffmpeg回调(因为核心不同可能会报错 ， 构建时会自动忽略报错的文件 ， 正常编译即可)
 */
public class RxFFmpegCallback extends BaseFFmpegCallback implements RxFFmpegInvoke.IFFmpegListener {
    public RxFFmpegCallback(MaterialDialog dialog) {
        this.dialog = dialog;
    }
}
