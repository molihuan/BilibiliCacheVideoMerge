package com.coder.ffmpeg;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ergodicDirBackPath {
    public static String inputxml = "";//目标弹幕文件全路径danmaku.xml
    public static String inputVideo = "";//目标视频文件全路径video.m4s
    public static String inputAudio = "";//目标音频文件全路径audio.m4s
    public static List<String> style_blv = new ArrayList();//存储blv格式的文件路径
    public static List<json_path> json_pathList = new ArrayList();//存储blv格式的文件路径

    //全路径
    public static void ergodicDirBackPathAboutm4s(String ergodicPath) {
        File f1 = new File(ergodicPath);
        File[] file_list = f1.listFiles();
        if (file_list != null) {
            for (File i : file_list) {
                if (i.isDirectory()) {
                    ergodicDirBackPathAboutm4s(i.getPath());
                } else {
                    switch (i.getName()) {
                        case "danmaku.xml":
                            inputxml = i.getPath();
                            break;
                        case "video.m4s":
                            inputVideo = i.getPath();
                            break;
                        case "audio.m4s":
                            inputAudio = i.getPath();
                            break;
                    }
                    if (i.getName().matches(".*.blv$")) {
                        style_blv.add(i.getPath());
                    }
                }
            }
        }
    }


    //全路径/storage/emulated/0/Android/data/tv.danmaku.bili/download
    //或者/storage/emulated/0/bilibili视频合并/temp/download
    //大致地读取json--------每一个总标题只读一个P里的json用于显示总标题
    public static List<json_path> roughlyReadJson(String ergodicPath, Context context) {
        File f1 = new File(ergodicPath);
        File[] file_list = f1.listFiles();///storage/emulated/0/Android/data/tv.danmaku.bili/download/1111、2222、3333[]
        try {
            if (file_list != null) {
                for (File i : file_list) {
                    File[] file_list2 = i.listFiles();///storage/emulated/0/Android/data/tv.danmaku.bili/download/1111/11、22、33[]
                    if (file_list2 != null) {
                        for (File n : file_list2) {

                            json_pathList.add(new json_path(readjson(n),i.getPath()));
                            break;
                        }
                    }else {
                        Toast.makeText(context, "当前选定的目录无bilibili缓存文件,ergodicDirBackPath错误:初步读取json文件失败,集合file_list2为空。", Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, "出错最终解决方案:请看使用教程", Toast.LENGTH_SHORT).show();
                        return null;
                    }
                }

            }else {
                Toast.makeText(context, "当前选定的目录无bilibili缓存文件,ergodicDirBackPath错误:初步读取json文件失败,集合file_list为空。", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "出错最终解决方案:请看使用教程", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "roughlyReadJson错误："+e.toString(), Toast.LENGTH_LONG).show();
            Toast.makeText(context, "出错最终解决方案:请看使用教程", Toast.LENGTH_SHORT).show();
        }
        return json_pathList;
    }

    //读取/storage/emulated/0/Android/data/tv.danmaku.bili/download/1111/11下的json文件
    public static JSONObject readjson(File b) {
        JSONObject testjson = null;
        try {
            FileInputStream in = new FileInputStream(b + "/entry.json");
            InputStreamReader isr = new InputStreamReader(in, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            isr.close();
            testjson = new JSONObject(builder.toString());//builder读取了JSON中的数据。
        } catch (Exception e) {
            Log.e("readjson","--------  "+e.toString()+"   ---------");
        }
        return testjson;
    }


    //Json辅助方法
///storage/emulated/0/Android/data/tv.danmaku.bili/download/11
    public static List<json_path> getJsonFZ(String path) {

        File file=new File(path);
        File[] file_list2 = file.listFiles();///storage/emulated/0/Android/data/tv.danmaku.bili/download/1111/11、22、23、44[]
        if (file_list2 != null) {
            for (File i : file_list2) {
                if (i.isDirectory()) {
                    getJsonFZ(i.getPath());//遍历///storage/emulated/0/Android/data/tv.danmaku.bili/download/1111下的所有JSON文件
                } else {
                    switch (i.getName()) {
                        case "entry.json":
                            json_pathList.add(new json_path(readjson(i.getParentFile()),i.getParent()));
                            break;
                    }
                }
            }

        }

        return json_pathList;
    }


    public static void clearPath() {
        inputxml = "";
        inputVideo = "";
        inputAudio = "";
        style_blv.clear();
        json_pathList.clear();
    }

}
