package com.coder.ffmpeg;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class splitMerge_Main extends AppCompatActivity {

    //恢复
    @Override
    protected void onResume() {
        super.onResume();
        for (FileAdapter.ViewHolder i : FileAdapter.all_holder) {
            i.file_checkBox.setVisibility(View.INVISIBLE);//不可见
            //Log.e("------",FileAdapter.all_holder.size()+"被隐藏");
        }
        Log.d("MainActivityLifeCycle", "-------onResume---------");
    }

    //暂停
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivityLifeCycle", "-------onPause---------");
    }

    //停止
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivityLifeCycle", "--------onStop--------");
    }

    //重启
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("MainActivityLifeCycle", "-------onRestart---------");
    }

    //销毁
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mpath.equals(rootParentPath)) {//如果到头了就删除当前Activity
            configureDataSet("data", "splitMerge_showPath", rootpath);//保存以前的位置
        } else {
            configureDataSet("data", "splitMerge_showPath", splitMerge_tv_lj.getText().toString());//保存以前的位置
        }

        FileAdapter.buju_code = 0;
        Log.d("MainActivityLifeCycle", "--------onDestroy--------");
    }

    //启动
    @Override
    protected void onStart() {
        super.onStart();
        FileAdapter.use_choose.clear();
        FileAdapter.all_holder.clear();
        Log.d("启动", "--------onStart--------");
    }
    public static final String rootpath = Environment.getExternalStorageDirectory().getAbsolutePath();
    ListView mListView;
    TextView splitMerge_tv_lj;
    Button choose_path, MR;
    EditText search_EditText;
    List<fileItem> mList = new ArrayList();
    String configurefile_path;//获取软件内部存储file文件夹的路径//配置文件路径
    FileAdapter adapter;
    static String mpath = rootpath + "/Android/data/tv.danmaku.bili";
    SharedPreferences msharedPreferences;
    SharedPreferences.Editor msharedPreferEditor;

    SearchView mSearchView = null;

    public static String backupdir = "/上一级目录/上一级目录";
    String MD5MergeFilePath = "";
    boolean bad = false;//是否有坏的文件

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
        mSearchView = (SearchView) searchItem.getActionView();
///////////////////////////////////////////////
        int id = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
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
                newText = newText.replaceAll(MainActivity.regEx1, "");//去除特殊符号防止下面的正则表达式错误导致奔溃
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
                        String LJ = changelist.get(i).getLJ();
                        int first = LJ.indexOf("/"); //单引号第一次出现的位置
                        int last = LJ.lastIndexOf("/"); //单引号最后一次出现的位置
                        String aa = LJ.substring(first, last + 1);//截取后变成新的字符串
                        String newLJ = LJ.replace(aa, "");

                        //Title = changelist.get(i).getLJ().split(":")[0].replaceAll("\n", "");
                        //提取标题
                        Log.e("输入为", newText);
                        if (!newLJ.matches(".*" + newText + ".*")) {//模糊匹配
                            Log.e("移除的为", newLJ);
                            changelist.remove(i);
                        }
                    }
                    if (changelist.size() == 0) changelist = null;
                    adapter = new FileAdapter(changelist, splitMerge_Main.this);//设置适配器数据
                    mListView.setAdapter(adapter);//添加适配器


                    //Toast.makeText(fileMainActivity.this, newText, Toast.LENGTH_LONG).show();
                }

                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);//回调
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//启用左上角返回键
        setTitle("请选择需拆分合并的文件");//去除标题栏的lable即文字

        setContentView(R.layout.splitmerge);

        File rootFile=new File(rootpath);
        rootParentPath=rootFile.getParent();

        FileAdapter.buju_code = 1;

        findID();


        //setViewSize(TBB,0,0);


        configurefile_path = getFilesDir().getAbsolutePath().replace("files", "shared_prefs");//获取软件内部存储shared_prefs文件夹的路径//配置文件路径


        mpath = configureDataRead("data", "splitMerge_showPath", "");//获取data.xml里面的合并路径
        File file = new File(mpath);
        if (!file.exists()) {//如果data.xml读取的合并路径不存在
            mpath = rootpath;//设置为根目录
        }
        splitMerge_tv_lj.setText(mpath);//设置textview的文字
        initView();


        initData(mpath);

    }

    /**
     * 设置控件大小
     *
     * @param view   控件
     * @param width  宽度，单位：像素
     * @param height 高度，单位：像素
     */
    public static void setViewSize(View view, int width, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    private void findID() {//获取id


        choose_path = findViewById(R.id.choose_path);
        MR = findViewById(R.id.MR);
        splitMerge_tv_lj = findViewById(R.id.splitMerge_tv_lj);


    }

    private void initView() {
        mListView = findViewById(R.id.splitMerge_file_list);
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
        mList.add(new fileItem(backupdir, true));//true是文件夹标识

        File f1 = new File(path);
        File[] arr1 = f1.listFiles();
        if (arr1 != null) {
            for (File a : arr1) {//读取download下的目录   主题
                Log.d("主：", a + "----------------------");
                if (a.isDirectory())
                    mList.add(new fileItem(a.toString(), true));//true是文件夹标识
                else {
                    mList.add(new fileItem(a.toString(), false));//false是文件标识
                }
            }

            adapter = new FileAdapter(mList, this);
            mListView.setAdapter(adapter);
            //路径输入监听
            splitMerge_tv_lj.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TZdialogView();
                }
            });
            //item监听
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    mpath = mList.get(position).getLJ();//获取item全路径

                    int first = mpath.indexOf("/"); //单引号第一次出现的位置
                    int last = mpath.lastIndexOf("/"); //单引号最后一次出现的位置
                    String aa = mpath.substring(first, last + 1);//截取后变成新的字符串
                    String fileName = mpath.replace(aa, "");

                    if (mpath.equals(backupdir)) {//点击的是返回上一级
                        File file1 = new File(splitMerge_tv_lj.getText().toString());
                        mpath = file1.getParent();
                        if (mpath.equals(rootParentPath)) {//如果到头了就删除当前Activity
                            finish();
                        }
                        splitMerge_tv_lj.setText(mpath);
                        initData(mpath);
                    } else {
                        File file2 = new File(mpath);
                        if (file2.isFile()) {//如果是文件就拆分合并
                            dialogView(file2, fileName);
                        } else {//不是则继续打开文件夹
                            splitMerge_tv_lj.setText(mpath);
                            initData(mpath);
                            search_EditText.setText("");//去除搜索框文字
                        }
                    }
                    //configureDataSet("data", "splitMerge_showPath", splitMerge_tv_lj.getText().toString());


                    mSearchView.clearFocus();//取消搜索焦点
                    mSearchView.clearAnimation();//取消搜索焦点
                    //Toast.makeText(fileMainActivity.this, mpath, Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            //Toast.makeText(splitMerge_Main.this, "返回根目录", Toast.LENGTH_SHORT).show();
            getdata(rootpath);
        }
    }

    //跳转路径弹窗对话框
    private void TZdialogView() {
        AlertDialog.Builder builder = new AlertDialog.Builder(splitMerge_Main.this, R.style.TransparentDialog);
        View alertdialog = LayoutInflater.from(splitMerge_Main.this).inflate(R.layout.tzalertdialog, null);
        EditText sr_path_edt = alertdialog.findViewById(R.id.sr_path_edt);
        Button choosealertdialog_yes = alertdialog.findViewById(R.id.tzalertdialog_yes);
        Button choosealertdialog_no = alertdialog.findViewById(R.id.tzalertdialog_no);
        builder.setCancelable(false);

        sr_path_edt.setText(splitMerge_tv_lj.getText());

        AlertDialog alertDialog = builder.setView(alertdialog).show();
        choosealertdialog_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = sr_path_edt.getText().toString();
                File file = new File(temp);
                if (file.exists()) {//判断是否存在
                    String cl = rootParentPath;
                    if (file.isDirectory()) {//判断是否是文件
                        cl = file.getPath();
                    } else {//不是文件就获取上一级目录
                        //Log.e("-------",file.getParent());
                        cl = file.getParent();
                    }
                    splitMerge_tv_lj.setText(cl);
                    initData(cl);

                } else {
                    Toast.makeText(splitMerge_Main.this, "路径不存在！", Toast.LENGTH_LONG).show();
                }

                alertDialog.dismiss();
            }
        });
        choosealertdialog_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    //合并拆分弹窗对话框
    private void dialogView(File file, String fileName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(splitMerge_Main.this, R.style.TransparentDialog);
        View alertdialog = LayoutInflater.from(splitMerge_Main.this).inflate(R.layout.choosealertdialog, null);
        RadioButton split_rbtn = alertdialog.findViewById(R.id.split_rbtn);
        RadioButton merge_rbtn = alertdialog.findViewById(R.id.merge_rbtn);
        EditText split_edt = alertdialog.findViewById(R.id.split_edt);
        TextView choosealertdialog_titel = alertdialog.findViewById(R.id.choosealertdialog_titel);
        Button choosealertdialog_yes = alertdialog.findViewById(R.id.choosealertdialog_yes);
        Button choosealertdialog_no = alertdialog.findViewById(R.id.choosealertdialog_no);
        builder.setCancelable(true);
        String size = configureDataRead("data", "split_size", "10");
        split_edt.setText(size);
        choosealertdialog_titel.setText("您需要对\n\"" + fileName + "\"");
        AlertDialog alertDialog = builder.setView(alertdialog).show();
        choosealertdialog_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //界面UI的更新必须在创新view的那个线程即主线程（UI线程）而且必须开辟新的线程进行耗时操作
                Handler handler = new Handler() {//界面UI的更新操作
                    @SuppressLint("HandlerLeak")
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what==1)
                            getdata(splitMerge_tv_lj.getText().toString());//刷新当前界面
                    }
                };


                final int[] pd_code = {0};//判断是否进行合并或者拆分了
                if (split_rbtn.isChecked() && !split_edt.getText().toString().equals("")) {//拆分

                    ProgressDialog progressDialog = ProgressDialog.show(splitMerge_Main.this, "提示", "正在拆分文件请稍后...");
                    progressDialog.setCancelable(false);//返回不能取消边缘不能点击
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                Split(mpath, Integer.parseInt(split_edt.getText().toString()), file.getParent());
                                pd_code[0] = 1;
                            } catch (Exception E) {
                                pd_code[0] = 0;
                            } finally {
                                progressDialog.cancel();//等待合并界面清除
                            }




                            Looper.prepare();
                            if (pd_code[0] == 1) {
                                Toast.makeText(splitMerge_Main.this, "拆分成功！\n拆分文件在当前目录哦！PS(拆分后的文件不要重命名不然无法还原成原文件)", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(splitMerge_Main.this, "错误！出现bug了！", Toast.LENGTH_LONG).show();
                            }
                            //处理完成后给handler发送消息
                            Message msg = new Message();
                            msg.what=1;
                            handler.sendMessage(msg);
                            Looper.loop();


                        }
                    }.start();


                }







                //合并
                if (merge_rbtn.isChecked()) {

                    ProgressDialog progressDialog = ProgressDialog.show(splitMerge_Main.this, "提示", "正在校验MD5进行合并请稍后...");
                    progressDialog.setCancelable(false);//返回不能取消边缘不能点击

                    new Thread() {
                        @Override
                        public void run() {

                            pd_code[0] = 1;
                            //选择的文件是否是MD5
                            if (file.getPath().matches(".*.MD5$")) {

                                MD5MergeFilePath = file.getPath();//获取MD5文件路径
                                try {
                                    //通过MD5来进行合并----不推荐   慢   但是可以效验
                                    Merge(file.getParent(), file.getParent(), 1);
                                } catch (Exception E) {
                                    pd_code[0] = 0;
                                } finally {
                                    progressDialog.cancel();//等待合并界面清除
                                }

                            } else {
                                try {
                                    //通过文件名来进行合并----推荐   快
                                    Merge(file.getParent(), file.getParent(), 0);
                                } catch (Exception E) {
                                    pd_code[0] = 0;
                                } finally {
                                    progressDialog.cancel();//等待合并界面清除
                                }
                            }




                            Looper.prepare();
                            if (pd_code[0] == 1 && !bad) {
                                Toast.makeText(splitMerge_Main.this, "合并成功！\n合并文件在当前目录哦！PS(如果小文件完整但合并后的大文件使用不了,大概率是某些小文件损坏了。建议用MD5校验(比较慢)后再进行合并!)", Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(splitMerge_Main.this, "错误！出现bug了！", Toast.LENGTH_LONG).show();
                            }
                            //处理完成后给handler发送消息
                            Message msg = new Message();
                            msg.what=1;
                            handler.sendMessage(msg);
                            Looper.loop();




                        }
                    }.start();
                }

                configureDataSet("data", "split_size", split_edt.getText().toString());

                alertDialog.dismiss();
            }
        });
        choosealertdialog_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    //分割，参数说明：需分割文件的全路径（输入），切割的大小MB为单位（设置），分割后文件存放的文件夹全路径（设置）
    public void Split(String SrcFilePath, int SingleGoalFileSize, String GoalFileDirectory) {
        //SingleGoalFileSize 单位：MB   ，校验路径和目录

        if (SrcFilePath.equals("") || SrcFilePath == null) {
            System.out.println("分割失败!");
            return;
        }

        File SrcFile = new File(SrcFilePath);  //新建文件
        long SrcFileSize = SrcFile.length();//源文件的大小
        long SingleFileSize = 1024 * 1024 * SingleGoalFileSize;//分割后的单个文件大小(单位字节)

        int GoalFileNum = (int) (SrcFileSize / SingleFileSize);  //获取文件的大小
        GoalFileNum = SrcFileSize % SingleFileSize == 0 ? GoalFileNum : GoalFileNum + 1;  //计算总的文件大小


        int x1 = SrcFilePath.lastIndexOf("/"); //获取文件路径的分隔符位置
        int x2 = 0;
        boolean huozui_code = true;
        String SrcFileName = "";
        try {//有后缀名
            x2 = SrcFilePath.lastIndexOf("."); //获取文件的后缀位置
            SrcFileName = SrcFilePath.substring(x1, x2); //截取文件名
        } catch (Exception E) {//无后缀名
            SrcFileName = SrcFilePath.substring(x1).replaceAll("/", "") + "拆分"; //截取文件名
            huozui_code = false;
            //Toast.makeText(splitMerge_Main.this,SrcFileName+E.toString(),Toast.LENGTH_LONG).show();
        }


        File goalDirectory = new File(GoalFileDirectory + File.separator + SrcFileName + File.separator);
        if (!goalDirectory.exists()) {
            goalDirectory.mkdirs();
        }

        String splitFileAllMD5 = "";

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        byte bytes[] = new byte[1024 * 1024];//每次读取文件的大小
        int len = -1;

        try {
            fis = new FileInputStream(SrcFilePath);  //新建输入流对象
            bis = new BufferedInputStream(fis);

            for (int i = 0; i < GoalFileNum; i++) {
                String CompleteSingleGoalFilePath = "";
                if (huozui_code) {
                    //有后缀分割后的单个文件完整路径名
                    CompleteSingleGoalFilePath = GoalFileDirectory + File.separator + SrcFileName + File.separator + SrcFileName + "-" + i + "标识" + SrcFilePath.substring(x2);//File.separator 的作用相当于 ' \  '所以用 File.separator 保证了在任何系统下不会出错。
                } else {
                    //无后缀分割后的单个文件完整路径名
                    CompleteSingleGoalFilePath = GoalFileDirectory + File.separator + SrcFileName + File.separator + SrcFileName + "-" + i + "标识";//File.separator 的作用相当于 ' \  '所以用 File.separator 保证了在任何系统下不会出错。
                }

                FileOutputStream fos = new FileOutputStream(CompleteSingleGoalFilePath);
                BufferedOutputStream bos = new BufferedOutputStream(fos);  //包装
                int count = 0;
                while ((len = bis.read(bytes)) != -1) {
                    bos.write(bytes, 0, len);//从源文件读取规定大小的字节数写入到单个目标文件中
                    count += len;
                    if (count >= SingleFileSize)
                        break;
                }
                bos.flush();
                bos.close();
                fos.close();

                //Log.e("-------------",fileToMD5(CompleteSingleGoalFilePath));
                splitFileAllMD5 += (fileToMD5(CompleteSingleGoalFilePath) + "\r\n");
            }
            //Log.e("------文件-------",splitFileAllMD5);
            String MD5FileName = GoalFileDirectory + File.separator + SrcFileName + File.separator + SrcFileName + ".MD5";//拆分时所有MD5保存文件全路径
            txtTool.writeFile(MD5FileName, splitFileAllMD5);//写入保存拆分后的文件的MD5值
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }

                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Toast.makeText(MainActivity.this,"分割成功！！！",Toast.LENGTH_SHORT).show();
    }

    //参数说明：分割后文件存放文件夹全路径(输入)或者MD5文件全路径,合并后文件存放的文件夹全路径（设置）
    public void Merge(String FilePath, String GoalFileDirectory, int MD5_code) {
        //合并后文件存放的文件夹全路径不存在则创建
        File goalDirectory = new File(GoalFileDirectory);
        if (!goalDirectory.exists()) {
            goalDirectory.mkdirs();
        }

        String CompleteGoalFilePath = "";//分割后的单个文件完整路径名
        String[] SingleFilePath;

        //通过MD5文件进行合并
        if (MD5_code == 1) {

            List<String> readMD5 = txtTool.readFile(MD5MergeFilePath);//从文件中获取所有拆分文件的MD5

            File f1 = new File(FilePath);
            File[] fileList = f1.listFiles();//遍历拆分文件目录

            SingleFilePath = new String[readMD5.size()];//存放排好序拆分文件全路径

            //遍历进行排序
            for (int i = 0; i < readMD5.size(); i++) {
                Log.d("-------合并时读取的MD--------", readMD5.get(i) + "长度----" + readMD5.size());
                for (int n = 0; n < fileList.length; n++) {
                    if (readMD5.get(i).equals(fileToMD5(fileList[n].getPath()))) {
                        SingleFilePath[i] = fileList[n].getPath();
                    }
                }
            }


            //分割后的单个文件完整路径名
            int x1 = SingleFilePath[0].lastIndexOf("/"); //获取文件路径的分隔符位置
            int x2 = 0;
            boolean huozui_code = true;//判断是否有后缀
            String GoalFileName = "";
            try {//有后缀名
                x2 = SingleFilePath[0].lastIndexOf("."); //获取文件的后缀位置
                GoalFileName = SingleFilePath[0].substring(x1, x2); //截取文件名
            } catch (Exception E) {//无后缀名
                GoalFileName = SingleFilePath[0].substring(x1).replaceAll("/", "") + "拆分"; //截取文件名
                huozui_code = false;
                //Toast.makeText(splitMerge_Main.this,SrcFileName+E.toString(),Toast.LENGTH_LONG).show();
            }


            if (huozui_code) {
                //有后缀分割后的单个文件完整路径名
                CompleteGoalFilePath = GoalFileDirectory + File.separator + GoalFileName.substring(0, GoalFileName.lastIndexOf("-")) + SingleFilePath[0].substring(x2);//File.separator 的作用相当于 ' \  '所以用 File.separator 保证了在任何系统下不会出错。
            } else {
                //无后缀分割后的单个文件完整路径名
                CompleteGoalFilePath = GoalFileDirectory + File.separator + GoalFileName.substring(0, GoalFileName.lastIndexOf("-"));//File.separator 的作用相当于 ' \  '所以用 File.separator 保证了在任何系统下不会出错。
            }


        } else {


            File FilePathes = new File(FilePath);
            File[] fristList = FilePathes.listFiles();
            int n = 0;
            for (File i : fristList) {//文件数组获取大小
                if (i.getPath().matches(".*-[0-9]*标识.*")) {
                    n++;
                }
            }
            File[] tempFilePath = new File[n];//创建中转文件数组
            SingleFilePath = new String[n];//创建最终路径数组排序好了的
            int k = 0;
            for (int i = 0; i < fristList.length; i++) {
                if (fristList[i].getPath().matches(".*-[0-9]*标识.*")) {
                    tempFilePath[k] = fristList[i];
                    k++;
                }
            }


            for (int i = 0; i < n; i++) {//排序
                int y1 = tempFilePath[i].getPath().lastIndexOf("-") + 1;
                int y2 = tempFilePath[i].getPath().lastIndexOf("标识");
                int index = Integer.valueOf(tempFilePath[i].getPath().substring(y1, y2));

                SingleFilePath[index] = tempFilePath[i].getPath();


            }


            if (GoalFileDirectory == null || "".equals(GoalFileDirectory)) {
                System.out.println("合并失败!");
                return;
            }

//        int x1 = SingleFilePath[0].lastIndexOf("/");
//        int x2 = SingleFilePath[0].lastIndexOf(".");
//        String GoalFileName = SingleFilePath[0].substring(x1,x2);


            int x1 = SingleFilePath[0].lastIndexOf("/"); //获取文件路径的分隔符位置
            int x2 = 0;
            boolean huozui_code = true;//判断是否有后缀
            String GoalFileName = "";
            try {//有后缀名
                x2 = SingleFilePath[0].lastIndexOf("."); //获取文件的后缀位置
                GoalFileName = SingleFilePath[0].substring(x1, x2); //截取文件名
            } catch (Exception E) {//无后缀名
                GoalFileName = SingleFilePath[0].substring(x1).replaceAll("/", "") + "拆分"; //截取文件名
                huozui_code = false;
                //Toast.makeText(splitMerge_Main.this,SrcFileName+E.toString(),Toast.LENGTH_LONG).show();
            }


            if (huozui_code) {
                //有后缀分割后的单个文件完整路径名
                CompleteGoalFilePath = GoalFileDirectory + File.separator + GoalFileName.substring(0, GoalFileName.lastIndexOf("-")) + SingleFilePath[0].substring(x2);//File.separator 的作用相当于 ' \  '所以用 File.separator 保证了在任何系统下不会出错。
            } else {
                //无后缀分割后的单个文件完整路径名
                CompleteGoalFilePath = GoalFileDirectory + File.separator + GoalFileName.substring(0, GoalFileName.lastIndexOf("-"));//File.separator 的作用相当于 ' \  '所以用 File.separator 保证了在任何系统下不会出错。
            }

        }


        bad = false;//是否有坏的文件

        //合并后的完整路径名
        //String CompleteGoalFilePath = GoalFileDirectory + File.separator + GoalFileName.substring(0,GoalFileName.lastIndexOf("-"))+ SingleFilePath[0].substring(x2);

        byte bytes[] = new byte[1024 * 1024];//每次读取文件的大小
        int len = -1;

        FileOutputStream fos = null;//将数据合并到目标文件中
        BufferedOutputStream bos = null;//使用缓冲字节流写入数据
        try {
            fos = new FileOutputStream(CompleteGoalFilePath);
            bos = new BufferedOutputStream(fos);

            for (int i = 0; i < SingleFilePath.length; i++) {

                i = pdIndex(SingleFilePath, i);//递归判断是否有损坏的文件

                FileInputStream fis = new FileInputStream(SingleFilePath[i]);//从分割后的文件读取数据
                BufferedInputStream bis = new BufferedInputStream(fis);//使用缓冲字节流读取数据
                while ((len = bis.read(bytes)) != -1)
                    bos.write(bytes, 0, len);

                bis.close();
                fis.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null)
                    bos.close();

                if (fos != null)
                    fos.close();

                if (bad) {
                    DeleteUtil.delete(CompleteGoalFilePath);//删除坏的文件
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //Toast.makeText(MainActivity.this,"合并成功！！！",Toast.LENGTH_SHORT).show();
    }


    private int pdIndex(String[] SingleFilePath, int i) {
        if (SingleFilePath[i] == null || "".equals(SingleFilePath)) {
            bad = true;

            //Log.e("--------损坏",i+"");
            Toast.makeText(splitMerge_Main.this, "序号为" + i + "的文件已损坏或者不存在!", Toast.LENGTH_LONG).show();

            i++;//跳过当前的路径
            pdIndex(SingleFilePath, i);
            //System.exit(0);
        } else {
            return i;
        }
        return i;

    }

    //获取文件的MD5值      文件全路径
    public static String fileToMD5(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            fis.close();
            BigInteger bigInt = new BigInteger(1, digest.digest());
            return bigInt.toString(16);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "-1";
    }

    @Override
    public void onBackPressed() {//重写返回键
        File file = new File(mpath);
        mpath = file.getParent();
        if (mpath.equals(rootParentPath)) {
            super.onBackPressed();
        } else {
            splitMerge_tv_lj.setText(mpath);
            initData(mpath);

            search_EditText.setText("");//去除搜索框文字
            mSearchView.clearFocus();//取消搜索焦点
            mSearchView.clearAnimation();//取消搜索焦点
        }
    }
}