package com.molihua.hlbmerge.interfaces;

import androidx.annotation.Nullable;

import com.molihua.hlbmerge.adapter.CacheFileListAdapter;
import com.molihua.hlbmerge.entity.CacheFile;
import com.molihua.hlbmerge.service.ICacheFileManager;

import java.util.List;

public interface IMainFileShowFragment {
    List<CacheFile> updateCollectionFileList();

    /**
     * 用来刷新的
     *
     * @return
     */
    List<CacheFile> updateChapterFileList();

    /**
     * 进入合集中
     *
     * @param collectionPath
     * @return
     */
    List<CacheFile> updateChapterFileList(String collectionPath);

    List<CacheFile> getSelectedCacheFileList();

    List<CacheFile> getAllCacheFileList();

    CacheFileListAdapter getCacheFileListAdapter();

    ICacheFileManager getPathCacheFileManager();

    void selectAllCacheFile(boolean status);

    void openCloseMultipleMode(@Nullable CacheFile cacheFile, boolean status);

    void openCloseMultipleMode(boolean status);

    boolean isMultipleSelectionMode();

    /**
     * 刷新UI
     */
    void refreshCacheFileList();

    /**
     * 设置整体是否可见
     *
     * @param state
     * @return
     */
    List<CacheFile> setWholeVisible(boolean state);

}
