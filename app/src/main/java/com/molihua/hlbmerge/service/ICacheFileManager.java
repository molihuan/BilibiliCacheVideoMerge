package com.molihua.hlbmerge.service;

import com.molihua.hlbmerge.adapter.CacheFileListAdapter;
import com.molihua.hlbmerge.entity.CacheFile;

import java.util.List;

/**
 * @ClassName: ICacheFileManager
 * @Author: molihuan
 * @Date: 2022/12/22/19:29
 * @Description:
 */
public interface ICacheFileManager {
    List<CacheFile> initCollectionFileList(String path, List<CacheFile> cacheFileList);

    /**
     * 更新列表
     *
     * @param path
     * @param cacheFileList
     * @return
     */
    List<CacheFile> updateCollectionFileList(String path, List<CacheFile> cacheFileList);

    List<CacheFile> initChapterFileList(String collectionPath, List<CacheFile> cacheFileList);

    List<CacheFile> updateChapterFileList(String collectionPath, List<CacheFile> cacheFileList);

    List<CacheFile> setBoxVisible(List<CacheFile> cacheFileList, CacheFileListAdapter cacheFileAdapter, boolean state);

    List<CacheFile> setBoxChecked(List<CacheFile> cacheFileList, CacheFileListAdapter cacheFileAdapter, boolean state);

    List<CacheFile> setWholeVisible(List<CacheFile> cacheFileList, boolean state);

    List<CacheFile> getSelectedCacheFileList(List<CacheFile> allCacheFileList, List<CacheFile> selectedCacheFileList);

    void refreshCacheFileList(CacheFileListAdapter cacheFileAdapter);

    /**
     * 显示加载弹窗
     */
    void showLoadingDialog();

    /**
     * 更新加载弹窗信息
     */
    void updateLoadingDialogMsg(String str);

    /**
     * 销毁加载弹窗
     */
    void dismissLoadingDialog();


}
