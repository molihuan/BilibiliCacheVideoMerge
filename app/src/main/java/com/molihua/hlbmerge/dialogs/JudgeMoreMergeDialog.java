package com.molihua.hlbmerge.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ToastUtils;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.entities.ListItemMain;
import com.molihua.hlbmerge.utils.MLHInitConfig;
import com.molihua.hlbmerge.utils.RxFfmpegTools;
import com.xuexiang.xui.widget.button.switchbutton.SwitchButton;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner;
import com.xuexiang.xui.widget.toast.XToast;

import java.util.List;

/**
 * 多选合并弹窗
 */
public class JudgeMoreMergeDialog {

    public static final int TYPE_ROUGH = 0;//进度条粗略
    public static final int TYPE_DETAILED = 1; //进度条详细

    /**
     * 显示自定义对话框
     */
    public static void showJudgeMergeDialog(List<ListItemMain> listItemMains, Context context, int type) {

        if (listItemMains.size() == 0) {
            //如果没有就提示
            ToastUtils.make().show("你还没有选择呢!");
            return;
        }

        View dialog_judgemerge = LayoutInflater.from(context).inflate(R.layout.dialog_judge_merge, null);//获取自定义布局
        SwitchButton switchBtn_XMLexport = dialog_judgemerge.findViewById(R.id.switchBtn_XMLexport);//获取控件
        MaterialSpinner dialog_materialspinner = dialog_judgemerge.findViewById(R.id.dialog_materialspinner);//获取控件
        switchBtn_XMLexport.setChecked(MLHInitConfig.isIsExportXml());//从配置中读取状态
        dialog_materialspinner.setSelectedIndex(MLHInitConfig.getExportType());

        dialog_materialspinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                MLHInitConfig.setExportType(position);//保存选择的
            }
        });

        switchBtn_XMLexport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//设置监听
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchBtn_XMLexport.isChecked()) {
                    //ToastUtils.make().show("推荐使用“弹弹play”看弹幕,软件下载请自行百度");
                    MLHInitConfig.getKv().encode("isExportXml", true);//是否导出弹幕

                } else {
                    XToast.normal(context, "关").show();
                    MLHInitConfig.getKv().encode("isExportXml", false);//是否导出弹幕
                    //ToastUtils.make().show("关");
                    //XToast.info(context,"关").show();
                    //XToast.warning(context,"关").show();
                }

            }
        });

        new MaterialDialog.Builder(context)//创建弹窗
                .customView(dialog_judgemerge, true)//设置布局资源
                .title("提示")
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {//设置监听
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        boolean exportXmlChecked = switchBtn_XMLexport.isChecked();
                        int selectedIndex = dialog_materialspinner.getSelectedIndex();

                        for (int i = 0; i < listItemMains.size(); i++) {
                            listItemMains.get(i).setExportXml(exportXmlChecked);//设置是否导出弹幕
                            listItemMains.get(i).setExportType(selectedIndex);//设置是导出类型
                        }


                        switch (type) {
                            case JudgeMoreMergeDialog.TYPE_DETAILED:
                                RxFfmpegTools.execStatement(listItemMains, context);//开始合并进度详细
                                break;
                            case JudgeMoreMergeDialog.TYPE_ROUGH:
                                MoreProgressDialog.showMoreProgressDialog(listItemMains, context, JudgeMoreMergeDialog.TYPE_ROUGH);//开始合并进度粗略
                                break;
                        }


                    }
                })
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ToastUtils.make().show("取消");
                    }
                })
                .show();//显示


    }
}
