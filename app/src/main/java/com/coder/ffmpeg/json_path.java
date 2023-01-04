package com.coder.ffmpeg;

import org.json.JSONObject;

public class json_path {
    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public String getPath() {
        return path;
    }

    private JSONObject jsonObject;
    private String path;///storage/emulated/0/Android/data/tv.danmaku.bili/download/1111

    public json_path(JSONObject jsonObject, String path) {
        this.jsonObject = jsonObject;
        this.path = path;
    }
}
