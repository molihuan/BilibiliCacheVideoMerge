package com.molihua.hlbmerge.dialog.impl;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.molihuan.utilcode.util.FileUtils;
import com.molihua.hlbmerge.utils.UriTool;
import com.molihuan.pathselector.entity.FileBean;
import com.molihuan.pathselector.fragment.BasePathSelectFragment;
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

/**
 * @ClassName: CopyProgressDialog
 * @Author: molihuan
 * @Date: 2022/12/28/19:27
 * @Description:
 */
public class CopyProgressDialog {

    private static boolean dataUseUri;

    public static MaterialDialog showCopyProgressDialog(List<FileBean> copySrcFileList, String copySrcParentPath, String currentPath, Context context, TextView tv, BasePathSelectFragment pathSelectFragment) {

        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title("提示")
                .content("正在复制文件...")
                .contentGravity(GravityEnum.CENTER)
                .progress(false, copySrcFileList.size(), true)
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
                        CopyProgressDialog.startCopy(copySrcFileList, copySrcParentPath, currentPath, (MaterialDialog) dialog, tv, pathSelectFragment);
                    }
                })
                .show();

        return materialDialog;
    }

    /**
     * 把源文件路径复制到currentPath下
     *
     * @param copySrcFileList
     * @param dialog
     */
    private static void startCopy(List<FileBean> copySrcFileList, String copySrcParentPath, String currentPath, MaterialDialog dialog, TextView tv, BasePathSelectFragment pathSelectFragment) {

        XTask.getTaskChain()
                .addTask(XTask.getTask(new TaskCommand() {
                    @Override
                    public void run() throws Exception {

                        for (FileBean fileBean : copySrcFileList) {

                            XTask.postToMain(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.setContent("正在复制:" + fileBean.getPath());
                                }
                            });

                            //是否需要使用uri
                            CopyProgressDialog.dataUseUri = FileTools.underAndroidDataUseUri(fileBean.getPath());
                            if (CopyProgressDialog.dataUseUri) {
                                //开始复制DocumentFile
                                UriTool.copyDocumentFile(pathSelectFragment, FileTools.getParentPath(fileBean.getPath()), fileBean.getPath(), currentPath);
                            } else {
                                //开始复制File
                                FileUtils.copy(fileBean.getPath(), fileBean.getPath().replace(copySrcParentPath, currentPath), new FileUtils.OnReplaceListener() {
                                    @Override
                                    public boolean onReplace(File file, File file1) {
                                        return false;
                                    }
                                });

                            }

                            dialog.incrementProgress(1);

                        }

                    }
                }))
                .setTaskChainCallback(new TaskChainCallbackAdapter() {
                    @Override
                    public void onTaskChainCompleted(@NonNull ITaskChainEngine engine, @NonNull ITaskResult result) {
                        //更新ui
                        dialog.setContent("处理完成");
                        dialog.setActionButton(DialogAction.NEGATIVE, "关闭");

                        pathSelectFragment.updateFileList();
                        tv.setText("复制");
                        copySrcFileList.clear();
                    }
                })
                .start();


    }
}
