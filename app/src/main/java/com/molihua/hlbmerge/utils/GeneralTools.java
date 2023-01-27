package com.molihua.hlbmerge.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.hjq.permissions.OnPermissionCallback;
import com.molihua.hlbmerge.activity.AbstractMainActivity;
import com.molihua.hlbmerge.dao.ConfigData;
import com.molihuan.pathselector.utils.FileTools;
import com.molihuan.pathselector.utils.PermissionsTools;

import java.util.List;


/**
 * @ClassName: GeneralTools
 * @Author: molihuan
 * @Date: 2022/12/27/22:07
 * @Description: 通用工具
 */
public class GeneralTools {
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


}
