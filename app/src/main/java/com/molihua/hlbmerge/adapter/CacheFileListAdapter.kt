package com.molihua.hlbmerge.adapter

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.molihua.hlbmerge.R
import com.molihua.hlbmerge.entity.CacheFile
import com.molihua.hlbmerge.service.BaseCacheFileManager

class CacheFileListAdapter(layoutResId: Int, data: MutableList<CacheFile>) :
    BaseQuickAdapter<CacheFile, BaseViewHolder>(layoutResId, data), LoadMoreModule {
    override fun convert(
        holder: BaseViewHolder,
        cacheFile: CacheFile
    ) {
        //图片加载128X72
        val checkBox: CheckBox = holder.getView(R.id.checkbox_item_file_choose)
        val container: LinearLayout = holder.getView(R.id.linl_item_file_container)
        val icoImgView: ImageView = holder.getView(R.id.imgv_item_file_ico)

        when (cacheFile.flag) {
            BaseCacheFileManager.FLAG_CACHE_FILE_COLLECTION -> {
                holder.setText(R.id.tv_item_file_name, cacheFile.collectionName)
                holder.setVisible(R.id.tv_item_file_describe, true)
                holder.setText(R.id.tv_item_file_describe, cacheFile.collectionPath)
                setImgCover(cacheFile, icoImgView)
            }

            BaseCacheFileManager.FLAG_CACHE_FILE_CHAPTER -> {
                holder.setText(R.id.tv_item_file_name, cacheFile.chapterName)
                holder.setVisible(R.id.tv_item_file_describe, true)
                holder.setText(R.id.tv_item_file_describe, cacheFile.chapterPath)
                setImgCover(cacheFile, icoImgView)
            }

            BaseCacheFileManager.FLAG_CACHE_FILE_BACK -> {
                holder.setText(R.id.tv_item_file_name, cacheFile.chapterName)
                holder.setVisible(R.id.tv_item_file_describe, false)
                icoImgView.setImageResource(com.molihuan.pathselector.R.mipmap.folder_mlh)
            }

            else -> {}
        }

        checkBox.visibility = when (cacheFile.boxVisibility) {
            View.VISIBLE -> {
                View.VISIBLE
            }

            View.INVISIBLE -> {
                View.INVISIBLE
            }

            View.GONE -> {
                View.GONE
            }

            else -> {
                View.GONE
            }
        }
        checkBox.setChecked(cacheFile.boxCheck == true)

        container.visibility = when (cacheFile.wholeVisibility) {
            View.VISIBLE -> {
                View.VISIBLE
            }

            View.INVISIBLE -> {
                View.INVISIBLE
            }

            View.GONE -> {
                View.GONE
            }

            else -> {
                View.GONE
            }
        }
    }

    private fun setImgCover(cacheFile: CacheFile, icoImgView: ImageView) {
        val coverUrl = cacheFile.coverUrl
        if (coverUrl != null) {
            Glide.with(context)
                .load(coverUrl)
                .error(com.molihuan.pathselector.R.mipmap.folder_mlh)
                .into(icoImgView)
        } else {
            icoImgView.setImageResource(com.molihuan.pathselector.R.mipmap.folder_mlh)
        }
    }
}