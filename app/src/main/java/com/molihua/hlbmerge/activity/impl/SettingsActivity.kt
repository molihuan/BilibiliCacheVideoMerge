package com.molihua.hlbmerge.activity.impl

import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blankj.molihuan.utilcode.util.ClipboardUtils
import com.blankj.molihuan.utilcode.util.TimeUtils
import com.molihua.hlbmerge.BuildConfig
import com.molihua.hlbmerge.R
import com.molihua.hlbmerge.activity.BaseActivity
import com.molihua.hlbmerge.dao.ConfigData
import com.molihua.hlbmerge.databinding.ActivitySettingsBinding
import com.molihua.hlbmerge.fragment.impl.BackTitlebarFragment
import com.molihua.hlbmerge.fragment.impl.BackTitlebarFragment.IClickListener
import com.molihua.hlbmerge.utils.FragmentTools
import com.molihua.hlbmerge.utils.UpdateTools
import com.molihuan.pathselector.PathSelector
import com.molihuan.pathselector.entity.FileBean
import com.molihuan.pathselector.entity.FontBean
import com.molihuan.pathselector.fragment.BasePathSelectFragment
import com.molihuan.pathselector.listener.CommonItemListener
import com.molihuan.pathselector.utils.MConstants
import com.molihuan.pathselector.utils.Mtools
import com.xuexiang.xui.widget.button.switchbutton.SwitchButton
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner

class SettingsActivity : BaseActivity<ActivitySettingsBinding>(),
    MaterialSpinner.OnItemSelectedListener<String> {

    private val context = this
    private val cachePathShowTv: TextView get() = binding.tvCachePathShow
    private val biliVersionMs: MaterialSpinner get() = binding.msBilibiliVersion
    private val customCachePathRela: RelativeLayout get() = binding.relaCustomCachePath
    private val outputPathShowTv: TextView get() = binding.tvOutputPathShow
    private val customOutputPathRela: RelativeLayout get() = binding.relaCustomOutputPath
    private val biliVersionLine: LinearLayout get() = binding.lineSwitchBilibiliAppVersion

    private val ffmpegCmdShowTv: TextView get() = binding.tvFfmpegCmdShow
    private val ffmpegCmdTypeLine: LinearLayout get() = binding.lineSwitchFfmpegCmdType
    private val ffmpegCmdTypeMs: MaterialSpinner get() = binding.msFfmpegCmdType
    private val customFfmpegCmdRela: RelativeLayout get() = binding.relaCustomFfmpegCmd

    private val ffmpegCoreTypeLine: LinearLayout get() = binding.lineSwitchFfmpegCoreType
    private val ffmpegCoreTypeMs: MaterialSpinner get() = binding.msFfmpegCoreType

    private val autoUpdataFrequencyMs: MaterialSpinner get() = binding.msAutoUpdata

    private val outputPathShowRela: RelativeLayout get() = binding.relalOutputPathShow

    private val switchSingleOutputDir: SwitchButton get() = binding.switchSingleOutputDir

    override fun getContentViewBinding(): ActivitySettingsBinding {
        return ActivitySettingsBinding.inflate(layoutInflater)
    }

    override fun getComponents() {

    }

    override fun initData() {

    }

    override fun initView() {

        //显示路径配置
        val cacheFilePath = ConfigData.getCacheFilePath()
        val updateFrequency = ConfigData.getUpdateFrequency()
        val ffmpegCmdTemplate = ConfigData.getFfmpegCmdTemplate()
        val ffmpegCoreType = ConfigData.getFfmpegCoreType()

        switchSingleOutputDir.isChecked = ConfigData.isSingleOutputDir()

        if (BuildConfig.FFMPEG_CORE_TYPE != ConfigData.FFMPEG_CORE_TYPE_All) {
            ffmpegCoreTypeLine.visibility = View.GONE
        } else {
            ffmpegCoreTypeMs.selectedIndex = ffmpegCoreType
        }

        cachePathShowTv.text = cacheFilePath
        outputPathShowTv.text = ConfigData.getOutputFilePath()
        ffmpegCmdShowTv.text = ffmpegCmdTemplate

        autoUpdataFrequencyMs.selectedIndex = updateFrequency

        ffmpegCmdTypeMs.setSelectedItem(ffmpegCmdTemplate)

        if (cacheFilePath == MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_INTERNAL) {
            biliVersionMs.selectedIndex = 0
            customCachePathRela.setAlpha(0.2f)
            biliVersionLine.setAlpha(1f)
        } else if (cacheFilePath == MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_ABROAD) {
            biliVersionMs.selectedIndex = 1
            customCachePathRela.setAlpha(0.2f)
            biliVersionLine.setAlpha(1f)
        } else if (cacheFilePath == MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_IPAD) {
            biliVersionMs.selectedIndex = 2
            customCachePathRela.setAlpha(0.2f)
            biliVersionLine.setAlpha(1f)
        } else if (cacheFilePath == MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_CONCEPT) {
            biliVersionMs.selectedIndex = 3
            customCachePathRela.setAlpha(0.2f)
            biliVersionLine.setAlpha(1f)
        } else {
            biliVersionMs.selectedIndex = 4
            customCachePathRela.setAlpha(1f)
            biliVersionLine.setAlpha(0.4f)
        }

        FragmentTools.fragmentReplace(
            supportFragmentManager,
            R.id.titlebar_show_area,
            BackTitlebarFragment("设置").setRightOption("恢复默认", object : IClickListener {
                override fun onClick(v: View?) {
                    //默认缓存路径
                    val cachePath =
                        MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_INTERNAL
                    ConfigData.setCacheFilePath(cachePath)
                    cachePathShowTv.text = cachePath
                    customCachePathRela.setAlpha(0.2f)
                    biliVersionMs.selectedIndex = 0
                    biliVersionLine.setAlpha(1f)
                    //默认输出路径
                    val path =
                        MConstants.DEFAULT_ROOTPATH + ConfigData.TYPE_OUTPUT_FILE_PATH_COMPLETE
                    ConfigData.setOutputFilePath(path)
                    outputPathShowTv.text = path
                    //默认更新设置
                    autoUpdataFrequencyMs.selectedIndex = 1
                    onItemSelected(
                        autoUpdataFrequencyMs,
                        1,
                        autoUpdataFrequencyMs.getId().toLong(),
                        null
                    )

                    ffmpegCoreTypeMs.selectedIndex = 0
                    onItemSelected(ffmpegCoreTypeMs, 0, ffmpegCoreTypeMs.id.toLong(), null)

                    Mtools.toast("恢复默认成功")
                }
            }),
            "setting_back_titlebar"
        )
    }

    override fun setListeners() {
        customCachePathRela.setOnClickListener {
            PathSelector.build(context, MConstants.BUILD_ACTIVITY)
                .setRequestCode(3660)
                .setTitlebarMainTitle(FontBean("选择缓存文件夹(download)"))
                .setAlwaysShowHandleFragment(true)
                .setMaxCount(1)
                .setHandleItemListeners(
                    object : CommonItemListener("选择") {
                        override fun onClick(
                            v: View?,
                            tv: TextView?,
                            selectedFiles: MutableList<FileBean?>?,
                            currentPath: String?,
                            pathSelectFragment: BasePathSelectFragment?
                        ): Boolean {
                            if (selectedFiles.isNullOrEmpty()) {
                                Mtools.toast("你还没有选择捏!请长按文件夹进行选择!")
                            } else {
                                val path = selectedFiles[0]!!.path
                                val success = ConfigData.setCacheFilePath(path)
                                cachePathShowTv.text = path
                                biliVersionMs.selectedIndex = 4
                                customCachePathRela.setAlpha(1f)
                                biliVersionLine.setAlpha(0.4f)

                                if (success) {
                                    Mtools.toast("设置成功")
                                } else {
                                    Mtools.toast("设置失败")
                                }
                            }
                            return false
                        }
                    }
                )
                .show()
        }
        customOutputPathRela.setOnClickListener {
            PathSelector.build(context, MConstants.BUILD_ACTIVITY)
                .setRequestCode(3660)
                .setShowSelectStorageBtn(true)
                .setTitlebarMainTitle(FontBean("选择输出路径"))
                .setAlwaysShowHandleFragment(true)
                .setMaxCount(1)
                .setHandleItemListeners(
                    object : CommonItemListener("选择") {
                        override fun onClick(
                            v: View?,
                            tv: TextView?,
                            selectedFiles: MutableList<FileBean?>?,
                            currentPath: String?,
                            pathSelectFragment: BasePathSelectFragment?
                        ): Boolean {
                            if (selectedFiles.isNullOrEmpty()) {
                                Mtools.toast("你还没有选择捏!请长按文件夹进行选择!")
                            } else {
                                val path = selectedFiles[0]!!.path
                                val success = ConfigData.setOutputFilePath(path)
                                outputPathShowTv.setText(path)
                                if (success) {
                                    Mtools.toast("设置成功")
                                } else {
                                    Mtools.toast("设置失败")
                                }
                            }
                            return false
                        }
                    }
                )
                .show()
        }
        customFfmpegCmdRela.setOnClickListener {
            MaterialDialog.Builder(this)
                .title("提示")
                .content("请使用三个%s分别代表:输入音频、输入视频、bvId、输出视频")
                .input(
                    "ffmpeg命令",
                    ConfigData.getFfmpegCmdTemplate(),
                    false
                ) { dialog, input -> }
                .positiveText(R.string.option_confirm_hlb)
                .negativeText(R.string.option_cancel_hlb)
                .onPositive { dialog, which ->
                    val inputText = dialog.inputEditText!!.getText().toString()
                    ConfigData.setFfmpegCmdTemplate(inputText)
                    ffmpegCmdShowTv.text = inputText
                }
                .show()
        }
        biliVersionMs.setOnItemSelectedListener(this)
        autoUpdataFrequencyMs.setOnItemSelectedListener(this)
        ffmpegCmdTypeMs.setOnItemSelectedListener(this)
        ffmpegCoreTypeMs.setOnItemSelectedListener(this)
        outputPathShowRela.setOnClickListener {
            ClipboardUtils.copyText(outputPathShowTv.getText())
            Mtools.toast("输出路径已复制到剪贴板")
        }
        switchSingleOutputDir.setOnCheckedChangeListener { buttonView, isChecked ->
            val id = buttonView!!.id
            if (id == R.id.switch_single_output_dir) {
                ConfigData.setSingleOutputDir(isChecked)
            }
        }
    }

    override fun onItemSelected(
        v: MaterialSpinner,
        position: Int,
        id: Long,
        item: String?
    ) {
        val mid = v.id
        when (mid) {
            R.id.ms_bilibili_version -> {
                val cachePath: String?
                when (position) {
                    0 -> cachePath =
                        MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_INTERNAL

                    1 -> cachePath =
                        MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_ABROAD

                    2 -> cachePath =
                        MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_IPAD

                    3 -> cachePath =
                        MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_CONCEPT

                    4 -> return
                    else -> cachePath =
                        MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_INTERNAL
                }

                ConfigData.setCacheFilePath(cachePath)
                cachePathShowTv.setText(cachePath)
                customCachePathRela.setAlpha(0.2f)
                biliVersionLine.setAlpha(1f)
            }

            R.id.ms_auto_updata -> {
                val nowMills = TimeUtils.getNowMills()
                val updateMills: Long
                when (position) {
                    0 -> updateMills = nowMills + UpdateTools.TIMESTAMP_DAY
                    2 -> updateMills = nowMills + UpdateTools.TIMESTAMP_MONTH
                    1 -> updateMills = nowMills + UpdateTools.TIMESTAMP_WEEK
                    else -> updateMills = nowMills + UpdateTools.TIMESTAMP_WEEK
                }
                ConfigData.setUpdateMills(updateMills)
                ConfigData.setUpdateFrequency(position)
            }

            R.id.ms_ffmpeg_cmd_type -> {
                val ffmpegCmd = item
                ConfigData.setFfmpegCmdTemplate(ffmpegCmd)
                ffmpegCmdShowTv.setText(ffmpegCmd)
            }

            R.id.ms_ffmpeg_core_type -> {
                ConfigData.setFfmpegCoreType(position)
                ConfigData.initFFmpegCore()
            }
        }
    }
}