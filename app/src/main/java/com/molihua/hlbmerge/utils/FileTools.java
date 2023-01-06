package com.molihua.hlbmerge.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.blankj.molihuan.utilcode.util.AppUtils;
import com.blankj.molihuan.utilcode.util.ConvertUtils;
import com.blankj.molihuan.utilcode.util.FileIOUtils;
import com.blankj.molihuan.utilcode.util.FileUtils;
import com.blankj.molihuan.utilcode.util.StringUtils;
import com.molihuan.pathselector.utils.MConstants;
import com.molihuan.pathselector.utils.Mtools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;
import java.util.UUID;

/**
 * @ClassName: FileTools
 * @Author: molihuan
 * @Date: 2022/12/22/20:24
 * @Description:
 */
public class FileTools {

    public static void shareFile(Context context, String filePath) {
        shareFile(context, filePath, null);
    }

    public static void shareFile(Context context, String filePath, String targetPackageName) {
        if (!FileUtils.isFileExists(filePath)) {
            throw new IllegalArgumentException(filePath + " does not exist");
        }
        shareFile(context, new File(filePath), targetPackageName);
    }

    public static void shareFile(Context context, final File file) {
        shareFile(context, file, null);
    }

    /**
     * 分享文件
     *
     * @param context
     * @param file
     * @param targetPackageName 指定目标包名
     */
    public static void shareFile(Context context, final File file, String targetPackageName) {
        final Uri uri;
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        //若SDK大于等于24  获取uri采用共享文件模式
        if (currentapiVersion >= 24) {
            uri = FileProvider.getUriForFile(context, AppUtils.getAppPackageName() + ".fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }

        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        //此处可发送多种文件
        share.setType(getMIMEType(new File(file.getAbsolutePath())));
        share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.addCategory(Intent.CATEGORY_DEFAULT);
        //是否指定目标包名
        if (!StringUtils.isTrimEmpty(targetPackageName)) {
            //share.setPackage("com.tencent.tim");
            //share.setPackage("com.tencent.mobileqq");
            //share.setPackage("com.tencent.mm");
        }

        if (share.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(share, "将文件发送到"));
            //context.startActivity(share);
        } else {
            Mtools.toast("没有相应的应用,无法分享!");
        }
    }

    /**
     * 获取MIMEType
     *
     * @param file
     * @return
     */
    public static String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0)
            return type;
        /* 获取文件的后缀名 */
        String fileType = fName.substring(dotIndex).toLowerCase();
        if ("".equals(fileType))
            return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (Map.Entry<String, String> entry : MConstants.mimeTypeMap.entrySet()) {
            if (fileType.equals(entry.getKey())) {
                type = entry.getValue();
            }
        }

        return type;
    }


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

        return file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return true;
                }
                return false;
            }
        });
    }

    public static String[] getCollectionChapterName(byte[] jsonByte, String[] result) {
        //把jsonByte转换成json字符串
        String jsonStr = ConvertUtils.bytes2String(jsonByte);
        return getCollectionChapterNameByJsonStr(jsonStr, result);
    }

    /**
     * 获取合集和章节名称
     *
     * @param jsonPath json文件路径
     * @param result
     * @return result[0]合集名称
     * result[1]章节名称
     */
    public static String[] getCollectionChapterName(String jsonPath, String[] result) {
        //把json文件转换成json字符串
        String jsonStr = FileIOUtils.readFile2String(jsonPath, "UTF-8");
        return getCollectionChapterNameByJsonStr(jsonStr, result);
    }

    /**
     * 通过json字符串解析名称
     *
     * @param jsonStr json字符串
     * @param result
     * @return
     */
    private static String[] getCollectionChapterNameByJsonStr(String jsonStr, String[] result) {

        if (StringUtils.isTrimEmpty(jsonStr)) {
            return null;
        }

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

        //二级json类型
        int subJsonType;

        //获取二级json对象
        JSONObject subJsonObject = null;
        try {
            subJsonObject = jsonObject.getJSONObject("page_data");
            subJsonType = 0;
        } catch (JSONException e) {
            e.printStackTrace();
            //如果没有获取到page_data就开始获取ep
            try {
                subJsonObject = jsonObject.getJSONObject("ep");
                subJsonType = 1;
            } catch (JSONException ex) {
                ex.printStackTrace();
                //都没有获取到就随机uuid
                result[1] = UUID.randomUUID().toString();
                return result;
            }
        }

        String parseKey;
        switch (subJsonType) {
            case 1:
                parseKey = "index_title";
                break;
            case 0:
            default:
                parseKey = "download_subtitle";

        }

        //通过二级json对象获取章节名称
        try {
            result[1] = subJsonObject
                    .getString(parseKey)
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
