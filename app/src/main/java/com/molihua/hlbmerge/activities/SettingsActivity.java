package com.molihua.hlbmerge.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.blankj.utilcode.util.ToastUtils;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.fragments.BackToolbarFragment;
import com.molihua.hlbmerge.interfaces.IActivityAndFragment;
import com.molihua.hlbmerge.utils.MLHInitConfig;
import com.molihua.hlbmerge.utils.PathTools;
import com.molihua.hlbmerge.utils.VersionTools;
import com.molihuan.pathselector.PathSelector;
import com.molihuan.pathselector.adapters.FileListAdapter;
import com.molihuan.pathselector.adapters.TabbarFileListAdapter;
import com.molihuan.pathselector.dao.SelectOptions;
import com.molihuan.pathselector.entities.FileBean;
import com.molihuan.pathselector.utils.Constants;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.button.switchbutton.SwitchButton;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置页面
 */
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, IActivityAndFragment, MaterialSpinner.OnItemSelectedListener {

    private BackToolbarFragment backToolbarFragment;
    private RelativeLayout linl_choose_download_path;
    private RelativeLayout relal_choose_complete_path;

    private RelativeLayout rell_switch_paly_version;
    private MaterialSpinner spinner_version;
    private SwitchButton switch_custom_path;
    private TextView tv_custom_path;
    private TextView tv_show_complete_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getComponents();//获取组件
        setListeners();//设置监听
        initData();//初始化数据
    }

    private void getComponents() {
        fragmentInit();
        linl_choose_download_path = findViewById(R.id.linl_choose_download_path);
        rell_switch_paly_version = findViewById(R.id.rell_switch_paly_version);
        spinner_version = findViewById(R.id.spinner_version);
        switch_custom_path = findViewById(R.id.switch_custom_path);
        tv_custom_path = findViewById(R.id.tv_custom_path);
        relal_choose_complete_path = findViewById(R.id.relal_choose_complete_path);
        tv_show_complete_path = findViewById(R.id.tv_show_complete_path);

    }

    private void fragmentInit() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();//开启事务
        backToolbarFragment = new BackToolbarFragment("设置");//实例化fragment
        FragmentTransaction transaction = fragmentTransaction.add(R.id.toolbar_area, backToolbarFragment);
        transaction.commitAllowingStateLoss();

    }

    private void setListeners() {

        linl_choose_download_path.setOnClickListener(this);
        rell_switch_paly_version.setOnClickListener(this);
        spinner_version.setOnItemSelectedListener(this);
        switch_custom_path.setOnClickListener(this);
        relal_choose_complete_path.setOnClickListener(this);
    }

    private void initData() {
        if (MLHInitConfig.isNullUserCustomPath()) {
            switch_custom_path.setChecked(false);
            tv_custom_path.setTextColor(Color.LTGRAY);
            spinner_version.setEnabled(true);
        } else {
            switch_custom_path.setChecked(true);
            spinner_version.setEnabled(false);
        }


        String biliDownPath = MLHInitConfig.getBiliDownPath();//读取配置
        spinner_version.setItems(ResUtils.getStringArray(R.array.version_show_title));//添加item

        int selectIndex;
        if (biliDownPath.matches(".*/tv.danmaku.bilibilihd/download$")) {
            selectIndex = 2;
        } else {
            if (biliDownPath.matches(".*/com.bilibili.app.in/download$")) {
                selectIndex = 1;
            } else {
                if (biliDownPath.matches(".*/com.bilibili.app.blue/download$")) {
                    selectIndex = 3;
                } else {
                    selectIndex = 0;
                }

            }
        }
        spinner_version.setSelectedIndex(selectIndex);//显示配置

        tv_show_complete_path.setText(MLHInitConfig.getUserCustomCompletePath());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linl_choose_download_path:
                if (switch_custom_path.isChecked()) {
                    openFileChoose();
                } else {
                    ToastUtils.make().show("请先点击右边的开关,再选择路径");
                }
                break;
            case R.id.switch_custom_path:
                boolean checked = switch_custom_path.isChecked();
                checkedChanged(checked);
                break;
            case R.id.relal_choose_complete_path:
                userCustomCompletePathChoose();
                break;

        }
    }

    /**
     * 文件选择器
     */
    private void openFileChoose() {
        PathSelector.build(this, Constants.BUILD_ACTIVITY)
                .requestCode(1000)
                .setMoreOPtions(new String[]{"选择路径"},
                        new SelectOptions.onToolbarOptionsListener() {
                            @Override
                            public void onOptionClick(View view, String currentPath, List<FileBean> fileBeanList, List<String> callBackData, TabbarFileListAdapter tabbarAdapter, FileListAdapter fileAdapter, List<FileBean> callBackFileBeanList) {
                                String path = null;
                                if (callBackData == null || callBackData.size() == 0) {
                                    path = currentPath;
                                } else {
                                    path = callBackData.get(0);
                                }
                                Toast.makeText(SettingsActivity.this, "设置路径为：\n" + path, Toast.LENGTH_LONG).show();

                                MLHInitConfig.setUserCustomPath(path);
                            }
                        })
                .start();
    }

    /**
     * 完成路径选择器
     */
    private void userCustomCompletePathChoose() {
        PathSelector.build(this, Constants.BUILD_ACTIVITY)
                .setMoreOPtions(new String[]{"选择路径"},
                        new SelectOptions.onToolbarOptionsListener() {
                            @Override
                            public void onOptionClick(View view, String currentPath, List<FileBean> fileBeanList, List<String> callBackData, TabbarFileListAdapter tabbarAdapter, FileListAdapter fileAdapter, List<FileBean> callBackFileBeanList) {
                                String path = null;
                                if (callBackData == null || callBackData.size() == 0) {
                                    path = currentPath;
                                } else {
                                    path = callBackData.get(0);
                                }
                                Toast.makeText(SettingsActivity.this, "设置路径为：\n" + path, Toast.LENGTH_LONG).show();
                                MLHInitConfig.setUserCustomCompletePath(path);
                                tv_show_complete_path.setText(MLHInitConfig.getUserCustomCompletePath());
                            }
                        })
                .start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (data != null) {
                ArrayList<String> essFileList = data.getStringArrayListExtra(Constants.CALLBACK_DATA_ARRAYLIST_STRING);
                MLHInitConfig.setUserCustomPath(essFileList.get(0));
                Toast.makeText(SettingsActivity.this, "设置路径为：\n" + essFileList.get(0), Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public Object invokeFuncAiF(int functionCode) {
        switch (functionCode) {
            case 0:
                return "设置";
        }
        return null;
    }


    @Override
    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {

        String tempFPath = null;//前面主要路径
        if (VersionTools.isAndroid11()) {//判断是否是安卓11
            tempFPath = PathTools.getOutputTempPath();
        } else {
            tempFPath = PathTools.getADPath();
        }

        //设置数据路径
        switch (item.toString()) {
            case "哔哩哔哩(国内版)":
                tempFPath = tempFPath + "/tv.danmaku.bili/download";
                break;
            case "bilibili(国际版)":
                tempFPath = tempFPath + "/com.bilibili.app.in/download";
                break;
            case "哔哩哔哩HD(国内平板版)":
                tempFPath = tempFPath + "/tv.danmaku.bilibilihd/download";
                break;
            case "哔哩哔哩(概念版)":
                tempFPath = tempFPath + "/com.bilibili.app.blue/download";
                break;
        }

        MLHInitConfig.setBiliDownPath(tempFPath);//写入配置

    }

    public void checkedChanged(boolean isChecked) {
        if (isChecked) {
            switch_custom_path.setChecked(true);
            tv_custom_path.setTextColor(Color.BLACK);
            spinner_version.setEnabled(false);
            ToastUtils.make().show("请点击“自定义缓存目录”选择路径");
        } else {
            MLHInitConfig.setUserCustomPath("");
            switch_custom_path.setChecked(false);
            tv_custom_path.setTextColor(Color.LTGRAY);
            spinner_version.setEnabled(true);
        }
    }
}