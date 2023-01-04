package com.molihua.hlbmerge.utils;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;

import java.io.File;

public class FileTools {

    public static final int FLAG_FILENAME_INDEX = 0;//文件重命名第一个索引


    /**
     * 根据视频路径获取缩略图
     * @param filePath
     * @param type
     * @return
     */
    public static Bitmap createVideoThumbnail(String filePath, int type) {
        Bitmap videoThumbnail = ThumbnailUtils.createVideoThumbnail(
                filePath, MediaStore.Video.Thumbnails.MINI_KIND);
        videoThumbnail = ThumbnailUtils.extractThumbnail(videoThumbnail, 512, 288);
        return videoThumbnail;
    }


    /**
     * 遍历删除文件夹下所有文件（除了excludeFileName文件名）
     * @param filePath
     * @param excludeFileName
     */
    public static void deleteTempFile(String filePath,String excludeFileName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                File rootFile = new File(filePath);
                File[] files = rootFile.listFiles();
                for (File file :files){
                    if (file.isDirectory()){
                        deleteTempFile(file.getPath(),excludeFileName);
                    }else {
                        if (!file.getName().equals(excludeFileName)){
                            FileUtils.delete(file);
                        }
                    }
                }
            }
        }).start();
    }


    /**
     * 删除相同的文件
     * @param FilePath
     */
    public static void deleteEqualFile(String FilePath){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (FilePath.matches(".*[(][0-9]+[)][.]mp4$")){//不是重命名的文件就不删除
                    String currentDirAbsolutePath =PathTools.getCurrentDirAbsolutePath(FilePath);//获取文件所在文件夹路径
                    String newFileMD5 = FileUtils.getFileMD5ToString(FilePath);//获取文件MD5值
                    LogUtils.e("deleteEqualFile:"+newFileMD5);
                    File currentDir = new File(currentDirAbsolutePath);
                    File[] files = currentDir.listFiles();//获取目录下的所有文件
                    if (files==null){
                        return;
                    }
                    for (int i = 0; i < files.length; i++) {//遍历

                        //如果文件的路径不等于当前文件并且文件的MD5值等于当前文件
                        if ((! files[i].getAbsolutePath().equals(FilePath))&&FileUtils.getFileMD5ToString(files[i]).equals(newFileMD5)){
                            FileUtils.delete(FilePath);//删除当前文件
                        }
                    }
                }
            }
        }).start();
    }


    /**
     * 递归判断文件是否已经存在
     * @param filePath
     * @param index
     * @return
     */
    public static String produceNoRepeatFilePath(String filePath,int index) {
        if (FileUtils.isFileExists(filePath)){//不存在直接返回
            int last = filePath.lastIndexOf('/');
            String fileParentPath = filePath.substring(0, last + 1);//文件在的文件夹路径
            String filename = FileUtils.getFileNameNoExtension(filePath);//文件名不带扩展名
            String fileExtension = FileUtils.getFileExtension (filePath);//文件扩展名

            if (filename.matches(".*[(][0-9]+[)]$")) {//判断是否已经重命名了
                int lastLeft = filename.lastIndexOf('(');
                filename=filename.substring(0,lastLeft+1)+index+").";
            } else {
                filename=filename + "(" + index + ").";
            }
            String newFilePath = fileParentPath + filename + fileExtension;
            return produceNoRepeatFilePath(newFilePath,index+1);
        }else {
            return filePath;
        }
    }





}
