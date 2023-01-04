package com.molihua.hlbmerge.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.blankj.utilcode.util.AppUtils;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.dialogs.StatementDialog;
import com.molihua.hlbmerge.fragments.BackToolbarFragment;
import com.molihua.hlbmerge.interfaces.IActivityAndFragment;
import com.molihua.hlbmerge.utils.VersionTools;
import com.molihua.hlbmerge.utils.WebTools;
import com.xuexiang.xui.widget.grouplist.XUIGroupListView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 关于我们页面
 */
public class AboutActivity extends AppCompatActivity implements View.OnClickListener, IActivityAndFragment {

    private BackToolbarFragment backToolbarFragment;
    private TextView mVersionTextView;
    private XUIGroupListView mAboutGroupListView;
    private TextView mCopyrightTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getComponents();//获取组件
        setListeners();//设置监听
        initData();//初始化数据
    }

    private void initData() {
        fragmentInit();
        mVersionTextView.setText(String.format("版本号：%s", AppUtils.getAppVersionName()));

        XUIGroupListView.newSection(this)
                .addItemView(mAboutGroupListView.createItemView("用户协议"), v -> StatementDialog.showStatementDialog(this))
                .addItemView(mAboutGroupListView.createItemView("视频教程"), v -> {
                    WebTools.openBrowser(AboutActivity.this, "https://space.bilibili.com/454222981");
                    //ToastUtils.make().show("爱吾特供版");
                })
                .addItemView(mAboutGroupListView.createItemView("检查更新"), v -> VersionTools.checkUpdata())
                .addTo(mAboutGroupListView);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.CHINA);
        String currentYear = dateFormat.format(new Date());
        mCopyrightTextView.setText(String.format("© %1$s molihuan All rights reserved.", currentYear));
    }

    private void fragmentInit() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();//开启事务
        backToolbarFragment = new BackToolbarFragment("关于");//实例化fragment


        FragmentTransaction transaction = fragmentTransaction.add(R.id.toolbar_area, backToolbarFragment);

        transaction.commitAllowingStateLoss();



    }

    private void setListeners() {
    }

    private void getComponents() {
        mVersionTextView=findViewById(R.id.version);
        mAboutGroupListView=findViewById(R.id.about_list);
        mCopyrightTextView=findViewById(R.id.copyright);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public Object invokeFuncAiF(int functionCode) {

        return null;
    }
}