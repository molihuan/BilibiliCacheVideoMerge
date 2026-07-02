package com.molihua.hlbmerge.service;

import com.molihua.hlbmerge.adapter.CacheFileListAdapter
import com.molihua.hlbmerge.entity.CacheFile

/**
 * @ClassName: ICacheFileManager
 * @Author: molihuan
 * @Date: 2022/12/22/19:29
 * @Description:
 */
interface ICacheFileManager {
    fun initCollectionFileList(path: String, cacheFileList: MutableList<CacheFile>): MutableList<CacheFile>

    /**
     * 更新列表
     *
     * @param path
     * @param cacheFileList
     * @return
     */
    fun updateCollectionFileList(path: String, cacheFileList: MutableList<CacheFile>): MutableList<CacheFile>

    fun initChapterFileList(collectionPath: String, cacheFileList: MutableList<CacheFile>): MutableList<CacheFile>

    fun updateChapterFileList(
        collectionPath: String,
        cacheFileList: MutableList<CacheFile>
    ): MutableList<CacheFile>

    fun setBoxVisible(
        cacheFileList: MutableList<CacheFile>,
        cacheFileAdapter: CacheFileListAdapter,
        state: Boolean
    ): MutableList<CacheFile>

    fun setBoxChecked(
        cacheFileList: MutableList<CacheFile>,
        cacheFileAdapter: CacheFileListAdapter,
        state: Boolean
    ): MutableList<CacheFile>

    fun setWholeVisible(cacheFileList: MutableList<CacheFile>, state: Boolean): MutableList<CacheFile>

    fun getSelectedCacheFileList(
        allCacheFileList: MutableList<CacheFile>,
        selectedCacheFileList: MutableList<CacheFile>
    ): MutableList<CacheFile>

    fun refreshCacheFileList(cacheFileAdapter: CacheFileListAdapter);

    /**
     * 显示加载弹窗
     */
    fun showLoadingDialog();

    /**
     * 更新加载弹窗信息
     */
    fun updateLoadingDialogMsg(str: String);

    /**
     * 销毁加载弹窗
     */
    fun dismissLoadingDialog();


}
