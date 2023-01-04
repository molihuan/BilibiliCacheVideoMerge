package com.molihua.hlbmerge;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.molihua.hlbmerge.activities.AboutActivity;
import com.molihua.hlbmerge.activities.LoadingAssetsHTMLActivity;
import com.molihua.hlbmerge.activities.PlayVideoActivity;
import com.molihua.hlbmerge.activities.SettingsActivity;
import com.molihua.hlbmerge.entities.ListItemMain;
import com.molihua.hlbmerge.fragments.CompleteVideoListFragment;
import com.molihua.hlbmerge.fragments.EmptyFragment;
import com.molihua.hlbmerge.fragments.FFmpegCmdFragment;
import com.molihua.hlbmerge.fragments.MainShowListFragment;
import com.molihua.hlbmerge.fragments.MoreChooseFragment;
import com.molihua.hlbmerge.interfaces.IActivityAndFragment;
import com.molihua.hlbmerge.utils.FragmentTools;
import com.molihua.hlbmerge.utils.JsonTools;
import com.molihua.hlbmerge.utils.MLHInitConfig;
import com.molihua.hlbmerge.utils.PathTools;
import com.molihua.hlbmerge.utils.PermissionsTools;
import com.molihua.hlbmerge.utils.WebTools;
import com.molihuan.pathselector.PathSelector;
import com.molihuan.pathselector.adapters.FileListAdapter;
import com.molihuan.pathselector.adapters.TabbarFileListAdapter;
import com.molihuan.pathselector.dao.SelectOptions;
import com.molihuan.pathselector.entities.FileBean;
import com.molihuan.pathselector.utils.Constants;
import com.xuexiang.xui.adapter.FragmentAdapter;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.searchview.MaterialSearchView;
import com.xuexiang.xui.widget.toast.XToast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IActivityAndFragment, ViewPager.OnPageChangeListener, NavigationView.OnNavigationItemSelectedListener {

    private MaterialSearchView search_view;//菜单搜索按钮
    private Toolbar body_toolbar;//tool_bar
    private DrawerLayout drawerLayout;//侧划页面布局
    private NavigationView navigationView;//侧滑视图
    private ViewPager body_view_pager;//ViewPager视图
    private BottomNavigationView bottom_navigationview_body_viewpager;//BottomNavigationView视图
    private MainShowListFragment mainShowListFragment;//主显示列表
    private CompleteVideoListFragment completeVideoListFragment;
    private MoreChooseFragment moreChooseFragment;
    //获取原始总数据ListItemMains用作数据的动态显示
    private List<ListItemMain> listItemMainsCache;
    //备份获取到的数据作为缓存
    private List<ListItemMain> listItemMainsBack = new ArrayList<>();
    //获取当前p的合集路径作为缓存
    private String parentPathCache;

    public MainShowListFragment getMainShowListFragment() {
        return mainShowListFragment;
    }

    //去除一些特殊的字符的正则表达式
    public static String regEx = "[\t\r\n`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】\"‘；：”“’。， 、？-]*";//   /r去换行
    private long firstBackTime;//第一次按返回键时间

    private boolean initCompetFileList = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PermissionsTools.getAllNeedPermissions(this, getContentResolver());//所有存储访问权限
        MLHInitConfig.initConfig(this);//初始化获取配置数据

        setContentView(R.layout.activity_main);

        getComponents();//获取组件
        setListeners();//设置监听
        initData();//初始化数据
    }


    private void initData() {


        try {
            if (MLHInitConfig.isNullUserCustomPath()) {
                JsonTools.initJson(getContentResolver(), MainActivity.this, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //侧滑菜单宽度
        ViewGroup.LayoutParams params = navigationView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels / 9 * 5; //屏幕的5 / 9
        navigationView.setLayoutParams(params);
        fragmentInit();

    }

    //fragment初始化
    private void fragmentInit() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();//开启事务
        mainShowListFragment = new MainShowListFragment();//实例化fragment
        //completeVideoListFragment=new CompleteVideoListFragment();
        //fragmentTransaction.add(R.id.main_showarea, mainShowListFragment).commitAllowingStateLoss();

        //主页
        FragmentAdapter<Fragment> adapter = new FragmentAdapter<Fragment>(getSupportFragmentManager());
        adapter.addFragment(mainShowListFragment, "主页");
        adapter.addFragment(new EmptyFragment(), "完成文件");
        adapter.addFragment(new FFmpegCmdFragment(), "工具");
        body_view_pager.setOffscreenPageLimit(3);
        body_view_pager.setAdapter(adapter);
        body_view_pager.addOnPageChangeListener(this);
        bottom_navigationview_body_viewpager.setOnItemSelectedListener(this::onNavigationItemSelected);

    }


    //获取组件
    private void getComponents() {
        body_toolbar = findViewById(R.id.body_toolbar);
        setSupportActionBar(body_toolbar);//支持ActionBar
        search_view = findViewById(R.id.search_view);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationview);
        body_view_pager = findViewById(R.id.body_view_pager);
        bottom_navigationview_body_viewpager = findViewById(R.id.bottom_navigationview_body_viewpager);
    }

    //设置监听
    private void setListeners() {
        search_viewListener();//搜索框监听
        navigationView.setNavigationItemSelectedListener(this);//侧滑栏item监听
    }


    //菜单监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //侧滑菜单
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);//打开左边侧滑菜单
                break;
            //刷新按钮
            case R.id.btn_refresh:

                refreshMainShowListView();
                csView();
                break;
        }

        return true;
    }

    private void csView() {


    }


    /**
     * 刷新MainShowListView
     */
    public void refreshMainShowListView() {
        int currentItem = body_view_pager.getCurrentItem();
        switch (currentItem) {
            case 0:
                if (MLHInitConfig.isNullUserCustomPath()) {
                    JsonTools.initJson(getContentResolver(), MainActivity.this, 0);
                }
                switch (mainShowListFragment.FLAG_PAGE) {
                    case 0:
                        mainShowListFragment.CollectionlistViewShow();//从文件中读取显示所有数据
                        break;
                    case 1:
                        //获取p listview item的上一级目录路径

                        try {
                            if (StringUtils.isEmpty(parentPathCache)) {//判断parentPathCache是否为null或空
                                //获取p listview item的上一级目录路径
                                parentPathCache = mainShowListFragment.getListItemMains().get(0).getParentPath();
                            }
                            mainShowListFragment.ChapterlistViewShow(parentPathCache);//显示所有数据
                        } catch (Exception e) {//处理异常
                            XToast.error(MainActivity.this, "发生了致命的错误!!!非常抱歉").show();
                            mainShowListFragment.clearListItemMains();
                            mainShowListFragment.CollectionlistViewShow();//从文件中读取显示所有数据
                            mainShowListFragment.FLAG_PAGE = 0;
                            e.printStackTrace();
                        }

                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + mainShowListFragment.FLAG_PAGE);
                }
                break;
            case 1:
                //completeVideoListFragment.clearVideoListItems();
                //completeVideoListFragment.listViewDataShow();
                break;
            case 2:
                break;
        }


    }


    //返回键监听
    @Override
    public void onBackPressed() {

        parentPathCache = null;//清除缓存

        //搜索框关闭
        if (search_view.isSearchOpen()) {
            search_view.closeSearch();
            return;
        }
        if (mainShowListFragment.getListItemMains() != null && mainShowListFragment.getListItemMains().size() > 0) {
            //获取CheckBox是否显示状态
            int checkBoxVisibility = mainShowListFragment.getListItemMains().get(0).getCheckBoxVisibility();
            //关闭CheckBox和MoreChoose按钮
            if (checkBoxVisibility == View.VISIBLE) {
                mainShowListFragment.showCheckBoxMoreChoose(false);
                return;
            }
        }
        //从p listview退到合集listview
        if (MainShowListFragment.FLAG_PAGE == 1) {
            mainShowListFragment.CollectionlistViewShow();//显示合集listview
            MainShowListFragment.FLAG_PAGE = 0;
            return;
        }

        //关闭左边侧滑栏
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }

        //按两次返回键退出程序-----------必须放最下面
        if (System.currentTimeMillis() - firstBackTime > 2000) {
            ToastUtils.make().show("再按一次返回键退出程序");
            firstBackTime = System.currentTimeMillis();
            return;
        }

        super.onBackPressed();

    }

    //提供fragment调用方法的接口
    @Override
    public Object invokeFuncAiF(int functionCode) {
        switch (functionCode) {
            case 0:
                showMoreChoose(true);
                return null;
            case 1:
                showMoreChoose(false);
                return null;
            case 2:
                if (search_view.isSearchOpen()) {
                    search_view.closeSearch();
                    return true;
                }
                return false;
            default:
                return null;
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_setting://设置
                Intent settingIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingIntent);
                break;
            case R.id.item_teach://教程

                String intenUrl = "https://space.bilibili.com/454222981";
                ToastUtils.make().show("正在跳转:" + intenUrl);
                WebTools.openBrowser(MainActivity.this, intenUrl);
                break;
            case R.id.item_aboutus://关于我们
                Intent intentAbout = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intentAbout);
                break;
            case R.id.item_exitapp://退出
                finish(); //退出activity
                System.exit(0);//退出应用
                break;
            case R.id.item_updatalog://更新日志
                Intent intentUpdata = new Intent(MainActivity.this, LoadingAssetsHTMLActivity.class);
                intentUpdata.putExtra("HTMLUrl", "file:///android_asset/upDataLog.html");
                intentUpdata.putExtra("title", "更新日志");
                startActivity(intentUpdata);
                break;
            case R.id.item_home://主页
                body_view_pager.setCurrentItem(0, true);
                break;
            case R.id.item_complete_video_list://合并完成列表
                body_view_pager.setCurrentItem(1, true);
//                mainShowListFragment.showCheckBoxMoreChoose(false);
//                refreshMainShowListView();
                ToastUtils.make().show("如果导出了弹幕可以挂载弹幕哦!");
                break;
            case R.id.item_ffmpeg://ffmpeg
                body_view_pager.setCurrentItem(2, true);
                mainShowListFragment.showCheckBoxMoreChoose(false);
                break;

            default:

                break;
        }
        drawerLayout.closeDrawers();//侧滑菜单关闭
        return true;
    }


    /**
     * 文件选择器
     */
    public void openFileChoose() {

        PathSelector.build((FragmentActivity) this, Constants.BUILD_FRAGMENT)
                .frameLayoutID(R.id.complate_file_ml)
                .setShowFileTypes("mp4", "xml", "mp3", "")
                .requestCode(100)
                .showToolBarFragment(false)
                .setRootPath(PathTools.getOutputPath())
                .setToolbarMainTitle("选择视频")
                .setFileItemListener(new SelectOptions.onFileItemListener() {
                    @Override
                    public boolean onFileItemClick(View view, String currentPath, List<FileBean> fileBeanList, List<String> callBackData, TabbarFileListAdapter tabbarAdapter, FileListAdapter fileAdapter, FileBean fileBean) {
                        if (fileBean.isFile()) {
                            String fileExtension = fileBean.getFileExtension();
                            if ("mp4".equals(fileExtension) || "mp3".equals(fileExtension)) {
                                Intent intent = new Intent(MainActivity.this, PlayVideoActivity.class);
                                intent.putExtra("videoPath", fileBean.getFilePath());
                                startActivity(intent);
                            } else {
                                ToastUtils.make().show("选择错误");
                            }
                        }
                        return false;
                    }

                    @Override
                    public boolean onLongFileItemClick(View view, String currentPath, List<FileBean> fileBeanList, List<String> callBackData, TabbarFileListAdapter tabbarAdapter, FileListAdapter fileAdapter, FileBean fileBean) {
                        return false;
                    }
                })
                .setMoreChooseItems(new String[]{"全选", "删除"},
                        new SelectOptions.onMoreChooseItemsListener() {
                            @Override
                            public void onItemsClick(View view, String currentPath, List<FileBean> fileBeanList, List<String> callBackData, TabbarFileListAdapter tabbarAdapter, FileListAdapter fileAdapter, List<FileBean> callBackFileBeanList) {
                                TextView textView = (TextView) view;
                                if (textView.getText().equals("全选")) {
                                    textView.setText("取消全选");

                                } else {
                                    textView.setText("全选");

                                }
                            }
                        },
                        new SelectOptions.onMoreChooseItemsListener() {
                            @Override
                            public void onItemsClick(View view, String currentPath, List<FileBean> fileBeanList, List<String> callBackData, TabbarFileListAdapter tabbarAdapter, FileListAdapter fileAdapter, List<FileBean> callBackFileBeanList) {
                                new MaterialDialog.Builder(MainActivity.this)
                                        .iconRes(R.drawable.xui_ic_default_tip_btn)
                                        .title("提示")
                                        .content("你确定要删除吗?")
                                        .positiveText("确定")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                List<FileBean> data = fileAdapter.getData();
                                                for (int i = data.size() - 1; i >= 0; i--) {
                                                    if (data.get(i).isChecked()) {
                                                        FileUtils.delete(data.get(i).getFilePath());
                                                        data.remove(i);
                                                    }
                                                }
                                                fileAdapter.notifyDataSetChanged();
                                            }
                                        })
                                        .negativeText("取消")
                                        .show();
                            }
                        }
                )
                .start();
        initCompetFileList = true;


    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        MenuItem item = bottom_navigationview_body_viewpager.getMenu().getItem(position);
        if (position == 1 && !initCompetFileList) {
            openFileChoose();
        }
        body_toolbar.setTitle(item.getTitle());
        item.setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //搜索框监听
    private void search_viewListener() {
        search_view.setVoiceSearch(false);//不启用声音搜索
        search_view.setEllipsize(true);


        search_view.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {

            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                //去除特殊符号防止下面的正则表达式错误导致奔溃
                newText = newText.replaceAll(MainActivity.regEx, "");
                //清除listviewitem以防止对下面产生影响
                mainShowListFragment.clearListItemMains();

                if (!StringUtils.isSpace(newText)) {//判断搜索框是否为空
                    listItemMainsCache.addAll(listItemMainsBack);//复制一个集合必须这样不能直接=

                    //把不符合条件的item清除
                    for (int i = listItemMainsCache.size() - 1; i >= 0; i--) {
                        String name = listItemMainsCache.get(i).getName();
                        if (!name.matches(".*" + newText + ".*")) {
                            listItemMainsCache.remove(i);
                        }
                    }

                    mainShowListFragment.setListItemMains(listItemMainsCache);//设置数据
                    mainShowListFragment.notifyDataAdapter();//刷新Adapter
                } else {
                    refreshMainShowListView();
                }


                return true;
            }
        });
        search_view.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                ToastUtils.make().show("搜索框开启");
                //初始化当前页的数据并缓存备份起来、以便查询的调用、优化性能
                switch (mainShowListFragment.FLAG_PAGE) {
                    case 0:
                        listItemMainsCache = mainShowListFragment.CollectionlistViewShow();//从文件中查所有数据
                        //备份起来、以便查询的调用
                        listItemMainsBack.addAll(listItemMainsCache);
                        break;
                    case 1:
                        //获取p listview item的上一级目录路径
                        parentPathCache = mainShowListFragment.getListItemMains().get(0).getParentPath();
                        //获取当前合集中的所有p
                        listItemMainsCache = mainShowListFragment.ChapterlistViewShow(parentPathCache);
                        //备份起来、以便查询的调用
                        listItemMainsBack.addAll(listItemMainsCache);
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + mainShowListFragment.FLAG_PAGE);
                }

            }

            @Override
            public void onSearchViewClosed() {
                ToastUtils.make().show("搜索框关闭");
                //清除缓存并显示数据
                listItemMainsCache.clear();
                listItemMainsBack.clear();

                refreshMainShowListView();

            }
        });
        search_view.setSubmitOnClick(true);
    }

    //返回授权状态
    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //保存这个uri目录的访问权限
        Uri uri;
        if (data == null) {
            return;
        }
        if (requestCode == 11 && (uri = data.getData()) != null) {
            getContentResolver().takePersistableUriPermission(uri, data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));//关键是这里，这个就是保存这个目录的访问权限

            try {
                if (MLHInitConfig.isNullUserCustomPath()) {
                    JsonTools.initJsonSynchronous(getContentResolver(), MainActivity.this, 0);//同步初始化json
                }
                refreshMainShowListView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //显示or隐藏多选按钮
    private void showMoreChoose(boolean state) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();//开启事务
        if (state) {
            moreChooseFragment = FragmentTools.showMoreChooseBtn(fragmentTransaction);
        } else {
            moreChooseFragment = FragmentTools.HiddenMoreChooseBtn(fragmentTransaction);
        }
    }

    //菜单添加
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_titlebar, menu);//获取menu_titlebar菜单并添加到当前页面
        MenuItem item = menu.findItem(R.id.btn_search);//获取menu_titlebar菜单中的btn_search选项
        search_view.setMenuItem(item);//把MaterialSearchView与btn_search选项绑定
        return true;
    }


}