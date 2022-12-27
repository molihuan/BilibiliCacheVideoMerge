package com.molihua.hlbmerge.dialog.impl;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.blankj.molihuan.utilcode.util.FileIOUtils;
import com.blankj.molihuan.utilcode.util.FileUtils;
import com.blankj.molihuan.utilcode.util.UriUtils;
import com.molihua.hlbmerge.dao.ConfigData;
import com.molihua.hlbmerge.entity.CacheFile;
import com.molihua.hlbmerge.service.impl.PathCacheFileManager;
import com.molihua.hlbmerge.service.impl.RxFFmpegCallback;
import com.molihua.hlbmerge.service.impl.UriCacheFileManager;
import com.molihua.hlbmerge.utils.RxFfmpegTools;
import com.molihuan.pathselector.utils.FileTools;
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
import java.util.Objects;

/**
 * @ClassName: MergeProgressDialog
 * @Author: molihuan
 * @Date: 2022/12/24/14:01
 * @Description: 合并进度弹窗
 */
public class MergeProgressDialog {

    //用户是否选择的标志位
    public static boolean FLAG_USER_HANDLE = false;
    //退出执行ffmpeg命令
    public static boolean FLAG_EXIT_RUN_COMMAND = false;
    public static final String CMD_TEMPLATE = "ffmpeg -i %s -i %s -c copy %s";

    /**
     * 显示合并进度弹窗
     *
     * @param cacheFileList
     * @return
     */
    public static MaterialDialog showMergeProgressDialog(List<CacheFile> cacheFileList, Fragment fragment) {
        //初始化
        FLAG_EXIT_RUN_COMMAND = false;
        Context context = fragment.getContext();
        Objects.requireNonNull(context, "context is null");

        List<CacheFile> handledCacheFileList;

        //是否需要使用uri
        boolean dataUseUri = FileTools.underAndroidDataUseUri(ConfigData.getCacheFilePath());
        //把合集item处理成章节item
        if (dataUseUri) {
            handledCacheFileList = UriCacheFileManager.collection2ChapterCacheFileList(fragment, cacheFileList);
        } else {
            handledCacheFileList = PathCacheFileManager.collection2ChapterCacheFileList(cacheFileList);
        }


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
                        RxFfmpegTools.exitRunCommand();
                        MergeProgressDialog.FLAG_EXIT_RUN_COMMAND = true;
                        RxFfmpegTools.exitRunCommand();
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
        //获取导出配置
        int exportType = ConfigData.getExportType();
        boolean exportDanmaku = ConfigData.isExportDanmaku();

        XTask.getTaskChain()
                .addTask(XTask.getTask(new TaskCommand() {
                    @Override
                    public void run() throws Exception {

                        String cmdStr;
                        CacheFile srcCacheFile;
                        CacheFile cacheFile;
                        String subOutPath;
                        String completeOutPath;
                        String completeOutPathSuf;

                        for (int i = 0; i < cacheFileList.size(); i++) {
                            //是否退出命令的执行
                            if (MergeProgressDialog.FLAG_EXIT_RUN_COMMAND) {
                                break;
                            }

                            srcCacheFile = cacheFileList.get(i);

                            //将uri转换为file并把路径返回存入CacheFile中
                            cacheFile = MergeProgressDialog.cacheFileUri2File(srcCacheFile, dialog);

                            //创建输出目录
                            subOutPath = outRoot + File.separator + cacheFile.getCollectionName();
                            FileUtils.createOrExistsDir(subOutPath);
                            //获取完整的输出目录
                            completeOutPath = subOutPath + File.separator + cacheFile.getChapterName();

                            //合并或复制视频
                            switch (exportType) {
                                case 0:
                                    completeOutPathSuf = completeOutPath + ".mp4";
                                    if (FileUtils.isFileExists(completeOutPathSuf)) {
                                        //处理已经存在的文件弹窗
                                        MergeProgressDialog.handleExistsFileDialog(dialog.getContext(), completeOutPathSuf, cacheFile, ffmpegCallback, exportType, exportDanmaku);
                                    } else {
                                        cmdStr = String.format(MergeProgressDialog.CMD_TEMPLATE, cacheFile.getAudioPath(), cacheFile.getVideoPath(), completeOutPathSuf);
                                        RxFfmpegTools.runCommand(cmdStr, ffmpegCallback);
                                        //是否导出弹幕
                                        if (exportDanmaku) {
                                            FileUtils.copy(cacheFile.getDanmakuPath(), completeOutPath + ".xml");
                                        }

                                    }
                                    break;
                                case 1:
                                    completeOutPathSuf = completeOutPath + ".mp4";
                                    if (FileUtils.isFileExists(completeOutPathSuf)) {
                                        MergeProgressDialog.handleExistsFileDialog(dialog.getContext(), completeOutPathSuf, cacheFile, ffmpegCallback, exportType, exportDanmaku);
                                    } else {
                                        MergeProgressDialog.copyVideoAudioWithProgress(cacheFile.getVideoPath(), completeOutPathSuf, ffmpegCallback);
                                        //是否导出弹幕
                                        if (exportDanmaku) {
                                            FileUtils.copy(cacheFile.getDanmakuPath(), completeOutPath + ".xml");
                                        }
                                    }
                                    break;
                                case 2:
                                    completeOutPathSuf = completeOutPath + ".mp3";
                                    if (FileUtils.isFileExists(completeOutPathSuf)) {
                                        MergeProgressDialog.handleExistsFileDialog(dialog.getContext(), completeOutPathSuf, cacheFile, ffmpegCallback, exportType, exportDanmaku);
                                    } else {
                                        MergeProgressDialog.copyVideoAudioWithProgress(cacheFile.getAudioPath(), completeOutPathSuf, ffmpegCallback);
                                        //是否导出弹幕
                                        if (exportDanmaku) {
                                            FileUtils.copy(cacheFile.getDanmakuPath(), completeOutPath + ".xml");
                                        }
                                    }
                                    break;
                                default:
                            }


                        }
                    }
                }))
                .setTaskChainCallback(new TaskChainCallbackAdapter() {
                    @Override
                    public void onTaskChainCompleted(@NonNull ITaskChainEngine engine, @NonNull ITaskResult result) {
                        //更新ui
                        dialog.setContent(String.format("成功数:%s,失败数:%s,\n合并文件保存在%s目录下", ffmpegCallback.getSuccessNum(), ffmpegCallback.getFailNum(), outRoot));
                        dialog.setActionButton(DialogAction.NEGATIVE, "关闭");
                    }
                })
                .start();

    }

    /**
     * 将uri转换为file并把路径返回存入CacheFile中
     *
     * @param cacheFile
     * @return 转换成功或不是Android11返回true
     */
    public static CacheFile cacheFileUri2File(CacheFile cacheFile, MaterialDialog dialog) {
        if (cacheFile.getUseUri()) {
            //获取临时文件名
            String audioTemp = ConfigData.TYPE_OUTPUT_FILE_PATH_TEMP + "/audio.mp3";
            String videoTemp = ConfigData.TYPE_OUTPUT_FILE_PATH_TEMP + "/video.mp4";
            String danmakuTemp = ConfigData.TYPE_OUTPUT_FILE_PATH_TEMP + "/danmaku.xml";

            dialog.setContent("正在为你复制缓存文件,\n" + ConfigData.getCacheFilePath() + "--->" + ConfigData.TYPE_OUTPUT_FILE_PATH_TEMP);

            //uri转byte
            byte[] bytesAudio = UriUtils.uri2Bytes(Uri.parse(cacheFile.getAudioPath()));
            byte[] bytesVideo = UriUtils.uri2Bytes(Uri.parse(cacheFile.getVideoPath()));
            byte[] bytesDanmaku = UriUtils.uri2Bytes(Uri.parse(cacheFile.getDanmakuPath()));

            //删除已经存在的临时文件名
            FileUtils.delete(audioTemp);
            FileUtils.delete(videoTemp);
            FileUtils.delete(danmakuTemp);
            //byte转file
            FileIOUtils.writeFileFromBytesByChannel(audioTemp, bytesAudio, true);
            boolean success = FileIOUtils.writeFileFromBytesByChannel(videoTemp, bytesVideo, true);
            FileIOUtils.writeFileFromBytesByChannel(danmakuTemp, bytesDanmaku, true);
            //拷贝一份，不能影响源CacheFile
            CacheFile tempCacheFile = null;
            try {
                tempCacheFile = cacheFile.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

            if (tempCacheFile != null) {
                //重新设置路径
                tempCacheFile.setAudioPath(audioTemp);
                tempCacheFile.setVideoPath(videoTemp);
                tempCacheFile.setDanmakuPath(danmakuTemp);
            }

            return tempCacheFile;
        } else {
            return cacheFile;
        }

    }

    /**
     * 复制Video、Audio带有进度设置
     *
     * @param src
     * @param target
     * @param ffmpegCallback
     */
    public static void copyVideoAudioWithProgress(String src, String target, RxFFmpegCallback ffmpegCallback) {
        boolean success = FileUtils.copy(src, target);
        if (ffmpegCallback == null) {
            return;
        }
        if (success) {
            ffmpegCallback.onFinish();
        } else {
            ffmpegCallback.onError(src + "复制失败");
        }
    }

    /**
     * 存在相同文件处理弹窗
     *
     * @param context
     * @param completeOutPathSuf
     * @param cacheFile
     * @param ffmpegCallback
     */
    public static void handleExistsFileDialog(Context context, String completeOutPathSuf, CacheFile cacheFile, RxFFmpegCallback ffmpegCallback, int type, boolean exportDanmaku) throws InterruptedException {
        //用户选择标志归位
        MergeProgressDialog.FLAG_USER_HANDLE = false;

        //放在主线程中去执行
        XTask.postToMain(new Runnable() {
            @Override
            public void run() {
                new MaterialDialog.Builder(context)
                        .title("文件已经存在")
                        .content(completeOutPathSuf + "\n已存在,请选择处理方法")
                        .cancelable(false)
                        .positiveText("都保存")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //获取扩展名
                                String extension = FileTools.getFileExtension(completeOutPathSuf);
                                //设置新名称
                                int k = 0;
                                String reName;
                                do {
                                    reName = completeOutPathSuf.replace("." + extension, "(" + k + ")." + extension);
                                    k++;
                                } while (FileUtils.isFileExists(reName));

                                //是否导出弹幕
                                if (exportDanmaku) {
                                    FileUtils.copy(cacheFile.getDanmakuPath(), reName.replace(extension, "xml"));
                                }

                                switch (type) {
                                    case 0:
                                        //构造ffmpeg命令
                                        String cmdStr = String.format(MergeProgressDialog.CMD_TEMPLATE, cacheFile.getAudioPath(), cacheFile.getVideoPath(), reName);
                                        RxFfmpegTools.runCommand(cmdStr, ffmpegCallback);
                                        break;
                                    case 1:
                                        MergeProgressDialog.copyVideoAudioWithProgress(cacheFile.getVideoPath(), reName, ffmpegCallback);
                                        break;
                                    case 2:
                                        MergeProgressDialog.copyVideoAudioWithProgress(cacheFile.getAudioPath(), reName, ffmpegCallback);
                                        break;
                                    default:
                                }

                                dialog.dismiss();
                            }
                        })
                        .neutralText("覆盖")
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //获取扩展名
                                String extension = FileTools.getFileExtension(completeOutPathSuf);
                                //删除已存在的
                                FileUtils.delete(completeOutPathSuf);

                                switch (type) {
                                    case 0:
                                        //构造ffmpeg命令
                                        String cmdStr = String.format(MergeProgressDialog.CMD_TEMPLATE, cacheFile.getAudioPath(), cacheFile.getVideoPath(), completeOutPathSuf);
                                        RxFfmpegTools.runCommand(cmdStr, ffmpegCallback);
                                        break;
                                    case 1:
                                        MergeProgressDialog.copyVideoAudioWithProgress(cacheFile.getVideoPath(), completeOutPathSuf, ffmpegCallback);
                                        break;
                                    case 2:
                                        MergeProgressDialog.copyVideoAudioWithProgress(cacheFile.getAudioPath(), completeOutPathSuf, ffmpegCallback);
                                        break;
                                    default:
                                }

                                //是否导出弹幕
                                if (exportDanmaku) {
                                    String targetXml = completeOutPathSuf.replace(extension, "xml");
                                    //删除已存在的
                                    FileUtils.delete(targetXml);
                                    FileUtils.copy(cacheFile.getDanmakuPath(), targetXml);
                                }

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


        //用户没有选择就休眠等待
        while (!MergeProgressDialog.FLAG_USER_HANDLE) {
            Thread.sleep(600);
        }


    }

}
