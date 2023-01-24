package com.molihua.hlbmerge.adapter;

import android.annotation.SuppressLint;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
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
public class CacheFileListAdapter extends BaseQuickAdapter<CacheFile, BaseViewHolder> implements LoadMoreModule {
    public CacheFileListAdapter(int layoutResId, @Nullable List<CacheFile> data) {
        super(layoutResId, data);
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void convert(@NonNull BaseViewHolder holder, CacheFile cacheFile) {
        //图片加载128X72

        CheckBox checkBox = holder.getView(R.id.checkbox_item_file_choose);
        LinearLayout container = holder.getView(R.id.linl_item_file_container);
        ImageView icoImgView = holder.getView(R.id.imgv_item_file_ico);


        switch (cacheFile.getFlag()) {
            case BaseCacheFileManager.FLAG_CACHE_FILE_COLLECTION:
                holder.setText(R.id.tv_item_file_name, cacheFile.getCollectionName());
                holder.setVisible(R.id.tv_item_file_describe, true);
                holder.setText(R.id.tv_item_file_describe, cacheFile.getCollectionPath());
                setImgCover(cacheFile, icoImgView);
                break;
            case BaseCacheFileManager.FLAG_CACHE_FILE_CHAPTER:
                holder.setText(R.id.tv_item_file_name, cacheFile.getChapterName());
                holder.setVisible(R.id.tv_item_file_describe, true);
                holder.setText(R.id.tv_item_file_describe, cacheFile.getChapterPath());
                setImgCover(cacheFile, icoImgView);
                break;
            case BaseCacheFileManager.FLAG_CACHE_FILE_BACK:
                holder.setText(R.id.tv_item_file_name, cacheFile.getChapterName());
                holder.setVisible(R.id.tv_item_file_describe, false);
                icoImgView.setImageResource(com.molihuan.pathselector.R.mipmap.folder_mlh);
                break;
            default:
        }

        checkBox.setVisibility(cacheFile.getBoxVisibility());
        checkBox.setChecked(cacheFile.getBoxCheck());

        container.setVisibility(cacheFile.getWholeVisibility());


    }

    private void setImgCover(CacheFile cacheFile, ImageView icoImgView) {
        String coverUrl = cacheFile.getCoverUrl();
        if (coverUrl != null) {
            Glide.with(getContext())
                    .load(coverUrl)
                    .error(com.molihuan.pathselector.R.mipmap.folder_mlh)
                    .into(icoImgView);
        } else {
            icoImgView.setImageResource(com.molihuan.pathselector.R.mipmap.folder_mlh);
        }
    }

}
