package com.molihua.hlbmerge.controllers;

import android.net.Uri;

import java.io.InputStream;

import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.parser.android.AndroidFileSource;

/**
 * 自定义弹幕加载器
 */
public class MyBiliDanmakuLoader implements ILoader {
    private static MyBiliDanmakuLoader _instance;

    private AndroidFileSource dataSource;

    private MyBiliDanmakuLoader() {

    }

    public static MyBiliDanmakuLoader instance() {
        if (_instance == null) {
            _instance = new MyBiliDanmakuLoader();
        }
        return _instance;
    }

    public void load(String uri) throws IllegalDataException {
        try {
            dataSource = new AndroidFileSource(uri);
        } catch (Exception e) {
            throw new IllegalDataException(e);
        }
    }
    public void load(Uri uri) throws IllegalDataException {
        try {
            dataSource = new AndroidFileSource(uri);
        } catch (Exception e) {
            throw new IllegalDataException(e);
        }
    }

    public void load(InputStream stream) {
        dataSource = new AndroidFileSource(stream);
    }

    @Override
    public AndroidFileSource getDataSource() {
        return dataSource;
    }
}
