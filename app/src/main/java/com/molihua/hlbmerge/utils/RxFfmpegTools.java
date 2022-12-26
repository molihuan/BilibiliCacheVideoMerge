package com.molihua.hlbmerge.utils;

import com.molihua.hlbmerge.service.impl.RxFFmpegCallback;

import io.microshow.rxffmpeg.RxFFmpegInvoke;

/**
 * @ClassName: RxFfmpegTools
 * @Author: molihuan
 * @Date: 2022/12/24/13:44
 * @Description:
 */
public class RxFfmpegTools {


    public static int runCommand(String cmdStr, RxFFmpegCallback ffmpegCallback) {
        String[] cmd = cmdStr.split(" ");

        //LogUtils.e(cmdStr);
        //执行命令
        return RxFFmpegInvoke.getInstance()
                .runCommand(cmd, ffmpegCallback);
    }

}
