package com.molihua.hlbmerge.dialogs;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ToastUtils;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.entities.ListItemMain;
import com.molihua.hlbmerge.utils.MLHInitConfig;
import com.molihua.hlbmerge.utils.PathTools;
import com.molihua.hlbmerge.utils.RxFfmpegTools;
import com.molihua.hlbmerge.utils.UriTools;
import com.molihua.hlbmerge.utils.VersionTools;
import com.xuexiang.xui.widget.button.switchbutton.SwitchButton;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner;
import com.xuexiang.xui.widget.toast.XToast;
/**
 * 单选合并弹窗
 */
public class JudgeSingleMergeDialog {
    /**
     * 显示自定义对话框
     */
    public static void showJudgeMergeDialog(ListItemMain listItemMain,Context context) {

        View dialog_judgemerge = LayoutInflater.from(context).inflate(R.layout.dialog_judge_merge, null);//获取自定义布局
        MaterialSpinner dialog_materialspinner= dialog_judgemerge.findViewById(R.id.dialog_materialspinner);//获取控件
        SwitchButton switchBtn_XMLexport = dialog_judgemerge.findViewById(R.id.switchBtn_XMLexport);//获取控件
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
                    MLHInitConfig.getKv().encode("isExportXml",true);//是否导出弹幕

                } else {
                    XToast.normal(context,"关").show();
                    MLHInitConfig.getKv().encode("isExportXml",false);//是否导出弹幕
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


                        if (VersionTools.isAndroid11AndNull()){//复制所有文件
                            PathTools.clearAudioVideoPath();//清除缓存
                            String tempOnePath = listItemMain.getPath();//获取tempP路径
                            Uri copyAllUri = UriTools.path2Uri(tempOnePath);//根据路径计算出Android/data对应位置的uri
                            UriTools.copyAllPathAndJson(context.getContentResolver(),context,copyAllUri,1);//复制uri下面的所有文件
                            PathTools.getAudioVideoJsonPath(tempOnePath);
                            listItemMain.setAudioPath(PathTools.getAudioPath());//设置路径AudioPath
                            listItemMain.setVideoPath(PathTools.getVideoPath());//设置路径VideoPath
                            listItemMain.setDanmakuXmlPath(PathTools.getDanmakuXmlPath());//设置路径DanmakuXmlPath
                            listItemMain.setBlvPathList(PathTools.getBlvPathList());//设置路径BlvPathList
                        }

                        boolean exportXmlChecked = switchBtn_XMLexport.isChecked();
                        listItemMain.setExportXml(exportXmlChecked);//设置是否导出弹幕

                        int selectedIndex = dialog_materialspinner.getSelectedIndex();
                        listItemMain.setExportType(selectedIndex);//设置是导出类型


                        RxFfmpegTools.execStatement(listItemMain,context, RxFfmpegTools.TYPE_SINGLE);//开始合并

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
