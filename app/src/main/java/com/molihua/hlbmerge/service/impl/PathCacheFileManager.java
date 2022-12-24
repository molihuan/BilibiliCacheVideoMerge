package com.molihua.hlbmerge.service.impl;

import android.annotation.SuppressLint;
import android.view.View;

import com.molihua.hlbmerge.adapter.CacheFileListAdapter;
import com.molihua.hlbmerge.entity.CacheFile;
import com.molihua.hlbmerge.service.BaseCacheFileManager;
import com.molihua.hlbmerge.utils.FileTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName: PathCacheFileManager
 * @Author: molihuan
 * @Date: 2022/12/22/20:18
 * @Description:
 */
public class PathCacheFileManager extends BaseCacheFileManager {


    @Override
    public List<CacheFile> updateCollectionFileList(String path, List<CacheFile> cacheFileList) {
        //初始化列表
        cacheFileList = initCollectionFileList(path, cacheFileList);

        String[] needPath = new String[4];
        String[] names = new String[2];
        //获取所有的合集
        File[] collectionFile = FileTools.getCollectionChapterFile(path);
        for (int i = 0; i < collectionFile.length; i++) {
            //获取每一个集合中的第一个章节
            File oneChapterPath = Objects.requireNonNull(collectionFile[i].listFiles())[0];
            //获取章节里需要的路径
            needPath = FileTools.getNeedPath(oneChapterPath, needPath);
            //获取合集名称和章节名称
            names = FileTools.getCollectionChapterName(needPath[2], names);
            cacheFileList.add(
                    new CacheFile()
                            .setFlag(BaseCacheFileManager.FLAG_CACHE_FILE_COLLECTION)
                            .setWholeVisibility(View.VISIBLE)
                            .setCollectionPath(collectionFile[i].getAbsolutePath())
                            .setCollectionName(names[0])
                            .setChapterName(names[1])
                            .setAudioPath(needPath[0])
                            .setVideoPath(needPath[1])
                            .setJsonPath(needPath[2])
                            .setDanmakuPath(needPath[3])
                            .setBoxVisibility(View.INVISIBLE)
                            .setBoxCheck(false)
            );
        }

        return cacheFileList;
    }

    @Override
    public List<CacheFile> initChapterFileList(String collectionPath, List<CacheFile> cacheFileList) {
        if (cacheFileList == null) {
            cacheFileList = new ArrayList<>();
        }

        cacheFileList.clear();

        cacheFileList.add(
                new CacheFile()
                        .setFlag(-1)
                        .setWholeVisibility(View.VISIBLE)
                        .setCollectionPath(collectionPath)
                        .setChapterName("...")
                        .setBoxVisibility(View.INVISIBLE)
                        .setBoxCheck(false)
        );

        return cacheFileList;
    }

    @Override
    public List<CacheFile> updateChapterFileList(String collectionPath, List<CacheFile> cacheFileList) {
        cacheFileList = initChapterFileList(collectionPath, cacheFileList);

        String[] needPath = new String[4];
        String[] names = new String[2];
        //获取一个合集下面所有的章节
        File[] chapterFile = FileTools.getCollectionChapterFile(collectionPath);
        for (int i = 0; i < chapterFile.length; i++) {
            //获取章节里需要的路径
            needPath = FileTools.getNeedPath(chapterFile[i], needPath);
            //获取合集名称和章节名称
            names = FileTools.getCollectionChapterName(needPath[2], names);
            cacheFileList.add(
                    new CacheFile()
                            .setFlag(BaseCacheFileManager.FLAG_CACHE_FILE_CHAPTER)
                            .setWholeVisibility(View.VISIBLE)
                            .setCollectionPath(collectionPath)
                            .setCollectionName(names[0])
                            .setChapterPath(chapterFile[i].getAbsolutePath())
                            .setChapterName(names[1])
                            .setAudioPath(needPath[0])
                            .setVideoPath(needPath[1])
                            .setJsonPath(needPath[2])
                            .setDanmakuPath(needPath[3])
                            .setBoxVisibility(View.INVISIBLE)
                            .setBoxCheck(false)
            );
        }

        return cacheFileList;
    }

    @Override
    public List<CacheFile> setBoxVisible(List<CacheFile> cacheFileList, CacheFileListAdapter cacheFileAdapter, boolean state) {

        int type;
        if (state) {
            type = View.VISIBLE;
        } else {
            type = View.INVISIBLE;
        }

        CacheFile cacheFile;
        for (int i = 0; i < cacheFileList.size(); i++) {
            cacheFile = cacheFileList.get(i);
            //判断是不是返回item
            if (cacheFile.getFlag() == BaseCacheFileManager.FLAG_CACHE_FILE_BACK) {
                continue;
            }
            cacheFile.setBoxVisibility(type);
            //显示和不显示时都设为不选中防止有缓存
            cacheFile.setBoxCheck(false);
        }

        return cacheFileList;
    }

    @Override
    public List<CacheFile> setBoxChecked(List<CacheFile> cacheFileList, CacheFileListAdapter cacheFileAdapter, boolean state) {
        CacheFile cacheFile;
        for (int i = 0; i < cacheFileList.size(); i++) {
            cacheFile = cacheFileList.get(i);
            //判断是不是返回item
            if (cacheFile.getFlag() == BaseCacheFileManager.FLAG_CACHE_FILE_BACK) {
                continue;
            }
            //如果是不可见的
            if (cacheFile.getBoxVisibility() != View.VISIBLE || cacheFile.getWholeVisibility() != View.VISIBLE) {
                cacheFile.setBoxCheck(false);
            } else {
                cacheFile.setBoxCheck(state);
            }

        }
        return cacheFileList;
    }

    @Override
    public List<CacheFile> setWholeVisible(List<CacheFile> cacheFileList, boolean state) {
        int type;
        if (state) {
            type = View.VISIBLE;
        } else {
            type = View.GONE;
        }

        CacheFile cacheFile;
        for (int i = 0; i < cacheFileList.size(); i++) {
            cacheFile = cacheFileList.get(i);
            //判断是不是返回item
            if (cacheFile.getFlag() == BaseCacheFileManager.FLAG_CACHE_FILE_BACK) {
                continue;
            }
            cacheFile.setWholeVisibility(type);
            //显示和不显示时都设为不选中防止有缓存
            cacheFile.setBoxCheck(false);
        }

        return cacheFileList;
    }

    @Override
    public List<CacheFile> getSelectedCacheFileList(List<CacheFile> allCacheFileList, List<CacheFile> selectedCacheFileList) {
        Objects.requireNonNull(allCacheFileList, "allCacheFileList is null");
        //初始化
        if (selectedCacheFileList == null) {
            selectedCacheFileList = new ArrayList<>();
        } else {
            selectedCacheFileList.clear();
        }
        //把勾选的添加到选择列表中
        for (CacheFile cacheFile : allCacheFileList) {
            if (cacheFile.getBoxCheck()) {
                selectedCacheFileList.add(cacheFile);
            }
        }

        return selectedCacheFileList;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void refreshCacheFileList(CacheFileListAdapter cacheFileAdapter) {
        cacheFileAdapter.notifyDataSetChanged();
    }
}
