package com.molihua.hlbmerge.dialog.impl;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;

import com.blankj.molihuan.utilcode.util.FileUtils;
import com.molihua.hlbmerge.dao.ConfigData;
import com.molihua.hlbmerge.entity.CacheFile;
import com.molihua.hlbmerge.service.BaseCacheFileManager;
import com.molihua.hlbmerge.service.impl.RxFFmpegCallback;
import com.xuexiang.xtask.XTask;
import com.xuexiang.xtask.core.ITaskChainEngine;
import com.xuexiang.xtask.core.param.ITaskResult;
import com.xuexiang.xtask.core.step.impl.TaskChainCallbackAdapter;
import com.xuexiang.xtask.core.step.impl.TaskCommand;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.GravityEnum;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.io.File;
import java.util.List;

import io.microshow.rxffmpeg.RxFFmpegInvoke;

/**
 * @ClassName: MergeProgressDialog
 * @Author: molihuan
 * @Date: 2022/12/24/14:01
 * @Description: 合并进度弹窗
 */
public class MergeProgressDialog {

    //用户是否选择的标志位
    public static boolean FLAG_USER_HANDLE = false;
    public static String cmdTemplate = "ffmpeg -i %s -i %s -c copy %s.mp4";


    public static MaterialDialog showMergeProgressDialog(List<CacheFile> cacheFileList, Context context) {

        //把合集item处理成章节item
        List<CacheFile> handledCacheFileList = BaseCacheFileManager.collection2ChapterCacheFileList(cacheFileList);

        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title("提示")
                .content("正在用吃奶的力气合并中...")
                .contentGravity(GravityEnum.CENTER)
                .progress(false, handledCacheFileList.size(), true)
                .cancelable(false)
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        MergeProgressDialog.startMerge(handledCacheFileList, (MaterialDialog) dialog);
                    }
                })
                .show();

        return materialDialog;
    }


    /**
     * 开始进行合并
     *
     * @param cacheFileList
     * @param dialog
     */
    public static void startMerge(List<CacheFile> cacheFileList, MaterialDialog dialog) {
        RxFFmpegCallback ffmpegCallback = new RxFFmpegCallback(dialog);
        //获取输出根目录
        String outRoot = ConfigData.getOutputFilePath();

        XTask.getTaskChain()
                .addTask(XTask.getTask(new TaskCommand() {
                    @Override
                    public void run() throws Exception {

                        String[] cmd;
                        String cmdStr;
                        CacheFile cacheFile;
                        String subOutPath;
                        String completeOutPath;

                        for (int i = 0; i < cacheFileList.size(); i++) {
                            cacheFile = cacheFileList.get(i);
                            //创建输出目录
                            subOutPath = outRoot + File.separator + cacheFile.getCollectionName();
                            FileUtils.createOrExistsDir(subOutPath);
                            //获取完整的输出目录
                            completeOutPath = subOutPath + File.separator + cacheFile.getChapterName();

                            //判断是否已经存在，如果存在则让用户选择都保留还是直接覆盖
                            if (FileUtils.isFileExists(completeOutPath + ".mp4")) {
                                //用户选择标志归位
                                MergeProgressDialog.FLAG_USER_HANDLE = false;
                                //处理弹窗
                                MergeProgressDialog.existsSameFileDialog(dialog.getContext(), completeOutPath, cacheFile, ffmpegCallback);
                                //用户没有选择就休眠等待
                                while (!MergeProgressDialog.FLAG_USER_HANDLE) {
                                    Thread.sleep(600);
                                }

                            } else {

                                //构造ffmpeg命令
                                cmdStr = String.format(MergeProgressDialog.cmdTemplate, cacheFile.getAudioPath(), cacheFile.getVideoPath(), completeOutPath);
                                cmd = cmdStr.split(" ");

                                //LogUtils.e(cmdStr);
                                //执行命令
                                RxFFmpegInvoke.getInstance()
                                        .runCommand(cmd, ffmpegCallback);

                            }


                        }

                    }
                }))
                .setTaskChainCallback(new TaskChainCallbackAdapter() {
                    @Override
                    public void onTaskChainCompleted(@NonNull ITaskChainEngine engine, @NonNull ITaskResult result) {
                        //更新ui
                        dialog.setContent(String.format("成功数:%s,失败数:%s,\n合并文件保存在%s目录下", ffmpegCallback.getSuccessNum(), ffmpegCallback.getFailNum(), outRoot));
                    }
                })
                .start();


    }

    /**
     * 存在相同文件处理弹窗
     *
     * @param context
     * @param completeOutPath
     * @param cacheFile
     * @param ffmpegCallback
     */
    public static void existsSameFileDialog(Context context, String completeOutPath, CacheFile cacheFile, RxFFmpegCallback ffmpegCallback) {
        //放在主线程中去执行
        XTask.postToMain(new Runnable() {
            @Override
            public void run() {
                new MaterialDialog.Builder(context)
                        .title("文件已经存在")
                        .content(completeOutPath + ".mp4已存在,请选择处理方法")
                        .cancelable(false)
                        .positiveText("都保存")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                int k = 0;
                                while (FileUtils.isFileExists(completeOutPath + "(" + k + ").mp4")) {
                                    k++;
                                }

                                //构造ffmpeg命令
                                String cmdStr = String.format(MergeProgressDialog.cmdTemplate, cacheFile.getAudioPath(), cacheFile.getVideoPath(), completeOutPath + "(" + k + ")");
                                String[] cmd = cmdStr.split(" ");
                                //LogUtils.e(cmdStr);
                                //执行命令
                                RxFFmpegInvoke.getInstance()
                                        .runCommand(cmd, ffmpegCallback);

                                dialog.dismiss();
                            }
                        })
                        .neutralText("覆盖")
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //删除已存在的
                                FileUtils.delete(completeOutPath + ".mp4");
                                //构造ffmpeg命令
                                String cmdStr = String.format(MergeProgressDialog.cmdTemplate, cacheFile.getAudioPath(), cacheFile.getVideoPath(), completeOutPath);
                                String[] cmd = cmdStr.split(" ");
                                //LogUtils.e(cmdStr);
                                //执行命令
                                RxFFmpegInvoke.getInstance()
                                        .runCommand(cmd, ffmpegCallback);
                                dialog.dismiss();
                            }
                        })
                        .negativeText("取消")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ffmpegCallback.onError("你取消了合并");
                                dialog.dismiss();
                            }
                        })
                        .dismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                //用户已经处理
                                MergeProgressDialog.FLAG_USER_HANDLE = true;
                            }
                        })
                        .show();
            }
        });
    }

}
