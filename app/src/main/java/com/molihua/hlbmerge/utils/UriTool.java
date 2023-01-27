package com.molihua.hlbmerge.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import com.blankj.molihuan.utilcode.util.FileIOUtils;
import com.blankj.molihuan.utilcode.util.FileUtils;
import com.blankj.molihuan.utilcode.util.UriUtils;
import com.molihua.hlbmerge.R;
import com.molihuan.pathselector.dialog.BaseDialog;
import com.molihuan.pathselector.dialog.impl.MessageDialog;
import com.molihuan.pathselector.entity.FontBean;
import com.molihuan.pathselector.utils.FileTools;
import com.molihuan.pathselector.utils.PermissionsTools;
import com.molihuan.pathselector.utils.UriTools;
import com.xuexiang.xtask.XTask;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName: UriTool
 * @Author: molihuan
 * @Date: 2022/12/24/21:28
 * @Description:
 */
public class UriTool {

    public static String tempPath;

    /**
     * 复制DocumentFile
     *
     * @param fragment
     * @param srcParentPath
     * @param srcPath
     * @param destPath
     */
    public static void copyDocumentFile(Fragment fragment, String srcParentPath, String srcPath, String destPath) {

        Context context = fragment.getContext();

        Uri uri = UriTools.path2Uri(srcPath, false);

        String existsPermission = PermissionsTools.existsGrantedUriPermission(uri, fragment);

        if (existsPermission == null) {

            //没有权限申请权限
            XTask.postToMain(new Runnable() {
                @Override
                public void run() {
                    //申请权限弹窗
                    new MessageDialog(context)
                            .setContent(new FontBean(String.format(context.getString(R.string.tip_uri_authorization_permission_content_hlb), srcPath)))
                            .setConfirm(new FontBean(context.getString(R.string.option_confirm_hlb), 15), new BaseDialog.IOnConfirmListener() {
                                @Override
                                public boolean onClick(View v, BaseDialog dialog) {
                                    //申请权限
                                    PermissionsTools.goApplyUriPermissionPage(uri, fragment);
                                    dialog.dismiss();
                                    return false;
                                }
                            })
                            .setCancel(new FontBean(context.getString(R.string.option_cancel_hlb), 15), new BaseDialog.IOnCancelListener() {
                                @Override
                                public boolean onClick(View v, BaseDialog dialog) {
                                    dialog.dismiss();
                                    return false;
                                }
                            })
                            .show();
                }
            });

            return;
        }

        Uri targetUri = Uri.parse(existsPermission + uri.toString().replaceFirst(UriTools.URI_PERMISSION_REQUEST_COMPLETE_PREFIX, ""));

        //Mtools.log(targetUri);

        DocumentFile rootDocumentFile = DocumentFile.fromSingleUri(context, targetUri);
        Objects.requireNonNull(rootDocumentFile, "rootDocumentFile is null");

        //创建一个 DocumentFile表示以给定的 Uri根的文档树。其实就是获取子目录的权限
        DocumentFile pickedDir = rootDocumentFile.fromTreeUri(context, targetUri);
        Objects.requireNonNull(pickedDir, "pickedDir is null");

        DocumentFile[] documentFiles = pickedDir.listFiles();

        if (documentFiles != null) {
            for (int i = 0; i < documentFiles.length; i++) {

                tempPath = srcPath + File.separator + documentFiles[i].getName();

                if (documentFiles[i].isDirectory()) {
//                    LogUtils.w(documentFiles[i].getUri() + "\n夹夹夹夹夹夹夹夹夹夹" + tempPath.replace(srcParentPath, destPath));
                    FileUtils.createOrExistsDir(tempPath.replace(srcParentPath, destPath));
                    copyDocumentFile(fragment, srcParentPath, tempPath, destPath);
                } else {
//                    LogUtils.w(documentFiles[i].getUri() + "\n文件文件文件文件文件" + tempPath.replace(srcParentPath, destPath));
                    byte[] srcBytes = UriUtils.uri2Bytes(documentFiles[i].getUri());
                    FileIOUtils.writeFileFromBytesByChannel(tempPath.replace(srcParentPath, destPath), srcBytes, true);
                }
            }
        }


    }

    public static void grantedUriPermission(String path, Activity context) {

        Objects.requireNonNull(context, "context is null");

        if (!FileTools.needUseUri(path)) {
            return;
        }

        Uri uri = UriTools.path2Uri(path, false);
        //获取权限,没有权限返回null有权限返回授权uri字符串
        String existsPermission = PermissionsTools.existsGrantedUriPermission(uri, context);

        if (existsPermission == null) {
            //没有权限申请权限
            new MaterialDialog.Builder(context)
                    .title("授权提示")
                    .content("需要授予\n" + path + "\n目录访问权限")
                    .cancelable(false)
                    .positiveText("授权")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            //申请权限
                            PermissionsTools.goApplyUriPermissionPage(uri, context);
                            dialog.dismiss();
                        }
                    })
                    .negativeText("取消")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    public static List<DocumentFile> getCollectionChapterFile(Fragment fragment, String currentPath) {
        //获取上下文
        Context context = fragment.getContext();
        Objects.requireNonNull(context, "context is null");

        Uri uri = UriTools.path2Uri(currentPath, false);
        //获取权限,没有权限返回null有权限返回授权uri字符串
        String existsPermission = PermissionsTools.existsGrantedUriPermission(uri, fragment);

        List<DocumentFile> fileList = new ArrayList<>();

        if (existsPermission == null) {
            //没有权限直接返回
            return fileList;
        }

        Uri targetUri = Uri.parse(existsPermission + uri.toString().replaceFirst(UriTools.URI_PERMISSION_REQUEST_COMPLETE_PREFIX, ""));
        DocumentFile rootDocumentFile = DocumentFile.fromSingleUri(context, targetUri);
        Objects.requireNonNull(rootDocumentFile, "rootDocumentFile is null");
        //创建一个 DocumentFile表示以给定的 Uri根的文档树。其实就是获取子目录的权限
        DocumentFile pickedDir = rootDocumentFile.fromTreeUri(context, targetUri);

        Objects.requireNonNull(pickedDir, "pickedDir is null");
        DocumentFile[] documentFiles = pickedDir.listFiles();

        //TODO 去除不是文件夹的item
        if (documentFiles == null) {
            return fileList;
        }

        for (DocumentFile documentFile : documentFiles) {
            if (documentFile.isDirectory()) {
                fileList.add(documentFile);
            }
        }
        return fileList;
    }

    public static Uri[] getNeedUri(DocumentFile chapterFile, Uri[] result) {
        DocumentFile[] files = chapterFile.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    getNeedUri(files[i], result);
                } else {
                    switch (files[i].getName()) {
                        case "audio.m4s":
                            result[0] = files[i].getUri();
                            break;
                        case "video.m4s":
                            result[1] = files[i].getUri();
                            break;
                        case "entry.json":
                            result[2] = files[i].getUri();
                            break;
                        case "danmaku.xml":
                            result[3] = files[i].getUri();
                            break;
                    }
                }
            }
        }
        return result;
    }


    public static String[] getCollectionChapterName(Uri jsonUri, String[] result) {
        //uri转byte
        byte[] jsonByte = UriUtils.uri2Bytes(jsonUri);
        //通过jsonByte获取名称
        result = FileTool.getCollectionChapterName(jsonByte, result);
        return result;
    }
}
