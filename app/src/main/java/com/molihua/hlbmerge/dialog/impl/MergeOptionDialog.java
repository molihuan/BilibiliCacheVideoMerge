package com.molihua.hlbmerge.dialog.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.dao.ConfigData;
import com.molihua.hlbmerge.entity.CacheFile;
import com.xuexiang.xui.widget.button.switchbutton.SwitchButton;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: JudgeMergeDialog
 * @Author: molihuan
 * @Date: 2022/12/21/15:50
 * @Description: 合并前选项弹窗
 */
public class MergeOptionDialog {


    public static MaterialDialog showMergeOptionDialog(CacheFile cacheFile, Context context) {
        List<CacheFile> cacheFileList = new ArrayList<>();
        cacheFileList.add(cacheFile);
        return showMergeOptionDialog(cacheFileList, context);
    }

    public static MaterialDialog showMergeOptionDialog(List<CacheFile> cacheFileList, Context context) {

        View dialog_judgemerge = LayoutInflater.from(context).inflate(R.layout.dialog_judge_merge, null);
        MaterialSpinner dialog_materialspinner = dialog_judgemerge.findViewById(R.id.dialog_materialspinner);
        SwitchButton switchBtn_XMLexport = dialog_judgemerge.findViewById(R.id.switchBtn_XMLexport);

        switchBtn_XMLexport.setChecked(ConfigData.isExportDanmaku());
        dialog_materialspinner.setSelectedIndex(ConfigData.getExportType());

        dialog_materialspinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                ConfigData.setExportType(position);
            }
        });

        switchBtn_XMLexport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ConfigData.setExportDanmaku(true);
                } else {
                    ConfigData.setExportDanmaku(false);
                }
            }
        });

        return new MaterialDialog.Builder(context)//创建弹窗
                .customView(dialog_judgemerge, true)//设置布局资源
                .title("提示")
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //打开合并进度窗口
                        MergeProgressDialog.showMergeProgressDialog(cacheFileList, context);
                    }
                })
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }
}
