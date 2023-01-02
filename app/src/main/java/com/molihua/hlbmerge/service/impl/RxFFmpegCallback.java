package com.molihua.hlbmerge.service.impl;

import com.xuexiang.xtask.XTask;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import io.microshow.rxffmpeg.RxFFmpegSubscriber;

/**
 * @ClassName: RxFFmpegCallback
 * @Author: molihuan
 * @Date: 2022/12/24/18:05
 * @Description:
 */
public class RxFFmpegCallback extends RxFFmpegSubscriber {
    private MaterialDialog dialog;
    private int successNum = 0;
    private int failNum = 0;

    public RxFFmpegCallback(MaterialDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void onFinish() {
        dialog.incrementProgress(1);
        successNum++;
    }

    @Override
    public void onProgress(int progress, long progressTime) {
        XTask.postToMain(new Runnable() {
            @Override
            public void run() {
                dialog.setContent("已处理progressTime=" + (double) progressTime / 1000000 + "秒");
            }
        });
    }

    @Override
    public void onCancel() {
        dialog.cancel();
    }

    @Override
    public void onError(String message) {
        dialog.incrementProgress(1);
        failNum++;
    }

    public int getSuccessNum() {
        return successNum;
    }

    public int getFailNum() {
        return failNum;
    }

    public void setSuccessNum(int successNum) {
        this.successNum = successNum;
    }

    public void setFailNum(int failNum) {
        this.failNum = failNum;
    }
}
