package com.molihua.hlbmerge.service.impl;

import android.net.Uri;
import android.view.View;

import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import com.molihua.hlbmerge.entity.CacheFile;
import com.molihua.hlbmerge.service.BaseCacheFileManager;
import com.molihua.hlbmerge.utils.UriTool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName: UriCacheFileManager
 * @Author: molihuan
 * @Date: 2022/12/22/20:18
 * @Description:
 */
public class UriCacheFileManager extends BaseCacheFileManager {

    private Fragment fragment;

    public UriCacheFileManager(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public List<CacheFile> updateCollectionFileList(String path, List<CacheFile> cacheFileList) {
        //初始化列表
        cacheFileList = initCollectionFileList(path, cacheFileList);

        Uri[] needUri = new Uri[4];
        String[] names = new String[2];
        //获取所有的合集路径
        DocumentFile[] collectionFiles = UriTool.getCollectionChapterFile(fragment, path);

        if (collectionFiles == null) {
            return cacheFileList;
        }
        for (int i = 0; i < collectionFiles.length; i++) {
            //获取每一个集合中的第一个章节路径
            DocumentFile oneChapterPath = Objects.requireNonNull(collectionFiles[i].listFiles())[0];

            //获取章节里需要的Uri
            needUri = UriTool.getNeedUri(oneChapterPath, needUri);

            //获取合集名称和章节名称
            names = UriTool.getCollectionChapterName(needUri[2], names);

            cacheFileList.add(
                    new CacheFile()
                            .setFlag(BaseCacheFileManager.FLAG_CACHE_FILE_COLLECTION)
                            .setWholeVisibility(View.VISIBLE)
                            .setCollectionPath(path + File.separator + collectionFiles[i].getName())
                            .setCollectionName(names[0])
                            .setChapterName(names[1])
                            .setAudioPath(needUri[0].toString())
                            .setVideoPath(needUri[1].toString())
                            .setJsonPath(needUri[2].toString())
                            .setDanmakuPath(needUri[3].toString())
                            .setBoxVisibility(View.INVISIBLE)
                            .setBoxCheck(false)
                            .setUseUri(true)
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
                        .setFlag(BaseCacheFileManager.FLAG_CACHE_FILE_BACK)
                        .setWholeVisibility(View.VISIBLE)
                        .setCollectionPath(collectionPath)
                        .setChapterName("...")
                        .setBoxVisibility(View.INVISIBLE)
                        .setBoxCheck(false)
                        .setUseUri(true)
        );

        return cacheFileList;
    }

    @Override
    public List<CacheFile> updateChapterFileList(String collectionPath, List<CacheFile> cacheFileList) {
        cacheFileList = initChapterFileList(collectionPath, cacheFileList);

        Uri[] needUri = new Uri[4];
        String[] names = new String[2];
        //获取一个合集下面所有的章节
        DocumentFile[] chapterFile = UriTool.getCollectionChapterFile(fragment, collectionPath);

        if (chapterFile == null) {
            return cacheFileList;
        }

        for (int i = 0; i < chapterFile.length; i++) {

            //获取章节里需要的Uri
            needUri = UriTool.getNeedUri(chapterFile[i], needUri);

            //获取合集名称和章节名称
            names = UriTool.getCollectionChapterName(needUri[2], names);

            cacheFileList.add(
                    new CacheFile()
                            .setFlag(BaseCacheFileManager.FLAG_CACHE_FILE_CHAPTER)
                            .setWholeVisibility(View.VISIBLE)
                            .setCollectionPath(collectionPath)
                            .setCollectionName(names[0])
                            .setChapterPath(collectionPath + File.separator + chapterFile[i].getName())
                            .setChapterName(names[1])
                            .setAudioPath(needUri[0].toString())
                            .setVideoPath(needUri[1].toString())
                            .setJsonPath(needUri[2].toString())
                            .setDanmakuPath(needUri[3].toString())
                            .setBoxVisibility(View.INVISIBLE)
                            .setBoxCheck(false)
                            .setUseUri(true)
            );
        }

        return cacheFileList;
    }

    /**
     * 将合集item转换为章节item
     *
     * @param collectionCacheFileList
     * @return
     */
    public static List<CacheFile> collection2ChapterCacheFileList(Fragment fragment, List<CacheFile> collectionCacheFileList) {
        Objects.requireNonNull(collectionCacheFileList, "collectionCacheFileList is null");

        //如果已经是章节了就直接返回
        if (collectionCacheFileList.get(0).getFlag() == BaseCacheFileManager.FLAG_CACHE_FILE_CHAPTER) {
            return collectionCacheFileList;
        }

        List<CacheFile> tempList = new ArrayList<>();

        Uri[] needUri = new Uri[4];
        String[] names = new String[2];

        String collectionPath;
        //遍历所有合集
        for (int n = 0; n < collectionCacheFileList.size(); n++) {
            //获取一个合集路径
            collectionPath = collectionCacheFileList.get(n).getCollectionPath();
            //获取一个合集下面所有的章节
            DocumentFile[] chapterFile = UriTool.getCollectionChapterFile(fragment, collectionPath);
            for (int i = 0; i < chapterFile.length; i++) {

                //获取章节里需要的Uri
                needUri = UriTool.getNeedUri(chapterFile[i], needUri);

                //获取合集名称和章节名称
                names = UriTool.getCollectionChapterName(needUri[2], names);

                tempList.add(
                        new CacheFile()
                                .setFlag(BaseCacheFileManager.FLAG_CACHE_FILE_CHAPTER)
                                .setWholeVisibility(View.VISIBLE)
                                .setCollectionPath(collectionPath)
                                .setCollectionName(names[0])
                                .setChapterPath(collectionPath + File.separator + chapterFile[i].getName())
                                .setChapterName(names[1])
                                .setAudioPath(needUri[0].toString())
                                .setVideoPath(needUri[1].toString())
                                .setJsonPath(needUri[2].toString())
                                .setDanmakuPath(needUri[3].toString())
                                .setBoxVisibility(View.INVISIBLE)
                                .setBoxCheck(false)
                                .setUseUri(true)
                );
            }

        }

        return tempList;
    }

}
