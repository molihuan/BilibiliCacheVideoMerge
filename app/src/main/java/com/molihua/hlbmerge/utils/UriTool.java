package com.molihua.hlbmerge.utils;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import com.blankj.molihuan.utilcode.util.UriUtils;
import com.molihuan.pathselector.utils.PermissionsTools;
import com.molihuan.pathselector.utils.UriTools;
import com.xuexiang.xtask.XTask;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.util.Objects;

/**
 * @ClassName: UriTool
 * @Author: molihuan
 * @Date: 2022/12/24/21:28
 * @Description:
 */
public class UriTool {

    public static void grantedUriPermission(String path, Fragment fragment) {
        //获取上下文
        Context context = fragment.getContext();
        Objects.requireNonNull(context, "context is null");

        Uri uri = UriTools.path2Uri(path, false);
        //获取权限,没有权限返回null有权限返回授权uri字符串
        String existsPermission = PermissionsTools.existsGrantedUriPermission(uri, fragment);

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
                            PermissionsTools.goApplyUriPermissionPage(uri, fragment);
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

    public static DocumentFile[] getCollectionChapterFile(Fragment fragment, String currentPath) {
        //获取上下文
        Context context = fragment.getContext();
        Objects.requireNonNull(context, "context is null");

        Uri uri = UriTools.path2Uri(currentPath, false);
        //获取权限,没有权限返回null有权限返回授权uri字符串
        String existsPermission = PermissionsTools.existsGrantedUriPermission(uri, fragment);

        if (existsPermission == null) {
            //没有权限申请权限
            XTask.postToMain(new Runnable() {
                @Override
                public void run() {
                    //申请权限弹窗
                    new MaterialDialog.Builder(context)
                            .title("授权提示")
                            .content("需要授予\n" + currentPath + "\n目录访问权限")
                            .cancelable(false)
                            .positiveText("授权")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    //申请权限
                                    PermissionsTools.goApplyUriPermissionPage(uri, fragment);
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
            });

            return null;
        }

        Uri targetUri = Uri.parse(existsPermission + uri.toString().replaceFirst(UriTools.URI_PERMISSION_REQUEST_COMPLETE_PREFIX, ""));
        DocumentFile rootDocumentFile = DocumentFile.fromSingleUri(context, targetUri);
        Objects.requireNonNull(rootDocumentFile, "rootDocumentFile is null");
        //创建一个 DocumentFile表示以给定的 Uri根的文档树。其实就是获取子目录的权限
        DocumentFile pickedDir = rootDocumentFile.fromTreeUri(context, targetUri);

        Objects.requireNonNull(pickedDir, "pickedDir is null");
        DocumentFile[] documentFiles = pickedDir.listFiles();

        return documentFiles;
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
        result = FileTools.getCollectionChapterName(jsonByte, result);
        return result;
    }
}
