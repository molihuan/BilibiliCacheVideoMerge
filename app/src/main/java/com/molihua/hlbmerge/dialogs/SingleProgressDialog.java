package com.molihua.hlbmerge.dialogs;

import android.content.Context;

import androidx.annotation.NonNull;

import com.molihua.hlbmerge.entities.ListItemMain;
import com.molihua.hlbmerge.utils.FileTools;
import com.molihua.hlbmerge.utils.PathTools;
import com.molihua.hlbmerge.utils.VersionTools;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.GravityEnum;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
/**
 * 单选合并进度弹窗
 */
public class SingleProgressDialog {

    private static ListItemMain listItemMain;

    public static ListItemMain getListItemMain() {
        return listItemMain;
    }

    public static void setListItemMain(ListItemMain listItemMain) {
        SingleProgressDialog.listItemMain = listItemMain;
    }


    /**
     * 带水平Loading进度条的Dialog
     */
    public static MaterialDialog showSingleProgressDialog(Context context) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .cancelable(false)
                .title("合并")
                .content("正在转换视频，请稍后...")
                .contentGravity(GravityEnum.CENTER)
                .progress(false, 100, true)
                .cancelListener(dialog -> {

                })
                //更新进度条移交给回调函数处理
                //.showListener(dialog -> updateProgress((MaterialDialog) dialog, context))
                .negativeText("取消")//合并运行时取消---->first step
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //中断 ffmpeg
                        RxFFmpegInvoke.getInstance().exit();
                        //
                    }
                })
                .show();
        return materialDialog;
    }



    /**
     * 完成时回调此方法进行显示耗时等提示
     * @param context
     * @param message
     * @param runTime
     */
    public static void showDialog(Context context, String message, String runTime) {
        if (VersionTools.isAndroid11AndNull()){
            FileTools.deleteTempFile(PathTools.getOutputTempPath(),"entry.json");//删除temp所有的文件除了json
        }

        DialogLoader.getInstance().showTipDialog(
                context,
                "提示",
                message + "\n\n耗时：" + runTime,
                "关闭"
                );
    }



    //优化内存使用
    static StringBuilder mUsDurationText = new StringBuilder();

    /**
     * 微秒转换为 时分秒毫秒,如 00:00:00.000
     *
     * @param us           微秒
     * @param autoEllipsis true:如果小时为0，则这样显示00:00.000;  false:全部显示 00:00:00.000
     * @return
     */
    public static String convertUsToTime(long us, boolean autoEllipsis) {

        mUsDurationText.delete(0, mUsDurationText.length());

        long ms = us / 1000;
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        //天
        long day = ms / dd;
        //小时
        long hour = (ms - day * dd) / hh;
        //分
        long minute = (ms - day * dd - hour * hh) / mi;
        //秒
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        //毫秒
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        String strDay = day < 10 ? "0" + day : "" + day; //天
        String strHour = hour < 10 ? "0" + hour : "" + hour;//小时
        String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
        String strSecond = second < 10 ? "0" + second : "" + second;//秒
        String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;//毫秒
        strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;

        if (autoEllipsis) {
            if (hour > 0) {
                mUsDurationText.append(strHour).append(":");
            }
        } else {
            mUsDurationText.append(strHour).append(":");
        }
        mUsDurationText.append(strMinute).append(":")
                .append(strSecond).append(".").append(strMilliSecond);
        return mUsDurationText.toString();
    }
}
