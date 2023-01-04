package com.molihua.hlbmerge.utils;

import android.os.Build;

import com.tencent.bugly.beta.Beta;

public class VersionTools {
    /**
     * 判断是否是安卓11以及以上
     */
    public static boolean isAndroid11 (){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ? true :false;
    }

    public static boolean isAndroid11AndNull (){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R&&MLHInitConfig.isNullUserCustomPath() ? true :false;
    }

    /**
     * 检查更新
     */
    public static void checkUpdata() {
        Beta.checkUpgrade();//检查更新
    }
}
