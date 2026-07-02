package com.molihua.hlbmerge.service

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.molihua.hlbmerge.adapter.CacheFileListAdapter
import com.molihua.hlbmerge.entity.CacheFile
import com.xuexiang.xtask.XTask
import com.xuexiang.xui.utils.WidgetUtils

abstract class BaseCacheFileManager(val mContext: Context) : ICacheFileManager {
    private val mLoadingDialog by lazy {
        WidgetUtils.getLoadingDialog(mContext)
            .setIconScale(0.7f)
            .setLoadingSpeed(8)
    }


    override fun initCollectionFileList(
        path: String,
        cacheFileList: MutableList<CacheFile>
    ): MutableList<CacheFile> {
        if (cacheFileList.isNotEmpty()) {
            cacheFileList.clear()
        }
        return cacheFileList
    }

    override fun setBoxVisible(
        cacheFileList: MutableList<CacheFile>,
        cacheFileAdapter: CacheFileListAdapter,
        state: Boolean
    ): MutableList<CacheFile> {
        val type: Int = if (state) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }

        var cacheFile: CacheFile
        for (i in cacheFileList.indices) {
            cacheFile = cacheFileList[i]
            //判断是不是返回item
            if (cacheFile.flag == FLAG_CACHE_FILE_BACK) {
                continue
            }
            cacheFile.boxVisibility = type
            //显示和不显示时都设为不选中防止有缓存
            cacheFile.boxCheck = false
        }

        return cacheFileList
    }

    override fun setBoxChecked(
        cacheFileList: MutableList<CacheFile>,
        cacheFileAdapter: CacheFileListAdapter,
        state: Boolean
    ): MutableList<CacheFile> {
        var cacheFile: CacheFile
        for (i in cacheFileList.indices) {
            cacheFile = cacheFileList[i]
            //判断是不是返回item
            if (cacheFile.flag == FLAG_CACHE_FILE_BACK) {
                continue
            }
            //如果是不可见的
            if (cacheFile.boxVisibility != View.VISIBLE || cacheFile.wholeVisibility != View.VISIBLE) {
                cacheFile.boxCheck = false
            } else {
                cacheFile.boxCheck = state
            }
        }
        return cacheFileList
    }

    override fun setWholeVisible(
        cacheFileList: MutableList<CacheFile>,
        state: Boolean
    ): MutableList<CacheFile> {
        val type: Int = if (state) {
            View.VISIBLE
        } else {
            View.GONE
        }

        var cacheFile: CacheFile
        for (i in cacheFileList.indices) {
            cacheFile = cacheFileList[i]
            //判断是不是返回item
            if (cacheFile.flag == FLAG_CACHE_FILE_BACK) {
                continue
            }
            cacheFile.wholeVisibility = type
            //显示和不显示时都设为不选中防止有缓存
            cacheFile.boxCheck = false
        }

        return cacheFileList
    }

    override fun getSelectedCacheFileList(
        allCacheFileList: MutableList<CacheFile>,
        selectedCacheFileList: MutableList<CacheFile>
    ): MutableList<CacheFile> {
        //初始化
        selectedCacheFileList.clear()

        //把勾选的添加到选择列表中
        for (cacheFile in allCacheFileList) {
            if (cacheFile.boxCheck == true) {
                selectedCacheFileList.add(cacheFile)
            }
        }

        return selectedCacheFileList
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun refreshCacheFileList(cacheFileAdapter: CacheFileListAdapter) {
        cacheFileAdapter.notifyDataSetChanged()
    }

    override fun showLoadingDialog() {
        XTask.postToMain {
            if (mLoadingDialog != null) {
                mLoadingDialog.show()
            }
        }
    }

    override fun updateLoadingDialogMsg(str: String) {
        XTask.postToMain { mLoadingDialog.updateMessage(str) }
    }

    override fun dismissLoadingDialog() {
        XTask.postToMain {
            if (mLoadingDialog != null) {
                mLoadingDialog.dismiss()
                //                    mLoadingDialog.hide();
            }
        }
    }

    companion object{
        //合集
        const val FLAG_CACHE_FILE_COLLECTION: Int = 0
        //章节
        const val FLAG_CACHE_FILE_CHAPTER: Int = 1
        //返回
        const val FLAG_CACHE_FILE_BACK: Int = -1
    }
}