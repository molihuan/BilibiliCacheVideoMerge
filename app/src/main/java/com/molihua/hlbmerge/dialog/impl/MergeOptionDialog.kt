package com.molihua.hlbmerge.dialog.impl

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import com.molihua.hlbmerge.R
import com.molihua.hlbmerge.dao.ConfigData.getExportType
import com.molihua.hlbmerge.dao.ConfigData.isExportDanmaku
import com.molihua.hlbmerge.dao.ConfigData.setExportDanmaku
import com.molihua.hlbmerge.dao.ConfigData.setExportType
import com.molihua.hlbmerge.entity.CacheFile
import com.xuexiang.xui.widget.button.switchbutton.SwitchButton
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner
import java.util.Objects

object MergeOptionDialog {

    /**
     * 单个合并
     *
     * @param cacheFile
     * @param fragment
     * @return
     */
    @JvmStatic
    fun showMergeOptionDialog(cacheFile: CacheFile?, fragment: Fragment): MaterialDialog {
        val cacheFileList: MutableList<CacheFile?> = ArrayList()
        cacheFileList.add(cacheFile)
        return showMergeOptionDialog(cacheFileList, fragment)
    }

    /**
     * 多个合并
     *
     * @param cacheFileList
     * @param fragment
     * @return
     */
    @JvmStatic
    fun showMergeOptionDialog(
        cacheFileList: MutableList<CacheFile?>?,
        fragment: Fragment
    ): MaterialDialog {
        val context = fragment.context
        Objects.requireNonNull<Context?>(context, "context is null")

        val dialog_judgemerge: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_judge_merge, null)
        val dialog_materialspinner =
            dialog_judgemerge.findViewById<MaterialSpinner>(R.id.dialog_materialspinner)
        val switchBtn_XMLexport =
            dialog_judgemerge.findViewById<SwitchButton>(R.id.switchBtn_XMLexport)

        switchBtn_XMLexport.isChecked = isExportDanmaku()
        dialog_materialspinner.selectedIndex = getExportType()

        dialog_materialspinner.setOnItemSelectedListener(object :
            MaterialSpinner.OnItemSelectedListener<Any?> {
            override fun onItemSelected(
                view: MaterialSpinner?,
                position: Int,
                id: Long,
                item: Any?
            ) {
                setExportType(position)
            }
        })

        switchBtn_XMLexport.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                setExportDanmaku(true)
            } else {
                setExportDanmaku(false)
            }
        }

        return MaterialDialog.Builder(context!!) //创建弹窗
            .customView(dialog_judgemerge, true) //设置布局资源
            .title("提示")
            .positiveText("确定")
            .onPositive { dialog, which -> //打开合并进度窗口
                MergeProgressDialog.showMergeProgressDialog(cacheFileList, fragment)
            }
            .negativeText("取消")
            .onNegative { dialog, which -> dialog.dismiss() }
            .show()
    }


}