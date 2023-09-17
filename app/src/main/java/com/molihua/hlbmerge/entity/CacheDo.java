package com.molihua.hlbmerge.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CacheDo implements Parcelable {
    private String audio;
    private String video;
    private String json;
    private String danmaku;

    private String title;

    private String subTitle;

    private String coverUrl;

    public static final Creator<CacheDo> CREATOR = new Creator<CacheDo>() {
        @Override
        public CacheDo createFromParcel(Parcel source) {
            // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
            CacheDo cd = new CacheDo();
            cd.setAudio(source.readString());
            cd.setVideo(source.readString());
            cd.setJson(source.readString());
            cd.setDanmaku(source.readString());
            cd.setTitle(source.readString());
            cd.setSubTitle(source.readString());
            cd.setCoverUrl(source.readString());
            return cd;
        }

        @Override
        public CacheDo[] newArray(int size) {
            return new CacheDo[size];
        }
    };

    public String getAudio() {
        return audio;
    }

    public CacheDo setAudio(String audio) {
        this.audio = audio;
        return this;
    }

    public String getVideo() {
        return video;
    }

    public CacheDo setVideo(String video) {
        this.video = video;
        return this;
    }

    public String getJson() {
        return json;
    }

    public CacheDo setJson(String json) {
        this.json = json;
        return this;
    }

    public String getDanmaku() {
        return danmaku;
    }

    public CacheDo setDanmaku(String danmaku) {
        this.danmaku = danmaku;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public CacheDo setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public CacheDo setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public CacheDo setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        // 1.必须按成员变量声明的顺序封装数据，不然会出现获取数据出错
        // 2.序列化对象

        parcel.writeString(audio);
        parcel.writeString(video);
        parcel.writeString(json);
        parcel.writeString(danmaku);
        parcel.writeString(title);
        parcel.writeString(subTitle);
        parcel.writeString(coverUrl);
    }

    @Override
    public String toString() {
        return "CacheDo{" +
                "audio='" + audio + '\'' +
                ", video='" + video + '\'' +
                ", json='" + json + '\'' +
                ", danmaku='" + danmaku + '\'' +
                ", title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                '}';
    }
}
