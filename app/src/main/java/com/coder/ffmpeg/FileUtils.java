package com.coder.ffmpeg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    public static boolean fileCopy(String oldFilePath,String newFilePath) {
        //如果原文件不存在
        if(fileExists(oldFilePath) == false){
            return false;
        }
        FileInputStream inputStream = null;
        FileOutputStream outputStream =null;
        try {
            //获得原文件流
            inputStream = new FileInputStream(new File(oldFilePath));
            byte[] data = new byte[1024];
            //输出流
            outputStream =new FileOutputStream(new File(newFilePath));
            //开始处理流
            while (inputStream.read(data) != -1) {
                outputStream.write(data);
            }
        }catch (Exception e) {
            return false;
        }finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }


}
