package com.molihua.hlbmerge.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import com.blankj.molihuan.utilcode.util.AppUtils;
import com.blankj.molihuan.utilcode.util.ConvertUtils;
import com.blankj.molihuan.utilcode.util.FileIOUtils;
import com.blankj.molihuan.utilcode.util.FileUtils;
import com.blankj.molihuan.utilcode.util.StringUtils;
import com.molihua.hlbmerge.entity.CacheSrc;
import com.molihuan.pathselector.utils.MConstants;
import com.molihuan.pathselector.utils.Mtools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.microshow.rxffmpeg.RxFFmpegInvoke;

/**
 * @ClassName: FileTools
 * @Author: molihuan
 * @Date: 2022/12/22/20:24
 * @Description:
 */
public class FileTool {
    /**
     * 获取上一级名称
     *
     * @param path
     * @return
     */
    public static String getParentName(String path) {

        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1);
        }

        // 拆分字符串获取路径元素
        String[] elements = path.split(File.separator);

        // 获取最后一个路径元素作为父目录名
        String parentName = elements[elements.length - 2];
        return parentName;
    }

    /**
     * 通过全路径获取名称
     *
     * @param path
     * @return
     */
    public static String getName(String path) {

        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1);
        }

        // 拆分字符串获取路径元素
        String[] elements = path.split(File.separator);

        // 获取最后一个路径元素作为父目录名
        String name = elements[elements.length - 1];
        return name;
    }

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
        Intent share = new Intent(Intent.ACTION_SEND);
        //若SDK大于等于24  获取uri采用共享文件模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, AppUtils.getAppPackageName() + ".fileprovider", file);
            share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }

        share.putExtra(Intent.EXTRA_STREAM, uri);
        //此处可发送多种文件
        share.setType(getMimeType(file));

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
     * 获取MimeType
     *
     * @param file
     * @return
     */
    public static String getMimeType(File file) {
        String type = "*/*";
        /* 获取文件的后缀名 */
        String fileType = FileUtils.getFileExtension(file);
        if ("".equals(fileType))
            return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (Map.Entry<String, String> entry : MConstants.mimeTypeMap.entrySet()) {
            if (fileType.equals(entry.getKey())) {
                type = entry.getValue();
                break;
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

    /**
     * 用于uri文件
     *
     * @param jsonByte
     * @param result
     * @return result[0]合集名称
     * result[1]章节名称
     * result[2]封面图片地址(无法解析则为null)
     */
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
     * result[2]封面图片地址(无法解析则为null)
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
    public static String[] getCollectionChapterNameByJsonStr(String jsonStr, String[] result) {

        JSONObject jsonObject;
        //将json字符串转换成json对象
        try {
            jsonObject = new JSONObject(jsonStr);
        } catch (JSONException e) {
            Mtools.log("无法将文件转换为json");
            result[0] = UUID.randomUUID().toString();
            result[1] = UUID.randomUUID().toString();
            return result;
        }
        //解析封面图片
        try {
            result[2] = jsonObject
                    .getString("cover");
        } catch (JSONException e) {
            Mtools.log("无法从json中解析封面地址");
            //e.printStackTrace();
        }

        //解析bv号
        try {
            result[3] = jsonObject
                    .getString("bvid");
        } catch (JSONException e) {
            Mtools.log("无法从json中解析bvid");
            //e.printStackTrace();
        }
        if (result[3] == null || result[3].trim().length() <= 1) {
            try {
                result[3] = "av" + jsonObject
                        .getString("avid");
            } catch (JSONException e) {
                Mtools.log("无法从json中解析bvid");
                //e.printStackTrace();
            }
        }


        //获取合集名称
        try {
            result[0] = jsonObject
                    .getString("title")
                    .replaceAll(LConstants.SPECIAL_CHARACTERS_REGULAR_RULE, "");
        } catch (JSONException e) {
            //e.printStackTrace();
            Mtools.log("无法从json中获取title字段");
            result[0] = UUID.randomUUID().toString();
        }

        //二级json类型
        int subJsonType;

        //获取二级json对象
        JSONObject subJsonObject;
        try {
            subJsonObject = jsonObject.getJSONObject("page_data");
            subJsonType = 0;
        } catch (JSONException e) {
            Mtools.log("无法从json中获取page_data字段");
            //如果没有获取到page_data就开始获取ep
            try {
                subJsonObject = jsonObject.getJSONObject("ep");
                subJsonType = 1;
            } catch (JSONException ex) {
                Mtools.log("无法从json中获取ep字段");
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

        //获取当前章节索引(1、2、3、4相等为第几集)如果是1可能只有一集章节名就用合集名代替
        int pageIndex = 0;
        try {
            pageIndex = subJsonObject.getInt("page");
        } catch (JSONException e) {
            Mtools.log("无法从json中获取page字段");
        }

        if (pageIndex == 1) {
            result[1] = result[0];
            return result;
        }

        //通过二级json对象获取章节名称
        try {
            result[1] = subJsonObject
                    .getString(parseKey)
                    .replaceAll(LConstants.SPECIAL_CHARACTERS_REGULAR_RULE, "")
                    .replaceFirst(result[0], "");
        } catch (JSONException e1) {
            Mtools.log("无法从json中获取" + parseKey + "字段");
            try {
                result[1] = subJsonObject
                        .getString("part")
                        .replaceAll(LConstants.SPECIAL_CHARACTERS_REGULAR_RULE, "")
                        .replaceFirst(result[0], "");
            } catch (JSONException e2) {
                Mtools.log("无法从json中获取part字段");
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
    public static CacheSrc getNeedPath(String chapterPath, CacheSrc result) {
        File file = new File(chapterPath);
        return getNeedPath(file, result);
    }

    public static CacheSrc getNeedPath(File chapterFile, CacheSrc result) {
        File[] files = chapterFile.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    getNeedPath(files[i], result);
                } else {
                    switch (files[i].getName()) {
                        case "audio.m4s":
                            result.setAudio(files[i].getAbsolutePath());

                            break;
                        case "video.m4s":
                            result.setVideo(files[i].getAbsolutePath());
                            break;
                        case "entry.json":
                            result.setJson(files[i].getAbsolutePath());
                            break;
                        case "danmaku.xml":
                            result.setDanmaku(files[i].getAbsolutePath());
                            break;
                    }
                }
            }
        }
        return result;
    }

    public static String needSrcErrorHandle(CacheSrc src, String preFoldName) {
        StringBuilder builder = new StringBuilder();

        if (src.getAudio() == null) {
            builder.append("audio.m4s,");
        }
        if (src.getVideo() == null) {
            builder.append("video.m4s,");
        }
        if (src.getJson() == null) {
            builder.append("entry.json,");
        }
        if (src.getDanmaku() == null) {
//            builder.append("danmaku.xml,");
        }

        if (builder.length() == 0) {
            return null;
        }

        builder.insert(0, preFoldName + "下");
        builder.append("没找到");
        return builder.toString();
    }

    /**
     * 获取视频里的Metadata中的Title
     *
     * @param videoPath
     * @return
     */
    public static String getVedioMetadataTitle(String videoPath) {
        String mediaInfo = RxFFmpegInvoke.getInstance().getMediaInfo(videoPath);

        String pattern = "title\\s*=\\s*'([A-Za-z0-9]+)';";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(mediaInfo);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

}
