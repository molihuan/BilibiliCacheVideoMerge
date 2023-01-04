package com.molihua.hlbmerge.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import com.blankj.utilcode.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UriTools {
    public static final String URI_ROOT="content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary";//根目录
    public static final String URI_ANRROID_DATA="content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fdata";//Android/data目录
    public static final String URI_DOMESTIC_DOWNLOAD="content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fdata%2Ftv.danmaku.bili%2Fdownload";//Android/data/tv.danmaku.bili/download目录
    public static final String URI_FOREIGN_DOWNLOAD="content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fdata%2Fcom.bilibili.app.in%2Fdownload";//Android/data/com.bilibili.app.in/download目录
    public static final String URI_HD_DOMESTIC_DOWNLOAD="content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fdata%2Ftv.danmaku.bilibilihd%2Fdownload";//tv.danmaku.bilibilihd/download目录
    public static final String URI_BLUE_DOMESTIC_DOWNLOAD="content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fdata%2Fcom.bilibili.app.blue%2Fdownload";//com.bilibili.app.blue/download目录
    public static final String URI_SEPARATOR="%2F";//路径分割符

    /**
     * 根据uri转换为路径（只针对/storage/emulated/0/Android/data）
     * @param fileUri path对应的uri: /storage/emulated/0/Android/data/tv.danmaku.bili/download/99999/1/entry.json
     * @return  /tv.danmaku.bili/download/99999/1/entry.json
     */
    public static String uri2Path(Uri fileUri){
        return fileUri.toString().replace(URI_ANRROID_DATA,"").replace(URI_SEPARATOR,"/");
    }

    /**
     * 根据路径转换为uri（只针对/storage/emulated/0/bilibili视频合并/temp）
     * @param filePath  /storage/emulated/0/bilibili视频合并/temp/tv.danmaku.bili/download/99999/1
     * @return  /storage/emulated/0/Android/data/tv.danmaku.bili/download/99999/1对应的uri
     */
    public static Uri path2Uri(String filePath){
        String s = URI_ANRROID_DATA + filePath.replace(PathTools.getOutputTempPath(), "").replace("/", URI_SEPARATOR);
        return Uri.parse(s);
    }


    /**
     * 通过uri复制所有文件或json文件
     * @param contentResolver
     * @param context
     * @param downloadUri
     * @param type
     */
    public static void copyAllPathAndJson(ContentResolver contentResolver, Context context, Uri downloadUri, int type){
        if (context == null || downloadUri == null) return;
        DocumentFile rootDocumentFile = DocumentFile.fromSingleUri(context, downloadUri);//通过URI创建DocumentFile对象
        if (rootDocumentFile == null) return;
        DocumentFile pickedDir = rootDocumentFile.fromTreeUri(context, downloadUri);//创建一个 DocumentFile表示以给定的 Uri根的文档树。其实就是获取子目录的权限
        for (DocumentFile i : pickedDir.listFiles()) {//遍历
            //LogUtils.e(i.getUri());
            if (i.isDirectory()) {
                copyAllPathAndJson(contentResolver,context, i.getUri(), type);//递归
            } else {
                switch (type) {
                    case 0://复制json文件
                        if (i.getName().equals("entry.json")) {
                            String targetPath=PathTools.getOutputTempPath()+uri2Path(i.getUri());//获取生成临时json全路径
                            if (FileUtils.isFileExists(targetPath)){
                                break;
                            }
                            String currentDirAbsolutePath = PathTools.getCurrentDirAbsolutePath(targetPath);//获取json当前文件夹路径
                            FileUtils.createOrExistsDir(currentDirAbsolutePath);//创建文件夹
                            copyFileByUri(contentResolver,i.getUri(),targetPath,"");//根据全路径复制json文件到temp目录
                        }
                        break;
                    case 1://复制所有文件除了json
                        if (!i.getName().equals("entry.json")) {
                            String targetPath=PathTools.getOutputTempPath()+uri2Path(i.getUri());//获取生成临时json全路径
                            if (FileUtils.isFileExists(targetPath)){
                                break;
                            }
                            String currentDirAbsolutePath = PathTools.getCurrentDirAbsolutePath(targetPath);//获取json当前文件夹路径
                            FileUtils.createOrExistsDir(currentDirAbsolutePath);//创建文件夹
                            copyFileByUri(contentResolver,i.getUri(),targetPath,"");//根据全路径复制json文件到temp目录
                        }
                        break;
                }

            }
        }
    }


    /**
     * 复制data里面的文件到指定目录targetPath--------uri为文件的Uri，targetPath为指定目录的全路径
     * @param contentResolver
     * @param sourceUri
     * @param targetPath
     * @param fileName
     * @return
     */
    private static boolean copyFileByUri(ContentResolver contentResolver, Uri sourceUri, String targetPath, String fileName) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = contentResolver.openInputStream(sourceUri);//读取源文件转换为输入流
            String allPath;
            if (fileName.equals("")){
                allPath=targetPath;//此时targetPath文件是全路径
            }else {
                allPath=targetPath + "/" + fileName;//此时targetPath是文件当前路径
            }
            File destFile = new File(allPath);
            out = new FileOutputStream(destFile);//目的路径文件转换为输出流

            if (out==null){
                return false;
            }

            byte[] flush = new byte[1024];
            int len = -1;
            while ((len = in.read(flush)) != -1) {//边读边写
                out.write(flush, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in==null||out==null){
                    return false;
                }
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }









}
