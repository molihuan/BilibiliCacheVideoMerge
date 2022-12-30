package com.molihua.hlbmerge.fragment.impl;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.molihuan.utilcode.util.FileUtils;
import com.blankj.molihuan.utilcode.util.ToastUtils;
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
import com.molihuan.pathselector.utils.MConstants;
import com.molihuan.pathselector.utils.Mtools;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

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
                .setShowTabbarFragment(false)
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
                                ToastUtils.make().show("选择错误");
                            }
                        }

                        return false;
                    }
                })
                .setHandleItemListeners(
                        new CommonItemListener("全选") {
                            @Override
                            public boolean onClick(View v, TextView tv, List<FileBean> selectedFiles, String currentPath, BasePathSelectFragment pathSelectFragment) {
                                pathSelectFragment.openCloseMultipleMode(true);
                                if (tv.getText().equals("全选")) {
                                    pathSelectFragment.selectAllFile(true);
                                    tv.setText("全不选");
                                } else {
                                    pathSelectFragment.selectAllFile(false);
                                    tv.setText("全选");
                                }
                                pathSelectFragment.refreshFileList();

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
//TODO 设置取消复制

//                                    List<CommonItemListener> handleItemListeners = pathSelectFragment.getHandleItemListeners();
//                                    for (CommonItemListener handleItemListener : handleItemListeners) {
//                                        if ("取消".equals(handleItemListener.getFontBean().getText())) {
//                                            handleItemListener.getFontBean().setText("取消复制");
//                                            pathSelectFragment.refreshHandleList();
//                                            break;
//                                        }
//                                    }

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
