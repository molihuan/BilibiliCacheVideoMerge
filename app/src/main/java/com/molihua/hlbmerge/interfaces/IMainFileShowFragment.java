package com.molihua.hlbmerge.interfaces;

import androidx.annotation.Nullable;

import com.molihua.hlbmerge.adapter.CacheFileListAdapter;
import com.molihua.hlbmerge.entity.CacheFile;
import com.molihua.hlbmerge.service.ICacheFileManager;

import java.util.List;

public interface IMainFileShowFragment {
    List<CacheFile> updateCollectionFileList();

    List<CacheFile> updateChapterFileList();

    List<CacheFile> updateChapterFileList(String collectionPath);

    List<CacheFile> getSelectedCacheFileList();

    List<CacheFile> getAllCacheFileList();

    CacheFileListAdapter getCacheFileListAdapter();

    ICacheFileManager getPathCacheFileManager();

    void selectAllCacheFile(boolean status);

    void openCloseMultipleMode(@Nullable CacheFile cacheFile, boolean status);

    void openCloseMultipleMode(boolean status);

    boolean isMultipleSelectionMode();

    void refreshCacheFileList();

    List<CacheFile> setWholeVisible(boolean state);

}
