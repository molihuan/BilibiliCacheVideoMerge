package com.molihua.hlbmerge.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.blankj.molihuan.utilcode.util.LogUtils;
import com.hjq.permissions.OnPermissionCallback;
import com.molihua.hlbmerge.activity.AbstractMainActivity;
import com.molihua.hlbmerge.dao.ConfigData;
import com.molihuan.pathselector.utils.FileTools;
import com.molihuan.pathselector.utils.PermissionsTools;
import com.molihuan.pathselector.utils.VersionTool;
import com.xuexiang.xui.widget.dialog.DialogLoader;

import java.util.List;

import rikka.shizuku.Shizuku;


/**
 * @ClassName: GeneralTools
 * @Author: molihuan
 * @Date: 2022/12/27/22:07
 * @Description: 通用工具
 */
public class GeneralTools {

    private static boolean isShizukuInstalled(Context context) {
        try {
            context.getPackageManager().getPackageInfo("moe.shizuku.privileged.api", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean getShizukuPermission(Context context) {
        Intent shizukuIntent = context.getPackageManager().getLaunchIntentForPackage("moe.shizuku.privileged.api");
        if (shizukuIntent != null) {
            if (Shizuku.pingBinder()) {
                boolean permissionIsGranted = Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
                if (permissionIsGranted) {
                    return true;
                } else {
                    Shizuku.addRequestPermissionResultListener(new Shizuku.OnRequestPermissionResultListener() {
                        @Override
                        public void onRequestPermissionResult(int requestCode, int grantResult) {
                            LogUtils.d("授权结果:" + requestCode + ":" + grantResult);
                            if (grantResult == 0) {
                                LogUtils.d("Shizuku授权成功");
                            }
                        }
                    });
                    Shizuku.requestPermission(0);
                }
            }
        }

        return true;
    }


    /**
     * 调用第三方浏览器打开网址
     *
     * @param context
     * @param url     要浏览的资源地址
     */
    public static void jumpBrowser(Context context, String url) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            Toast.makeText(context.getApplicationContext(), "请下载浏览器", Toast.LENGTH_SHORT).show();
        }
    }

    public static void initPermissionsOfStorage(AbstractMainActivity context) {
        //存储权限的申请
        if (!VersionTool.isAndroid13()) {
            PermissionsTools.generalPermissionsOfStorage(context, new OnPermissionCallback() {
                @Override
                public void onGranted(@NonNull List<String> permissions, boolean all) {
                    boolean dataUseUri = FileTools.underAndroidDataUseUri(ConfigData.getCacheFilePath());
                    if (!dataUseUri) {
                        //获取数据刷新列表
                        context.updateCollectionFileList();
                        context.refreshCacheFileList();
                    }
                }
            });
        }


        PermissionsTools.specialPermissionsOfStorageWithDialog(context, true, new OnPermissionCallback() {
            @Override
            public void onGranted(@NonNull List<String> permissions, boolean all) {
                boolean dataUseUri = FileTools.underAndroidDataUseUri(ConfigData.getCacheFilePath());
                if (!dataUseUri) {
                    //获取数据刷新列表
                    context.updateCollectionFileList();
                    context.refreshCacheFileList();
                }
            }
        });
    }

    public static boolean isApkDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 跳转项目地址
     *
     * @param context
     */
    public static void jumpProjectAddress(Context context) {
        DialogLoader.getInstance().showConfirmDialog(
                context,
                "国内选GITEE国外选GITHUB",
                "GITEE",
                (dialog, which) -> {
                    GeneralTools.jumpBrowser(context, LConstants.PROJECT_ADDRESS_GITEE);
                    dialog.dismiss();
                },
                "GITHUB",
                (dialog, which) -> {
                    GeneralTools.jumpBrowser(context, LConstants.PROJECT_ADDRESS_GITHUB);
                    dialog.dismiss();
                }
        ).setCanceledOnTouchOutside(true);
    }

    public static void jumpProjectIssues(Context context) {
        DialogLoader.getInstance().showConfirmDialog(
                context,
                "国内选GITEE国外选GITHUB",
                "GITEE",
                (dialog, which) -> {
                    GeneralTools.jumpBrowser(context, LConstants.PROJECT_ADDRESS_GITEE + "/issues");
                    dialog.dismiss();
                },
                "GITHUB",
                (dialog, which) -> {
                    GeneralTools.jumpBrowser(context, LConstants.PROJECT_ADDRESS_GITHUB + "/issues");
                    dialog.dismiss();
                }
        ).setCanceledOnTouchOutside(true);
    }


}
