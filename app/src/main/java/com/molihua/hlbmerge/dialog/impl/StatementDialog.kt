package com.molihua.hlbmerge.dialog.impl

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.blankj.molihuan.utilcode.util.ActivityUtils
import com.blankj.molihuan.utilcode.util.ResourceUtils
import com.molihua.hlbmerge.R
import com.molihua.hlbmerge.activity.impl.HtmlActivity
import com.molihua.hlbmerge.dao.ConfigData.setAgreeTerm
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction
import com.xuexiang.xui.widget.dialog.materialdialog.GravityEnum
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import kotlin.system.exitProcess

object StatementDialog {
    interface IButtonCallback {
        fun onClick(dialog: MaterialDialog, which: DialogAction)
    }


    private fun getCustomViewOfDialog(context: Context?): View {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_statement, null) //获取自定义布局
        val tx_statement = view.findViewById<TextView>(R.id.tx_statement)
        val statementContent = ResourceUtils.readAssets2String("statement.md") //从Assets中读取
        tx_statement.setText(statementContent)
        return view
    }

    @JvmStatic
    fun showStatementDialog(context: Context) {
        showStatementDialog(context, null)
    }

    @JvmStatic
    fun showStatementDialog(context: Context, positiveCallback: IButtonCallback?) {
        MaterialDialog.Builder(context)
            .autoDismiss(false) //是否点击按钮自动关闭
            .cancelable(false) //外部不可点击
            .customView(getCustomViewOfDialog(context), true) //布局可以用view
            .title("用户协议")
            .titleGravity(GravityEnum.CENTER) //标题居中
            .neutralText("隐私政策")
            .onNeutral { dialog, which ->
                val intentPrivacy = Intent(context, HtmlActivity::class.java)
                intentPrivacy.putExtra("url", "file:///android_asset/privacy.html")
                intentPrivacy.putExtra("title", "隐私政策")
                context.startActivity(intentPrivacy)
            }
            .positiveText("已阅读并同意")
            .onPositive { dialog, which ->
                positiveCallback?.onClick(dialog, which)
                setAgreeTerm(true)
                dialog.dismiss()
            }
            .negativeText("不同意")
            .onNegative { dialog, which ->
                setAgreeTerm(false)
                ActivityUtils.finishAllActivities() //退出所有activity
                exitProcess(0) //退出应用
            }
            .show()
    }

}