package com.molihua.hlbmerge.service.impl;

import android.content.Context;
import android.view.View;

import com.molihua.hlbmerge.entity.CacheFile;
import com.molihua.hlbmerge.entity.CacheSrc;
import com.molihua.hlbmerge.service.BaseCacheFileManager;
import com.molihua.hlbmerge.utils.FileTool;

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


    public PathCacheFileManager(Context mContext) {
        super(mContext);
    }

    @Override
    public List<CacheFile> updateCollectionFileList(String path, List<CacheFile> cacheFileList) {
        //初始化列表
        cacheFileList = initCollectionFileList(path, cacheFileList);

        CacheSrc<String> needPath = new CacheSrc<>();

        String[] names = new String[3];
        //获取所有的合集
        File[] collectionFile = FileTool.getCollectionChapterFile(path);


        if (collectionFile == null || collectionFile.length == 0) {
            return cacheFileList;
        }


        for (int i = 0; i < collectionFile.length; i++) {

            File[] allFiles = collectionFile[i].listFiles();


            if (allFiles == null || allFiles.length == 0) {
                continue;
            }

            //获取每一个集合中的第一个章节
            File oneChapterPath = allFiles[0];
            //获取章节里需要的路径
            needPath = FileTool.getNeedPath(oneChapterPath, needPath);

            String prePathName = collectionFile[i].getName();

            //校验needPath
            String srcErrorMsg = FileTool.needSrcErrorHandle(needPath, prePathName);
            if (srcErrorMsg != null) {
                names[0] = srcErrorMsg;
                names[1] = srcErrorMsg;
            } else {
                //获取合集名称和章节名称
                names = FileTool.getCollectionChapterName(needPath.getJson(), names);
            }


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
                            .setAudioPath(needPath.getAudio())
                            .setVideoPath(needPath.getVideo())
                            .setJsonPath(needPath.getJson())
                            .setDanmakuPath(needPath.getDanmaku())
                            .setBoxVisibility(View.INVISIBLE)
                            .setBoxCheck(false)
                            .setUseUri(false)
                            .setCoverUrl(names[2])
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
                        .setChapterName("返回上一级(长按多选)")
                        .setBoxVisibility(View.INVISIBLE)
                        .setBoxCheck(false)
                        .setUseUri(false)
        );

        return cacheFileList;
    }

    @Override
    public List<CacheFile> updateChapterFileList(String collectionPath, List<CacheFile> cacheFileList) {
        cacheFileList = initChapterFileList(collectionPath, cacheFileList);

        CacheSrc<String> needPath = new CacheSrc<>();
        String[] names = new String[3];
        //获取一个合集下面所有的章节
        File[] chapterFile = FileTool.getCollectionChapterFile(collectionPath);
        for (int i = 0; i < chapterFile.length; i++) {
            //获取章节里需要的路径
            needPath = FileTool.getNeedPath(chapterFile[i], needPath);

            String prePathName = chapterFile[i].getName();
            //校验needPath
            String srcErrorMsg = FileTool.needSrcErrorHandle(needPath, prePathName);
            if (srcErrorMsg != null) {
                names[0] = FileTool.getName(collectionPath);
                names[1] = srcErrorMsg;
            } else {
                //获取合集名称和章节名称
                names = FileTool.getCollectionChapterName(needPath.getJson(), names);
            }
            cacheFileList.add(
                    new CacheFile()
                            .setFlag(BaseCacheFileManager.FLAG_CACHE_FILE_CHAPTER)
                            .setWholeVisibility(View.VISIBLE)
                            .setCollectionPath(collectionPath)
                            .setCollectionName(names[0])
                            .setChapterPath(chapterFile[i].getAbsolutePath())
                            .setChapterName(names[1])
                            .setAudioPath(needPath.getAudio())
                            .setVideoPath(needPath.getVideo())
                            .setJsonPath(needPath.getJson())
                            .setDanmakuPath(needPath.getDanmaku())
                            .setBoxVisibility(View.INVISIBLE)
                            .setBoxCheck(false)
                            .setUseUri(false)
                            .setCoverUrl(names[2])
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

        CacheSrc<String> needPath = new CacheSrc<>();
        String[] names = new String[3];

        String collectionPath;
        //遍历所有合集
        for (int n = 0; n < collectionCacheFileList.size(); n++) {
            //获取一个合集路径
            collectionPath = collectionCacheFileList.get(n).getCollectionPath();
            //获取一个合集下面所有的章节
            File[] chapterFile = FileTool.getCollectionChapterFile(collectionPath);
            for (int i = 0; i < chapterFile.length; i++) {
                //获取章节里需要的路径
                needPath = FileTool.getNeedPath(chapterFile[i], needPath);

                String prePathName = chapterFile[i].getName();
                //校验needPath
                String srcErrorMsg = FileTool.needSrcErrorHandle(needPath, prePathName);
                if (srcErrorMsg != null) {
                    names[0] = FileTool.getName(collectionPath);
                    names[1] = srcErrorMsg;
                } else {
                    //获取合集名称和章节名称
                    names = FileTool.getCollectionChapterName(needPath.getJson(), names);
                }
                tempList.add(
                        new CacheFile()
                                .setFlag(BaseCacheFileManager.FLAG_CACHE_FILE_CHAPTER)
                                .setWholeVisibility(View.VISIBLE)
                                .setCollectionPath(collectionPath)
                                .setCollectionName(names[0])
                                .setChapterPath(chapterFile[i].getAbsolutePath())
                                .setChapterName(names[1])
                                .setAudioPath(needPath.getAudio())
                                .setVideoPath(needPath.getVideo())
                                .setJsonPath(needPath.getJson())
                                .setDanmakuPath(needPath.getDanmaku())
                                .setBoxVisibility(View.INVISIBLE)
                                .setBoxCheck(false)
                                .setUseUri(false)
                                .setCoverUrl(names[2])
                );
            }

        }

        return tempList;
    }


}
