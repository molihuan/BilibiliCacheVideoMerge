package com.molihua.hlbmerge.service.impl;

import android.view.View;

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


        if (collectionFile == null || collectionFile.length == 0) {
            return cacheFileList;
        }


        for (int i = 0; i < collectionFile.length; i++) {

            if (collectionFile[i].listFiles() == null || collectionFile[i].listFiles().length == 0) {
                return cacheFileList;
            }

            //获取每一个集合中的第一个章节
            File oneChapterPath = collectionFile[i].listFiles()[0];
            //获取章节里需要的路径
            needPath = FileTools.getNeedPath(oneChapterPath, needPath);

            //获取合集名称和章节名称
            names = FileTools.getCollectionChapterName(needPath[2], names);

            if (names == null) {
                return cacheFileList;
            }

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
                            .setUseUri(false)
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
                        .setUseUri(false)
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
                            .setUseUri(false)
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
    public static List<CacheFile> collection2ChapterCacheFileList(List<CacheFile> collectionCacheFileList) {
        Objects.requireNonNull(collectionCacheFileList, "collectionCacheFileList is null");

        //如果已经是章节了就直接返回
        if (collectionCacheFileList.get(0).getFlag() == BaseCacheFileManager.FLAG_CACHE_FILE_CHAPTER) {
            return collectionCacheFileList;
        }

        List<CacheFile> tempList = new ArrayList<>();

        String[] needPath = new String[4];
        String[] names = new String[2];

        String collectionPath;
        //遍历所有合集
        for (int n = 0; n < collectionCacheFileList.size(); n++) {
            //获取一个合集路径
            collectionPath = collectionCacheFileList.get(n).getCollectionPath();
            //获取一个合集下面所有的章节
            File[] chapterFile = FileTools.getCollectionChapterFile(collectionPath);
            for (int i = 0; i < chapterFile.length; i++) {
                //获取章节里需要的路径
                needPath = FileTools.getNeedPath(chapterFile[i], needPath);
                //获取合集名称和章节名称
                names = FileTools.getCollectionChapterName(needPath[2], names);
                tempList.add(
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
                                .setUseUri(false)
                );
            }

        }

        return tempList;
    }


}
