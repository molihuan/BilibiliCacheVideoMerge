package com.molihua.hlbmerge.adapter;

import android.annotation.SuppressLint;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.entity.CacheFile;
import com.molihua.hlbmerge.service.BaseCacheFileManager;

import java.util.List;

/**
 * @ClassName: CacheFileListAdapter
 * @Author: molihuan
 * @Date: 2022/12/21/17:31
 * @Description:
 */
public class CacheFileListAdapter extends BaseQuickAdapter<CacheFile, BaseViewHolder> {
    public CacheFileListAdapter(int layoutResId, @Nullable List<CacheFile> data) {
        super(layoutResId, data);
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void convert(@NonNull BaseViewHolder holder, CacheFile cacheFile) {

        CheckBox checkBox = holder.getView(R.id.checkbox_item_file_choose);
        LinearLayout container = holder.getView(R.id.linl_item_file_container);

        switch (cacheFile.getFlag()) {
            case BaseCacheFileManager.FLAG_CACHE_FILE_COLLECTION:
                holder.setText(R.id.tv_item_file_name, cacheFile.getCollectionName());
                holder.setVisible(R.id.tv_item_file_describe, true);
                holder.setText(R.id.tv_item_file_describe, cacheFile.getCollectionPath());
                break;
            case BaseCacheFileManager.FLAG_CACHE_FILE_CHAPTER:
                holder.setText(R.id.tv_item_file_name, cacheFile.getChapterName());
                holder.setVisible(R.id.tv_item_file_describe, true);
                holder.setText(R.id.tv_item_file_describe, cacheFile.getChapterPath());
                break;
            case BaseCacheFileManager.FLAG_CACHE_FILE_BACK:
                holder.setText(R.id.tv_item_file_name, cacheFile.getChapterName());
                holder.setVisible(R.id.tv_item_file_describe, false);
                break;
            default:
        }

        checkBox.setVisibility(cacheFile.getBoxVisibility());
        checkBox.setChecked(cacheFile.getBoxCheck());

        container.setVisibility(cacheFile.getWholeVisibility());


    }
}
