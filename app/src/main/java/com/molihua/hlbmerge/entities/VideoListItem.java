package com.molihua.hlbmerge.entities;

import android.view.View;
/**
 * 视频实体类
 */
public class VideoListItem {
    private String videoPath;
    private String videoName;
    private String videoTime;
    private int checkBoxVisibility;//checkBox是否可见
    private boolean checkBoxCheck;//checkBox是否选中



    public VideoListItem(String videoPath, String videoName, String videoTime) {
        this.videoPath = videoPath;
        this.videoName = videoName;
        this.videoTime = videoTime;
        this.checkBoxVisibility = View.GONE;
        this.checkBoxCheck = false;
    }

    public VideoListItem(String videoPath, String videoName, String videoTime, int checkBoxVisibility, boolean checkBoxCheck) {
        this.videoPath = videoPath;
        this.videoName = videoName;
        this.videoTime = videoTime;
        this.checkBoxVisibility = checkBoxVisibility;
        this.checkBoxCheck = checkBoxCheck;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoTime() {
        return videoTime;
    }

    public void setVideoTime(String videoTime) {
        this.videoTime = videoTime;
    }

    public int getCheckBoxVisibility() {
        return checkBoxVisibility;
    }

    public void setCheckBoxVisibility(int checkBoxVisibility) {
        this.checkBoxVisibility = checkBoxVisibility;
    }

    public boolean isCheckBoxCheck() {
        return checkBoxCheck;
    }

    public void setCheckBoxCheck(boolean checkBoxCheck) {
        this.checkBoxCheck = checkBoxCheck;
    }

    @Override
    public String toString() {
        return "VideoListItem{" +
                "videoPath='" + videoPath + '\'' +
                ", videoName='" + videoName + '\'' +
                ", videoTime='" + videoTime + '\'' +
                '}';
    }
}
