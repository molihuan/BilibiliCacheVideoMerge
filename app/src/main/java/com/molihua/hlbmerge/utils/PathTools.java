package com.molihua.hlbmerge.utils;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.molihua.hlbmerge.entities.ListItemMain;
import com.molihua.hlbmerge.entities.VideoListItem;
import com.tencent.mmkv.MMKV;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PathTools {
    private static String externalStoragePath = PathUtils.getExternalStoragePath();//获取外部存储根目录


    private static String audioPath;
    private static String videoPath;
    private static String entryJsonPath;
    private static String danmakuXmlPath;
    private static List<String> blvPathList = new ArrayList<String>();//存储blv格式的文件路径
    //获取/Android/data目录
    public static String getADPath(){
       return externalStoragePath+"/Android/data";
    }
    //输出文件夹
    public static String getOutputPath(){
        MMKV kv = MLHInitConfig.getKv();
        if (!kv.containsKey("userCustomCompletePath")){
            return externalStoragePath+"/bilibili视频合并";
        }

        return MLHInitConfig.getUserCustomCompletePath();
    }
    //合并临时文件夹
    public static String getOutputTempPath(){return externalStoragePath+"/bilibili视频合并/temp";}

    public static String getAudioPath() {
        return audioPath;
    }

    public static String getVideoPath() {
        return videoPath;
    }

    public static String getDanmakuXmlPath() {
        return danmakuXmlPath;
    }

    public static List<String> getBlvPathList() {
        return blvPathList;
    }

    /**
     * 初始化创建一些文件夹
     */
    public static void initCreateDir(){
        //安卓11才执行
        if (VersionTools.isAndroid11()){
            boolean isExistsDir = FileUtils.createOrExistsDir(getOutputTempPath());//创建bilibili视频合并/temp目录
            if (!isExistsDir){
                ToastUtils.make().show("bilibili视频合并/temp文件夹初始化失败,请赋予权限后重试!!!");
            }
        }
    }

    /**
     * 根据文件全路径（字符串）获取当前路径（字符串）（文件所在文件夹路径）有/
     * @param FilePath
     * @return
     */
    public static String getCurrentDirAbsolutePath(String FilePath){
        int last = FilePath.lastIndexOf('/');
        return FilePath.substring(0,last+1);
    }

    /**
     * 返回文件绝对地址不带后缀
     * @param filePath 文件全路径
     * @return
     */

    public static String getFileAbsolutePathNoExtension(String filePath){
        return getCurrentDirAbsolutePath(filePath) + FileUtils.getFileNameNoExtension(filePath);
    }

    /**
     * 获取download目录
     *
     * @return
     */
    public static String getBiliDownPath(){

        return MLHInitConfig.isNullUserCustomPath()?MLHInitConfig.getBiliDownPath():MLHInitConfig.getUserCustomPath();

    }

    /**
     * 获取所有下载合集的路径
     * @param listItemMains
     * @param type
     * @return
     */
    public static List<ListItemMain> getCollectionPaths(List<ListItemMain> listItemMains, int type){
        if (listItemMains!=null){
            listItemMains.clear();//清除数据
        }
        File file = new File(getBiliDownPath());//获取download文件
        File[] files = file.listFiles();

        if (files!=null){
            for (int i = 0; i < files.length; i++) {
                String firstChapterPath = getFirstChapterPath(files[i].getPath());//获取第一个p的路径
                String jsonPath = getJsonPathByChapter(firstChapterPath);//获取p中的json文件路径
                String name = JsonTools.readNameByJson(jsonPath, JsonTools.TYPE_TITLE);//从json文件中获取title出错则返回""

                if (StringUtils.isSpace(name)){//为空则说明读取json文件失败
                    name=files[i].getName();//获取文件夹名称
                }

                listItemMains.add(new ListItemMain(name,files[i].getPath()));//
                clearAudioVideoPath();//清除当前类静态数据
            }
        }
        return listItemMains;
    }
    /**
     * 获取合集下所有p的路径
     * @param listItemMains
     * @param CollectionPath
     * @return
     */
    public static List<ListItemMain> getChapterPaths(List<ListItemMain> listItemMains, String CollectionPath){
        //LogUtils.e("需要遍历合集的路径为："+CollectionPath);
        listItemMains.clear();//清除数据

        File file = new File(CollectionPath);
        File[] files = file.listFiles();
        if (files!=null){
            for (int i = 0; i < files.length; i++) {
                getAudioVideoJsonPath(files[i].getPath());
                String name = JsonTools.readNameByJson(entryJsonPath, JsonTools.TYPE_SUBTITLE);
                String outputDirName = JsonTools.readNameByJson(entryJsonPath, JsonTools.TYPE_TITLE);

                if (StringUtils.isSpace(name)){//为空则说明读取json文件失败
                    name=files[i].getName();//获取文件夹名称
                }
                if (StringUtils.isSpace(outputDirName)){
                    outputDirName=files[i].getName();//获取文件夹名称
                }

                if (i==0){//添加返回上一级按钮
                    listItemMains.add(new ListItemMain("返回上一级",files[i].getPath()));
                }

                listItemMains.add(new ListItemMain(name,outputDirName,files[i].getPath(),audioPath,videoPath,danmakuXmlPath,blvPathList));
                clearAudioVideoPath();//清除当前类静态数据
            }
        }
        return listItemMains;
    }


    /**
     * 获取合集中第一个p的路径
     * @param path  合集路径
     * @return  p的路径
     */
    public static String getFirstChapterPath(String path){
        File file = new File(path);
        File[] files = file.listFiles();
        if (files==null||files.length==0)return "错误";
        return files[0].getPath();
    }

    /**
     * 获取p中的json文件路径
     * @param chapterPath
     * @return
     */
    public static String getJsonPathByChapter(String chapterPath){
        File file = new File(chapterPath);
        File[] files = file.listFiles();
        if (files!=null){
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().equals("entry.json")) {
                    //LogUtils.e(files[i].getAbsolutePath());
                    return files[i].getAbsolutePath();
                }
            }
        }

        return "";//没有获取到则返回空
    }


    /**
     * 获取一p中audio.m4s、video.m4s等路径
     * @param chapterPath
     */
    public static void getAudioVideoJsonPath(String chapterPath){
        File file = new File(chapterPath);
        File[] files = file.listFiles();
        if (files!=null){
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()){
                    getAudioVideoJsonPath(files[i].getPath());
                }else {
                    switch (files[i].getName()){
                        case "audio.m4s":
                            audioPath=files[i].getAbsolutePath();
                            break;
                        case "video.m4s":
                            videoPath=files[i].getAbsolutePath();
                            break;
                        case "entry.json":
                            entryJsonPath=files[i].getAbsolutePath();
                            break;
                        case "danmaku.xml":
                            danmakuXmlPath=files[i].getAbsolutePath();
                            break;
                    }
                    if (files[i].getName().matches(".*.blv$")) {
                        blvPathList.add(files[i].getAbsolutePath());
                    }
                }
            }
        }
    }


    public static void traverseVideoFile(List<VideoListItem> videoListItems, String path){

        File rootFile = new File(path);
        File[] files = rootFile.listFiles();
        if (files==null)return;
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()){
                if (file.getName().equals("temp"))continue;
                traverseVideoFile(videoListItems,file.getPath());
            }else {
                if (file.getName().matches(".*[.]mp4$")){
                    videoListItems.add(new VideoListItem(file.getAbsolutePath(),file.getName(),"1:00"));
                }
            }
        }

    }


    public static void clearAudioVideoPath(){
        audioPath="";
        videoPath="";
        entryJsonPath="";
        danmakuXmlPath="";
        blvPathList.clear();
    }

     public static boolean createOutputPath(){
         String outputPath = getOutputPath();
         boolean orExistsDir = FileUtils.createOrExistsDir(outputPath);
         LogUtils.e("创建"+outputPath+"---->"+orExistsDir);
         return orExistsDir;
     }






}
