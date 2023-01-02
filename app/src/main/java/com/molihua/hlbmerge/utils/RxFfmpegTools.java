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

    /**
     * 同步执行命令
     *
     * @param cmdStr
     * @param ffmpegCallback
     * @return
     */
    public static int runCommand(String cmdStr, RxFFmpegCallback ffmpegCallback) {
        String[] cmd = cmdStr.split(" ");

        //LogUtils.e(cmdStr);
        //执行命令
        return RxFFmpegInvoke.getInstance()
                .runCommand(cmd, ffmpegCallback);
    }

    /**
     * 退出命令执行
     */
    public static void exitRunCommand() {
        RxFFmpegInvoke.getInstance().exit();
    }

}
