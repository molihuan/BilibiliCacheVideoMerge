package com.molihua.hlbmerge.fragment.impl;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.blankj.molihuan.utilcode.util.FileUtils;
import com.blankj.molihuan.utilcode.util.ToastUtils;
import com.blankj.molihuan.utilcode.util.ZipUtils;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.activity.impl.PlayVideoActivity;
import com.molihua.hlbmerge.dao.ConfigData;
import com.molihua.hlbmerge.dialog.impl.CopyProgressDialog;
import com.molihua.hlbmerge.fragment.AbstractMainFragment;
import com.molihuan.pathselector.PathSelector;
import com.molihuan.pathselector.entity.FileBean;
import com.molihuan.pathselector.fragment.BasePathSelectFragment;
import com.molihuan.pathselector.fragment.impl.PathSelectFragment;
import com.molihuan.pathselector.listener.CommonItemListener;
import com.molihuan.pathselector.listener.FileItemListener;
import com.molihuan.pathselector.utils.FileTools;
import com.molihuan.pathselector.utils.MConstants;
import com.molihuan.pathselector.utils.Mtools;
import com.xuexiang.xtask.XTask;
import com.xuexiang.xtask.core.ITaskChainEngine;
import com.xuexiang.xtask.core.param.ITaskResult;
import com.xuexiang.xtask.core.step.impl.TaskChainCallbackAdapter;
import com.xuexiang.xtask.core.step.impl.TaskCommand;
import com.xuexiang.xui.widget.dialog.MiniLoadingDialog;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: MainCompleteFragment
 * @Author: molihuan
 * @Date: 2022/12/21/21:10
 * @Description:
 */
public class MainCompleteFragment extends AbstractMainFragment {
    private PathSelectFragment pathSelectFragment;

    private List<FileBean> copySrcFileList;
    private String copySrcParentPath;

    private TextView copyBtnTextView;

    private boolean stopDeleteFlag;

    @Override
    public int setFragmentViewId() {
        return R.layout.fragment_main_complete;
    }

    @Override
    public void getComponents(View view) {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        openFileChoose();
    }

    @Override
    public void setListeners() {

    }

    public PathSelectFragment getPathSelectFragment() {
        return pathSelectFragment;
    }

    public void refreshFileList() {
        pathSelectFragment.updateFileList();
    }

    private void openFileChoose() {

        pathSelectFragment = PathSelector.build(this, MConstants.BUILD_FRAGMENT)
                .setRootPath(ConfigData.getOutputFilePath())
                .setFrameLayoutId(R.id.main_complete_view)
                .setShowTitlebarFragment(false)
                .setAlwaysShowHandleFragment(true)
                .setSelectFileTypes("mp4", "xml", "mp3", "m4s", "")
                .setFileItemListener(new FileItemListener() {
                    @Override
                    public boolean onClick(View v, FileBean file, String currentPath, BasePathSelectFragment pathSelectFragment) {
                        if (!file.isDir()) {
                            String fileExtension = file.getFileExtension();
                            if ("mp4".equals(fileExtension) || "mp3".equals(fileExtension) || "m4s".equals(fileExtension)) {
                                Intent intent = new Intent(mActivity, PlayVideoActivity.class);
                                intent.putExtra("videoPath", file.getPath());
                                startActivity(intent);
                            } else {
                                ToastUtils.make().show("此文件不支持播放");
                            }
                        }

                        return false;
                    }
                })
                .setHandleItemListeners(
                        new CommonItemListener("压缩") {
                            @Override
                            public boolean onClick(View v, TextView tv, List<FileBean> selectedFiles, String currentPath, BasePathSelectFragment pathSelectFragment) {


                                if (selectedFiles == null || selectedFiles.size() == 0) {
                                    Mtools.toast("你还没有选择文件捏!(长按进行选择)");
                                    return false;
                                }

                                List<String> pathList = new ArrayList<>();


                                MiniLoadingDialog miniLoadingDialog = new MiniLoadingDialog(mActivity, "压缩处理中");
                                miniLoadingDialog.setCancelable(false);
                                miniLoadingDialog.show();


                                XTask.getTaskChain()
                                        .addTask(XTask.getTask(new TaskCommand() {
                                            @Override
                                            public void run() throws Exception {
                                                String zipPath;
                                                for (int i = 0; i < selectedFiles.size(); i++) {
                                                    zipPath = selectedFiles.get(i).getPath();
                                                    if (FileTools.underAndroidDataUseUri(zipPath)) {
                                                        throw new RuntimeException("Files cannot be compressed under Android/data");
                                                    }
                                                    pathList.add(zipPath);
                                                }

                                                if (FileUtils.createOrExistsDir(ConfigData.TYPE_OUTPUT_FILE_PATH_ZIP)) {
                                                    try {
                                                        ZipUtils.zipFiles(pathList, ConfigData.TYPE_OUTPUT_FILE_PATH_ZIP + File.separator + "video.zip");
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }


                                            }
                                        }))
                                        .setTaskChainCallback(new TaskChainCallbackAdapter() {
                                            @Override
                                            public void onTaskChainCompleted(@NonNull ITaskChainEngine engine, @NonNull ITaskResult result) {
                                                //更新ui
                                                miniLoadingDialog.dismiss();
                                                Mtools.toast("压缩成功,压缩文件保存在" + ConfigData.TYPE_OUTPUT_FILE_PATH_ZIP + File.separator + "video.zip");
                                                pathSelectFragment.updateFileList();
                                                pathSelectFragment.openCloseMultipleMode(false);
                                            }

                                            @Override
                                            public void onTaskChainError(@NonNull ITaskChainEngine engine, @NonNull ITaskResult result) {
                                                miniLoadingDialog.dismiss();
                                                Mtools.toast("压缩失败,如果你的手机为Android 11及以上,则不支持直接压缩Android/data下的文件,请先将文件或文件夹复制出Android/data再进行压缩", Toast.LENGTH_LONG);
                                                pathSelectFragment.updateFileList();
                                                pathSelectFragment.openCloseMultipleMode(false);
                                                super.onTaskChainError(engine, result);
                                            }
                                        })
                                        .start();


                                return false;
                            }
                        },
                        new CommonItemListener("复制") {
                            @Override
                            public boolean onClick(View v, TextView tv, List<FileBean> selectedFiles, String currentPath, BasePathSelectFragment pathSelectFragment) {
                                copyBtnTextView = tv;
                                if (tv.getText().equals("复制")) {

                                    if (selectedFiles == null || selectedFiles.size() == 0) {
                                        Mtools.toast("你还没有选择文件捏!(长按进行选择)");
                                        return false;
                                    }

                                    tv.setText("粘贴");

                                    //获取源文件路径
                                    copySrcFileList = new ArrayList<>(selectedFiles);
                                    //获取源文件父目录
                                    copySrcParentPath = currentPath;
                                    pathSelectFragment.openCloseMultipleMode(false);

                                } else {
                                    if (copySrcFileList == null || copySrcFileList.size() == 0) {
                                        return false;
                                    }
                                    CopyProgressDialog.showCopyProgressDialog(copySrcFileList, copySrcParentPath, currentPath, mActivity, tv, pathSelectFragment);

                                }

                                return false;
                            }
                        },
                        new CommonItemListener("删除") {
                            @Override
                            public boolean onClick(View v, TextView tv, List<FileBean> selectedFiles, String currentPath, BasePathSelectFragment pathSelectFragment) {
                                if (selectedFiles == null || selectedFiles.size() == 0) {
                                    Mtools.toast("你还没有选择文件捏!(长按进行选择)");
                                    return false;
                                }
                                //初始化标志位
                                stopDeleteFlag = false;

                                new MaterialDialog.Builder(mActivity)
                                        .title("删除")
                                        .content("你确定要删除此文件吗?(此操作无法撤回)")
                                        .cancelable(false)
                                        .positiveText("确定")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                for (FileBean selectedFile : selectedFiles) {
                                                    if (stopDeleteFlag) {
                                                        break;
                                                    }
                                                    FileUtils.delete(selectedFile.getPath());
                                                }
                                                pathSelectFragment.updateFileList();
                                                pathSelectFragment.openCloseMultipleMode(false);
                                            }
                                        })
                                        .negativeText("取消")
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                stopDeleteFlag = true;
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();

                                return false;
                            }
                        },
                        new CommonItemListener("取消") {
                            @Override
                            public boolean onClick(View v, TextView tv, List<FileBean> selectedFiles, String currentPath, BasePathSelectFragment pathSelectFragment) {
                                pathSelectFragment.openCloseMultipleMode(false);
                                if (copyBtnTextView != null) {
                                    copyBtnTextView.setText("复制");
                                }
                                tv.setText("取消");
                                return false;
                            }
                        }
                )
                .show();

    }

    @Override
    public boolean onBackPressed() {
        if (pathSelectFragment != null && pathSelectFragment.onBackPressed()) {
            return true;
        }
        return false;
    }


}
