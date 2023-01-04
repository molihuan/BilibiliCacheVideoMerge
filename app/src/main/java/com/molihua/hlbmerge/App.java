package com.molihua.hlbmerge;

import android.app.Application;

import com.hjq.http.EasyConfig;
import com.molihua.hlbmerge.myhttp.RequestHandler;
import com.tencent.bugly.Bugly;
import com.tencent.mmkv.BuildConfig;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xui.XUI;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import okhttp3.OkHttpClient;

public class App extends Application {


    @Override
    public void onCreate() {
        XUI.init(this);//初始化XUI
        XUI.debug(false);  //开启UI框架调试日志
        RxFFmpegInvoke.getInstance().setDebug(false);//开启/关闭 debug 模式
        String rootDir = MMKV.initialize(this);// 腾讯的键值对存储mmkv初始化
        //LogUtils.e("mmkv root: " + rootDir);
        Bugly.init(getApplicationContext(), "ac467503ed", false);//bugly调试

        //EasyHttp初始化
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        EasyConfig.with(okHttpClient)
                // 是否打印日志
                .setLogEnabled(BuildConfig.DEBUG)
                // 设置服务器配置
                .setServer("https://www.bilibili.com/")
                // 设置请求处理策略
                .setHandler(new RequestHandler(this))
                // 设置请求重试次数
                .setRetryCount(3)
                // 添加全局请求参数
                //.addParam("token", "6666666")
                // 添加全局请求头
                //.addHeader("time", "20191030")
                // 启用配置
                .into();


        super.onCreate();
    }

}
