package com.coder.ffmpeg;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.tencent.bugly.Bugly;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;
import utils.CPUUtils;
import utils.Utils;

/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity {


    ///////////////////////////////////////////////////////
    //定义------存储权限有关量
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    /**
     * Check permission.
     */
//然后通过一个函数来申请读写存储的权限
    public void checkPermission() {
        try {
            //检测是否有读写的权限
            int permission = ActivityCompat.checkSelfPermission(this,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }

            //安卓11判断是否需要所有文件权限      暂时不需要

            if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager())) {
                //表明已经有这个权限了
            } else {
                //Toast.makeText(MainActivity.this, "为了软件能在Android 11上更好的运行请授予权限,开发者承诺不会获取无关需求的其他信息和添加恶意代码,如有疑问请找专业人士拆包查看。", Toast.LENGTH_LONG).show();
                Android11AllFilePermission();
            }

            //安卓11data目录访问权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Iterator<UriPermission> it = getContentResolver().getPersistedUriPermissions().iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (it.next().isReadPermission()) {
                            break;
                        }
                    } else {
                        Android11Permission();
                        break;
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "读取存储有关的权限给予出错，请联系开发者", Toast.LENGTH_LONG).show();
        }
    }

    ///////////////////////////////////////////////////////
    /**
     * 外部存储根目录
     * ROOTPATH:/storage/emulated/0 或者/storage/sdcard或者/storage/emulated/10
     */
    public static final String ROOTPATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    String mpath = ROOTPATH + "/Android/data/tv.danmaku.bili/download";//初始化合并的路径/storage/emulated/0/Android/data/tv.danmaku.bili/download
    String mpath2 = ROOTPATH + "/Android/data/com.bilibili.app.in/download";
    String BdownloadPath = mpath;
    public static String backPath = ROOTPATH + "/Android/data/tv.danmaku.bili/download";//备份的路径
    /**
     * The Hecheng.
     */
    Button hecheng, /**
     * The Not all choose.
     */
    not_all_choose, /**
     * The All choose.
     */
    all_choose;//刷新和自定义合成取消全选全选按钮控件
    /**
     * The Configurefile path.
     */
    String configurefile_path;//获取软件内部存储file文件夹的路径//配置文件路径
    /**
     * The Cehua title.
     */
    TextView cehuaTitle;
    /**
     * The Swith code.
     */
    int swithCode = 0, /**
     * The Hbzt.
     */
    hbzt = -1;
    private long firstBackTime;//按键时间
    private static final int logShow = 1;
    /**
     * The Log file.
     */
    List<String> logFile;
    /**
     * The M list view.
     */
    ListView mListView;//列表视图
    /**
     * The Adapter.
     */
    listAdapter adapter;//适配器
    /**
     * The M search view.
     */
    SearchView mSearchView;
    /**
     * The Search edit text.
     */
    EditText search_EditText;
    private not_tv_btn_fragment notFragment;
    private yes_tv_btn_fragment yesFragment;
    /**
     * The Mshared preferences.
     */
    SharedPreferences msharedPreferences;
    /**
     * The Mshared prefer editor.
     */
    SharedPreferences.Editor msharedPreferEditor;
    /**
     * The M list.
     */
    List<list_Item> mList = new ArrayList();//类集合list_Item（文字标题：缓存集合的总标题的路径）
    /**
     * The Choose part.
     */
    String choosePart = "/00";
    /**
     * The Download path.
     */
//String downloadPath = "/storage/emulated/0/Android/data/tv.danmaku.bili/download";


    /**
     * The Target path.
     */
    static String targetPath = ROOTPATH + "/bilibili视频合并/temp/download";
    /**
     * The Mpath.
     */


    /**
     * The constant regEx1.
     */
//去除一些特殊的字符的正则表达式
    static String regEx1 = "[\t\r\n`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】\"‘；：”“’。， 、？-]*";//   /r去换行
    /**
     * The Drawer layout.
     */
    DrawerLayout drawerLayout;


    public static String completionPath = ROOTPATH + "/bilibili视频合并/";
    /**
     * The Export barrage xml.
     */
    boolean exportBarrage_xml;//是否导出xml弹幕文件
    /**
     * The Mand p code.
     */
    int MandP_code = 0;//判断是在总标题还是在P下
    /**
     * The Preliminary reading 1 position.
     */
    int PreliminaryReading1position = -1;
    private long startTime;//记录开始时间
    private long endTime;//记录结束时间
    private ProgressDialog mProgressDialog;
    private int singleMany_code = 0;




    ///storage/emulated/0
    public static String csPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    public String dataReadmergePath="";

    //添加右上角菜单三个点，进行回调
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gy, menu);//添加menu_gy菜单

//找到SearchView并配置相关参数
        MenuItem searchItem = menu.findItem(R.id.wengjian_search);
        mSearchView = (SearchView) searchItem.getActionView();
/////////////////////////////////////////////////获取输入框
        int id = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        search_EditText = (EditText) mSearchView.findViewById(id);
/////////////////////////////////////////////改变搜索图标
        int magId = mSearchView.getContext().getResources().getIdentifier("android:id/search_button", null, null);
        ImageView magImage = (ImageView) mSearchView.findViewById(magId);
        magImage.setImageResource(R.drawable.ic_search);//改变搜索图标
//////////////////////////////////////////改变搜索图标


        //设置搜索框展开时是否显示提交按钮，可不显示
        mSearchView.setSubmitButtonEnabled(false);
        //让键盘的回车键设置成搜索
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        //设置提示词
        mSearchView.setQueryHint("请输入关键字");
        // 设置搜索文本监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.replaceAll(regEx1, "");//去除特殊符号防止下面的正则表达式错误导致奔溃
                //                如果newText不是长度为0的字符串
                if (TextUtils.isEmpty(newText)) {
//                    清除ListView的过滤
                    //Refresh();

                    if (MandP_code == 0) {
                        mList.clear();//清除mList中<list_Item>类对象
                        ergodicDirBackPath.clearPath();//清除残留，下面要用
                        PreliminaryReading1();
                    } else {
                        mList.clear();//清除mList中<list_Item>类对象
                        ergodicDirBackPath.clearPath();//清除残留，下面要用
                        PreliminaryReading1();
                        PreliminaryReading2(PreliminaryReading1position);
                    }
                } else {
//                    使用用户输入的内容对ListView的列表项进行过滤


                    if (MandP_code == 0) {
                        mList.clear();//清除mList中<list_Item>类对象
                        ergodicDirBackPath.clearPath();//清除残留，下面要用
                        PreliminaryReading1();
                    } else {
                        mList.clear();//清除mList中<list_Item>类对象
                        ergodicDirBackPath.clearPath();//清除残留，下面要用
                        PreliminaryReading1();
                        PreliminaryReading2(PreliminaryReading1position);
                    }


                    String Title;


                    //List<list_Item> changelist=mList;
                    List<list_Item> changelist = new ArrayList();
                    changelist.addAll(mList);


                    //Log.e("listview有", String.valueOf(changelist.size()));
                    //Log.e("输入为", newText);

                    for (int i = mList.size() - 1; i >= 0; i--)//这是一个坑，会越界，因为下面remove了
                    {
                        Title = mList.get(i).getMC().replaceAll("\n", "");
                        //提取标题

                        if (!Title.matches(".*" + newText + ".*")) {//模糊匹配
                            //Log.e("移除的为", Title);
                            mList.remove(i);
                        }
                    }


                    for (int i = 0; i < mList.size(); i++) {
                        //Log.e("mList", mList.get(i).getMC());
                        Log.e("changelist", mList.get(i).getMC() + mList.get(i).getLJ());
                    }

                    //Log.e("listview还剩", String.valueOf(changelist.size()));
                    adapter = new listAdapter(mList, MainActivity.this);//设置适配器数据
                    mListView.setAdapter(adapter);//添加适配器

                    //mList.clear();
                    //mList.addAll(changelist);


                    //Toast.makeText(MainActivity.this,newText,Toast.LENGTH_LONG).show();
                }

                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);//回调
    }


    //重写onOptionsItemSelected方法，进行菜单item监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {

            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);//打开侧滑菜单
                break;
            case R.id.btn_Refresh://刷新按钮
                //Refresh();
                configureData();
                if (MandP_code == 0) {
                    mList.clear();//清除mList中<list_Item>类对象
                    ergodicDirBackPath.clearPath();//清除残留，下面要用
                    PreliminaryReading1();
                } else {
                    mList.clear();//清除mList中<list_Item>类对象
                    ergodicDirBackPath.clearPath();//清除残留，下面要用
                    PreliminaryReading1();
                    PreliminaryReading2(PreliminaryReading1position);
                }

                break;
            default:

                break;
        }
        return super.onOptionsItemSelected(item);//回调
    }


    /**
     * 安卓11所有权限申请
     */
    private void Android11AllFilePermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.TransparentDialog);
        View permission_dialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.permission_dialog, null);
        Button Permission_dialog_yes = permission_dialog.findViewById(R.id.Permission_dialog_yes);
        Button Permission_dialog_no = permission_dialog.findViewById(R.id.Permission_dialog_no);
        TextView Permission_dialog_Tips = permission_dialog.findViewById(R.id.Permission_dialog_Tips);
        Permission_dialog_Tips.setText("为了软件能在Android 11上更好的运行请授予权限,开发者承诺不会获取无关需求的其他信息和添加恶意代码,如有疑问请找专业人士拆包查看。");
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.setView(permission_dialog).show();
        Permission_dialog_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });
        Permission_dialog_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "如果可以运行的话也可不给权限,这个不是必须的,只是为了适配一些奇怪的手机,但是另外两个是必须的。", Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
            }
        });


    }

    //获取某个文件目录的权限
//方法很简单，使用android.intent.action.OPEN_DOCUMENT_TREE(调用SAF框架的文件选择器选择一个文件夹)的Intent就可以授权了
    private void Android11Permission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.TransparentDialog);
        View permission_dialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.permission_dialog, null);
        Button Permission_dialog_yes = permission_dialog.findViewById(R.id.Permission_dialog_yes);
        Button Permission_dialog_no = permission_dialog.findViewById(R.id.Permission_dialog_no);
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.setView(permission_dialog).show();
        Permission_dialog_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri1 = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fdata");
                Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent1.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                intent1.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri1);
                startActivityForResult(intent1, 11);
                alertDialog.dismiss();
            }
        });
        Permission_dialog_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "请您自行将" + ROOTPATH + "/Android/data/tv.danmaku.bili/下的download文件夹移出/Android/data,再选择合并目录为您移出的download", Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
            }
        });


    }


    //返回授权状态
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri;
        if (data == null) {
            return;
        }
        if (requestCode == 11 && (uri = data.getData()) != null) {
            getContentResolver().takePersistableUriPermission(uri, data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));//关键是这里，这个就是保存这个目录的访问权限
        }

    }

    /**
     * 侧滑菜单
     */
//侧滑菜单相关
    public void cehuaMenu() {
        drawerLayout = findViewById(R.id.drawerlayout);
        NavigationView navigationView = findViewById(R.id.navigationview);


//侧滑菜单宽度
        ViewGroup.LayoutParams params = navigationView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels / 3 * 2; //屏幕的三分之一
        navigationView.setLayoutParams(params);
//侧滑菜单宽度


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);//设置返回键
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);//更改返回键的图标
        }

        //侧滑菜单item监听
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.item_Stool://文件合并拆分
                        Intent intent1 = new Intent(MainActivity.this, splitMerge_Main.class);
                        startActivity(intent1);
                        break;
                    case R.id.item_setting://设置
                        getAllJson();
                        Intent intent2 = new Intent(MainActivity.this, settings.class);
                        startActivity(intent2);
                        break;
                    case R.id.item_teach://教程
                        String intenUrl = "https://b23.tv/sSFTVx";
                        openBrowser(MainActivity.this, intenUrl);
                        //Toast.makeText(MainActivity.this, "暂时还没有此功能哦！", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.item_about://关于我们
                        authorlayout();//关于信息弹窗
                        //Toast.makeText(MainActivity.this,"点击了关于菜单",Toast.LENGTH_LONG).show();
                        break;
                    case R.id.item_exit_app://退出
                        finish(); //退出activity
                        System.exit(0);//退出应用
                        break;
                    case R.id.item_updatalog://更新日志

                        Intent intent3 = new Intent(MainActivity.this, web_updatalog.class);
                        startActivity(intent3);
                        break;
                    case R.id.choose_path:

                        break;
                    default:

                        break;
                }


                drawerLayout.closeDrawers();//侧滑菜单关闭
                return true;
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏      但是cehuaMenu()又加上了
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bugly.init(getApplicationContext(), "d4d84e84bb", false);//bugly调试
        RxFFmpegInvoke.getInstance().setDebug(false);//RxFFmpeg调试


        cehuaMenu();//侧滑菜单
        findID();
        initView();

        checkPermission();//动态申请存储读取权限
        Frame_swith(swithCode);//多选按钮的显示的添加

        //保存选择的合并目录到文件data.xml，并读取data.xml中的合并路径
        configurefile_path = getFilesDir().getAbsolutePath().replace("files", "shared_prefs");//获取软件内部存储shared_prefs件夹的路径//配置文件路径
        //Log.e("shared_prefs视频合并配置文件路径",configurefile_path);
        configureData();
        dataReadmergePath=configureDataRead("data", "mergePath", "");//获取data.xml里面的合并路径




        //Testfun();


        initData();//初始化数据，ListView填充


    }

    private void Testfun() {
//        new Thread(){
//            @Override
//            public void run() {
//                Looper.prepare();
//                Toast.makeText(MainActivity.this,ROOTPATH,Toast.LENGTH_LONG).show();
//                Looper.loop();
//            }
//        }.start();
    }


    private void initView() {

        //删除json文件
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && dataReadmergePath.matches(".+bilibili视频合并/temp/download$")) {
            try {
                deleteFile(targetPath);
            } catch (Exception e) {

            }
        }

        try {
            cehuaTitle.setText("您正在使用" + CPUUtils.getCPUAbi() + "架构");
        } catch (Exception e) {
            cehuaTitle.setText("此软件好像不支持您的设备架构");
        }

    }


    private void findID() {
        all_choose = findViewById(R.id.all_choose);
        not_all_choose = findViewById(R.id.not_all_choose);
        hecheng = findViewById(R.id.hecheng);
        mListView = findViewById(R.id.Main_list);

        NavigationView navigationView = findViewById(R.id.navigationview);
        View headView = navigationView.getHeaderView(0);
        cehuaTitle = headView.findViewById(R.id.cehuaTitle);

        //Log.d("99999999",ref.toString());

    }

    private void Refresh() {
        mList.clear();//清除mList中<list_Item>类对象
        ergodicDirBackPath.clearPath();//清除残留，下面要用
        //path_configurefile();//获取合并目录

        configureData();
        //Log.e("调用了刷新", "------------------");
        initData();//填充数据
    }

    /**
     * On clic button.
     *
     * @param view the view
     */
//对Fragment上的控件进行监听，必须这样写，Oncliclintener会报错
    public void onClic_button(View view) {

        //search_EditText.setText("");//去除搜索框文字
        mSearchView.clearFocus();//取消搜索焦点
        mSearchView.clearAnimation();//取消搜索焦点

        switch (view.getId()) {
            case R.id.not_all_choose://取消全选监听
                //Toast.makeText(MainActivity.this, "目前取消全选有bug哦，麻烦你手动取消吧，给你带来了不好的体验我深感抱歉。", Toast.LENGTH_LONG).show();
                for (listAdapter.ViewHolder i : listAdapter.more_holder) {
                    i.more_checkBox.setChecked(false);//取消选中
                }
                break;
            case R.id.all_choose://全选按钮监听
                //Toast.makeText(MainActivity.this, "目前全选有bug哦，麻烦你手动勾选吧，给你带来了不好的体验我深感抱歉。", Toast.LENGTH_LONG).show();
                for (listAdapter.ViewHolder i : listAdapter.more_holder) {
                    i.more_checkBox.setChecked(true);//选中
                }
                break;
            case R.id.hecheng://批量合并按钮监听
                moreHeCheng();
                break;

        }
    }

    //批量合成
    private void moreHeCheng() {
        if (listAdapter.more_choose.size() != 0) {
            singleMany_code = 1;
            ///////////////////////////////////////////////
            //有坑必须用ProgressDialog下面的方式才能按要求显示
            ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "提示", "正在准备合并资源,时间与合并文件大小有关(50MB——>1秒),请稍后...");
            progressDialog.setCancelable(false);//返回不能取消边缘不能点击
            ///////////////////////////////////////////////
            //必须开线程才能显示ProgressDialog
            new Thread() {
                @Override
                public void run() {
                    for (String wmp : listAdapter.more_choose) {
                        String jtpath = wmp;//合并路径
                        //Log.d("子目录为：", jtpath);
                        //复制选中所有的文件
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && dataReadmergePath.matches(".+bilibili视频合并/temp/download$")) {
                            choosePart = jtpath.replaceAll(ROOTPATH + "/bilibili视频合并/temp/download", "");
                            //Toast.makeText(MainActivity.this,Title,Toast.LENGTH_SHORT).show();

                            Uri uri;
                            if (configureDataRead("data", "settings_switch_chooseBiliVersion", "tv.danmaku.bili").equals("tv.danmaku.bili")) {
                                uri = getUriForPath(BdownloadPath + choosePart);
                            } else {
                                uri = getUriForPath(mpath2 + choosePart);
                            }

                            //Uri uri = getUriForPath(BdownloadPath + choosePart);
                            ergodicFileByUri(MainActivity.this, uri, 1);
                        }
                        try {


                            if (jtpath.matches(mpath + "/[0-9]*[^/]*")) {
                                //Log.e("主标题",jtpath+"6666666666"+mpath+"[0-9]*");
                                ergodicChapter(jtpath);
                            } else {
                                //Log.e("PPPP",jtpath+"6666666666"+mpath+"[0-9]*");
                                ergodicChapter2(wmp, ergodicDirBackPath.readjson(new File(wmp)));
                            }
                        } catch (Exception e) {

                        }


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && dataReadmergePath.matches(".+bilibili视频合并/temp/download$")) {
                            deleteFile(targetPath);
                        }
                        //在子线程中弹出Toast，会报错：java.lang.RuntimeException: Can’t toast on a thread that has not called Looper.prepare()。
                        //解决方式：先调用Looper.prepare();再调用Toast.makeText().show();最后再调用Looper.loop();
                    }
                    singleMany_code = 0;//是批量合成
                    progressDialog.cancel();//等待合并界面清除
                    Looper.prepare();
                    if (hbzt == 0) {
                        Toast.makeText(MainActivity.this, "转换成功\n,文件在“" + ROOTPATH + "/bilibili视频合并”目录下！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "转换可能有点小问题哦！！！\n自行验证。文件在“" + ROOTPATH + "/bilibili视频合并”目录下！", Toast.LENGTH_LONG).show();
                    }
                    Looper.loop();
                    hbzt = -1;
                }
            }.start();
        } else {
            Toast.makeText(MainActivity.this, "您还没有选择呢!", Toast.LENGTH_LONG).show();
        }

    }


    //在指定位置添加Fragment多选和单选
    private void Frame_swith(int framentCode) {
        switch (framentCode) {
            case 0://单个合成  显示大小已经改为0
                notFragment = new not_tv_btn_fragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout_content, notFragment).commitAllowingStateLoss();//Fragment替换
                break;
            case 1://多个合成
                yesFragment = new yes_tv_btn_fragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout_content, yesFragment).commitAllowingStateLoss();
                break;

        }
    }


    private void initData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            File filehebin1=new File(completionPath+"自定义缓存目录");
            if (!filehebin1.exists()){
                filehebin1.mkdirs();
            }
        }



        PreliminaryReading1();
    }


    /**
     * 获取复制所有json文件
     * 必须是安卓11
     */
    private void getAllJson() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && dataReadmergePath.matches(".+bilibili视频合并/temp/download$")) {
            Uri uri = null;
            if (configureDataRead("data", "settings_switch_chooseBiliVersion", "tv.danmaku.bili").equals("tv.danmaku.bili")) {
                uri = getUriForPath(BdownloadPath);
            } else {
                uri = getUriForPath(mpath2);
            }
            Log.e("999999999999999","我使用了uri");


            ergodicFileByUri(MainActivity.this, uri, 0);
        }
    }


    //初步读取数据
    private void PreliminaryReading1() {

        Log.e(" ",dataReadmergePath.matches(".+bilibili视频合并/temp/download$")+"");



        //复制所有json文件必须是安卓11
        getAllJson();

        Log.e("mpath：", "----------------------" + mpath + "----------------------");

        //粗略的读取每个总标题的一个json文件和路径，用json_path类封装
        List<json_path> jp = ergodicDirBackPath.roughlyReadJson(mpath, MainActivity.this);


        //判断是那个页面
        MandP_code = 0;


        try {

            //bug调试
//            Log.e("jp.size()", "--------------------  " + jp.size() + "  --------------------");
//            for (json_path i : jp) {
//                Log.e("List<json_path> jp", "--------------------  " + i.getPath() + "  --------------------");
//            }
            //bug调试


            for (json_path i : jp) {
                String maname = "";
                try {
                    maname = i.getJsonObject().getString("title").replaceAll(regEx1, "");//获取总标题
                } catch (Exception e) {
                    maname = "获取标题失败,请进入查看";
                }

                Log.e("maname", "--------------------  " + maname + "  --------------------");

                mList.add(new list_Item(i.getPath(), maname));//在listview中显示（总标题总文件路径、读取的总标题名）如：治愈甜美999:/storage/emulated/0/Android/data/tv.danmaku.bili/download/99999

                adapter = new listAdapter(mList, MainActivity.this);//设置适配器数据
                mListView.setAdapter(adapter);//添加适配器


                //ListViewItem监听
                //一级监听对总标题进行监听
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        PreliminaryReading1position = position;

                        PreliminaryReading2(position);

                    }

                });

                //长按多选
                mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        if (swithCode == 0) {
                            swithCode = 1;//使用第二个frament即多选的
                            for (listAdapter.ViewHolder i : listAdapter.more_holder) {
                                i.more_checkBox.setVisibility(View.VISIBLE);//可见
                            }
                            //listAdapter.more_holder.get(position).more_checkBox.setChecked(true);//长按时就选中
                        } else {
                            swithCode = 0;
                            for (listAdapter.ViewHolder i : listAdapter.more_holder) {
                                i.more_checkBox.setVisibility(View.GONE);//不可见
                                i.more_checkBox.setChecked(false);
                            }
                        }
                        Frame_swith(swithCode);

                        return true;
                    }
                });
                //主标题


            }

        } catch (Exception e) {
            //e.printStackTrace();
            //mList.add(new list_Item(""));
            adapter = new listAdapter(null, MainActivity.this);//设置适配器数据为空
            mListView.setAdapter(adapter);//为mListView设置适配器
            Log.e("PreliminaryReading1()", "--------------------  这个方法有问题  --------------------");
            Toast.makeText(MainActivity.this, "当前选定的目录无bilibili缓存文件哦,错误代码：001", Toast.LENGTH_LONG).show();
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            return;
        }


    }

    private void PreliminaryReading2(int position) {
        MandP_code = 1;
        if (swithCode == 0) {

            ergodicDirBackPath.clearPath();//清除残留，下面要用

            //添加返回上一级
            JSONObject obj = null;
            try {
                obj = new JSONObject();
                obj.put("backupbtn", "返回上一级");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ergodicDirBackPath.json_pathList.add(new json_path(obj, splitMerge_Main.backupdir));
            //添加返回上一级

            List<json_path> jpFZ = ergodicDirBackPath.getJsonFZ(mList.get(position).getLJ());//遍历选择这个总标题下面的所有P的所有json文件

            mList.clear();//清除残留，下面要用


            for (json_path n : jpFZ) {
                try {

                    String finame = n.getPath();//随便反正后面会覆盖--------这是每一P的名称
                    JSONObject temp = n.getJsonObject();
                    try {
                        JSONObject jaj = temp.getJSONObject("page_data");
                        finame = jaj.getString("part").replaceAll(regEx1, "");
                    } catch (Exception e) {
                        e.printStackTrace();
                        //Log.d("提示", "---------错误");
                        //Toast.makeText(MainActivity.this, "当前选定的目录无bilibili缓存文件哦", Toast.LENGTH_SHORT).show();
                        try {
                            //直接传入JSONObject来构造一个实例
                            JSONObject jaj = temp.getJSONObject("ep");
                            finame = jaj.getString("index").replaceAll(regEx1, "") + jaj.getString("index_title").replaceAll(regEx1, "");//章节名
                        } catch (Exception err) {
                            try {
                                //直接传入JSONObject来构造一个实例
                                finame = "文件大小为" + temp.getString("total_bytes").replaceAll(regEx1, "");//章节名
                            } catch (Exception er) {
                                try {
                                    finame = temp.getString("backupbtn").replaceAll(regEx1, "");//返回上一级
                                } catch (Exception eo) {
                                    Toast.makeText(MainActivity.this, "从json中获取文件名错误,方法PreliminaryReading2错误：" + eo.toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }

                    mList.add(new list_Item(n.getPath(), finame));//在listview中显示（文字标题：缓存集合的总标题的路径）如：治愈甜美999:/storage/emulated/0/Android/data/tv.danmaku.bili/download/99999
                    adapter = new listAdapter(mList, MainActivity.this);//设置适配器数据
                    mListView.setAdapter(adapter);//添加适配器


                    //二级监听对P进行监听
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Toast.makeText(MainActivity.this, mList.get(position).getLJ(), Toast.LENGTH_SHORT).show();
                            if (!mList.get(position).getLJ().equals(splitMerge_Main.backupdir)) {//判断是不是返回上一级
                                if (swithCode == 0) {


                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.TransparentDialog);
                                    View alertdialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.mergedialog, null);
                                    Button alertdialog_yes = alertdialog.findViewById(R.id.alertdialog_yes);
                                    Button alertdialog_no = alertdialog.findViewById(R.id.alertdialog_no);
                                    Switch TM_xml = alertdialog.findViewById(R.id.TM_xml);//是否导出xml弹幕
                                    builder.setCancelable(false);
                                    AlertDialog alertDialog = builder.setView(alertdialog).show();

                                    exportBarrage_xml = Boolean.valueOf(configureDataRead("data", "export_barrage_switch_d", "false"));//读取开关信息
                                    TM_xml.setChecked(exportBarrage_xml);

                                    TM_xml.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if (TM_xml.isChecked()) {
                                                Toast.makeText(MainActivity.this, "推荐使用“弹弹play”看弹幕,软件下载请自行百度", Toast.LENGTH_LONG).show();
                                                exportBarrage_xml = true;
                                            } else {
                                                Toast.makeText(MainActivity.this, "关闭", Toast.LENGTH_LONG).show();
                                                exportBarrage_xml = false;
                                            }
                                            configureDataSet("data", "export_barrage_switch_d", String.valueOf(exportBarrage_xml));//存储开关信息
                                        }
                                    });


                                    alertdialog_yes.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.dismiss();//关闭AlertDialog

                                            ///////////////////////////////////////////////
                                            //系统的ProgressDialog
                                            //有坑必须用ProgressDialog下面的方式才能按要求显示
//                                            ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "提示", "正在准备合并文件请稍后...");
//                                            progressDialog.setCancelable(false);//返回不能取消边缘不能点击
                                            ///////////////////////////////////////////////


                                            String jtpath = mList.get(position).getLJ();//选择P章节的全路径
                                            Log.e("子目录为：", jtpath);
                                            //复制选中所有的文件
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && dataReadmergePath.matches(".+bilibili视频合并/temp/download$")) {
                                                choosePart = jtpath.replaceAll(ROOTPATH + "/bilibili视频合并/temp/download", "");
                                                //Toast.makeText(MainActivity.this,Title,Toast.LENGTH_SHORT).show();
                                                Uri uri;
                                                if (configureDataRead("data", "settings_switch_chooseBiliVersion", "tv.danmaku.bili").equals("tv.danmaku.bili")) {
                                                    uri = getUriForPath(BdownloadPath + choosePart);
                                                } else {
                                                    uri = getUriForPath(mpath2 + choosePart);
                                                }

                                                //Log.e("uri：",BdownloadPath + choosePart);
                                                ergodicFileByUri(MainActivity.this, uri, 1);
                                            }


                                            //界面UI的更新必须在创新view的那个线程即主线程（UI线程）而且必须开辟新的线程进行耗时操作
//                                            Handler handler = new Handler() {//界面UI的更新操作
//                                                @SuppressLint("HandlerLeak")
//                                                @Override
//                                                public void handleMessage(Message msg) {
//                                                    //progress_text.setText(msg.obj.toString());
//                                                    progressDialog.setMessage(msg.obj.toString());
//                                                    //Log.e("逐行读取", i);
//
//                                                }
//                                            };

//                                            new Thread() {//开辟线程进行耗时操作，不要堵塞主线程，即使堵塞了也不会有提示，就是相关的代码不生效，大坑
//                                                @Override
//                                                public void run() {
                                            //ergodicChapter2(jtpath, n.getJsonObject());//遍历并且选出合并的文件进行合成操作
//                                                    for (int i = 0; i + 3 < logFile.size(); i += 4) {
//                                                        //处理完成后给handler发送消息
//                                                        Message msg = new Message();
//                                                        msg.obj = logFile.get(i) + logFile.get(i + 1) + logFile.get(i + 2) + logFile.get(i + 3);
//                                                        handler.sendMessage(msg);
//                                                        try {
//                                                            Thread.sleep(45);
//                                                        } catch (InterruptedException e) {
//                                                            e.printStackTrace();
//                                                        }
//                                                    }
                                            //progressDialog.cancel();//等待合并界面清除
                                            //在子线程中弹出Toast，会报错：java.lang.RuntimeException: Can’t toast on a thread that has not called Looper.prepare()。
                                            //解决方式：先调用Looper.prepare();再调用Toast.makeText().show();最后再调用Looper.loop();
                                            //Looper.prepare();
//                                            if (hbzt == 0) {
//                                                Toast.makeText(MainActivity.this, "转换成功,文件在“/storage/emulated/0/bilibili视频合并”目录下！", Toast.LENGTH_LONG).show();
//                                            } else {
//                                                Toast.makeText(MainActivity.this, "转换可能有点小问题哦！！！自行验证。文件在“/storage/emulated/0/bilibili视频合并”目录下！", Toast.LENGTH_LONG).show();
//                                            }
                                            //Looper.loop();
//                                                }
//                                            }.start();

                                            //进行合并
                                            ergodicChapter2(jtpath, ergodicDirBackPath.readjson(new File(jtpath)));
                                            //删除安卓11复制的文件/storage/emulated/0/bilibili视频合并/temp/download
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && dataReadmergePath.matches(".+bilibili视频合并/temp/download$")) {
                                                deleteFile(targetPath);
                                            }
                                            hbzt = -1;
                                        }
                                    });
                                    alertdialog_no.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.dismiss();
                                        }
                                    });

                                }
                            } else {
                                Frame_swith(0);
                                listAdapter.more_choose.clear();//清除P中已经打钩的
                                Refresh();
                            }


                        }
                    });

                    mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            if (swithCode == 0) {
                                swithCode = 1;//使用第二个frament即多选的
                                for (listAdapter.ViewHolder i : listAdapter.more_holder) {
                                    i.more_checkBox.setVisibility(View.VISIBLE);//可见
                                }
                                listAdapter.more_holder.get(0).more_checkBox.setVisibility(View.GONE);//不可见
                                //Log.e("44444444444",listAdapter.more_holder.get(0).tvText.getText().toString());
                                //listAdapter.more_holder.get(position).more_checkBox.setChecked(true);//长按时就选中
                            } else {
                                swithCode = 0;
                                for (listAdapter.ViewHolder i : listAdapter.more_holder) {
                                    i.more_checkBox.setVisibility(View.GONE);//不可见
                                    i.more_checkBox.setChecked(false);
                                }
                            }

                            Frame_swith(swithCode);

                            return true;
                        }
                    });
                } catch (Exception e) {
                    //e.printStackTrace();
                    //mList.add(new list_Item(""));
                    adapter = new listAdapter(null, MainActivity.this);//设置适配器数据为空
                    mListView.setAdapter(adapter);//为mListView设置适配器
                    Toast.makeText(MainActivity.this, "PreliminaryReading2错误：" + e.toString(), Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    //参数：缓存集合的某一个章节P路径    新的方法
    private void ergodicChapter2(String choosePath, JSONObject testjson) {

        //Log.d("子目录为：",b+"----------------------");
        String zjName = "";
        String mainName = "";


        try {
            mainName = completionPath + testjson.getString("title").replaceAll(regEx1, "");//获取总标题
        } catch (Exception e) {
            mainName = completionPath + "获取标题失败合并视频";
        }


        try {

            //直接传入JSONObject来构造一个实例
            JSONObject jaj = testjson.getJSONObject("page_data");
            zjName = mainName + "/" + jaj.getString("part").replaceAll(regEx1, "");//章节名
            //Log.d("输出文件目录", zjName);
        } catch (Exception e) {
            //e.printStackTrace();
            //Log.d("提示", "---------错误");
            //Toast.makeText(MainActivity.this, "当前选定的目录无bilibili缓存文件哦", Toast.LENGTH_SHORT).show();
            try {

                //直接传入JSONObject来构造一个实例
                JSONObject jaj = testjson.getJSONObject("ep");
                zjName = mainName + "/" + jaj.getString("index").replaceAll(regEx1, "") + jaj.getString("index_title").replaceAll(regEx1, "");//章节名
            } catch (Exception err) {
                try {

                    //直接传入JSONObject来构造一个实例
                    zjName = mainName + "/文件大小为" + testjson.getString("total_bytes").replaceAll(regEx1, "");//章节名

                } catch (Exception er) {
                    Toast.makeText(MainActivity.this, "从json中获取文件名错误,方法ergodicChapter2错误：" + er.toString(), Toast.LENGTH_LONG).show();
                }
            }

        }


        try {
            File f3 = new File(mainName);
            if (!f3.exists()) {//判断文件夹是否存在不存在则创建
                f3.mkdirs();
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "创建文件夹错误,Code:2,错误信息：" + e.toString(), Toast.LENGTH_LONG).show();
        }


        putAllFiles(new File(choosePath), zjName);//遍历选择章节P目录下面的所有文件


    }

    //参数：缓存集合的某一个章节路径
    private void ergodicChapter(String choosePath) {
        File f2 = new File(choosePath);
        File[] arr2 = f2.listFiles();
        if (arr2 != null) {
            for (File b : arr2) {//读取目录   章节
                //Log.d("子目录为：",b+"----------------------");
                String zjName = "";
                String mainName = "";
                JSONObject testjson = readjson(b);

                try {
                    mainName = completionPath + testjson.getString("title").replaceAll(regEx1, "");//获取总标题
                } catch (Exception e) {
                    mainName = completionPath + "获取标题失败合并视频";
                }


                try {

                    //直接传入JSONObject来构造一个实例
                    JSONObject jaj = testjson.getJSONObject("page_data");
                    zjName = mainName + "/" + jaj.getString("part").replaceAll(regEx1, "");//章节名
                    //Log.d("输出文件目录", zjName);
                } catch (Exception e) {
                    //e.printStackTrace();
                    //Log.d("提示", "---------错误");
                    //Toast.makeText(MainActivity.this, "当前选定的目录无bilibili缓存文件哦", Toast.LENGTH_SHORT).show();
                    try {

                        //直接传入JSONObject来构造一个实例
                        JSONObject jaj = testjson.getJSONObject("ep");
                        zjName = mainName + "/" + jaj.getString("index").replaceAll(regEx1, "") + jaj.getString("index_title").replaceAll(regEx1, "");//章节名
                    } catch (Exception err) {
                        try {

                            //直接传入JSONObject来构造一个实例
                            zjName = mainName + "/文件大小为" + testjson.getString("total_bytes").replaceAll(regEx1, "");//章节名
                        } catch (Exception er) {
                            Toast.makeText(MainActivity.this, "从json中获取文件名错误,方法ergodicChapter错误：" + er.toString(), Toast.LENGTH_LONG).show();
                        }
                    }

                }


                try {
                    File f3 = new File(mainName);
                    if (!f3.exists()) {//判断文件是否存在不存在则创建
                        f3.mkdirs();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "创建文件夹错误,Code:1,错误信息：" + e.toString(), Toast.LENGTH_LONG).show();
                }

                putAllFiles(b, zjName);//遍历目录下面的所有文件
            }
        } else {
            Toast.makeText(MainActivity.this, "当前选定的目录无bilibili缓存文件，方法错误：ergodicChapter 集合arr2为空", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Readjson json object.
     *
     * @param b the b
     * @return the json object
     */
//读取json文件
    public static JSONObject readjson(File b) {
        JSONObject testjson = null;
        try {
            FileInputStream in = new FileInputStream(b + "/entry.json");
            InputStreamReader isr = new InputStreamReader(in, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            isr.close();
            testjson = new JSONObject(builder.toString());//builder读取了JSON中的数据。
        } catch (Exception e) {

        }
        return testjson;
    }

    //删除文件或文件夹
    public boolean deleteFile(String deletePath) {

        DeleteUtil.delete(deletePath);
//        File file = new File(deletePath);
//        File[] file_list = file.listFiles();
//        for (File dele_file : file_list) {
//            if (dele_file.isDirectory()) {
//                deleteFile(dele_file.toString());
//            } else {
//                if (!dele_file.toString().matches(".*entry.json$")) {
//                    DeleteUtil.delete(dele_file.toString());
//                }
//            }
//        }
        return true;
    }

    /**
     * Put all files.
     *
     * @param file   the file
     * @param output the output
     */
//遍历单个的文件下面的所有文件  //章节文件夹//输出文件路径但是没有后缀/storage/emulated/0/bilibili视频合并/cs/起飞
    public void putAllFiles(File file, String output) {
        Log.e("方法相关：", "putAllFiles删除已经合并的运行了");

        ergodicDirBackPath.ergodicDirBackPathAboutm4s(file.getPath());//遍历文件获取danmaku.xml、audio.m4s、video.m4s、blv全路径

        if (ergodicDirBackPath.style_blv.size() != 0) {
            String temp = ergodicDirBackPath.style_blv.toString().replace(" ", "").replace("[", "file '").replace("]", "'").replace(",", "'\nfile '");
            String str = ergodicDirBackPath.style_blv.get(0);
            int first = str.indexOf("/"); //单引号第一次出现的位置
            int last = str.lastIndexOf("/"); //单引号最后一次出现的位置
            String aa = str.substring(first, last + 1);//截取后变成新的字符串
            //Log.d("目录为", aa+"--------------------");
            creatFile(aa, "blv.txt", temp.replace(aa, ""));
            //run(configurefile_path+"/blv.txt",output);
            if (fileCZ(output.replace(" ", "") + ".mp4"))
                run(aa + "blv.txt", output);
        } else {

            if (fileCZ(output.replace(" ", "") + ".mp4")) {//判断文件是否合并过了，MP4文件不存在
                Log.e("文件相关", "MP4文件不存在");
                if (exportBarrage_xml) {
                    boolean pd = FileUtils.fileCopy(ergodicDirBackPath.inputxml, output + ".xml");//复制弹幕（xml）文件并重命名
                }

                run(ergodicDirBackPath.inputVideo, ergodicDirBackPath.inputAudio, output);

                //Log.e("-----------", "if合并中");
                //return;
            } else {//MP4文件存在
                Log.e("文件相关", "MP4文件存在");
                int Indexe = 0;
                for (int i = 0; i < 100; i++) {
                    if (fileCZ(output.replace(" ", "") + "(" + i + ").mp4")) {//不存在
                        Log.e("-----------", i + ".mp4不存在");
                        Indexe = i;//获得索引
                        break;
                    }
                }
                //Log.e("-----------", "Indexe为" + Indexe);
                if (exportBarrage_xml) {
                    boolean pd = FileUtils.fileCopy(ergodicDirBackPath.inputxml, output + "(" + Indexe + ")" + ".xml");//复制文件并重命名
                    //Log.e("++++++++++++++++", oo + "");
                }
                run(ergodicDirBackPath.inputVideo, ergodicDirBackPath.inputAudio, output + "(" + Indexe + ")");

                String newCreatePath = output.replace(" ", "") + "(" + Indexe + ").mp4";
                long newCreateSize = getFileSize(newCreatePath);
                long noNumberSize = getFileSize(output.replace(" ", "") + ".mp4");
                long numberSize;

                //与没有序号的进行比较大小noNumber
                if (noNumberSize == newCreateSize) {
                    deleteFile(newCreatePath);
                }
                //与有序号的进行比较大小
                for (int i = 0; i < Indexe; i++) {
                    numberSize = getFileSize(output.replace(" ", "") + "(" + i + ").mp4");
                    if (numberSize == newCreateSize) {
                        deleteFile(newCreatePath);
                        break;
                    }
                }

            }

        }


        ergodicDirBackPath.clearPath();//清除静态全路径数据、防止对下一次合并造成影响


    }


    /**
     * 获取指定文件大小
     *
     * @param filePath the file path
     * @return file size
     * @throws Exception
     */
    public static long getFileSize(String filePath) {

        Log.e("路径相关", filePath);

        File file = new File(filePath);
        long size = 0;
        try {
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                size = fis.available();
                fis.close();
            } else {
                //file.createNewFile();
                Log.e("获取文件大小", "文件不存在!");
            }
        } catch (Exception e) {

        }
        return size;
    }


    //配置文件数据
    private void configureData() {
        String configureName = "data";//文件名data.xml
        if (fileCZ(configurefile_path + "/" + configureName + ".xml")) {
            Log.e("文件方面", "--------------------  data.xml文件不存在  --------------------");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                //安卓11视频缓存合并目录
                configureDataSet(configureName, "mergePath", targetPath);
            } else {
                //安卓5~10视频缓存合并目录
                configureDataSet(configureName, "mergePath", mpath);
            }

//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//                //安卓7以下
//                //拆分合并显示目录
//                configureDataSet(configureName, "splitMerge_showPath", ROOTPATH.replace("emulated/0", "sdcard"));
//
//            } else {
            //拆分合并显示目录
            configureDataSet(configureName, "splitMerge_showPath", ROOTPATH);

//            }
            //拆分合并大小
            configureDataSet(configureName, "split_size", "10");
            //导出开关---单个
            configureDataSet(configureName, "export_barrage_switch_d", "false");

            configureDataSet(configureName, "settings_switch_chooseBiliVersion", "tv.danmaku.bili");

            //configureDataSet(configureName, "settings_switch_chooseBiliVersion", "com.bilibili.app.in");

            authorlayout();//文件不存在即第一次使用则打开关于弹窗

        } else {
            Log.e("文件方面", "--------------------  data.xml文件存在  --------------------");
        }

        if (configureDataRead(configureName, "settings_switch_chooseBiliVersion", "").equals("com.bilibili.app.in")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && dataReadmergePath.matches(".+bilibili视频合并/temp/download$")) {
                //安卓11视频缓存合并目录
                configureDataSet(configureName, "mergePath", targetPath);
            } else {
                //安卓5~10视频缓存合并目录
                configureDataSet(configureName, "mergePath", mpath2);
            }
        }
//        else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                //安卓11视频缓存合并目录
//                configureDataSet(configureName, "mergePath", targetPath);
//            } else {
//                //安卓5~10视频缓存合并目录
//                configureDataSet(configureName, "mergePath", mpath);
//            }
//        }
        mpath = configureDataRead(configureName, "mergePath", "");//获取视频合并路径

    }

    //使用sharedPreferences进行数据存储
    private void configureDataSet(String fileName, String key, String value) {
        msharedPreferences = getSharedPreferences(fileName, MODE_PRIVATE);//在内部存储中的shared_prefs中创建data的文件
        msharedPreferEditor = msharedPreferences.edit();
        msharedPreferEditor.putString(key, value);//添加数据
        msharedPreferEditor.apply();//进行提交
    }

    //使用sharedPreferences进行数据读取
    private String configureDataRead(String fileName, String key, String defValue) {
        msharedPreferences = getSharedPreferences(fileName, MODE_PRIVATE);//在内部存储中的shared_prefs中创建data的文件
        return msharedPreferences.getString(key, defValue);//后面那个参数是如果没有mergePath对应的值则传入后面的数据

    }


    /**
     * Creat file.
     *
     * @param path     the path
     * @param filename the filename
     * @param content  the content
     */
//传入文件路径，文件名，文件内容=》在指定位置创建文件
    public void creatFile(String path, String filename, String content) {//例:filename="/a.txt"
        try {//例:path=Environment.getExternalStorageDirectory()+ "/new"
            File fss = new File(path);
            if (!fss.exists()) {//文件目录是否存在
                try {
                    fss.mkdirs();//不存在则创建
                } catch (Exception e) {
                    //可能没有权限
                }
            }
            File fs = new File(path + filename);
            FileOutputStream outputStream = new FileOutputStream(fs);
            outputStream.write(content.getBytes());//写入
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * File cz boolean.
     *
     * @param output the output
     * @return the boolean
     */
//判断文件是否存在     output为全路径
    public static boolean fileCZ(String output) {
        String filePath = output;
        File file = new File(filePath);
        if (!file.exists()) {// 文件不存在
            return true;
        } else return false;
    }


    /**
     * 设置Dialog窗口的大小
     */
    private void setWindowSize() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager m = getWindowManager();
        m.getDefaultDisplay().getMetrics(dm);
        // 为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        // p.height = (int) (d.getHeight() * 1.0); //高度设置为屏幕的1.0
        p.width = (int) (dm.widthPixels * 0.86); // 宽度设置为屏幕的0.85
        p.alpha = 1.0f; // 设置本身透明度
        p.dimAmount = 0.6f; // 设置黑暗度
        getWindow().setAttributes(p);
    }

    /**
     * Authorlayout.
     */
//关于弹窗
    public void authorlayout() {
        AlertDialog.Builder authorbuilder = new AlertDialog.Builder(MainActivity.this, R.style.authorDialog);///
        View author_layout = LayoutInflater.from(MainActivity.this).inflate(R.layout.author_layout, null);
        Button yes_author = author_layout.findViewById(R.id.yes_author);
        TextView textview_author = author_layout.findViewById(R.id.textview_author);
        textview_author.setText("    本《声明》包括:特别鸣谢和《软件协议》。\n    一、特别鸣谢\n     为本软件开发提供重要帮助的开源项目和用户:\n    【ffmpeg开源项目】:此软件是基于ffmpeg进行开发的。\n    【RxFFmpeg开源项目】:感谢其提供的解决方案。\n    【CSDN网站】:作者从网站上查找了大量的资料，解决了许多的问题。\n    【云注入】:感谢云注入曾经提供的网络弹窗技术支持。(现已不用)\n    【Bugly】:感谢腾讯提供的技术服务。\n    非常感谢以下网友帮助测试反馈Bug并提出建议:\n      【@B站:wtybilibiliwty】\n      【@B站:会飞的梧鼠】\n      【@B站:Link_bbj】\n      【@基安:神马都是神马】\n      【@基安:dreampjk】\n      【@大灰辉】\n      【@B站:兵封千里】\n      【@B站:残情义梦】\n      【@B站:Cugires】\n      【@B站:时光流逝124】\n      【@B站:HAPPY-YIFAN】\n      【@基安:qazplmqwe】\n      【@B站:啵啵虎超帅】\n      【@B站:欧皇护体这个人】\n      【@爱吾:天涯的芳草】\n      【@B站:蒙古上单一】\n      【@B站:偶然忆起曾经】\n      【@B站:孤云闲人】\n      【@B站:冰色铠甲】\n      【@基安:氿洛】\n      【@B站:Ayakuzihs】\n      【@基安:zZ不是酱油】\n      【@B站:羽翼已丰】\n      【@B站:jangdoo】\n      【@B站:慈爱の勇者】\n      【@俊总】:感谢俊总提供会员共享账号\n      【@舒总】:感谢舒总提供机型测试\n      【@HL】:帮助测试并反馈Bug\n      【@欣】:提供部分设计思路，帮助测试反馈了大量的bug并提出建议。\n      【@娜】:设计软件图标，提供部分图片资源支持。\n  《软件协议》描述我们与您之间关于本软件许可使用及相关方面的权利义务。请您务必审慎阅读、充分理解各条款内容，并选择接受或不接受（未成年人应在法定监护人陪同下审阅）。除非您接受本《软件协议》条款，否则您无权下载、安装或使用本软件及其相关服务。您的安装、使用行为将视为对本《协议》的接受，并同意接受本《协议》各项条款的约束。\n\n  二、《软件协议》\n    【关于本软件】此软件是由我们开发的一款将B站缓存进行合并的工具，使用了自己蹩脚的编程能力做的。\n    【用户禁止行为】除非我们书面许可，您不得从事下列行为：\n      （1）删除本软件及其副本上关于原作者著作权的信息。\n      （2）用于商业用途。\n    【法律责任与免责】\n      （1）本软件仅用于学习和技术交流，不可用于商业用途，造成的损失原作者不承担任何责任。\n      （2）可以进行二次修改，但不可删除本软件及其副本上关于原作者著作权的信息，二次修改后的软件造成您的损失原作者不承担任何责任。\n      （3）如此软件侵犯到您的权益请联系我QQ邮箱：1492906929@qq.com，我会进行处理，给您的不便我深感抱歉。\n    【其他条款】\n      （1）电子文本形式的授权协议如同双方书面签署的协议一样，具有完全的和等同的法律效力。您使用本软件或本服务即视为您已阅读并同意受本协议的约束。协议许可范围以外的行为，将直接违反本授权协议并构成侵权，我们有权随时终止授权，责令停止损害，并保留追究相关责任的权力。\n      （2）我们有权在必要时修改本协议条款。您可以在本软件的最新版本中查阅相关协议条款。本协议条款变更后，如果您继续使用本软件，即视为您已接受修改后的协议。如果您不接受修改后的协议，应当停止使用本软件。\n      （3）我们保留对本协议的最终解释权。");

        RelativeLayout author_TC = author_layout.findViewById(R.id.author_TC);//获取视图
//弹窗宽度getLayoutParams()的对象必须有父布局不然空指针
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) author_TC.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels; //屏幕的宽度
//弹窗高度
        params.height = getResources().getDisplayMetrics().heightPixels / 3 * 2; //屏幕的高度的2/3
        author_TC.setLayoutParams(params);//进行设置


        authorbuilder.setCancelable(false);
        AlertDialog alertDialog_author = authorbuilder.setView(author_layout).show();
        yes_author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog_author.dismiss();
            }
        });
    }

    /**
     * Gets uri for path.
     *
     * @param path the path
     * @return the uri for path
     */
/////////通过路径获取URI     path为/storage/emulated/0/Android/data下面的路径注意是全路径
    public Uri getUriForPath(String path) {
        String[] paths = path.replaceAll(ROOTPATH + "/Android/data", "").split("/");
        StringBuilder stringBuilder = new StringBuilder("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fdata");
        for (String p : paths) {
            if (p.length() == 0) continue;
            stringBuilder.append("%2F").append(p);
        }
        return Uri.parse(stringBuilder.toString());
    }


    //复制data里面的文件到指定目录targetPath--------uri为文件的Uri，targetPath为指定目录的全路径
    private boolean copyByUri(Uri uri, String targetPath, String fileName) {
        if (!fileCZ(targetPath + "/" + fileName)) {
            //Log.d("Uri", uri.toString());
            //Toast.makeText(MainActivity.this,"文件已经存在是否要覆盖！",Toast.LENGTH_SHORT).show();
            return true;
        }

        dirCZ(targetPath);

        InputStream in = null;
        OutputStream out = null;
        try {
            in = getContentResolver().openInputStream(uri);//读取源文件转换为输入流
            File destFile = new File(targetPath + "/" + fileName);
            out = new FileOutputStream(destFile);//目的路径文件转换为输出流

            byte[] flush = new byte[1024];
            int len = -1;
            while ((len = in.read(flush)) != -1) {//边读边写
                out.write(flush, 0, len);
            }

            //Toast.makeText(MainActivity.this,"成功！",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "没有权限！处理失败！", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    /**
     * Dir cz.
     *
     * @param path the path
     */
//判断文件夹是否存在,不存在则创建------path为全路径
    public static void dirCZ(String path) {
        File fss = new File(path);
        if (!fss.exists()) {//文件目录是否存在
            try {
                fss.mkdirs();//不存在则创建
            } catch (Exception e) {
                //可能没有权限

            }
        }
    }


    /**
     * Ergodic file by uri.
     *
     * @param context     the context
     * @param downloadUri the download uri
     * @param code        the code
     */
//通过URI遍历文件---------通过DocumentFile来操作文件
    public void ergodicFileByUri(Context context, Uri downloadUri, int code) {
        if (context == null || downloadUri == null) return;
        DocumentFile documentFile = DocumentFile.fromSingleUri(context, downloadUri);//通过URI创建DocumentFile对象
        if (documentFile == null) return;
        //通过documentFile对文件进行操作
        DocumentFile pickedDir = documentFile.fromTreeUri(MainActivity.this, downloadUri);//创建一个 DocumentFile表示以给定的 Uri根的文档树。其实就是获取子目录的权限

        //Log.e("uri：","jjjjjjjjjjjjjjjjjjjjj");
        for (DocumentFile i : pickedDir.listFiles()) {
            if (i.isDirectory()) {
                //Log.d("文件夹",i.getName());
                ergodicFileByUri(MainActivity.this, i.getUri(), code);//递归
            } else {
                switch (code) {
                    case 0:
                        if (i.getName().equals("entry.json")) {//
                            String temp = i.getUri().toString().replaceAll("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fdata", "");
                            String partPath = "";
                            if (configureDataRead("data", "settings_switch_chooseBiliVersion", "tv.danmaku.bili").equals("tv.danmaku.bili")) {
                                partPath = temp.replaceAll("%2Ftv.danmaku.bili%2Fdownload", "").replaceAll("%2F", "/").replaceAll("/" + i.getName(), "");
                            } else {
                                partPath = temp.replaceAll("%2Fcom.bilibili.app.in%2Fdownload", "").replaceAll("%2F", "/").replaceAll("/" + i.getName(), "");
                            }

                            Log.d("文件", i.getName() + "------------" + partPath);
                            copyByUri(i.getUri(), targetPath + partPath, i.getName());
                        }
                        break;
                    case 1:
                        String temp = i.getUri().toString().replaceAll("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fdata", "");

                        String partPath = "";
                        if (configureDataRead("data", "settings_switch_chooseBiliVersion", "tv.danmaku.bili").equals("tv.danmaku.bili")) {
                            partPath = temp.replaceAll("%2Ftv.danmaku.bili%2Fdownload", "").replaceAll("%2F", "/").replaceAll("/" + i.getName(), "");
                        } else {
                            partPath = temp.replaceAll("%2Fcom.bilibili.app.in%2Fdownload", "").replaceAll("%2F", "/").replaceAll("/" + i.getName(), "");
                        }

                        //String partPath = temp.replaceAll("%2Ftv.danmaku.bili%2Fdownload", "").replaceAll("%2F", "/").replaceAll("/" + i.getName(), "");
                        Log.e("文件", i.getName() + "------------" + partPath);
                        copyByUri(i.getUri(), targetPath + partPath, i.getName());
                        break;
                }

            }
        }
    }

    @Override
    public void onBackPressed() {//重写返回键


        if (swithCode == 0) {
            if (MandP_code == 1) {
                Refresh();
                return;
            }
            if (System.currentTimeMillis() - firstBackTime > 2000) {
                Toast.makeText(MainActivity.this, "再按一次返回键退出程序", Toast.LENGTH_SHORT).show();
                firstBackTime = System.currentTimeMillis();
                return;
            }
            super.onBackPressed();
        } else {
            swithCode = 0;
            Frame_swith(swithCode);
            for (listAdapter.ViewHolder i : listAdapter.more_holder) {
                i.more_checkBox.setVisibility(View.GONE);//不可见
                i.more_checkBox.setChecked(false);//取消选中
            }
        }
    }

    /**
     * 调用第三方浏览器打开
     *
     * @param context the context
     * @param url     要浏览的资源地址
     */
    public static void openBrowser(Context context, String url) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
// 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
// 官方解释 : Name of the component implementing an activity that can display the intent
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(context.getPackageManager()); // 打印Log   ComponentName到底是什么 L.d("componentName = " + componentName.getClassName());
            context.startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            Toast.makeText(context.getApplicationContext(), "请下载浏览器", Toast.LENGTH_SHORT).show();
        }
    }

//逐行读取存储的ffmpeg日志文件txt存入集合newList中并返回集合，strFilePath为全路径

    /**
     * Read txt file list.
     *
     * @param strFilePath the str file path
     * @return the list
     */
    public static List<String> ReadTxtFile(String strFilePath) {
        String path = strFilePath;
        List<String> newList = new ArrayList();
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Log.d("TestFile", "The File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        newList.add(line + "\n");
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");
            } catch (IOException e) {
                Log.d("TestFile", e.getMessage());
            }
        }

        return newList;
    }


    private void openProgressDialog() {
        //统计开始时间
        startTime = System.nanoTime();
        mProgressDialog = Utils.openProgressDialog(MainActivity.this);

    }

    /**
     * 取消进度条
     *
     * @param dialogTitle Title
     */
    private void cancelProgressDialog(String dialogTitle) {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
        if (!TextUtils.isEmpty(dialogTitle)) {
            showDialog(dialogTitle);
        }
    }

    private void showDialog(String message) {
        //统计结束时间
        endTime = System.nanoTime();
        Utils.showDialog(MainActivity.this, message, Utils.convertUsToTime((endTime - startTime) / 1000, false));

    }

    /**
     * 设置进度条
     */
    private void setProgressDialog(int progress, long progressTime) {
        if (mProgressDialog != null) {
            mProgressDialog.setProgress(progress);
            //progressTime 可以在结合视频总时长去计算合适的进度值
            mProgressDialog.setMessage("已处理progressTime=" + (double) progressTime / 1000000 + "秒");
        }
    }

    public int finlishT_code = -1;

    /**
     * The type My rx f fmpeg subscriber.
     */
    public static class MyRxFFmpegSubscriber extends RxFFmpegSubscriber {

        private WeakReference<MainActivity> mWeakReference;


        /**
         * Instantiates a new My rx f fmpeg subscriber.
         *
         * @param homeFragment the home fragment
         */
        public MyRxFFmpegSubscriber(MainActivity homeFragment) {
            mWeakReference = new WeakReference<>(homeFragment);
        }


        @Override
        public void onFinish() {
            final MainActivity mHomeFragment = mWeakReference.get();
            if (mHomeFragment != null) {
                mHomeFragment.cancelProgressDialog("处理成功!\n\t\t\t\t合并文件保存在“" + ROOTPATH + "/bilibili视频合并”目录下！");

            }
        }

        @Override
        public void onProgress(int progress, long progressTime) {
            final MainActivity mHomeFragment = mWeakReference.get();
            if (mHomeFragment != null) {
                //progressTime 可以在结合视频总时长去计算合适的进度值
                mHomeFragment.setProgressDialog(progress, progressTime);
            }
        }

        @Override
        public void onCancel() {
            final MainActivity mHomeFragment = mWeakReference.get();
            if (mHomeFragment != null) {
                mHomeFragment.cancelProgressDialog("您取消了");
            }
        }

        @Override
        public void onError(String message) {
            final MainActivity mHomeFragment = mWeakReference.get();
            if (mHomeFragment != null) {
                mHomeFragment.cancelProgressDialog("错误！");
            }
        }
    }


    /**
     * Run int.
     *
     * @param inputVideo the input video
     * @param inputAudio the input audio
     * @param output     the output
     * @return the int
     */
//private void ffmpegTest(String inputVideo,String inputAudio,String output) {
    //new Thread() {
    //@Override
    //M4s合并
    public int run(String inputVideo, String inputAudio, String output) {
        if (singleMany_code == 0) {
            openProgressDialog();
        }

        MyRxFFmpegSubscriber myRxFFmpegSubscriber = new MyRxFFmpegSubscriber(MainActivity.this);
        //long startTime = System.currentTimeMillis();
        String cmd = "ffmpeg -i %s -i %s -c copy %s.mp4";
        String result = String.format(cmd, inputVideo, inputAudio, output.replaceAll(" ", ""));
        //Log.d("文件", inputVideo+"\n"+inputAudio+"\n"+output.replace(" ","")+"\n");
        //String result ="ffmpeg -i /sdcard/Android/data/tv.danmaku.bilj/download/18366470/4/64/video.m4s -i /sdcard/Android/data/tv.danmaku.bilj/download/18366470/4/64/audio.m4s -c copy /sdcard/bilibili视频合并/1.mp4";
        //int cljg = FFmpegCmd.runCmd(result.split(" "));

//        new Thread(){
//            @Override
//            public void run() {
//                Looper.prepare();
//                Toast.makeText(MainActivity.this,"执行的命令是"+result,Toast.LENGTH_LONG).show();
//                Looper.loop();
//            }
//        }.start();

        Log.e("模式", "m4s格式");
        Log.e("执行语句为", result);
        int cljg = 0;
        try {
            if (singleMany_code == 0) {
                RxFFmpegInvoke.getInstance().runCommandRxJava(result.split(" ")).subscribe(myRxFFmpegSubscriber);
            } else {
                RxFFmpegInvoke.getInstance().runFFmpegCmd(result.split(" "));
            }
        } catch (Exception e) {
            cljg = -1;
        }

        return hbzt = cljg;//0则表示正常
        //logFile = ReadTxtFile(output.replace(" ", "") + ".txt");
        //deleteFile(output.replace(" ", "") + ".txt");//删除日志文件
        //Log.e("合并判断", String.valueOf(cljg));

        //Log.d("FFmpegTest", "run: 耗时：" + (System.currentTimeMillis() - startTime));
    }
    //}.start();
    //}

    /**
     * Run int.
     *
     * @param pathTxt the path txt
     * @param output  the output
     * @return the int
     */
//private void ffmpegTest(String inputVideo,String inputAudio,String output) {
    //new Thread() {
    //@Override
    //blv格式合并
    public int run(String pathTxt, String output) {
        if (singleMany_code == 0) {
            openProgressDialog();
        }
        MyRxFFmpegSubscriber myRxFFmpegSubscriber = new MyRxFFmpegSubscriber(MainActivity.this);
        //Log.d("文件", pathTxt+"\n"+output.replace(" ","")+"\n");
        //long startTime = System.currentTimeMillis();
        String cmd = "ffmpeg -f concat -i %s -c copy %s.mp4";
        String result = String.format(cmd, pathTxt, output.replace(" ", ""));

        Log.e("模式", "blv格式");
        Log.e("执行语句为", result);
        int cljg = 0;
        try {
            if (singleMany_code == 0) {
                RxFFmpegInvoke.getInstance().runCommandRxJava(result.split(" ")).subscribe(myRxFFmpegSubscriber);
            } else {
                RxFFmpegInvoke.getInstance().runFFmpegCmd(result.split(" "));
            }
        } catch (Exception e) {
            cljg = -1;
        }

        return hbzt = cljg;//0则表示正常

        //return hbzt = FFmpegCmd.runCmd(result.split(" "));

        //Log.d("FFmpegTest", "run: 耗时：" + (System.currentTimeMillis() - startTime));
    }
    //}.start();
    //}







    /*
    ///////////////////////////////////////////////////////////
//存储数据------switch_configure
    public void save(String content, String filename) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = openFileOutput(filename, MODE_PRIVATE);//创建输出流
            fileOutputStream.write(content.getBytes());//把content写入输出流
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();//关闭输出流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //读取数据------switch_configure
    public String read(String filename) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = openFileInput(filename);
            byte[] buff = new byte[1024];
            StringBuilder sb = new StringBuilder("");
            int len = 0;
            while ((len = fileInputStream.read(buff)) > 0) {
                sb.append(new String(buff, 0, len));//拼接
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();//关闭输出流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
/////////////////////////////////////

 */


    //保存选择的合并目录到文件path_configure.txt，并读取path_configure.txt中的合并路径
    /*
    private void path_configurefile() {
        String configureName = "path_configure.txt";
        if (fileCZ(configurefile_path + "/" + configureName)) {// path_configure.txt文件不存在
            save(mpath, configureName);//保存选择的合并目录到文件path_configure.txt
            authorlayout();//文件不存在即第一次使用则打开关于弹窗
            Log.d("path_configurefile", "txt文件不存在--------------------");
        } else Log.d("path_configurefile", "txt文件存在--------------------");
        mpath = read(configureName);
        //Toast.makeText(MainActivity.this, mpath, Toast.LENGTH_SHORT).show();
    }
*/
    /*

    //初步读取数据
    private void PreliminaryReading() {
        //复制所有json文件必须是安卓11
        getAllJson();

        File f1 = new File(mpath);///storage/emulated/0/Android/data/tv.danmaku.bili/download
        File[] arr1 = f1.listFiles();//获取/storage/emulated/0/Android/data/tv.danmaku.bili/download路径下的所有文件的全路径如：/storage/emulated/0/Android/data/tv.danmaku.bili/download/99999等等[]
        if (arr1 != null) {
            for (File a : arr1) {//例如/storage/emulated/0/Android/data/tv.danmaku.bili/download/99999
                //Log.e("总标题", a + "----------------------");
                File f2 = new File(a.toString());
                File[] arr2 = f2.listFiles();///storage/emulated/0/Android/data/tv.danmaku.bili/download/99999/1等等[]
                if (arr2 != null) {
                    for (File b : arr2) {//读取download下的一个缓存集合的任意1P来获取总标题名例如/storage/emulated/0/Android/data/tv.danmaku.bili/download/99999/1
                        //Log.e("p目录为",b+"----------------------");
                        try {
                            JSONObject testjson = readjson(b);
                            //直接传入JSONObject来构造一个实例
                            //mList.add(new list_Item(testjson.getString("title").replaceAll(regEx1, "") + ":\n" + a.toString(), mc));//在listview中显示（文字标题：缓存集合的总标题的路径）如：治愈甜美999:/storage/emulated/0/Android/data/tv.danmaku.bili/download/99999
                            //Log.d("子目录为：",testjson.getString("title").replaceAll(regEx1, "") + ":\n" + a.toString()+"----------------------");
                            adapter = new listAdapter(mList, this);//设置适配器数据
                            mListView.setAdapter(adapter);//添加适配器
                            //ListViewItem监听
                            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                                    if (swithCode == 0) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.TransparentDialog);
                                        View alertdialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.alertdialog, null);
                                        Button alertdialog_yes = alertdialog.findViewById(R.id.alertdialog_yes);
                                        Button alertdialog_no = alertdialog.findViewById(R.id.alertdialog_no);
                                        Switch TM_xml = alertdialog.findViewById(R.id.TM_xml);
                                        builder.setCancelable(false);
                                        AlertDialog alertDialog = builder.setView(alertdialog).show();

                                        exportBarrage_xml = Boolean.valueOf(configureDataRead("data", "export_barrage_switch_d", "false"));//读取开关信息
                                        TM_xml.setChecked(exportBarrage_xml);


                                        TM_xml.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                if (TM_xml.isChecked()) {
                                                    Toast.makeText(MainActivity.this, "推荐使用“弹弹play”看弹幕,软件下载请自行百度", Toast.LENGTH_LONG).show();
                                                    exportBarrage_xml = true;
                                                } else {
                                                    Toast.makeText(MainActivity.this, "关闭", Toast.LENGTH_LONG).show();
                                                    exportBarrage_xml = false;
                                                }
                                                configureDataSet("data", "export_barrage_switch_d", String.valueOf(exportBarrage_xml));//存储开关信息
                                            }
                                        });


                                        alertdialog_yes.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                alertDialog.dismiss();//关闭AlertDialog

                                                ///////////////////////////////////////////////
                                                //系统的ProgressDialog
                                                //有坑必须用ProgressDialog下面的方式才能按要求显示
                                                ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "提示", "正在准备合并文件请稍后...");
                                                progressDialog.setCancelable(false);//返回不能取消边缘不能点击
                                                ///////////////////////////////////////////////


                                                String wmp = mList.get(position).getLJ();//(文字标题：缓存集合的某一个章节的路径）
                                                String jtpath = wmp.split(":")[1].replaceAll("\n", "");//按：进行分割并去除换行符得到缓存集合的某一个章节路径
                                                //Log.d("子目录为：",jtpath);
                                                //复制选中所有的文件
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                                    choosePart = jtpath.replaceAll("/storage/emulated/0/bilibili视频合并/temp/download", "");
                                                    //Toast.makeText(MainActivity.this,Title,Toast.LENGTH_SHORT).show();
                                                    Uri uri = getUriForPath(downloadPath + choosePart);
                                                    ergodicFileByUri(MainActivity.this, uri, 1);
                                                }


//                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.TransparentDialog);
//                                                View alertdialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.progress_dialog, null);
//                                                TextView progress_text=alertdialog.findViewById(R.id.progress_text);
//                                                AlertDialog progressDialog = builder.setView(alertdialog).show();


                                                //界面UI的更新必须在创新view的那个线程即主线程（UI线程）而且必须开辟新的线程进行耗时操作
                                                Handler handler = new Handler() {//界面UI的更新操作
                                                    @SuppressLint("HandlerLeak")
                                                    @Override
                                                    public void handleMessage(Message msg) {
                                                        //progress_text.setText(msg.obj.toString());
                                                        progressDialog.setMessage(msg.obj.toString());
                                                        //Log.e("逐行读取", i);

                                                    }
                                                };

                                                new Thread() {//开辟线程进行耗时操作，不要堵塞主线程，即使堵塞了也不会有提示，就是相关的代码不生效，大坑
                                                    @Override
                                                    public void run() {
                                                        ergodicChapter(jtpath);//遍历并且选出合并的文件进行合成操作

                                                        for (int i = 0; i + 3 < logFile.size(); i += 4) {
                                                            //处理完成后给handler发送消息
                                                            Message msg = new Message();
                                                            msg.obj = logFile.get(i) + logFile.get(i + 1) + logFile.get(i + 2) + logFile.get(i + 3);
                                                            handler.sendMessage(msg);
                                                            try {
                                                                Thread.sleep(45);
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }


                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                                            deleteFile(targetPath);
                                                        }

                                                        progressDialog.cancel();//等待合并界面清除
                                                        //在子线程中弹出Toast，会报错：java.lang.RuntimeException: Can’t toast on a thread that has not called Looper.prepare()。
                                                        //解决方式：先调用Looper.prepare();再调用Toast.makeText().show();最后再调用Looper.loop();
                                                        Looper.prepare();
                                                        if (hbzt == 0) {
                                                            Toast.makeText(MainActivity.this, "转换成功,文件在“/storage/emulated/0/bilibili视频合并”目录下！", Toast.LENGTH_LONG).show();
                                                        } else {
                                                            Toast.makeText(MainActivity.this, "转换可能有点小问题哦！！！自行验证。文件在“/storage/emulated/0/bilibili视频合并”目录下！", Toast.LENGTH_LONG).show();
                                                        }
                                                        Looper.loop();
                                                        hbzt = -1;


                                                    }
                                                }.start();


                                            }
                                        });
                                        alertdialog_no.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                alertDialog.dismiss();
                                            }
                                        });

                                    }






                                }

                            });
                            //长按多选
                            mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                    if (swithCode == 0) {
                                        swithCode = 1;//使用第二个frament即多选的
                                        for (listAdapter.ViewHolder i : listAdapter.more_holder) {
                                            i.more_checkBox.setVisibility(View.VISIBLE);//可见
                                        }
                                        //listAdapter.more_holder.get(position).more_checkBox.setChecked(true);//长按时就选中
                                    } else {
                                        swithCode = 0;
                                        for (listAdapter.ViewHolder i : listAdapter.more_holder) {
                                            i.more_checkBox.setVisibility(View.GONE);//不可见
                                            i.more_checkBox.setChecked(false);
                                        }
                                    }
                                    Frame_swith(swithCode);

                                    return true;
                                }
                            });
                            //主标题
                        } catch (Exception e) {
                            //e.printStackTrace();
                            //mList.add(new list_Item(""));
                            adapter = new listAdapter(null, this);//设置适配器数据为空
                            mListView.setAdapter(adapter);//为mListView设置适配器
                            Toast.makeText(MainActivity.this, "当前选定的目录无bilibili缓存文件哦", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;//获取到缓存集合的某一P路径就可以了不用深度遍历
                    }
                } else {
                    Toast.makeText(MainActivity.this, "当前选定的目录无bilibili缓存文件哦", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } else {
            Toast.makeText(MainActivity.this, "当前选定的目录无bilibili缓存文件哦", Toast.LENGTH_SHORT).show();
        }
    }


        */


}