package com.molihua.hlbmerge.activity.impl;

import android.content.Intent;
import android.widget.TextView;

import com.blankj.molihuan.utilcode.util.AppUtils;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.activity.BaseActivity;
import com.molihua.hlbmerge.dialog.impl.StatementDialog;
import com.molihua.hlbmerge.fragment.impl.BackTitlebarFragment;
import com.molihua.hlbmerge.utils.FragmentTools;
import com.molihua.hlbmerge.utils.GeneralTools;
import com.molihua.hlbmerge.utils.LConstants;
import com.molihua.hlbmerge.utils.UpdataTools;
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

    private TextView mVersionTextView;
    private XUIGroupListView mAboutGroupListView;
    private TextView mCopyrightTextView;

    @Override
    public int setContentViewID() {
        return R.layout.activity_about;
    }

    @Override
    public void getComponents() {
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
                new BackTitlebarFragment("关于我们"),
                "about_back_titlebar"
        );

        mVersionTextView.setText(String.format("版本号：%s", AppUtils.getAppVersionName()));

        XUIGroupListView.newSection(this)
                .addItemView(mAboutGroupListView.createItemView("用户协议"), v -> StatementDialog.showStatementDialog(this))
                .addItemView(mAboutGroupListView.createItemView("视频教程"), v -> {
                    GeneralTools.jumpBrowser(this, LConstants.URL_BILIBILI_HOMEPAGE);
                })
                .addItemView(mAboutGroupListView.createItemView("开源许可"), v -> {
                    Intent intent = new Intent(this, HtmlActivity.class);
                    intent.putExtra("url", "file:///android_asset/openSourceLicense.html");
                    intent.putExtra("title", "开源许可");
                    startActivity(intent);
                })
                .addItemView(mAboutGroupListView.createItemView("检查更新"), v -> UpdataTools.limitClickCheckUpdata(this))
                .addTo(mAboutGroupListView);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.CHINA);
        String currentYear = dateFormat.format(new Date());
        mCopyrightTextView.setText(String.format("© %1$s molihuan All rights reserved.", currentYear));

    }

    @Override
    public void setListeners() {

    }
}
