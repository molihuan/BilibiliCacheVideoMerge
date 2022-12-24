package com.molihua.hlbmerge.fragment.impl;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.molihuan.utilcode.util.RegexUtils;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.entity.CacheFile;
import com.molihua.hlbmerge.fragment.AbstractMainTitlebarFragment;
import com.molihua.hlbmerge.service.BaseCacheFileManager;
import com.molihua.hlbmerge.utils.LConstants;
import com.xuexiang.xui.widget.searchview.MaterialSearchView;

import java.util.List;

/**
 * @ClassName: MainTitlebarFragment
 * @Author: molihuan
 * @Date: 2022/12/20/16:33
 * @Description:
 */
public class MainTitlebarFragment extends AbstractMainTitlebarFragment implements View.OnClickListener, MaterialSearchView.SearchViewListener, MaterialSearchView.OnQueryTextListener {

    private ImageView navigatImgView;
    private ImageView refreshImgView;
    private ImageView searchImgView;
    private TextView mainTitleTv;

    private MaterialSearchView searchView;


    @Override
    public int setFragmentViewId() {
        return R.layout.fragment_main_titlebar;
    }

    @Override
    public void getComponents(View view) {
        navigatImgView = view.findViewById(R.id.imgv_navigation_titlebar);
        refreshImgView = view.findViewById(R.id.imgv_refresh_titlebar);
        searchImgView = view.findViewById(R.id.imgv_seach_titlebar);
        mainTitleTv = view.findViewById(R.id.tv_main_title_titlebar);
        searchView = view.findViewById(R.id.search_view);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        searchView.setVoiceSearch(false);
        searchView.setEllipsize(true);
    }

    @Override
    public void setListeners() {
        navigatImgView.setOnClickListener(this);
        refreshImgView.setOnClickListener(this);
        searchImgView.setOnClickListener(this);
        searchView.setOnSearchViewListener(this);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitOnClick(true);
    }

    @Override
    public void showHideSearchView(boolean status) {
        if (status) {
            searchView.showSearch();
        } else {
            searchView.closeSearch();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imgv_navigation_titlebar) {
            //打开左边侧滑菜单
            abstractMainActivity.showHideNavigation(true);
        } else if (id == R.id.imgv_refresh_titlebar) {

            //多选模式不允许刷新
            if (abstractMainActivity.isMultipleSelectionMode()) {
                return;
            }

            CacheFile cacheFile = abstractMainActivity.getAllCacheFileList().get(0);
            if (cacheFile.getFlag() == BaseCacheFileManager.FLAG_CACHE_FILE_COLLECTION) {
                abstractMainActivity.updateCollectionFileList();
            } else {
                abstractMainActivity.updateChapterFileList();
            }

            abstractMainActivity.refreshCacheFileList();

        } else if (id == R.id.imgv_seach_titlebar) {

            //多选模式不允许搜索
            if (abstractMainActivity.isMultipleSelectionMode()) {
                return;
            }
            //打开了就关闭
            if (searchView.isSearchOpen()) {
                showHideSearchView(false);
            } else {
                showHideSearchView(true);
            }


        }

    }

    /**
     * 关键字过滤
     *
     * @param key
     * @return
     */
    public List<CacheFile> filterCacheFileList(String key) {

        List<CacheFile> allCacheFileList = abstractMainActivity.getAllCacheFileList();

        //将关键字处理成正则
        key = ".*" + key.replace(LConstants.SPECIAL_CHARACTERS_REGULAR_RULE, "") + ".*";

        CacheFile cacheFile;

        for (int i = 0; i < allCacheFileList.size(); i++) {
            cacheFile = allCacheFileList.get(i);

            switch (cacheFile.getFlag()) {
                case BaseCacheFileManager.FLAG_CACHE_FILE_COLLECTION:
                    if (RegexUtils.isMatch(key, cacheFile.getCollectionName())) {
                        cacheFile.setWholeVisibility(View.VISIBLE);
                    } else {
                        cacheFile.setWholeVisibility(View.GONE);
                    }
                    break;
                case BaseCacheFileManager.FLAG_CACHE_FILE_CHAPTER:
                    if (RegexUtils.isMatch(key, cacheFile.getChapterName())) {
                        cacheFile.setWholeVisibility(View.VISIBLE);
                    } else {
                        cacheFile.setWholeVisibility(View.GONE);
                    }
                    break;
                case BaseCacheFileManager.FLAG_CACHE_FILE_BACK:
                    cacheFile.setWholeVisibility(View.VISIBLE);
                    break;
                default:
                    
            }

        }

        return allCacheFileList;
    }


    @Override
    public boolean onBackPressed() {

        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
            return true;
        }

        return false;
    }

    @Override
    public void onSearchViewShown() {
        mainTitleTv.setVisibility(View.INVISIBLE);
        refreshImgView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSearchViewClosed() {
        mainTitleTv.setVisibility(View.VISIBLE);
        refreshImgView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filterCacheFileList(newText);
        abstractMainActivity.refreshCacheFileList();
        return false;
    }

    @Override
    public MaterialSearchView getSearchView() {
        return searchView;
    }

    @Override
    public void setMainTitle(String text) {
        mainTitleTv.setText(text);
    }

    @Override
    public void showHideImgView(boolean status) {
        if (status) {
            refreshImgView.setVisibility(View.VISIBLE);
            searchImgView.setVisibility(View.VISIBLE);
        } else {
            refreshImgView.setVisibility(View.INVISIBLE);
            searchImgView.setVisibility(View.INVISIBLE);
        }
    }


}
