package com.molihua.hlbmerge.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewbinding.ViewBinding
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.molihua.hlbmerge.fragment.AbstractMainFfmpegFragment
import com.molihua.hlbmerge.fragment.AbstractMainFileShowFragment
import com.molihua.hlbmerge.fragment.AbstractMainHandleFragment
import com.molihua.hlbmerge.fragment.AbstractMainTitlebarFragment
import com.molihua.hlbmerge.fragment.impl.MainCompleteFragment
import com.molihua.hlbmerge.interfaces.IMainFileShowFragment
import com.molihua.hlbmerge.interfaces.IMainTitlebarFragment
import com.molihuan.pathselector.fragment.impl.PathSelectFragment
import com.molihuan.pathselector.utils.PermissionsTools
import com.molihuan.pathselector.utils.VersionTool

abstract class AbstractMainActivity<T : ViewBinding>: BaseActivity<T>(), IMainTitlebarFragment, IMainFileShowFragment {
    abstract fun getBottomNavigView(): BottomNavigationView

    abstract fun getDrawerLayout(): DrawerLayout

    abstract fun getNavigationView(): NavigationView

    abstract fun getViewPager(): ViewPager

    abstract fun showHideNavigation(status: Boolean)

    abstract fun handleShowHide(isShow: Boolean)

    abstract fun getMainFileShowFragment(): AbstractMainFileShowFragment

    abstract fun getMainTitlebarFragment(): AbstractMainTitlebarFragment

    abstract fun getMainFfmpegFragment(): AbstractMainFfmpegFragment

    abstract fun getMainCompleteFragment(): MainCompleteFragment

    abstract fun getMainHandleFragment(): AbstractMainHandleFragment

    abstract fun getCompletePathSelectFragment(): PathSelectFragment

    abstract fun refreshCompleteFileList()

    @SuppressLint("WrongConstant")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //保存这个uri目录的访问权限
        if (VersionTool.isAndroid11()) {
            if (requestCode == PermissionsTools.PERMISSION_REQUEST_CODE) {
                if (data != null) {
                    val uri: Uri?
                    if ((data.data.also { uri = it }) != null) {
                        contentResolver
                            .takePersistableUriPermission(
                                uri!!,
                                data.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                            )
                    }
                    //获取数据刷新列表
                    updateCollectionFileList()
                    refreshCacheFileList()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}