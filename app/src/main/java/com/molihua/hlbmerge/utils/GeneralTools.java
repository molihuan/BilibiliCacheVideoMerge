package com.molihua.hlbmerge.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;


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

    /**
     * 检查更新
     */
    public static void checkUpdata() {
        
    }

}
