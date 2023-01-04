package com.molihua.hlbmerge.fragment.impl;

import android.view.View;
import android.widget.TextView;

import com.blankj.molihuan.utilcode.util.ClipboardUtils;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.dialog.impl.MergeOptionDialog;
import com.molihua.hlbmerge.entity.CacheFile;
import com.molihua.hlbmerge.fragment.AbstractMainHandleFragment;
import com.molihua.hlbmerge.service.BaseCacheFileManager;
import com.molihuan.pathselector.utils.Mtools;

import java.util.List;

/**
 * @ClassName: MainHandleFragment
 * @Author: molihuan
 * @Date: 2022/12/21/19:53
 * @Description:
 */
public class MainHandleFragment extends AbstractMainHandleFragment implements View.OnClickListener {
    private TextView selectAllTv;
    private TextView copyPathTv;
    private TextView mergeTv;
    private TextView cancelTv;

    @Override
    public int setFragmentViewId() {
        return R.layout.fragment_main_handle;
    }

    @Override
    public void getComponents(View view) {
        selectAllTv = view.findViewById(R.id.tv_select_all);
        copyPathTv = view.findViewById(R.id.tv_copy_path);
        mergeTv = view.findViewById(R.id.tv_merge);
        cancelTv = view.findViewById(R.id.tv_cancel);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        selectAllTv.setText("全选");
        copyPathTv.setText("复制路径");
        mergeTv.setText("合并");
        cancelTv.setText("取消");
    }

    @Override
    public void setListeners() {
        selectAllTv.setOnClickListener(this);
        copyPathTv.setOnClickListener(this);
        mergeTv.setOnClickListener(this);
        cancelTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_select_all) {

            TextView tv = (TextView) v;
            if ("全选".equals(tv.getText())) {
                abstractMainActivity.selectAllCacheFile(true);
                tv.setText("全不选");
            } else {
                abstractMainActivity.selectAllCacheFile(false);
                tv.setText("全选");
            }

        } else if (id == R.id.tv_copy_path) {
            List<CacheFile> selectedCacheFileList = abstractMainActivity.getSelectedCacheFileList();
            String toastText;
            //是否需要将信息复制到粘贴板上
            boolean isCopy;

            switch (selectedCacheFileList.size()) {
                case 0:
                    toastText = "你还没有选择捏";
                    isCopy = false;
                    break;
                case 1:
                    CacheFile cacheFile = selectedCacheFileList.get(0);
                    switch (cacheFile.getFlag()) {
                        case BaseCacheFileManager.FLAG_CACHE_FILE_COLLECTION:
                            toastText = cacheFile.getCollectionPath();
                            isCopy = true;
                            break;
                        case BaseCacheFileManager.FLAG_CACHE_FILE_CHAPTER:
                            toastText = cacheFile.getChapterPath();
                            isCopy = true;
                            break;
                        case BaseCacheFileManager.FLAG_CACHE_FILE_BACK:
                        default:
                            toastText = "选择错误";
                            isCopy = false;
                    }
                    break;
                default:
                    toastText = "只能选择一个";
                    isCopy = false;
            }


            if (isCopy) {
                ClipboardUtils.copyText(toastText);
                Mtools.toast("复制成功");
            } else {
                Mtools.toast(toastText);
            }


        } else if (id == R.id.tv_merge) {
            MergeOptionDialog.showMergeOptionDialog(abstractMainActivity.getSelectedCacheFileList(), abstractMainActivity.getMainFileShowFragment());
        } else if (id == R.id.tv_cancel) {
            abstractMainActivity.openCloseMultipleMode(false);
        }
    }
}
