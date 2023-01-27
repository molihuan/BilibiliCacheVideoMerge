package com.molihua.hlbmerge.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.molihua.hlbmerge.fragment.AbstractMainFfmpegFragment;
import com.molihua.hlbmerge.fragment.AbstractMainFileShowFragment;
import com.molihua.hlbmerge.fragment.AbstractMainHandleFragment;
import com.molihua.hlbmerge.fragment.AbstractMainTitlebarFragment;
import com.molihua.hlbmerge.fragment.impl.MainCompleteFragment;
import com.molihua.hlbmerge.interfaces.IMainFileShowFragment;
import com.molihua.hlbmerge.interfaces.IMainTitlebarFragment;
import com.molihuan.pathselector.fragment.impl.PathSelectFragment;
import com.molihuan.pathselector.utils.PermissionsTools;
import com.molihuan.pathselector.utils.VersionTool;

/**
 * @ClassName: AbstractMainActivity
 * @Author: molihuan
 * @Date: 2022/12/20/17:10
 * @Description:
 */
public abstract class AbstractMainActivity extends BaseActivity implements IMainTitlebarFragment, IMainFileShowFragment {
    public abstract BottomNavigationView getBottomNavigView();

    public abstract DrawerLayout getDrawerLayout();

    public abstract NavigationView getNavigationView();

    public abstract ViewPager getViewPager();

    public abstract void showHideNavigation(boolean status);

    public abstract void handleShowHide(boolean isShow);

    public abstract AbstractMainFileShowFragment getMainFileShowFragment();

    public abstract AbstractMainTitlebarFragment getMainTitlebarFragment();

    public abstract AbstractMainFfmpegFragment getMainFfmpegFragment();

    public abstract MainCompleteFragment getMainCompleteFragment();

    public abstract AbstractMainHandleFragment getMainHandleFragment();

    public abstract PathSelectFragment getCompletePathSelectFragment();

    public abstract void refreshCompleteFileList();


    @Override
    @SuppressLint("WrongConstant")
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //保存这个uri目录的访问权限
        if (VersionTool.isAndroid11()) {
            if (requestCode == PermissionsTools.PERMISSION_REQUEST_CODE) {
                if (data != null) {
                    Uri uri;
                    if ((uri = data.getData()) != null) {
                        getContentResolver()
                                .takePersistableUriPermission(uri,
                                        data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                                );
                    }
                    //获取数据刷新列表
                    updateCollectionFileList();
                    refreshCacheFileList();
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    

}
