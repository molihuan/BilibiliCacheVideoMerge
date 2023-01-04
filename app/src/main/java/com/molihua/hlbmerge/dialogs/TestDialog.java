package com.molihua.hlbmerge.dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;

import com.molihua.hlbmerge.utils.PathTools;
import com.molihua.hlbmerge.utils.TimeTools;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.lang.ref.WeakReference;

import io.microshow.rxffmpeg.RxFFmpegSubscriber;
/**
 * 测试弹窗
 */
public class TestDialog {

    private static long startTime;//记录开始时间
    private long endTime;//记录结束时间
    private static ProgressDialog mProgressDialog;
    private static MaterialDialog materialDialog;
    private static Context context;

    public static void openProgressDialog(Context context) {
        TestDialog.context=context;
        //统计开始时间
        startTime = System.nanoTime();
        mProgressDialog = TimeTools.openProgressDialog(context);
        //materialDialog=SingleProgressDialog.showHorizontalLoadingProgressDialog(context)

    }

    /**
     * 取消进度条
     *
     * @param dialogTitle Title
     */
    private void cancelProgressDialog(String dialogTitle) {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
        if (materialDialog != null) {
            materialDialog.cancel();
        }



        if (!TextUtils.isEmpty(dialogTitle)) {
            showDialog(dialogTitle);
        }
    }

    private void showDialog(String message) {
        //统计结束时间
        endTime = System.nanoTime();
        TimeTools.showDialog(context, message, TimeTools.convertUsToTime((endTime - startTime) / 1000, false));

    }

    /**
     * 设置进度条
     */
    private void setProgressDialog(int progress, long progressTime) {
        if (mProgressDialog != null) {
            mProgressDialog.setProgress(progress);
            //progressTime 可以在结合视频总时长去计算合适的进度值
            mProgressDialog.setMessage("已处理progressTime=" + (double) progressTime / 1000000 + "秒");
        }
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

        private WeakReference<TestDialog> mWeakReference;


        /**
         * Instantiates a new My rx f fmpeg subscriber.
         *
         * @param homeFragment the home fragment
         */
        public MyRxFFmpegSubscriber(TestDialog homeFragment) {
            mWeakReference = new WeakReference<>(homeFragment);
        }


        @Override
        public void onFinish() {
            final TestDialog mHomeFragment = mWeakReference.get();
            if (mHomeFragment != null) {
                mHomeFragment.cancelProgressDialog("处理成功!\n\t\t\t\t合并文件保存在“" + PathTools.getOutputPath() + "”目录下！");
            }
        }

        @Override
        public void onProgress(int progress, long progressTime) {
            final TestDialog mHomeFragment = mWeakReference.get();
            if (mHomeFragment != null) {
                //progressTime 可以在结合视频总时长去计算合适的进度值
                mHomeFragment.setProgressDialog(progress, progressTime);
            }
        }

        @Override
        public void onCancel() {
            final TestDialog mHomeFragment = mWeakReference.get();
            if (mHomeFragment != null) {
                mHomeFragment.cancelProgressDialog("您取消了");
            }
        }

        @Override
        public void onError(String message) {
            final TestDialog mHomeFragment = mWeakReference.get();
            if (mHomeFragment != null) {
                mHomeFragment.cancelProgressDialog("错误！");
            }
        }
    }
}
