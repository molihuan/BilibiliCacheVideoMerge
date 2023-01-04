package com.molihua.hlbmerge.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.molihua.hlbmerge.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class JsonTools {
    public static final int TYPE_TITLE = 0;//主标题
    public static final int TYPE_SUBTITLE = 1;//副标题


    /**
     * 复制所有的json
     *
     * @param contentResolver
     * @param context
     * @param type
     */
    public static void initJson(ContentResolver contentResolver, Context context, int type) {
        if (VersionTools.isAndroid11AndNull()) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //删除缓存的json文件
                        FileUtils.deleteAllInDir(PathTools.getOutputTempPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    JsonTools.initJsonSynchronous(contentResolver, context, type);
                    ToastUtils.make().show("如果没有显示,请过几秒后再次刷新!!!还是不行清除一下软件数据。");

                }
            }).start();
        }
    }

    /**
     * 同步复制所有的json
     *
     * @param contentResolver
     * @param context
     * @param type
     */
    public static void initJsonSynchronous(ContentResolver contentResolver, Context context, int type) {
        if (VersionTools.isAndroid11AndNull()) {
            String biliDownPath = MLHInitConfig.getBiliDownPath();//读取配置
            if (biliDownPath.matches(".*/tv.danmaku.bilibilihd/download$")) {//国内平板
                UriTools.copyAllPathAndJson(contentResolver, context, Uri.parse(UriTools.URI_HD_DOMESTIC_DOWNLOAD), type);
            } else {
                if (biliDownPath.matches(".*/com.bilibili.app.in/download$")) {//国外版
                    UriTools.copyAllPathAndJson(contentResolver, context, Uri.parse(UriTools.URI_FOREIGN_DOWNLOAD), type);
                } else {//国内版
                    if (biliDownPath.matches(".*/com.bilibili.app.blue/download$")) {
                        UriTools.copyAllPathAndJson(contentResolver, context, Uri.parse(UriTools.URI_BLUE_DOMESTIC_DOWNLOAD), type);
                    } else {
                        UriTools.copyAllPathAndJson(contentResolver, context, Uri.parse(UriTools.URI_DOMESTIC_DOWNLOAD), type);
                    }

                }
            }
        }
    }


    /**
     * 通过json文件读取主副标题,如果读取失败则返回空字符
     *
     * @param jsonFilePath
     * @param nameType
     * @return
     */
    public static String readNameByJson(String jsonFilePath, int nameType) {
        String errorTitle = null;
        try {
            JSONObject jsonObject = readJsonFile(jsonFilePath);//解析entry.json文件
            if (jsonObject == null) {
                return errorTitle;
            }

            String title = null;
            try {
                title = jsonObject.getString("title").replaceAll(MainActivity.regEx, "");//有问题特殊符号
            } catch (JSONException e) {
                title = "";
            }
            switch (nameType) {
                case TYPE_TITLE:
                    //主标题
                    return title;
                case TYPE_SUBTITLE:
                    //副标题
                    //replaceFirst去掉与主标题重复的
                    String download_subtitle = null;
                    JSONObject page_dataJson = jsonObject.getJSONObject("page_data");
                    try {

                        download_subtitle = page_dataJson.getString("download_subtitle").replaceFirst(title, "");

                    } catch (JSONException e1) {

                        try {
                            download_subtitle = page_dataJson.getString("part").replaceFirst(title, "");
                        } catch (JSONException e2) {

                        }

                    }
                    return StringUtils.isSpace(download_subtitle) ? errorTitle : download_subtitle.replaceAll(MainActivity.regEx, "");
                default:
                    return errorTitle;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return errorTitle;
        }
    }

    //解析json文件
    public static JSONObject readJsonFile(String jsonPath) {
        JSONObject jsonObject = null;
        BufferedReader br = null;
        InputStreamReader isr = null;
        try {
            FileInputStream in = new FileInputStream(jsonPath);
            isr = new InputStreamReader(in, "UTF-8");
            if (isr == null) {
                return null;
            }
            br = new BufferedReader(isr);
            if (br == null) {
                return null;
            }
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            jsonObject = new JSONObject(builder.toString());//builder读取了JSON中的数据。
        } catch (Exception e) {

        } finally {
            try {
                if (br == null || isr == null) {
                    return null;
                }
                br.close();
                isr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    /**
     * 解析网络json
     *
     * @param jsonContent
     * @return
     */
    public static Map<String, String> parsWebJson(String jsonContent) {
        JSONObject jsonObject = null;
        Map<String, String> dataMap = new HashMap<>();
        try {

            jsonObject = new JSONObject(jsonContent);
            JSONObject data = jsonObject.getJSONObject("data");
            String pic = data.getString("pic");//封面
            String title = data.getString("title");//标题
            String cid = data.getString("cid");//弹幕
            dataMap.put("pic", pic);
            dataMap.put("title", title);
            dataMap.put("cid", cid);

        } catch (Exception e) {

        }
        return dataMap;
    }
}
