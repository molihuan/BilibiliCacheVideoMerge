package com.molihua.hlbmerge.service;

import android.view.View;

import com.molihua.hlbmerge.entity.CacheFile;
import com.molihua.hlbmerge.utils.FileTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: BaseCacheFileManager
 * @Author: molihuan
 * @Date: 2022/12/22/19:28
 * @Description:
 */
public abstract class BaseCacheFileManager implements ICacheFileManager {
    //合集
    public final static int FLAG_CACHE_FILE_COLLECTION = 0;
    //章节
    public final static int FLAG_CACHE_FILE_CHAPTER = 1;
    //返回
    public final static int FLAG_CACHE_FILE_BACK = -1;

    @Override
    public List<CacheFile> initCollectionFileList(String path, List<CacheFile> cacheFileList) {
        if (cacheFileList == null) {
            cacheFileList = new ArrayList<>();
        } else if (cacheFileList.size() != 0) {
            cacheFileList.clear();
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
                );
            }

        }

        return tempList;
    }
}
