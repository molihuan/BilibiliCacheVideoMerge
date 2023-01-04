package com.coder.ffmpeg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class txtTool {
    /**
     * 读TXT文件
     */
    // txt文件全路径
    public static List<String> readFile(String txtPath) {
        List<String> txtRedContent = new ArrayList();//存放读取到的信息

        //防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
        //不关闭文件会导致资源的泄露，读写文件都同理
        //Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；详细解读https://stackoverflow.com/a/12665271
        try (FileReader reader = new FileReader(txtPath);
             BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
        ) {
            String line;
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据存入集合中
                txtRedContent.add(line);
                //System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return txtRedContent;
    }

    /**
     * 写入TXT文件
     */
    // txt文件全路径
    public static void writeFile(String txtPath, String txtContent) {
        File writeName = new File(txtPath);
        BufferedWriter out = null;
        try {
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            FileWriter writer = new FileWriter(writeName);
            out = new BufferedWriter(writer);
            out.write(txtContent); // \r\n即为换行
            out.flush(); // 把缓存区内容压入文件
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();//关闭流
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
