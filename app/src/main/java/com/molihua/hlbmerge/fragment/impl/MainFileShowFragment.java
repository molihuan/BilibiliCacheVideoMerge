package com.molihua.hlbmerge.fragment.impl;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.adapter.CacheFileListAdapter;
import com.molihua.hlbmerge.dao.ConfigData;
import com.molihua.hlbmerge.dialog.impl.MergeOptionDialog;
import com.molihua.hlbmerge.entity.CacheFile;
import com.molihua.hlbmerge.fragment.AbstractMainFileShowFragment;
import com.molihua.hlbmerge.service.BaseCacheFileManager;
import com.molihua.hlbmerge.service.ICacheFileManager;
import com.molihua.hlbmerge.service.impl.PathCacheFileManager;
import com.molihua.hlbmerge.service.impl.UriCacheFileManager;
import com.molihuan.pathselector.utils.FileTools;
import com.molihuan.pathselector.utils.PermissionsTools;
import com.molihuan.pathselector.utils.VersionTool;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: MainFileShowFragment
 * @Author: molihuan
 * @Date: 2022/12/21/15:50
 * @Description:
 */
public class MainFileShowFragment extends AbstractMainFileShowFragment implements OnItemClickListener, OnItemLongClickListener {

    private RecyclerView mRecycView;

    //List和Adapter
    private List<CacheFile> selectedCacheFileList;
    private List<CacheFile> allCacheFileList;
    private CacheFileListAdapter cacheFileListAdapter;

    private ICacheFileManager pathCacheFileManager;
    private ICacheFileManager uriCacheFileManager;

    //当前是否为多选模式
    private boolean multipleSelectionMode = false;


    @Override
    public int setFragmentViewId() {
        return R.layout.fragment_main_file_show;
    }

    @Override
    public void getComponents(View view) {
        mRecycView = view.findViewById(R.id.cache_file_recyclerview);
    }

    @Override
    public void initData() {
        //权限申请
//        UriTool.grantedUriPermission(ConfigData.getCacheFilePath(), this);


        pathCacheFileManager = new PathCacheFileManager();
        uriCacheFileManager = new UriCacheFileManager(this);

        allCacheFileList = new ArrayList<>();
        updateCollectionFileList();
    }

    @Override
    public void initView() {
        mRecycView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));//设置布局管理者

        cacheFileListAdapter = new CacheFileListAdapter(R.layout.item_cache_file, allCacheFileList);//适配器添加数据
        mRecycView.setAdapter(cacheFileListAdapter);//RecyclerView设置适配器

    }

    @Override
    public void setListeners() {
        cacheFileListAdapter.setOnItemClickListener(this);
        cacheFileListAdapter.setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        if (adapter instanceof CacheFileListAdapter) {
            CacheFile item = allCacheFileList.get(position);

            if (multipleSelectionMode) {
                //多选模式下不能点击返回item
                if (item.getFlag() == BaseCacheFileManager.FLAG_CACHE_FILE_BACK) {
                    return;
                }
                //如果已经勾选了
                if (item.getBoxCheck()) {
                    item.setBoxCheck(false);
                } else {
                    item.setBoxCheck(true);
                }
                refreshCacheFileList();

            } else {

                switch (item.getFlag()) {
                    case BaseCacheFileManager.FLAG_CACHE_FILE_COLLECTION:
                        updateChapterFileList(item.getCollectionPath());
                        refreshCacheFileList();
                        break;
                    case BaseCacheFileManager.FLAG_CACHE_FILE_CHAPTER:
                        //打开合并弹窗
                        MergeOptionDialog.showMergeOptionDialog(item, this);
                        break;
                    case BaseCacheFileManager.FLAG_CACHE_FILE_BACK:
                        updateCollectionFileList();
                        refreshCacheFileList();
                        break;
                    default:
                }

            }


        }
    }

    @Override
    public boolean onItemLongClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {

        if (adapter instanceof CacheFileListAdapter) {
            CacheFile item = allCacheFileList.get(position);

            if (item.getFlag() != BaseCacheFileManager.FLAG_CACHE_FILE_BACK) {
                openCloseMultipleMode(item, !multipleSelectionMode);
            }

            return true;
        }

        return false;
    }

    @Override
    public List<CacheFile> updateCollectionFileList() {
        //是否需要使用uri
        boolean dataUseUri = FileTools.underAndroidDataUseUri(ConfigData.getCacheFilePath());

        if (dataUseUri) {
            allCacheFileList = uriCacheFileManager.updateCollectionFileList(ConfigData.getCacheFilePath(), allCacheFileList);
        } else {
            allCacheFileList = pathCacheFileManager.updateCollectionFileList(ConfigData.getCacheFilePath(), allCacheFileList);
        }

        return allCacheFileList;
    }

    @Override
    public List<CacheFile> updateChapterFileList() {
        boolean dataUseUri = FileTools.underAndroidDataUseUri(ConfigData.getCacheFilePath());

        if (dataUseUri) {
            allCacheFileList = uriCacheFileManager.updateChapterFileList(allCacheFileList.get(0).getCollectionPath(), allCacheFileList);
        } else {
            allCacheFileList = pathCacheFileManager.updateChapterFileList(allCacheFileList.get(0).getCollectionPath(), allCacheFileList);
        }

        return allCacheFileList;
    }

    @Override
    public List<CacheFile> updateChapterFileList(String collectionPath) {
        boolean dataUseUri = FileTools.underAndroidDataUseUri(ConfigData.getCacheFilePath());

        if (dataUseUri) {
            allCacheFileList = uriCacheFileManager.updateChapterFileList(collectionPath, allCacheFileList);
        } else {
            allCacheFileList = pathCacheFileManager.updateChapterFileList(collectionPath, allCacheFileList);
        }

        return allCacheFileList;
    }

    @Override
    public List<CacheFile> getSelectedCacheFileList() {
        selectedCacheFileList = pathCacheFileManager.getSelectedCacheFileList(allCacheFileList, selectedCacheFileList);
        return selectedCacheFileList;
    }

    @Override
    public List<CacheFile> getAllCacheFileList() {
        return allCacheFileList;
    }

    @Override
    public CacheFileListAdapter getCacheFileListAdapter() {
        return cacheFileListAdapter;
    }

    @Override
    public ICacheFileManager getPathCacheFileManager() {
        return pathCacheFileManager;
    }

    @Override
    public void selectAllCacheFile(boolean status) {
        if (multipleSelectionMode) {
            pathCacheFileManager.setBoxChecked(allCacheFileList, cacheFileListAdapter, status);
            refreshCacheFileList();
        }
    }

    @Override
    public void openCloseMultipleMode(@Nullable CacheFile cacheFile, boolean status) {
        //长按进行多选模式切换
        multipleSelectionMode = status;
        //显示隐藏checkbox
        pathCacheFileManager.setBoxVisible(allCacheFileList, null, multipleSelectionMode);

        abstractMainActivity.handleShowHide(multipleSelectionMode);

        //如果是多选模式则勾选当前长按的选项
        if (multipleSelectionMode) {
            //选择不是返回item
            if (cacheFile != null && cacheFile.getFlag() != BaseCacheFileManager.FLAG_CACHE_FILE_BACK) {
                cacheFile.setBoxCheck(true);
            }
        }
        //刷新
        refreshCacheFileList();
    }

    @Override
    public void openCloseMultipleMode(boolean status) {
        openCloseMultipleMode(null, status);
    }

    @Override
    public boolean isMultipleSelectionMode() {
        return multipleSelectionMode;
    }

    @Override
    public void refreshCacheFileList() {
        pathCacheFileManager.refreshCacheFileList(cacheFileListAdapter);
    }

    @Override
    public List<CacheFile> setWholeVisible(boolean state) {
        return pathCacheFileManager.setWholeVisible(allCacheFileList, state);
    }


    @Override
    public boolean onBackPressed() {
        //如果当前是多选模式则先退出多选模式
        if (multipleSelectionMode) {
            openCloseMultipleMode(false);
            return true;
        }

        if (allCacheFileList != null && allCacheFileList.size() > 0) {
            Integer flag = allCacheFileList.get(0).getFlag();
            if (flag != BaseCacheFileManager.FLAG_CACHE_FILE_COLLECTION) {
                updateCollectionFileList();
                refreshCacheFileList();
                return true;
            }
        }

        return false;
    }


    @SuppressLint("WrongConstant")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //保存这个uri目录的访问权限
        if (VersionTool.isAndroid11()) {
            if (requestCode == PermissionsTools.PERMISSION_REQUEST_CODE) {
                Uri uri;
                if ((uri = data.getData()) != null) {
                    mActivity.getContentResolver()
                            .takePersistableUriPermission(uri,
                                    data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                            );
                }
                //获取数据刷新列表
                updateCollectionFileList();
                refreshCacheFileList();

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
