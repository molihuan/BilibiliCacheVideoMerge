package com.molihua.hlbmerge.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ToastUtils;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.molihua.hlbmerge.MainActivity;
import com.molihua.hlbmerge.R;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.util.Iterator;
import java.util.List;

public class PermissionsTools  {


    public static void getAllNeedPermissions(Activity context,ContentResolver contentResolver){
        generalPermissionsOfStorage(context);//普通存储访问权限

        if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager())) {
            //表明已经有这个权限了
        } else {
            new MaterialDialog.Builder(context)
                    .iconRes(R.drawable.xui_ic_default_tip_btn)
                    .title("提示")
                    .content("需要所有文件访问权限(用于创建文件和文件夹)")
                    .positiveText("爷给")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            specialPermissionsOfStorage(context);//特殊存储访问权限
                            getAndroidDataPermissionDialog(context,contentResolver);
                        }
                    })
                    .negativeText("就不给")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            ToastUtils.make().show("不给所有文件访问权限软件无法运行");
                        }
                    })
                    .show();
        }






    }

    /**
     * 获取Android/data目录访问权限弹窗
     * @param activity
     * @param contentResolver
     */
    public static void getAndroidDataPermissionDialog(Activity activity, ContentResolver contentResolver){
        //安卓11data目录访问权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Iterator<UriPermission> it = contentResolver.getPersistedUriPermissions().iterator();
            while (true) {
                if (it.hasNext()) {
                    if (it.next().isReadPermission()) {
                        break;
                    }
                } else {

                    new MaterialDialog.Builder(activity)
                            .iconRes(R.drawable.xui_ic_default_tip_btn)
                            .title("提示")
                            .content("需要Android/data访问权限(用于读取B站缓存文件)")
                            .positiveText("爷给")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    getAndroidDataPermission(activity,contentResolver);//沙盒存储访问权限
                                }
                            })
                            .negativeText("就不给")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    ToastUtils.make().show("不给Android/data访问权限软件无法运行");
                                }
                            })
                            .show();


                    break;
                }
            }
        }
    }



    /**
     * 获取一般读写权限
     */
    public static void generalPermissionsOfStorage(Context context) {

        //PermissionUtils.permission(PERMISSIONS_STORAGE).request();//读写权限动态获取

        boolean isGet = XXPermissions.isGranted(context, Permission.Group.STORAGE);
        if (isGet) return;//已有权限则返回

        //获取基本读取权限
        XXPermissions.with(context)
                // 申请单个权限
                //.permission(Permission.MANAGE_EXTERNAL_STORAGE)
                // 申请多个权限
                .permission(Permission.Group.STORAGE)
                // 设置不触发错误检测机制（局部设置）
                .unchecked()
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            ToastUtils.make().show("基本读取权限获取成功");
                            if (!VersionTools.isAndroid11()){
                                ((MainActivity)context).refreshMainShowListView();
                            }
                        } else {
                            ToastUtils.make().show("获取部分权限成功，但部分权限未正常授予");
                        }
                    }
                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            ToastUtils.make().show("被永久拒绝授权，请手动授予读取权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(context, permissions);
                        } else {
                            ToastUtils.make().show("获取读取权限失败");
                        }
                    }
                });
    }

    /**
     * 获取全文件读取权限
     * @param context
     */
    public static void specialPermissionsOfStorage(Context context) {
        if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager())) {
            //表明已经有这个权限了
        } else {

            //获取全文件读取权限
            XXPermissions.with(context)
                    // 申请单个权限
                    .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    // 申请多个权限
                    //.permission(Permission.Group.STORAGE)
                    // 设置不触发错误检测机制（局部设置）

                    .unchecked()
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            if (all) {
                                ToastUtils.make().show("所以文件访问权限获取成功");
                                if (VersionTools.isAndroid11()){
                                    PathTools.initCreateDir();//初始化创建temp目录
                                }
                            } else {
                                ToastUtils.make().show("获取部分权限成功，但部分权限未正常授予");
                            }
                        }
                        @Override
                        public void onDenied(List<String> permissions, boolean never) {
                            if (never) {
                                ToastUtils.make().show("被永久拒绝授权，请手动授予读取权限");
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                XXPermissions.startPermissionActivity(context, permissions);
                            } else {
                                ToastUtils.make().show("获取读取权限失败");
                            }
                        }
                    });
        }
    }



    /**
     *获取Android/data目录访问权限
     * @param activity  this
     * @param contentResolver   getContentResolver()
     */
    public static void getAndroidDataPermission(Activity activity, ContentResolver contentResolver){
        //安卓11data目录访问权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Iterator<UriPermission> it = contentResolver.getPersistedUriPermissions().iterator();
            while (true) {
                if (it.hasNext()) {
                    if (it.next().isReadPermission()) {
                        break;
                    }
                } else {
                    Uri uri = Uri.parse(UriTools.URI_ANRROID_DATA);
                    Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    intent1.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                            | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                    intent1.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
                    activity.startActivityForResult(intent1, 11);
                    break;
                }
            }
        }
    }








}
