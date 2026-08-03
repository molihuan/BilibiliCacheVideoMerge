package com.molihua.hlbmerge.fragment.impl

import android.view.View
import android.widget.TextView
import com.blankj.molihuan.utilcode.util.StringUtils
import com.molihua.hlbmerge.R
import com.molihua.hlbmerge.fragment.BaseFragment
import com.xuexiang.xui.widget.button.shadowbutton.ShadowImageView
import com.xuexiang.xui.widget.textview.autofit.AutoFitTextView

class BackTitlebarFragment @JvmOverloads constructor(
    var title: String?,
    var subtitle: String? = null
) : BaseFragment(), View.OnClickListener {

    private var btn_back_toolbar: ShadowImageView? = null
    private var main_title_toolbar: AutoFitTextView? = null
    private var subtitle_toolbar: AutoFitTextView? = null

    private var rightOptionTv: TextView? = null
    private var rightOptionText: String? = null
    private var rightOptionClickListener: IClickListener? = null

    interface IClickListener {
        fun onClick(v: View?)
    }

    fun setRightOption(
        rightOptionText: String?,
        rightOptionClickListener: IClickListener
    ): BackTitlebarFragment {
        this.rightOptionText = rightOptionText
        this.rightOptionClickListener = rightOptionClickListener
        return this
    }


    override fun setFragmentViewId(): Int {
        return R.layout.fragment_back_titlebar
    }

    override fun getComponents(view: View?) {
        if (view == null) {
            return
        }
        btn_back_toolbar = view.findViewById(R.id.btn_back_toolbar)
        main_title_toolbar = view.findViewById(R.id.main_title_toolbar)
        subtitle_toolbar = view.findViewById(R.id.subtitle_toolbar)
        rightOptionTv = view.findViewById(R.id.tv_right_option)
    }

    override fun initView() {
        if (StringUtils.isTrimEmpty(title)) {
            main_title_toolbar!!.visibility = View.INVISIBLE
        } else {
            main_title_toolbar!!.text = title
        }

        if (StringUtils.isTrimEmpty(subtitle)) {
            subtitle_toolbar!!.visibility = View.GONE
        } else {
            subtitle_toolbar!!.text = subtitle
        }

        if (rightOptionText != null) {
            rightOptionTv!!.visibility = View.VISIBLE
            rightOptionTv!!.text = rightOptionText
            rightOptionTv!!.setOnClickListener(this)
        }
    }

    override fun setListeners() {
        btn_back_toolbar?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val id = v!!.id
        if (id == R.id.btn_back_toolbar) {
            mActivity!!.onBackPressed()
        } else if (id == R.id.tv_right_option) {
            rightOptionClickListener!!.onClick(v)
        }
    }
}