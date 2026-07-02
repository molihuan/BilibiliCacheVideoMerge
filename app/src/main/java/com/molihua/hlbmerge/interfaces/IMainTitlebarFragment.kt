package com.molihua.hlbmerge.interfaces;

import com.molihua.hlbmerge.fragment.impl.MainTitlebarFragment;
import com.xuexiang.xui.widget.searchview.MaterialSearchView;

interface IMainTitlebarFragment {
    fun setMainTitle(text: String)

    fun showHideImgView(status: Boolean)

    fun showTitleImgView(showImg: MainTitlebarFragment.ImgView)

    fun showHideSearchView(status: Boolean)

    fun getSearchView(): MaterialSearchView
}
