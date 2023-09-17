package com.molihua.hlbmerge.entity;

public class CacheSrc<T> {
    private T audio;
    private T video;
    private T json;
    private T danmaku;

    public T getAudio() {
        return audio;
    }

    public CacheSrc setAudio(T audio) {
        this.audio = audio;
        return this;
    }

    public T getVideo() {
        return video;
    }

    public CacheSrc setVideo(T video) {
        this.video = video;
        return this;
    }

    public T getJson() {
        return json;
    }

    public CacheSrc setJson(T json) {
        this.json = json;
        return this;
    }

    public T getDanmaku() {
        return danmaku;
    }

    public CacheSrc setDanmaku(T danmaku) {
        this.danmaku = danmaku;
        return this;
    }
}
