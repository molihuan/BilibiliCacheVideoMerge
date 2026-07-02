package com.molihua.hlbmerge.interfaces;

import com.molihua.hlbmerge.adapter.CacheFileListAdapter
import com.molihua.hlbmerge.entity.CacheFile
import com.molihua.hlbmerge.service.ICacheFileManager

interface IMainFileShowFragment {
    fun updateCollectionFileList(): MutableList<CacheFile>

    /**
     * 用来刷新的
     *
     * @return
     */
    fun updateChapterFileList(): MutableList<CacheFile>

    /**
     * 进入合集中
     *
     * @param collectionPath
     * @return
     */
    fun updateChapterFileList(collectionPath: String): MutableList<CacheFile>

    fun getSelectedCacheFileList(): MutableList<CacheFile>

    fun getAllCacheFileList(): MutableList<CacheFile>

    fun getCacheFileListAdapter(): CacheFileListAdapter

    fun getPathCacheFileManager(): ICacheFileManager

    fun selectAllCacheFile(status: Boolean);

    fun openCloseMultipleMode(cacheFile: CacheFile, status: Boolean);

    fun openCloseMultipleMode(status: Boolean);

    fun isMultipleSelectionMode(): Boolean

    /**
     * 刷新UI
     */
    fun refreshCacheFileList();

    /**
     * 设置整体是否可见
     *
     * @param state
     * @return
     */
    fun setWholeVisible(state: Boolean): MutableList<CacheFile>

}
