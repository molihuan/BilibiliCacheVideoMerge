package com.molihua.hlbmerge.interfaces;

import com.molihua.hlbmerge.fragment.impl.MainTitlebarFragment;
import com.xuexiang.xui.widget.searchview.MaterialSearchView;

public interface IMainTitlebarFragment {
    void setMainTitle(String text);
    
    void showHideImgView(boolean status);

    void showTitleImgView(MainTitlebarFragment.ImgView showImg);

    void showHideSearchView(boolean status);

    MaterialSearchView getSearchView();
}
