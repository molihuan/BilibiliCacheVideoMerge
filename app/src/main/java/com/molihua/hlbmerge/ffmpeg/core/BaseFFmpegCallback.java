package com.molihua.hlbmerge.ffmpeg.core;

import com.blankj.molihuan.utilcode.util.FileUtils;
import com.molihua.hlbmerge.dao.ConfigData;
import com.xuexiang.xtask.XTask;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

/**
 * @ClassName: BaseFFmpegCallback
 * @Author: molihuan
 * @Date: 2023/01/21/13:59
 * @Description:
 */
public class BaseFFmpegCallback {
    protected MaterialDialog dialog;
    protected int successNum = 0;
    protected int failNum = 0;

    //回调事件模板
    public void onFinish() {
        dialog.incrementProgress(1);
        successNum++;
        FileUtils.deleteAllInDir(ConfigData.TYPE_OUTPUT_FILE_PATH_TEMP);
    }

    public void onProgress(int progress, long progressTime) {
        XTask.postToMain(new Runnable() {
            @Override
            public void run() {
                dialog.setContent("已处理progressTime=" + (double) progressTime / 1000000 + "秒");
            }
        });
    }

    public void onCancel() {
        dialog.cancel();
    }

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
