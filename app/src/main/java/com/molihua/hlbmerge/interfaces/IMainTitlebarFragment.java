package com.molihua.hlbmerge.interfaces;

import com.xuexiang.xui.widget.searchview.MaterialSearchView;

public interface IMainTitlebarFragment {
    void setMainTitle(String text);

    void showHideImgView(boolean status);

    void showHideSearchView(boolean status);

    MaterialSearchView getSearchView();
}
