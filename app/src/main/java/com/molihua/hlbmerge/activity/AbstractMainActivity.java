package com.molihua.hlbmerge.activity;

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
}
