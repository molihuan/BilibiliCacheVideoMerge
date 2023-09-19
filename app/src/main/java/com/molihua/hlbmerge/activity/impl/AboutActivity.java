package com.molihua.hlbmerge.activity.impl;

import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.molihuan.utilcode.util.AppUtils;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.activity.BaseActivity;
import com.molihua.hlbmerge.dialog.impl.StatementDialog;
import com.molihua.hlbmerge.fragment.impl.BackTitlebarFragment;
import com.molihua.hlbmerge.utils.FragmentTools;
import com.molihua.hlbmerge.utils.GeneralTools;
import com.xuexiang.xui.utils.XToastUtils;
import com.xuexiang.xui.widget.grouplist.XUIGroupListView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @ClassName: AboutActivity
 * @Author: molihuan
 * @Date: 2022/12/27/22:14
 * @Description:
 */
public class AboutActivity extends BaseActivity {

    private TextView mDescribeTextView;
    private TextView mVersionTextView;
    private XUIGroupListView mAboutGroupListView;
    private TextView mCopyrightTextView;

    @Override
    public int setContentViewID() {
        return R.layout.activity_about;
    }

    @Override
    public void getComponents() {
        mDescribeTextView = findViewById(R.id.describe);
        mVersionTextView = findViewById(R.id.version);
        mAboutGroupListView = findViewById(R.id.about_list);
        mCopyrightTextView = findViewById(R.id.copyright);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {

        FragmentTools.fragmentReplace(
                getSupportFragmentManager(),
                R.id.titlebar_show_area,
                new BackTitlebarFragment("关于"),
                "about_back_titlebar"
        );

        mDescribeTextView.setText("将B站缓存视频合并导出为mp4");
        mVersionTextView.setText(String.format("版本号：%s", AppUtils.getAppVersionName()));

        XUIGroupListView.newSection(this)
                .addItemView(mAboutGroupListView.createItemView("用户协议"), v -> StatementDialog.showStatementDialog(this))

                .addItemView(mAboutGroupListView.createItemView("问题反馈"), v -> {
                    GeneralTools.jumpProjectIssues(this);
                })
                .addItemView(mAboutGroupListView.createItemView("开源许可"), v -> {
                    Intent intent = new Intent(this, HtmlActivity.class);
                    intent.putExtra("url", "file:///android_asset/openSourceLicense.html");
                    intent.putExtra("title", "开源许可");
                    startActivity(intent);
                })
                .addItemView(mAboutGroupListView.createItemView(getString(R.string.UPDATE_LOGS)), v -> {

                    Intent intent = new Intent(this, HtmlActivity.class);
                    intent.putExtra("url", "file:///android_asset/updataLog.html");
                    intent.putExtra("title", "更新日志");
                    startActivity(intent);
                })
                .addItemView(mAboutGroupListView.createItemView(getString(R.string.SPONSOR)), v -> {
                    XToastUtils.success("可以给项目一个Star吗？非常感谢，你的支持是我唯一的动力。", Toast.LENGTH_LONG);
                })
                .addTo(mAboutGroupListView);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.CHINA);
        String currentYear = dateFormat.format(new Date());
        mCopyrightTextView.setText(String.format("© %1$s molihuan All rights reserved.", currentYear));

    }

    @Override
    public void setListeners() {

    }
}
