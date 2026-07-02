package com.molihua.hlbmerge.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.molihuan.pathselector.utils.Mtools

object FragmentTools {
    /**
     * 显示 隐藏 fragment
     *
     * @param fragmentManager Fragment管理者
     * @param frameLayoutID   添加的地方frameLayout
     * @param fragment        添加的布局Fragment
     * @param tag             tag
     * @param isShow          是否显示
     * @return
     */
    @JvmStatic
    fun fragmentShowHide(
        fragmentManager: FragmentManager,
        frameLayoutID: Int,
        fragment: Fragment?,
        tag: String?,
        isShow: Boolean
    ): Fragment? {
        // Fragment获取事务
        var fragmentTransaction = fragmentManager.beginTransaction()

        if (fragment == null) {
            Mtools.log("fragment is null and Unable to add")
            return fragment
        }
        //判断是否已经被添加过了
        if (!fragment.isAdded()) {
            try {
                fragmentTransaction = fragmentTransaction.add(frameLayoutID, fragment, tag)
            } catch (e: Exception) {
                Mtools.log("frameLayoutID may not exist and cannot be added")
                e.printStackTrace()
            }
        }

        if (isShow) {
            //显示fragment
            fragmentTransaction.show(fragment)
        } else {
            //隐藏fragment
            fragmentTransaction.hide(fragment)
        }

        //提交事务
        fragmentTransaction.commitAllowingStateLoss()
        return fragment
    }

    @JvmStatic
    fun fragmentReplace(
        fragmentManager: FragmentManager,
        frameLayoutID: Int,
        fragment: Fragment?,
        tag: String?
    ): Fragment? {
        // Fragment获取事务
        var fragmentTransaction = fragmentManager.beginTransaction()

        if (fragment == null) {
            Mtools.log("fragment is null and Unable to replace")
            return fragment
        }

        try {
            fragmentTransaction = fragmentTransaction.replace(frameLayoutID, fragment, tag)
        } catch (e: Exception) {
            Mtools.log("frameLayoutID may not exist and cannot be replace")
            e.printStackTrace()
        }


        //提交事务
        fragmentTransaction.commitAllowingStateLoss()
        return fragment
    }
}