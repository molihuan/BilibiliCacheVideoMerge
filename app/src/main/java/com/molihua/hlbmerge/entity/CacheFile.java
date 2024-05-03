package com.molihua.hlbmerge.entity;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: CacheFile
 * @Author: molihuan
 * @Date: 2022/12/21/17:13
 * @Description:
 */
public class CacheFile implements Serializable, Cloneable {
    //如果是合集则为0，如果是章节则是1
    private Integer flag;
    //整体是否可见
    private Integer wholeVisibility;

    //合集路径
    private String collectionPath;
    //合集名
    private String collectionName;
    ///////////////////////////////////////////////////////////////////
    //章节路径
    private String chapterPath;
    //章节名
    private String chapterName;
    //章节下audio路径
    private String audioPath;
    //章节下video路径
    private String videoPath;
    //章节下json路径
    private String jsonPath;
    //弹幕文件路径
    private String danmakuPath;
    //checkBox是否可见
    private Integer boxVisibility;
    //checkBox是否选中
    private Boolean boxCheck;
    //存储blv格式的文件路径
    private List<String> blvPathList;
    //是否使用uri地址
    private Boolean useUri;
    //document
    private DocumentFile documentFile;
    //图片封面地址
    private String coverUrl;
    //bvid
    private String bvId;


    public CacheFile() {
    }

    public Integer getFlag() {
        return flag;
    }

    public CacheFile setFlag(Integer flag) {
        this.flag = flag;
        return this;
    }

    public Integer getWholeVisibility() {
        return wholeVisibility;
    }

    public CacheFile setWholeVisibility(Integer wholeVisibility) {
        this.wholeVisibility = wholeVisibility;
        return this;
    }

    public String getCollectionPath() {
        return collectionPath;
    }

    public CacheFile setCollectionPath(String collectionPath) {
        this.collectionPath = collectionPath;
        return this;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public CacheFile setCollectionName(String collectionName) {
        this.collectionName = collectionName;
        return this;
    }

    public String getChapterPath() {
        return chapterPath;
    }

    public CacheFile setChapterPath(String chapterPath) {
        this.chapterPath = chapterPath;
        return this;
    }

    public String getChapterName() {
        return chapterName;
    }

    public CacheFile setChapterName(String chapterName) {
        this.chapterName = chapterName;
        return this;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public CacheFile setAudioPath(String audioPath) {
        this.audioPath = audioPath;
        return this;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public CacheFile setVideoPath(String videoPath) {
        this.videoPath = videoPath;
        return this;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public CacheFile setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
        return this;
    }

    public Integer getBoxVisibility() {
        return boxVisibility;
    }

    public CacheFile setBoxVisibility(Integer boxVisibility) {
        this.boxVisibility = boxVisibility;
        return this;
    }

    public Boolean getBoxCheck() {
        return boxCheck;
    }

    public CacheFile setBoxCheck(Boolean boxCheck) {
        this.boxCheck = boxCheck;
        return this;
    }

    public String getDanmakuPath() {
        return danmakuPath;
    }

    public CacheFile setDanmakuPath(String danmakuPath) {
        this.danmakuPath = danmakuPath;
        return this;
    }

    public List<String> getBlvPathList() {
        return blvPathList;
    }

    public CacheFile setBlvPathList(List<String> blvPathList) {
        this.blvPathList = blvPathList;
        return this;
    }

    public Boolean getUseUri() {
        return useUri;
    }

    public CacheFile setUseUri(Boolean useUri) {
        this.useUri = useUri;
        return this;
    }

    public DocumentFile getDocumentFile() {
        return documentFile;
    }

    public CacheFile setDocumentFile(DocumentFile documentFile) {
        this.documentFile = documentFile;
        return this;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public CacheFile setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
        return this;
    }

    public String getBvId() {
        return bvId;
    }

    public CacheFile setBvId(String bvId) {
        this.bvId = bvId;
        return this;
    }

    @NonNull
    @Override
    public CacheFile clone() throws CloneNotSupportedException {
        CacheFile cloneCacheFile = (CacheFile) super.clone();
        if (blvPathList != null) {
            cloneCacheFile.setBlvPathList(new ArrayList<>(blvPathList));
        }
        return cloneCacheFile;
    }

    @Override
    public String toString() {
        return "CacheFile{" +
                "flag=" + flag +
                ", wholeVisibility=" + wholeVisibility +
                ", collectionPath='" + collectionPath + '\'' +
                ", collectionName='" + collectionName + '\'' +
                ", chapterPath='" + chapterPath + '\'' +
                ", chapterName='" + chapterName + '\'' +
                ", audioPath='" + audioPath + '\'' +
                ", videoPath='" + videoPath + '\'' +
                ", jsonPath='" + jsonPath + '\'' +
                ", danmakuPath='" + danmakuPath + '\'' +
                ", boxVisibility=" + boxVisibility +
                ", boxCheck=" + boxCheck +
                ", useUri=" + useUri +
                ", documentFile=" + documentFile +
                ", coverUrl=" + coverUrl +
                ", bvId=" + bvId +
                '}';
    }


}
