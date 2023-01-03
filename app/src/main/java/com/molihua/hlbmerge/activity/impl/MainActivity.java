package com.molihua.hlbmerge.activity.impl;

import android.content.Intent;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.hjq.permissions.OnPermissionCallback;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.activity.AbstractMainActivity;
import com.molihua.hlbmerge.adapter.CacheFileListAdapter;
import com.molihua.hlbmerge.dao.ConfigData;
import com.molihua.hlbmerge.entity.CacheFile;
import com.molihua.hlbmerge.fragment.AbstractMainFfmpegFragment;
import com.molihua.hlbmerge.fragment.AbstractMainFileShowFragment;
import com.molihua.hlbmerge.fragment.AbstractMainHandleFragment;
import com.molihua.hlbmerge.fragment.AbstractMainTitlebarFragment;
import com.molihua.hlbmerge.fragment.impl.MainCompleteFragment;
import com.molihua.hlbmerge.fragment.impl.MainFfmpegFragment;
import com.molihua.hlbmerge.fragment.impl.MainFileShowFragment;
import com.molihua.hlbmerge.fragment.impl.MainHandleFragment;
import com.molihua.hlbmerge.fragment.impl.MainTitlebarFragment;
import com.molihua.hlbmerge.service.ICacheFileManager;
import com.molihua.hlbmerge.utils.FragmentTools;
import com.molihua.hlbmerge.utils.GeneralTools;
import com.molihua.hlbmerge.utils.LConstants;
import com.molihua.hlbmerge.utils.UMTools;
import com.molihua.hlbmerge.utils.UpdataTools;
import com.molihuan.pathselector.fragment.impl.PathSelectFragment;
import com.molihuan.pathselector.utils.FileTools;
import com.molihuan.pathselector.utils.Mtools;
import com.molihuan.pathselector.utils.PermissionsTools;
import com.umeng.analytics.MobclickAgent;
import com.xuexiang.xui.adapter.FragmentAdapter;
import com.xuexiang.xui.widget.searchview.MaterialSearchView;

import java.util.List;

public class MainActivity extends AbstractMainActivity implements NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

    private BottomNavigationView bottomNavigView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ViewPager viewPager;

    private AbstractMainTitlebarFragment mainTitlebarFragment;
    private AbstractMainFileShowFragment mainFileShowFragment;
    private AbstractMainFfmpegFragment mainFfmpegFragment;
    private MainCompleteFragment mainCompleteFragment;
    private AbstractMainHandleFragment mainHandleFragment;

    private long firstBackTime;


    @Override
    public int setContentViewID() {
        //防止键盘的弹出将布局顶上去
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return R.layout.activity_main;
    }

    @Override
    public void getComponents() {
        bottomNavigView = findViewById(R.id.bottom_navigationview_body_viewpager);
        drawerLayout = findViewById(R.id.side_container_drawerlayout);
        navigationView = findViewById(R.id.side_navigationview);
        viewPager = findViewById(R.id.main_view_pager);

    }


    @Override
    public void initData() {
        //友盟初始化
        UMTools.init(this);
        //自动周期检测更新
        UpdataTools.autoCheckUpdata(this);
        //存储权限的申请
        PermissionsTools.generalPermissionsOfStorage(this, new OnPermissionCallback() {
            @Override
            public void onGranted(@NonNull List<String> permissions, boolean all) {
                boolean dataUseUri = FileTools.underAndroidDataUseUri(ConfigData.getCacheFilePath());
                if (!dataUseUri) {
                    //获取数据刷新列表
                    updateCollectionFileList();
                    refreshCacheFileList();
                }
            }
        });

        PermissionsTools.specialPermissionsOfStorageWithDialog(this, true, new OnPermissionCallback() {
            @Override
            public void onGranted(@NonNull List<String> permissions, boolean all) {
                boolean dataUseUri = FileTools.underAndroidDataUseUri(ConfigData.getCacheFilePath());
                if (!dataUseUri) {
                    //获取数据刷新列表
                    updateCollectionFileList();
                    refreshCacheFileList();
                }
            }
        });

        mainTitlebarFragment = new MainTitlebarFragment();
        mainFileShowFragment = new MainFileShowFragment();
        mainFfmpegFragment = new MainFfmpegFragment();
        mainCompleteFragment = new MainCompleteFragment();
        mainHandleFragment = new MainHandleFragment();

    }

    @Override
    public void initView() {

        //加载主显示区
        FragmentAdapter<Fragment> adapter = new FragmentAdapter<>(getSupportFragmentManager());
        adapter.addFragment(mainFileShowFragment, "主页");
        adapter.addFragment(mainCompleteFragment, "完成文件");
        adapter.addFragment(mainFfmpegFragment, "工具");
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);

        //加载titlebar
        FragmentTools.fragmentShowHide(
                getSupportFragmentManager(),
                R.id.frameLayout_main_titlebar_area,
                mainTitlebarFragment,
                LConstants.TAG_FRAGMENT_MAIN_TITLEBAR,
                true
        );

    }

    @Override
    public void setListeners() {
        bottomNavigView.setOnItemSelectedListener(this::onNavigationItemSelected);
        navigationView.setNavigationItemSelectedListener(this);
        viewPager.addOnPageChangeListener(this);

    }

    /**
     * 底部导航按钮监听
     *
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent = null;
        int id = item.getItemId();
        if (id == R.id.item_home) {
            viewPager.setCurrentItem(0, true);
        } else if (id == R.id.item_complete_video_list) {
            viewPager.setCurrentItem(1, true);
        } else if (id == R.id.item_ffmpeg) {
            viewPager.setCurrentItem(2, true);
        } else if (id == R.id.item_setting) {
            intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.item_teach) {
            GeneralTools.jumpBrowser(this, LConstants.URL_BILIBILI_HOMEPAGE);
        } else if (id == R.id.item_aboutus) {
            intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.item_updatalog) {
            intent = new Intent(this, HtmlActivity.class);
            intent.putExtra("url", "file:///android_asset/updataLog.html");
            intent.putExtra("title", "更新日志");
            startActivity(intent);
        } else if (id == R.id.item_exitapp) {
            MobclickAgent.onKillProcess(this);
            finish();
            System.exit(0);
        }
        //侧滑菜单关闭
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void onBackPressed() {
        //关闭侧滑菜单
        if (drawerLayout.isOpen()) {
            showHideNavigation(false);
            return;
        }

        if (viewPager.getCurrentItem() == 0 && mainTitlebarFragment != null && mainTitlebarFragment.onBackPressed()) {
            return;
        }

        if (viewPager.getCurrentItem() == 0 && mainFileShowFragment != null && mainFileShowFragment.onBackPressed()) {
            return;
        }

        if (viewPager.getCurrentItem() == 1 && mainCompleteFragment != null && mainCompleteFragment.onBackPressed()) {
            return;
        }

        //按两次返回键退出程序
        if (System.currentTimeMillis() - firstBackTime > 2000) {
            Mtools.toast("再按一次返回键退出程序");
            firstBackTime = System.currentTimeMillis();
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        MobclickAgent.onKillProcess(this);
        super.onDestroy();
    }

    @Override
    public void handleShowHide(boolean isShow) {
        //加载handle
        FragmentTools.fragmentShowHide(
                getSupportFragmentManager(),
                R.id.frameLayout_main_handle_area,
                mainHandleFragment,
                LConstants.TAG_FRAGMENT_MAIN_HANDLE,
                isShow
        );
    }


    @Override
    public void onPageSelected(int position) {

        switch (position) {
            case 0:
                showHideImgView(true);
                handleShowHide(isMultipleSelectionMode());
                break;
            case 1:
                showHideSearchView(false);
                showTitleImgView(MainTitlebarFragment.ImgView.REFRESH);
                handleShowHide(false);
                break;
            case 2:
                showHideSearchView(false);
                showHideImgView(false);
                handleShowHide(false);

                break;
            default:
        }

        MenuItem item = bottomNavigView.getMenu().getItem(position);
        setMainTitle(item.getTitle().toString());
        item.setChecked(true);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public BottomNavigationView getBottomNavigView() {
        return bottomNavigView;
    }

    @Override
    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    @Override
    public NavigationView getNavigationView() {
        return navigationView;
    }

    @Override
    public ViewPager getViewPager() {
        return viewPager;
    }

    @Override
    public AbstractMainFileShowFragment getMainFileShowFragment() {
        return mainFileShowFragment;
    }

    @Override
    public AbstractMainTitlebarFragment getMainTitlebarFragment() {
        return mainTitlebarFragment;
    }

    @Override
    public AbstractMainFfmpegFragment getMainFfmpegFragment() {
        return mainFfmpegFragment;
    }

    @Override
    public MainCompleteFragment getMainCompleteFragment() {
        return mainCompleteFragment;
    }

    @Override
    public AbstractMainHandleFragment getMainHandleFragment() {
        return mainHandleFragment;
    }

    @Override
    public PathSelectFragment getCompletePathSelectFragment() {
        return mainCompleteFragment.getPathSelectFragment();
    }

    @Override
    public void refreshCompleteFileList() {
        mainCompleteFragment.refreshFileList();
    }

    @Override
    public void showHideNavigation(boolean status) {
        if (status) {
            drawerLayout.openDrawer(GravityCompat.START);
        } else {
            drawerLayout.close();
        }
    }

    @Override
    public void setMainTitle(String text) {
        mainTitlebarFragment.setMainTitle(text);
    }

    @Override
    public void showHideImgView(boolean status) {
        mainTitlebarFragment.showHideImgView(status);
    }

    @Override
    public void showTitleImgView(MainTitlebarFragment.ImgView showImg) {
        mainTitlebarFragment.showTitleImgView(showImg);
    }


    @Override
    public void showHideSearchView(boolean status) {
        mainTitlebarFragment.showHideSearchView(status);
    }

    @Override
    public MaterialSearchView getSearchView() {
        return mainTitlebarFragment.getSearchView();
    }

    @Override
    public List<CacheFile> updateCollectionFileList() {
        return mainFileShowFragment.updateCollectionFileList();
    }

    @Override
    public List<CacheFile> updateChapterFileList() {
        return mainFileShowFragment.updateChapterFileList();
    }

    @Override
    public List<CacheFile> updateChapterFileList(String collectionPath) {
        return mainFileShowFragment.updateChapterFileList(collectionPath);
    }

    @Override
    public List<CacheFile> getSelectedCacheFileList() {
        return mainFileShowFragment.getSelectedCacheFileList();
    }

    @Override
    public List<CacheFile> getAllCacheFileList() {
        return mainFileShowFragment.getAllCacheFileList();
    }

    @Override
    public CacheFileListAdapter getCacheFileListAdapter() {
        return mainFileShowFragment.getCacheFileListAdapter();
    }

    @Override
    public ICacheFileManager getPathCacheFileManager() {
        return mainFileShowFragment.getPathCacheFileManager();
    }

    @Override
    public void selectAllCacheFile(boolean status) {
        mainFileShowFragment.selectAllCacheFile(status);
    }

    @Override
    public void openCloseMultipleMode(@Nullable CacheFile cacheFile, boolean status) {
        mainFileShowFragment.openCloseMultipleMode(cacheFile, status);
    }

    @Override
    public void openCloseMultipleMode(boolean status) {
        mainFileShowFragment.openCloseMultipleMode(status);
    }

    @Override
    public boolean isMultipleSelectionMode() {
        return mainFileShowFragment.isMultipleSelectionMode();
    }

    @Override
    public void refreshCacheFileList() {
        mainFileShowFragment.refreshCacheFileList();
    }

    @Override
    public List<CacheFile> setWholeVisible(boolean state) {
        return mainFileShowFragment.setWholeVisible(state);
    }
}