package com.molihua.hlbmerge.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import com.blankj.molihuan.utilcode.util.FileUtils;
import com.blankj.molihuan.utilcode.util.UriUtils;
import com.molihua.hlbmerge.R;
import com.molihuan.pathselector.dialog.BaseDialog;
import com.molihuan.pathselector.dialog.impl.MessageDialog;
import com.molihuan.pathselector.entity.FontBean;
import com.molihuan.pathselector.utils.FileTools;
import com.molihuan.pathselector.utils.PermissionsTools;
import com.molihuan.pathselector.utils.ReflectTools;
import com.molihuan.pathselector.utils.UriTools;
import com.xuexiang.xtask.XTask;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
     * 复制DocumentFile文件夹
     *
     * @param fragment      存储uri权限的fragment
     * @param srcParentPath 选择文件夹的父目录
     * @param srcPath       选择的文件夹
     * @param destPath      目标文件夹
     */
    public static void copyDocumentDir(Fragment fragment, String srcParentPath, String srcPath, String destPath) {

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
                    FileUtils.createOrExistsDir(tempPath.replace(srcParentPath, destPath));
                    copyDocumentDir(fragment, srcParentPath, tempPath, destPath);
                } else {
                    documentFile2File(documentFiles[i].getUri(), tempPath.replace(srcParentPath, destPath));
                }

            }
        }


    }

    /**
     * 获取uri权限
     *
     * @param path
     * @param context
     */
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

    /**
     * 通过uri获取文件长度
     *
     * @param uri
     * @return
     */
    public static long getUriFileLength(Uri uri) {
        switch (uri.getScheme()) {
            case ContentResolver.SCHEME_FILE:
                return new File(uri.getPath()).length();
            case ContentResolver.SCHEME_CONTENT:
                ContentResolver cr = ReflectTools.getApplicationByReflect().getContentResolver();
                Cursor cursor = cr.query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    if (cursor.isNull(columnIndex)) {
                        return 0L;
                    } else {
                        return cursor.getLong(columnIndex);
                    }
                } else {
                    return 0;
                }
            default:
                return 0L;
        }
    }


    public interface IDocumentFileListener {
        /**
         * 转换进度
         *
         * @param currentPosition
         */
        void onConvertProgress(long currentPosition, long total);
    }


    public static boolean documentFile2File(Uri srcFile, String targetPath) {
        return documentFile2File(srcFile, targetPath, null);
    }

    /**
     * 将documentfile转换为file
     *
     * @param srcFile
     * @param targetPath
     * @param listener
     * @return
     */
    public static boolean documentFile2File(Uri srcFile, String targetPath, IDocumentFileListener listener) {
        long currentPosition = 0;
        long total = getUriFileLength(srcFile);
        InputStream fis = null;
        OutputStream fos = null;

        try {
            fis = ReflectTools.getApplicationByReflect().getContentResolver().openInputStream(srcFile);
            fos = new FileOutputStream(targetPath);
            byte[] buf = new byte[1024];
            int count = 0;
            if (listener == null) {
                while ((count = fis.read(buf)) != -1) {
                    fos.write(buf, 0, count);
                }
            } else {
                while ((count = fis.read(buf)) != -1) {
                    currentPosition += count;
                    if (currentPosition % 40960 == 0) {
                        listener.onConvertProgress(currentPosition, total);
                    }
                    fos.write(buf, 0, count);
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) fos.close();
                if (fis != null) fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
