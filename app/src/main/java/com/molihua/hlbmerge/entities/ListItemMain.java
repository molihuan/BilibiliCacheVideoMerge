package com.molihua.hlbmerge.entities;

import android.view.View;

import com.blankj.utilcode.util.FileUtils;
import com.molihua.hlbmerge.utils.MLHInitConfig;
import com.molihua.hlbmerge.utils.PathTools;
import com.molihua.hlbmerge.utils.StringTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.molihua.hlbmerge.MainActivity.regEx;
/**
 * 准备合并实体类
 */
public class ListItemMain {
    private String name;//p名或合集名
    private String outputDirName;//合集名
    private String path;//路径或合集路径
    private String audioPath;//p下audio路径
    private String videoPath;
    private int checkBoxVisibility;//checkBox是否可见
    private boolean checkBoxCheck;//checkBox是否选中
    private String danmakuXmlPath;//弹幕文件路径
    private List<String> blvPathList = new ArrayList();//存储blv格式的文件路径
    private boolean isExportXml;//是否导出弹幕
    private int ExportType;//导出类型，0有声音视频，1无声音视频，2仅仅音频

    public ListItemMain(String name, String path) {
        this.name = name;
        this.outputDirName="";
        this.path = path;
        this.audioPath = "";
        this.videoPath = "";
        this.checkBoxVisibility = View.GONE;
        this.checkBoxCheck = false;
        this.danmakuXmlPath="";
        this.isExportXml=false;
        this.ExportType= MLHInitConfig.TYPE_EXPORT_VIDEO_AUDIO;
    }

    public ListItemMain(String name, String outputDirName, String path, String audioPath, String videoPath,String danmakuXmlPath,List<String> blvPathList) {
        this.name = name;
        this.outputDirName=outputDirName;
        this.path = path;
        this.audioPath = audioPath;
        this.videoPath = videoPath;
        this.checkBoxVisibility = View.GONE;
        this.checkBoxCheck = false;
        this.danmakuXmlPath=danmakuXmlPath;
        this.blvPathList.addAll(blvPathList);
        this.isExportXml=false;
        this.ExportType= MLHInitConfig.TYPE_EXPORT_VIDEO_AUDIO;
    }

    public int getExportType() {
        return ExportType;
    }

    public void setExportType(int exportType) {
        ExportType = exportType;
    }

    public boolean isExportXml() {
        return isExportXml;
    }

    public void setExportXml(boolean exportXml) {
        isExportXml = exportXml;
    }

    //获取从json中读取到的文件名
    public String getName() {
        return name.replaceAll(regEx, "");
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }
    //获取路径
    public String getParentPath() {
        return new File(path).getParent() ;
    }
    //获取父文件夹的名称
    public String getParentDirName() {
        return FileUtils.getDirName(getParentPath());
    }

    public void setPath(String path) {
        this.path = path;
    }
    //获取p中Audio.m4s路径
    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }
    //获取p中Video.m4s路径
    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
    //获取p中item的checkbox是否显示
    public int getCheckBoxVisibility() {
        return checkBoxVisibility;
    }

    public void setCheckBoxVisibility(int checkBoxVisibility) {
        this.checkBoxVisibility = checkBoxVisibility;
    }
    //获取p中item的checkbox是否勾选
    public boolean isCheckBoxCheck() {
        return checkBoxCheck;
    }

    public void setCheckBoxCheck(boolean checkBoxCheck) {
        this.checkBoxCheck = checkBoxCheck;
    }

    public String getDanmakuXmlPath() {
        return danmakuXmlPath;
    }

    public void setDanmakuXmlPath(String danmakuXmlPath) {
        this.danmakuXmlPath = danmakuXmlPath;
    }

    public List<String> getBlvPathList() {
        return blvPathList;
    }

    public void setBlvPathList(List<String> blvPathList) {
        this.blvPathList = blvPathList;
    }

    /**
     * 总：/storage/emulated/0/bilibili视频合并/交公粮时间到了/好可怕的笑容.mp4
     *
     * 获取:交公粮时间到了
     */
    public String getOutputDirName() {
        return outputDirName.replaceAll(regEx, "");
    }
    /**
     * 总：/storage/emulated/0/bilibili视频合并/交公粮时间到了/好可怕的笑容.mp4
     *
     * 获取:/storage/emulated/0/bilibili视频合并/交公粮时间到了
     */
    public String getFullOutputDirPath() {
        return StringTools.deleteAllSpaceByJudgeACSII(PathTools.getOutputPath()+"/"+ getOutputDirName());
    }
    /**
     * 总：/storage/emulated/0/bilibili视频合并/交公粮时间到了/好可怕的笑容.mp4
     *
     * 获取:/storage/emulated/0/bilibili视频合并/交公粮时间到了/好可怕的笑容
     */
    public String getFullOutputPath() {
        return StringTools.deleteAllSpaceByJudgeACSII(getFullOutputDirPath()+"/"+name);
    }

    public void setOutputDirName(String outputDirName) {
        this.outputDirName = outputDirName;
    }

    @Override
    public String toString() {
        return "ListItemMain{" +
                "name='" + name + '\'' +
                ", outputDirName='" + outputDirName + '\'' +
                ", path='" + path + '\'' +
                ", audioPath='" + audioPath + '\'' +
                ", videoPath='" + videoPath + '\'' +
                '}';
    }
}
