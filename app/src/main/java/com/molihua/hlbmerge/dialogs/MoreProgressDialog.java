package com.molihua.hlbmerge.dialogs;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.molihua.hlbmerge.entities.ListItemMain;
import com.molihua.hlbmerge.utils.FileTools;
import com.molihua.hlbmerge.utils.PathTools;
import com.molihua.hlbmerge.utils.RxFfmpegTools;
import com.molihua.hlbmerge.utils.UriTools;
import com.molihua.hlbmerge.utils.VersionTools;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.GravityEnum;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.util.List;
/**
 * 多选合并进度弹窗
 */
public class MoreProgressDialog {

    public static final int TYPE_ROUGH=0;//进度条粗略
    public static final int TYPE_DETAILED=1;//进度条详细


    /**
     * 带水平Loading进度条的Dialog
     */
    public static MaterialDialog showMoreProgressDialog(List<ListItemMain> listItemMains,Context context,int type) {

        int progressMax=100;

        switch (type){
            case MoreProgressDialog.TYPE_ROUGH:
                //粗略更新进度
                progressMax=listItemMains.size();
                break;
            case MoreProgressDialog.TYPE_DETAILED:
                //详细更新进度
                break;
        }

        //显示弹窗
        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title("提示")
                .cancelable(false)
                .content("在用吃奶的力气合并中...")
                .contentGravity(GravityEnum.CENTER)
                .progress(false, progressMax, true)
                .cancelListener(dialog -> {
                    LogUtils.e("moreMergeTagerMp4FileList-----------");
                    try {
                        for (int i = 0; i < RxFfmpegTools.moreMergeTagerMp4FileList.size(); i++) {
                            FileTools.deleteEqualFile(RxFfmpegTools.moreMergeTagerMp4FileList.get(i));//删除合并后已存在相同的文件
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (VersionTools.isAndroid11AndNull()){
                        FileTools.deleteTempFile(PathTools.getOutputTempPath(),"entry.json");//删除temp所有的文件除了json
                    }

                })
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        LogUtils.e("moreMergeTagerMp4FileList-----------");

                        if (VersionTools.isAndroid11AndNull()){
                            FileTools.deleteTempFile(PathTools.getOutputTempPath(),"entry.json");//删除temp所有的文件除了json
                        }

                        try {
                            for (int i = 0; i < RxFfmpegTools.moreMergeTagerMp4FileList.size(); i++) {
                                FileTools.deleteEqualFile(RxFfmpegTools.moreMergeTagerMp4FileList.get(i));//删除合并后已存在相同的文件
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                })
                .show();

        switch (type){
            case MoreProgressDialog.TYPE_ROUGH:
                //粗略更新进度
                updateProgress(materialDialog,listItemMains,context);
                break;
            case MoreProgressDialog.TYPE_DETAILED:
                //详细更新进度

                break;
        }


        return materialDialog;
    }

    /**
     * 执行命令
     * 更新进度条
     * @param dialogInterface
     */
    private static void updateProgress(MaterialDialog dialogInterface,List<ListItemMain> listItemMains,Context context) {
        final MaterialDialog dialog = dialogInterface;

        //执行ffmpeg同步任务是耗时任务，所以需要开一个线程来执行一堆的ffmpeg合并任务，防止阻塞UI
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < listItemMains.size(); i++) {
                    ListItemMain listItemMain = listItemMains.get(i);

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

                    RxFfmpegTools.execStatement(listItemMain,context, RxFfmpegTools.TYPE_MORE);//执行命令

                    int finalI = i+1;
                    ((Activity)context).runOnUiThread(() -> {//在线程中更新UI非常方便
                        dialog.setProgress(finalI);
                    });
                }
                ((Activity)context).runOnUiThread(() -> {//在线程中更新UI非常方便
                    dialog.setContent("合并完成");
                });

            }
        }).start();







    }
}
