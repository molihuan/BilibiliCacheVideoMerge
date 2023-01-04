package com.coder.ffmpeg;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class fileMainActivity extends AppCompatActivity {

    ListView mListView;
    Button choose_path, MR;
    TextView tv_lj;
    EditText search_EditText;
    List<fileItem> mList = new ArrayList();
    String configurefile_path;//获取软件内部存储file文件夹的路径//配置文件路径
    FileAdapter adapter;

    public static final String rootpath = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String mpath = rootpath + "/Android/data/tv.danmaku.bili";
    SharedPreferences msharedPreferences;
    SharedPreferences.Editor msharedPreferEditor;


    SearchView mSearchView=null;
    private String updir="/上一级目录/上一级目录";
    String rootParentPath;//根目录的父目录路径



    //左上角返回键监听
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wj, menu);//添加menu_gy菜单
        //找到SearchView并配置相关参数
        MenuItem searchItem = menu.findItem(R.id.wengjian_search);
        mSearchView= (SearchView) searchItem.getActionView();
///////////////////////////////////////////////
        int id =mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        search_EditText = (EditText) mSearchView.findViewById(id);
///////////////////////////////////////////



        //设置搜索框展开时是否显示提交按钮，可不显示
        mSearchView.setSubmitButtonEnabled(false);
        //让键盘的回车键设置成搜索
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        //设置提示词
        mSearchView.setQueryHint("请输入关键字");
        // 设置搜索文本监听
        mSearchView.getQuery();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                //                如果newText不是长度为0的字符串
                newText=newText.replaceAll(MainActivity.regEx1,"");//去除特殊符号防止下面的正则表达式错误导致奔溃
                if (TextUtils.isEmpty(newText)) {
//                    清除ListView的过滤

                    initData(mpath);
                } else {
//                  使用用户输入的内容对ListView的列表项进行过滤
                    initData(mpath);
                    List<fileItem> changelist = mList;

                    Log.e("listview有", String.valueOf(changelist.size()));

                    for (int i = changelist.size() - 1; i >= 0; i--)//这是一个坑，会越界，因为下面remove了
                    {
                        String LJ=changelist.get(i).getLJ();
                        int first = LJ.indexOf("/"); //单引号第一次出现的位置
                        int last = LJ.lastIndexOf("/"); //单引号最后一次出现的位置
                        String aa = LJ.substring(first, last+1);//截取后变成新的字符串
                        String newLJ=LJ.replace(aa,"");

                        //Title = changelist.get(i).getLJ().split(":")[0].replaceAll("\n", "");
                        //提取标题
                        //Log.e("输入为", newText);
                        if (!newLJ.matches(".*" + newText + ".*")) {//模糊匹配
                            //Log.e("移除的为", newLJ);
                            changelist.remove(i);
                        }
                    }
                    if (changelist.size()==0)changelist=null;
                    adapter = new FileAdapter(changelist, fileMainActivity.this);//设置适配器数据
                    mListView.setAdapter(adapter);//添加适配器


                    //Toast.makeText(fileMainActivity.this, newText, Toast.LENGTH_LONG).show();
                }

                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);//回调
    }

    //启动
    @Override
    protected void onStart() {
        super.onStart();
        FileAdapter.use_choose.clear();
        Log.d("启动", "--------onStart--------");
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//启用左上角返回键
        setTitle("自定义缓存目录");//去除标题栏的lable即文字
        setContentView(R.layout.activity_file_main);

        File rootFile=new File(rootpath);
        rootParentPath=rootFile.getParent();






        tv_lj = findViewById(R.id.tv_lj);
        choose_path = findViewById(R.id.choose_path);
        MR = findViewById(R.id.MR);

        configurefile_path = getFilesDir().getAbsolutePath().replace("files", "shared_prefs");//获取软件内部存储shared_prefs文件夹的路径//配置文件路径


        mpath = configureDataRead("data", "mergePath", "");//获取data.xml里面的合并路径
        Log.e("mergePath",mpath);
        File file = new File(mpath);
        if (!file.exists()) {//如果data.xml读取的合并路径不存在
            Log.e("mergePath",mpath+"不存在");
            mpath = rootpath;//设置为根目录
        }

        tv_lj.setText(mpath);//设置textview的文字
        initView();


        initData(mpath);





        //还原默认按钮监听
        MR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {//安卓11
                    configureDataSet("data", "mergePath", MainActivity.targetPath);
                } else {
                    configureDataSet("data", "mergePath", mpath + "/download");
                }
                Toast.makeText(fileMainActivity.this, "设置成功！", Toast.LENGTH_SHORT).show();
            }
        });
        //选择按钮监听
        choose_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (FileAdapter.use_choose.size()) {
                    case 0:
                        Toast.makeText(fileMainActivity.this, "您还没有选择哦！", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        configureDataSet("data", "mergePath", FileAdapter.use_choose.get(0));
                        Toast.makeText(fileMainActivity.this, "选择成功", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(fileMainActivity.this, "不能选择多个路径哦！", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void initView() {
        mListView = findViewById(R.id.file_list);
    }

    private void initData(String path) {

        getdata(path);
    }

    //使用sharedPreferences进行数据存储fileName不要后缀
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




    private void getdata(String path) {


        mList.clear();//清除mList中<list_Item>对象
        mList.add(new fileItem(updir, true));//true是文件夹标识

        File f1 = new File(path);
        File[] arr1 = f1.listFiles();
        if (arr1 != null) {
            for (File a : arr1) {//读取download下的目录   主题
                Log.d("主：", a + "----------------------");
                if (a.isDirectory())
                    mList.add(new fileItem(a.toString(), true));//true是文件夹标识
                else {
                    mList.add(new fileItem(a.toString(), false));
                }
            }
            for (fileItem item:mList) {
                Log.e("-----------",item.getLJ()+"----"+item.geticon());
            }

            adapter = new FileAdapter(mList, this);
            mListView.setAdapter(adapter);
            //item监听
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (FileAdapter.use_choose.isEmpty()) {

                        mpath = mList.get(position).getLJ();
                        if (mpath.equals(updir)) {//如果点击的是返回上一级
                            File file=new File(tv_lj.getText().toString());
                            mpath=file.getParent();
                            if (mpath.equals(rootParentPath)) {//如果到头了就删除当前Activity
                                finish();
                            }
                            tv_lj.setText(mpath);
                            initData(mpath);
                        }else {
                            File file2=new File(mpath);
                            if (file2.isFile()) {//如果是文件不做

                            } else {//不是则继续打开文件夹
                                tv_lj.setText(mpath);
                                initData(mpath);
                            }
                        }

                        search_EditText.setText("");//去除搜索框文字
                        mSearchView.clearFocus();//取消搜索焦点
                        mSearchView.clearAnimation();//取消搜索焦点
                        //Toast.makeText(fileMainActivity.this, mpath, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            getdata(rootpath);
        }
    }

    @Override
    public void onBackPressed() {//重写返回键
        File file=new File(mpath);
        mpath=file.getParent();
        if (mpath.equals(rootParentPath)) {
            super.onBackPressed();
        } else {
            tv_lj.setText(mpath);
            initData(mpath);

            search_EditText.setText("");//去除搜索框文字
            mSearchView.clearFocus();//取消搜索焦点
            mSearchView.clearAnimation();//取消搜索焦点
        }
    }
}