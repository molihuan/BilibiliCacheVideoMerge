package com.molihua.hlbmerge.service;

import android.annotation.SuppressLint;
import android.view.View;

import com.molihua.hlbmerge.adapter.CacheFileListAdapter;
import com.molihua.hlbmerge.entity.CacheFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
