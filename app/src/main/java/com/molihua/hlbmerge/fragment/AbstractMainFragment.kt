package com.molihua.hlbmerge.fragment;

import android.content.Context
import androidx.viewbinding.ViewBinding
import com.molihua.hlbmerge.activity.AbstractMainActivity

/**
 * @ClassName: AbstractMainFragment
 * @Author: molihuan
 * @Date: 2022/12/20/17:14
 * @Description:
 */
abstract class AbstractMainFragment : BaseFragment() {
    @JvmField
    protected var abstractMainActivity: AbstractMainActivity<ViewBinding>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = getActivity();
        if (activity is AbstractMainActivity<*>) {
            abstractMainActivity = activity as AbstractMainActivity<ViewBinding>
        }
    }
}
