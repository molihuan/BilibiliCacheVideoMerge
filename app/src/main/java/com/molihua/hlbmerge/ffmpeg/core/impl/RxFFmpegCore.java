package com.molihua.hlbmerge.ffmpeg.core.impl;

import com.molihua.hlbmerge.ffmpeg.core.BaseFFmpegCallback;
import com.molihua.hlbmerge.ffmpeg.core.BaseFFmpegCore;
import com.molihuan.pathselector.utils.Mtools;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import io.microshow.rxffmpeg.RxFFmpegInvoke;

/**
 * @ClassName: RxFFmpegCore
 * @Author: molihuan
 * @Date: 2023/01/20/23:17
 * @Description: (因为核心不同可能会报错 ， 构建时会自动忽略报错的文件 ， 正常编译即可)
 */
public class RxFFmpegCore extends BaseFFmpegCore {

    @Override
    public void setDebug(boolean isDebug) {
        RxFFmpegInvoke.getInstance().setDebug(isDebug);
    }

    @Override
    public BaseFFmpegCallback getFFmpegCallback(MaterialDialog dialog) {
        return new RxFFmpegCallback(dialog);
    }

    @Override
    public int runCommand(String cmdStr, BaseFFmpegCallback ffmpegCallback) {

        String[] cmd = cmdStr.split(" ");
        Mtools.log("核心:" + getClass().getSimpleName() + "\n命令:" + cmdStr);

        //执行命令
        return RxFFmpegInvoke.getInstance()
                .runCommand(cmd, (RxFFmpegInvoke.IFFmpegListener) ffmpegCallback);
    }

    @Override
    public void exitRunCommand() {
        RxFFmpegInvoke.getInstance().exit();
    }


}
