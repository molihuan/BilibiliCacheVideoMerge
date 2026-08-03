package com.molihua.hlbmerge.fragment.impl

import android.view.View
import android.widget.Button
import com.molihua.hlbmerge.R
import com.molihua.hlbmerge.fragment.AbstractMainFfmpegFragment
import com.molihuan.pathselector.utils.Mtools
import com.xuexiang.xui.widget.edittext.ClearEditText
import com.xuexiang.xui.widget.edittext.MultiLineEditText

class MainToolsFragment : AbstractMainFfmpegFragment(), View.OnClickListener {
    private var ffmpegCmdMlet: MultiLineEditText? = null

    private var runFfmpegCmdBtn: Button? = null

    private var avbvCet: ClearEditText? = null
    private var barrageBtn: Button? = null
    private var picBtn: Button? = null
    override fun setFragmentViewId(): Int {
        return R.layout.fragment_main_tools
    }

    override fun getComponents(view: View?) {
        if (view == null) {
            return
        }
        ffmpegCmdMlet = view.findViewById(R.id.mlet_ffmpeg_cmd)
        runFfmpegCmdBtn = view.findViewById(R.id.btn_run_ffmpeg_cmd)
        barrageBtn = view.findViewById(R.id.btn_barrage_download)
        picBtn = view.findViewById(R.id.btn_pic_download)
    }

    override fun setListeners() {
        runFfmpegCmdBtn?.setOnClickListener(this)
        barrageBtn?.setOnClickListener(this)
        picBtn?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        Mtools.toast("开发中....")
    }
}