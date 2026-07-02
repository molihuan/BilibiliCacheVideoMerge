package com.molihua.hlbmerge.utils

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.blankj.molihuan.utilcode.util.LogUtils
import com.molihua.hlbmerge.activity.AbstractMainActivity
import com.molihua.hlbmerge.activity.impl.MainActivity
import com.molihua.hlbmerge.dao.ConfigData
import com.molihuan.pathselector.utils.FileTools
import com.molihuan.pathselector.utils.PermissionsTools
import com.molihuan.pathselector.utils.VersionTool
import com.xuexiang.xui.widget.dialog.DialogLoader
import rikka.shizuku.Shizuku

object GeneralTools {

    private fun isShizukuInstalled(context: Context): Boolean {
        try {
            context.packageManager.getPackageInfo("moe.shizuku.privileged.api", 0)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return false
    }


    @JvmStatic
    fun getShizukuPermission(context: Context): Boolean {
        val shizukuIntent =
            context.packageManager.getLaunchIntentForPackage("moe.shizuku.privileged.api")
        if (shizukuIntent != null) {
            if (Shizuku.pingBinder()) {
                val permissionIsGranted =
                    Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
                if (permissionIsGranted) {
                    return true
                } else {
                    Shizuku.addRequestPermissionResultListener { requestCode, grantResult ->
                        LogUtils.d("授权结果:$requestCode:$grantResult")
                        if (grantResult == 0) {
                            LogUtils.d("Shizuku授权成功")
                        }
                    }
                    Shizuku.requestPermission(0)
                }
            }
        }

        return true
    }

    /**
     * 调用第三方浏览器打开网址
     *
     * @param context
     * @param url     要浏览的资源地址
     */
    @JvmStatic
    fun jumpBrowser(context: Context, url: String?) {
        val intent = Intent().apply {
            setAction(Intent.ACTION_VIEW)
            setData(Uri.parse(url))
        }
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(Intent.createChooser(intent, "请选择浏览器"))
        } else {
            Toast.makeText(context.applicationContext, "请下载浏览器", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @JvmStatic
    fun initPermissionsOfStorage(context: MainActivity) {
        //存储权限的申请
        if (!VersionTool.isAndroid13()) {
            PermissionsTools.generalPermissionsOfStorage(context
            ) { permissions, all ->
                val dataUseUri = FileTools.underAndroidDataUseUri(ConfigData.getCacheFilePath())
                if (!dataUseUri) {
                    //获取数据刷新列表
                    context.updateCollectionFileList()
                    context.refreshCacheFileList()
                }
            }
        }


        PermissionsTools.specialPermissionsOfStorageWithDialog(
            context,
            true
        ) { permissions, all ->
            val dataUseUri = FileTools.underAndroidDataUseUri(ConfigData.getCacheFilePath())
            if (!dataUseUri) {
                //获取数据刷新列表
                context.updateCollectionFileList()
                context.refreshCacheFileList()
            }
        }
    }

    @JvmStatic
    fun isApkDebug(context: Context): Boolean {
        try {
            val info = context.applicationInfo
            return (info.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * 跳转项目地址
     *
     * @param context
     */
    @JvmStatic
    fun jumpProjectAddress(context: Context) {
        DialogLoader.getInstance().showConfirmDialog(
            context,
            "国内选GITEE国外选GITHUB",
            "GITEE",
            { dialog: DialogInterface?, which: Int ->
                jumpBrowser(context, LConstants.PROJECT_ADDRESS_GITEE)
                dialog!!.dismiss()
            },
            "GITHUB",
            { dialog: DialogInterface?, which: Int ->
                jumpBrowser(context, LConstants.PROJECT_ADDRESS_GITHUB)
                dialog!!.dismiss()
            }
        ).setCanceledOnTouchOutside(true)
    }

    @JvmStatic
    fun jumpProjectIssues(context: Context) {
        DialogLoader.getInstance().showConfirmDialog(
            context,
            "国内选GITEE国外选GITHUB",
            "GITEE",
            { dialog: DialogInterface?, which: Int ->
                jumpBrowser(context, LConstants.PROJECT_ADDRESS_GITEE + "/issues")
                dialog!!.dismiss()
            },
            "GITHUB",
            { dialog: DialogInterface?, which: Int ->
                jumpBrowser(context, LConstants.PROJECT_ADDRESS_GITHUB + "/issues")
                dialog!!.dismiss()
            }
        ).setCanceledOnTouchOutside(true)
    }


}