package com.molihua.hlbmerge.utils;

import com.blankj.molihuan.utilcode.util.FileIOUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.UUID;

/**
 * @ClassName: FileTools
 * @Author: molihuan
 * @Date: 2022/12/22/20:24
 * @Description:
 */
public class FileTools {
    /**
     * 获取所有的合集路径、获取一个合集下的所有章节路径
     *
     * @return 所有的合集路径、个合集下的所有章节路径即：allCollectionPath或allChapterPath
     */
    public static String[] getCollectionChapterPath(String path) {
        File[] collectionFile = getCollectionChapterFile(path);
        String[] result = new String[collectionFile.length];
        for (int i = 0; i < collectionFile.length; i++) {
            result[i] = collectionFile[i].getAbsolutePath();
        }
        return result;
    }

    public static File[] getCollectionChapterFile(String path) {
        File file = new File(path);
        return file.listFiles();
    }


    /**
     * 获取合集和章节名称
     *
     * @param jsonPath
     * @param result
     * @return result[0]合集名称
     * result[1]章节名称
     */
    public static String[] getCollectionChapterName(String jsonPath, String[] result) {
        //把json文件转换成json字符串
        String jsonStr = FileIOUtils.readFile2String(jsonPath, "UTF-8");
        JSONObject jsonObject;
        //将json字符串转换成json对象
        try {
            jsonObject = new JSONObject(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
            result[0] = UUID.randomUUID().toString();
            result[1] = UUID.randomUUID().toString();
            return result;
        }

        //获取合集名称
        try {
            result[0] = jsonObject
                    .getString("title")
                    .replaceAll(LConstants.SPECIAL_CHARACTERS_REGULAR_RULE, "");
        } catch (JSONException e) {
            e.printStackTrace();
            result[0] = UUID.randomUUID().toString();
        }

        //获取二级json对象
        JSONObject subJsonObject = null;
        try {
            subJsonObject = jsonObject.getJSONObject("page_data");
        } catch (JSONException e) {
            e.printStackTrace();
            result[1] = UUID.randomUUID().toString();
            return result;
        }

        //通过二级json对象获取章节名称
        try {
            result[1] = subJsonObject
                    .getString("download_subtitle")
                    .replaceAll(LConstants.SPECIAL_CHARACTERS_REGULAR_RULE, "")
                    .replaceFirst(result[0], "");
        } catch (JSONException e1) {
            e1.printStackTrace();
            try {
                result[1] = subJsonObject
                        .getString("part")
                        .replaceAll(LConstants.SPECIAL_CHARACTERS_REGULAR_RULE, "")
                        .replaceFirst(result[0], "");
            } catch (JSONException e2) {
                e2.printStackTrace();
                result[1] = UUID.randomUUID().toString();
            }
        }

        return result;
    }

    /**
     * 通过章节路径获取需要的路径
     *
     * @param chapterPath
     * @param result
     * @return result[0]:audio.m4s
     * result[1]:video.m4s
     * result[2]:entry.json
     * result[3]:danmaku.xml
     */
    public static String[] getNeedPath(String chapterPath, String[] result) {
        File file = new File(chapterPath);
        return getNeedPath(file, result);
    }

    public static String[] getNeedPath(File chapterFile, String[] result) {
        File[] files = chapterFile.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    getNeedPath(files[i], result);
                } else {
                    switch (files[i].getName()) {
                        case "audio.m4s":
                            result[0] = files[i].getAbsolutePath();
                            break;
                        case "video.m4s":
                            result[1] = files[i].getAbsolutePath();
                            break;
                        case "entry.json":
                            result[2] = files[i].getAbsolutePath();
                            break;
                        case "danmaku.xml":
                            result[3] = files[i].getAbsolutePath();
                            break;
                    }
                }
            }
        }
        return result;
    }

}
