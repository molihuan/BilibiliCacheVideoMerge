package com.molihua.hlbmerge.ffmpeg.core.impl;

import androidx.annotation.Nullable;

import com.coder.ffmpeg.call.IFFmpegCallBack;
import com.molihua.hlbmerge.ffmpeg.core.BaseFFmpegCallback;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

/**
 * @ClassName: FFmpegCommandCallback
 * @Author: molihuan
 * @Date: 2023/01/21/14:35
 * @Description:(因为核心不同可能会报错 ， 构建时会自动忽略报错的文件 ， 正常编译即可)
 */
public class FFmpegCommandCallback extends BaseFFmpegCallback implements IFFmpegCallBack {

    public FFmpegCommandCallback(MaterialDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onComplete() {
        super.onFinish();
    }

    @Override
    public void onError(int i, @Nullable String s) {
        super.onError("errorCode:" + i + "-----" + s);
    }


}
