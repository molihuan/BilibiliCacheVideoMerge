package com.molihua.hlbmerge.dialogs;

import android.content.Context;
import android.text.TextUtils;

import com.blankj.utilcode.util.FileUtils;
import com.molihua.hlbmerge.utils.FileTools;
import com.molihua.hlbmerge.utils.PathTools;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.lang.ref.WeakReference;

import io.microshow.rxffmpeg.RxFFmpegSubscriber;
/**
 * 单选合并进度回调
 */
public class SingleProgressCallBack {

    private static long startTime;//记录开始时间
    private long endTime;//记录结束时间

    private static MaterialDialog materialDialog;
    private static Context context;
    private static String tagerMp4File;


    public static void openProgressDialog(Context context,String tagerMp4File) {
        SingleProgressCallBack.context=context;
        SingleProgressCallBack.tagerMp4File=tagerMp4File;
        //统计开始时间
        startTime = System.nanoTime();
        materialDialog=SingleProgressDialog.showSingleProgressDialog(context);


    }

    /**
     * 取消进度条
     *
     * @param dialogTitle Title
     */
    private void cancelProgressDialog(String dialogTitle) {

        if (materialDialog != null) {
            //合并运行时取消---->third step
            materialDialog.cancel();//取消进度

        }


        if (!TextUtils.isEmpty(dialogTitle)) {
            showDialog(dialogTitle);
        }
    }

    private void showDialog(String message) {


        //统计结束时间
        endTime = System.nanoTime();
        SingleProgressDialog.showDialog(context, message, SingleProgressDialog.convertUsToTime((endTime - startTime) / 1000, false));

    }

    /**
     * 设置进度条
     */
    private void setProgressDialog(int progress, long progressTime) {
        
        if (materialDialog != null) {
            materialDialog.setProgress(progress);
            //progressTime 可以在结合视频总时长去计算合适的进度值
            materialDialog.setContent("已处理progressTime=" + (double) progressTime / 1000000 + "秒");
        }
    }



    /**
     * The type My rx f fmpeg subscriber.
     */
    public static class MyRxFFmpegSubscriber extends RxFFmpegSubscriber {

        private WeakReference<SingleProgressCallBack> mWeakReference;


        /**
         * Instantiates a new My rx f fmpeg subscriber.
         *
         * @param homeFragment the home fragment
         */
        public MyRxFFmpegSubscriber(SingleProgressCallBack homeFragment) {
            mWeakReference = new WeakReference<>(homeFragment);
        }

        //处理完成
        @Override
        public void onFinish() {
            final SingleProgressCallBack mHomeFragment = mWeakReference.get();
            if (mHomeFragment != null) {
                mHomeFragment.cancelProgressDialog("处理成功!\n\t\t\t\t合并文件保存在“" + PathTools.getOutputPath() + "”目录下！");
                FileTools.deleteEqualFile(tagerMp4File);//删除合并后已存在相同的文件
            }
        }

        @Override
        public void onProgress(int progress, long progressTime) {
            final SingleProgressCallBack mHomeFragment = mWeakReference.get();
            if (mHomeFragment != null) {
                //progressTime 可以在结合视频总时长去计算合适的进度值
                mHomeFragment.setProgressDialog(progress, progressTime);
            }
        }

        @Override
        public void onCancel() {
            //合并运行时取消---->second step
            final SingleProgressCallBack mHomeFragment = mWeakReference.get();
            if (mHomeFragment != null) {
                mHomeFragment.cancelProgressDialog("您取消了");
                FileUtils.delete(tagerMp4File);//删除未完成的文件
            }
        }

        @Override
        public void onError(String message) {
            final SingleProgressCallBack mHomeFragment = mWeakReference.get();
            if (mHomeFragment != null) {
                mHomeFragment.cancelProgressDialog("错误！");
            }
        }
    }
}
