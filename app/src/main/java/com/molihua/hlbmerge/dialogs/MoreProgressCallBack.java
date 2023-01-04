package com.molihua.hlbmerge.dialogs;

import android.app.Activity;
import android.content.Context;

import com.molihua.hlbmerge.entities.ListItemMain;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.util.List;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
/**
 * 多选合并进度回调
 */
public class MoreProgressCallBack implements RxFFmpegInvoke.IFFmpegListener {

    private static long startTime;//记录开始时间
    private long endTime;//记录结束时间

    private static MaterialDialog materialDialog;
    private static Context context;
    private static List<ListItemMain> listItemMains;


    public static MaterialDialog openProgressDialog(List<ListItemMain> listItemMains,Context context) {
        MoreProgressCallBack.context=context;

        MoreProgressCallBack.listItemMains=listItemMains;
        //统计开始时间
        startTime = System.nanoTime();
        //如果已经有弹窗或者弹窗取消就不创建弹窗了
        if (materialDialog==null||materialDialog.isCancelled()){
            materialDialog=MoreProgressDialog.showMoreProgressDialog(listItemMains,context,MoreProgressDialog.TYPE_DETAILED);
        }

        return materialDialog;

    }


    @Override
    public void onFinish() {
        if (materialDialog != null&&materialDialog.isCancelled()) {
//            ((Activity)context).runOnUiThread(()->{
//                materialDialog.setContent("合并完成");
//            });

        }



    }

    @Override
    public void onProgress(int progress, long progressTime) {
        if (materialDialog != null) {
            ((Activity)context).runOnUiThread(()->{
                materialDialog.setProgress(progress);
                //progressTime 可以在结合视频总时长去计算合适的进度值
                materialDialog.setContent("已处理progressTime=" + (double) progressTime / 1000000 + "秒");
            });
        }
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(String message) {

    }
}
