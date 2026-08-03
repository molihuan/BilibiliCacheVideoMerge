package com.molihua.hlbmerge.entity

class CacheSrc<T> {
    var audio: T? = null
        private set
    var video: T? = null
        private set
    var json: T? = null
        private set
    var danmaku: T? = null
        private set

    fun setAudio(audio: T): CacheSrc<T> {
        this.audio = audio
        return this
    }

    fun setVideo(video: T): CacheSrc<T> {
        this.video = video
        return this
    }

    fun setJson(json: T): CacheSrc<T> {
        this.json = json
        return this
    }

    fun setDanmaku(danmaku: T): CacheSrc<T> {
        this.danmaku = danmaku
        return this
    }
}