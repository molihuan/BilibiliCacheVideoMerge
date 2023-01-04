package com.coder.ffmpeg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class settings extends AppCompatActivity implements View.OnClickListener ,CompoundButton.OnCheckedChangeListener{
    LinearLayout chooseDownloadPath;
    Switch switch_chooseBiliVersion;
    SharedPreferences msharedPreferences;
    SharedPreferences.Editor msharedPreferEditor;
    boolean swith_code;

    //重写onOptionsItemSelected方法，进行菜单item监听  返回键监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {

            case  android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);//回调
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("设置");
        setContentView(R.layout.activity_settings);

        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);//设置返回键
        }

        findID();
        initView();
        setclick();




    }

    private void initView() {
        //读取开关信息
        if(configureDataRead("data", "settings_switch_chooseBiliVersion", "tv.danmaku.bili").equals("tv.danmaku.bili")){
            switch_chooseBiliVersion.setChecked(false);
        }else {
            switch_chooseBiliVersion.setChecked(true);
        }
    }

    private void findID(){
        chooseDownloadPath=findViewById(R.id.chooseDownloadPath);
        switch_chooseBiliVersion=findViewById(R.id.switch_chooseBiliVersion);


    }
    private void setclick(){
        chooseDownloadPath.setOnClickListener(this);
        switch_chooseBiliVersion.setOnCheckedChangeListener(this);
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


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chooseDownloadPath:
                //跳转
                if (switch_chooseBiliVersion.isChecked()){
                    Toast.makeText(settings.this, "设置此选项请取消勾选“Play版本”！", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(settings.this, fileMainActivity.class);
                    startActivity(intent);//跳转选择合并目录页面
                }

                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.switch_chooseBiliVersion:
                if (switch_chooseBiliVersion.isChecked()) {
                    //Toast.makeText(settings.this, "开", Toast.LENGTH_LONG).show();
                    configureDataSet("data", "settings_switch_chooseBiliVersion", "com.bilibili.app.in");
                    swith_code = true;
                } else {
                    //Toast.makeText(settings.this, "关", Toast.LENGTH_LONG).show();
                    configureDataSet("data", "settings_switch_chooseBiliVersion", "tv.danmaku.bili");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        //安卓11视频缓存合并目录
                        configureDataSet("data", "mergePath", MainActivity.targetPath);
                    } else {
                        //安卓5~10视频缓存合并目录
                        configureDataSet("data", "mergePath", MainActivity.backPath);
                    }
                    swith_code = false;
                }
                switch_chooseBiliVersion.setChecked(swith_code);
                break;
        }
    }
}