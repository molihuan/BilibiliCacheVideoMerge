package com.molihua.hlbmerge.fragment.impl;

import android.view.View;

import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.fragment.AbstractMainFragment;
import com.molihuan.pathselector.PathSelector;
import com.molihuan.pathselector.entity.FileBean;
import com.molihuan.pathselector.fragment.BasePathSelectFragment;
import com.molihuan.pathselector.fragment.impl.PathSelectFragment;
import com.molihuan.pathselector.listener.CommonItemListener;
import com.molihuan.pathselector.listener.FileItemListener;
import com.molihuan.pathselector.utils.MConstants;
import com.molihuan.pathselector.utils.Mtools;

import java.util.List;

/**
 * @ClassName: MainCompleteFragment
 * @Author: molihuan
 * @Date: 2022/12/21/21:10
 * @Description:
 */
public class MainCompleteFragment extends AbstractMainFragment {
    private PathSelectFragment pathSelectFragment;

    @Override
    public int setFragmentViewId() {
        return R.layout.fragment_main_complete;
    }

    @Override
    public void getComponents(View view) {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        openFileChoose();
    }

    @Override
    public void setListeners() {

    }

    @Override
    public boolean onBackPressed() {
        if (pathSelectFragment != null && pathSelectFragment.onBackPressed()) {
            return true;
        }
        return false;
    }

    /**
     * 文件选择器
     */
    private void openFileChoose() {

        pathSelectFragment = PathSelector.build(this, MConstants.BUILD_FRAGMENT)
                .setFrameLayoutId(R.id.main_complete_view)
                .setShowTitlebarFragment(false)
                .setShowFileTypes("mp4", "xml", "mp3", "")
                .setFileItemListener(new FileItemListener() {
                    @Override
                    public boolean onClick(View v, FileBean file, String currentPath, BasePathSelectFragment pathSelectFragment) {
                        Mtools.toast(file.getPath());
                        return false;
                    }
                })
                .setHandleItemListeners(
                        new CommonItemListener("全选") {
                            @Override
                            public boolean onClick(View v, List<FileBean> selectedFiles, String currentPath, BasePathSelectFragment pathSelectFragment) {
                                return false;
                            }
                        },
                        new CommonItemListener("删除") {
                            @Override
                            public boolean onClick(View v, List<FileBean> selectedFiles, String currentPath, BasePathSelectFragment pathSelectFragment) {
                                return false;
                            }
                        }
                )
                .show();
        
    }


}
