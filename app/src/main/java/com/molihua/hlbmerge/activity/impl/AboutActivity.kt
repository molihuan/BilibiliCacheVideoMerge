package com.molihua.hlbmerge.activity.impl

import android.content.Intent
import android.view.View
import android.widget.Toast
import com.blankj.molihuan.utilcode.util.AppUtils
import com.molihua.hlbmerge.R
import com.molihua.hlbmerge.activity.BaseActivity
import com.molihua.hlbmerge.databinding.ActivityAboutBinding
import com.molihua.hlbmerge.dialog.impl.StatementDialog
import com.molihua.hlbmerge.fragment.impl.BackTitlebarFragment
import com.molihua.hlbmerge.utils.FragmentTools
import com.molihua.hlbmerge.utils.GeneralTools
import com.xuexiang.xui.utils.XToastUtils
import com.xuexiang.xui.widget.grouplist.XUIGroupListView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AboutActivity : BaseActivity<ActivityAboutBinding>() {

    private val context = this
    override fun getContentViewBinding(): ActivityAboutBinding {
        return ActivityAboutBinding.inflate(layoutInflater)
    }

    override fun getComponents() {

    }

    override fun initData() {

    }


    override fun initView() {
        FragmentTools.fragmentReplace(
            supportFragmentManager,
            R.id.titlebar_show_area,
            BackTitlebarFragment("关于"),
            "about_back_titlebar"
        )

        binding.apply {

            describe.text = "将B站缓存视频合并导出为mp4"
            version.text = String.format("版本号：%s", AppUtils.getAppVersionName())

            XUIGroupListView.newSection(context)
                .addItemView(
                    aboutList.createItemView("用户协议")
                ) { v: View? -> StatementDialog.showStatementDialog(context) }

                .addItemView(
                    aboutList.createItemView("问题反馈")
                ) { v: View? ->
                    GeneralTools.jumpProjectIssues(context)
                }
                .addItemView(
                    aboutList.createItemView("开源许可")
                ) { v: View? ->
                    val intent = Intent(context, HtmlActivity::class.java)
                        .apply {
                            putExtra("url", "file:///android_asset/openSourceLicense.html")
                            putExtra("title", "开源许可")
                        }

                    startActivity(intent)
                }
                .addItemView(
                    aboutList.createItemView(getString(R.string.UPDATE_LOGS))
                ) { v: View? ->
                    val intent = Intent(context, HtmlActivity::class.java)
                        .apply {
                            putExtra("url", "file:///android_asset/updataLog.html")
                            putExtra("title", "更新日志")
                        }

                    startActivity(intent)
                }
                .addItemView(
                    aboutList.createItemView(getString(R.string.SPONSOR))
                ) { v: View? ->
                    XToastUtils.success(
                        "可以给项目一个Star吗？非常感谢，你的支持是我唯一的动力。",
                        Toast.LENGTH_LONG
                    )
                }
                .addTo(aboutList)


            val dateFormat = SimpleDateFormat("yyyy", Locale.CHINA)
            val currentYear = dateFormat.format(Date())
            copyright.text = "© ${currentYear} molihuan All rights reserved."


        }
    }
}