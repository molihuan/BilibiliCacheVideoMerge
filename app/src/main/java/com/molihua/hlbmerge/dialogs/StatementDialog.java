package com.molihua.hlbmerge.dialogs;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.activities.LoadingAssetsHTMLActivity;
import com.molihua.hlbmerge.utils.MLHInitConfig;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.GravityEnum;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

/**
 * 声明弹窗
 */
public class StatementDialog {
    //private static TextView tx_statement;

    private static View getCustomViewOfDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_statement, null);//获取自定义布局
        TextView tx_statement = view.findViewById(R.id.tx_statement);
        String statementContent = ResourceUtils.readAssets2String("statement.txt");//从Assets中读取
        tx_statement.setText(statementContent);
        return view;
    }


    public static void showStatementDialog(Context context) {
        new MaterialDialog.Builder(context)
                .autoDismiss(false)//是否点击按钮自动关闭
                .cancelable(false)//外部不可点击
                .customView(getCustomViewOfDialog(context), true)//布局可以用view
                .title("用户协议")
                .titleGravity(GravityEnum.CENTER)//标题居中
                .neutralText("隐私政策")
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intentPrivacy = new Intent(context, LoadingAssetsHTMLActivity.class);
                        intentPrivacy.putExtra("HTMLUrl", "file:///android_asset/privacy.html");
                        intentPrivacy.putExtra("title", "隐私政策");
                        context.startActivity(intentPrivacy);
                    }
                })

                .positiveText("已阅读并同意")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        MLHInitConfig.getKv().encode("isAgreeTerms", true);//是否同意调款
                        dialog.dismiss();
                    }
                })
                .negativeText("不同意")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        MLHInitConfig.getKv().encode("isAgreeTerms", false);//是否同意调款
                        ActivityUtils.finishAllActivities(); //退出所有activity
                        System.exit(0);//退出应用
                    }
                })
                .show();
    }


}
