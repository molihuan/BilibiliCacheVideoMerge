package com.molihua.hlbmerge.activity.impl

import android.content.Intent
import android.view.MenuItem
import android.view.WindowManager
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.blankj.molihuan.utilcode.util.ClipboardUtils
import com.blankj.molihuan.utilcode.util.DeviceUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.molihua.hlbmerge.BuildConfig
import com.molihua.hlbmerge.R
import com.molihua.hlbmerge.activity.AbstractMainActivity
import com.molihua.hlbmerge.adapter.CacheFileListAdapter
import com.molihua.hlbmerge.dao.ConfigData
import com.molihua.hlbmerge.databinding.ActivityMainBinding
import com.molihua.hlbmerge.entity.CacheFile
import com.molihua.hlbmerge.fragment.AbstractMainFfmpegFragment
import com.molihua.hlbmerge.fragment.AbstractMainFileShowFragment
import com.molihua.hlbmerge.fragment.AbstractMainHandleFragment
import com.molihua.hlbmerge.fragment.AbstractMainTitlebarFragment
import com.molihua.hlbmerge.fragment.impl.MainCompleteFragment
import com.molihua.hlbmerge.fragment.impl.MainFileShowFragment
import com.molihua.hlbmerge.fragment.impl.MainHandleFragment
import com.molihua.hlbmerge.fragment.impl.MainTitlebarFragment
import com.molihua.hlbmerge.fragment.impl.MainTitlebarFragment.ImgView
import com.molihua.hlbmerge.fragment.impl.MainToolsFragment
import com.molihua.hlbmerge.service.ICacheFileManager
import com.molihua.hlbmerge.utils.FragmentTools
import com.molihua.hlbmerge.utils.FragmentTools.fragmentShowHide
import com.molihua.hlbmerge.utils.GeneralTools
import com.molihua.hlbmerge.utils.GeneralTools.jumpProjectAddress
import com.molihua.hlbmerge.utils.InitTool
import com.molihua.hlbmerge.utils.LConstants
import com.molihua.hlbmerge.utils.UpdateTools
import com.molihua.hlbmerge.utils.UriTool
import com.molihuan.pathselector.fragment.impl.PathSelectFragment
import com.molihuan.pathselector.utils.Mtools
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.analytics.MobclickAgent
import com.xuexiang.xui.adapter.FragmentAdapter
import com.xuexiang.xui.widget.searchview.MaterialSearchView
import kotlin.system.exitProcess

class MainActivity : AbstractMainActivity<ActivityMainBinding>(),
    NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {
    private val mBottomNavigView: BottomNavigationView get() = binding.bottomNavigationviewBodyViewpager
    private val mDrawerLayout: DrawerLayout get() = binding.sideContainerDrawerlayout
    private val mNavigationView: NavigationView get() = binding.sideNavigationview
    private val mViewPager: ViewPager get() = binding.mainViewPager

    private val mMainTitlebarFragment: AbstractMainTitlebarFragment by lazy { MainTitlebarFragment() }
    private val mMainFileShowFragment: AbstractMainFileShowFragment by lazy { MainFileShowFragment() }
    private val mMainFfmpegFragment: AbstractMainFfmpegFragment by lazy { MainToolsFragment() }
    private val mMainCompleteFragment: MainCompleteFragment by lazy { MainCompleteFragment() }
    private val mMainHandleFragment: AbstractMainHandleFragment by lazy { MainHandleFragment() }

    private var firstBackTime: Long = 0


    override fun getContentViewBinding(): ActivityMainBinding {
        //防止键盘的弹出将布局顶上去
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun getComponents() {

    }

    override fun initData() {
        //友盟初始化
        InitTool.initWithDialog(this, true, false)
        //自动周期检测更新
        UpdateTools.autoCheckUpdata(this)
    }

    override fun initView() {
        //侧边栏手机信息
        val phoneInfoTv = mNavigationView.getHeaderView(0).findViewById<TextView>(R.id.phone_info)
        val infos = """
            Android:${DeviceUtils.getSDKVersionName()}   App版本:${BuildConfig.VERSION_NAME}
            机型:${DeviceUtils.getManufacturer()}/${DeviceUtils.getModel()}
            设备id:${CrashReport.getUserId()}
        """.trimIndent()

        phoneInfoTv.text = infos
        phoneInfoTv.setOnClickListener {
            ClipboardUtils.copyText(infos)
            Mtools.toast("已复制到剪贴板")
        }


        //加载主显示区
        val adapter = FragmentAdapter<Fragment?>(getSupportFragmentManager())
        adapter.addFragment(mMainFileShowFragment, "主页")
        adapter.addFragment(mMainCompleteFragment, "完成文件")
        adapter.addFragment(mMainFfmpegFragment, "工具")
        mViewPager.setOffscreenPageLimit(3)
        mViewPager.setAdapter(adapter)


        //加载titlebar
        fragmentShowHide(
            supportFragmentManager,
            R.id.frameLayout_main_titlebar_area,
            mMainTitlebarFragment,
            LConstants.TAG_FRAGMENT_MAIN_TITLEBAR,
            true
        )
    }

    override fun setListeners() {
        mBottomNavigView.setOnItemSelectedListener(::onNavigationItemSelected)
        mNavigationView.setNavigationItemSelectedListener(this)
        mViewPager.addOnPageChangeListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var intent: Intent?
        when (item.itemId) {
            R.id.item_home -> {
                mViewPager.setCurrentItem(0, true)
            }

            R.id.item_complete_video_list -> {
                mViewPager.setCurrentItem(1, true)
            }

            R.id.item_ffmpeg -> {
                mViewPager.setCurrentItem(2, true)
            }

            R.id.item_setting -> {
                intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }

            R.id.item_teach -> {
                jumpProjectAddress(this)
            }

            R.id.item_aboutus -> {
                intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }

            R.id.item_updatalog -> {
                UpdateTools.limitClickCheckUpdata(this)
            }

            R.id.item_exitapp -> {
                MobclickAgent.onKillProcess(this)
                finish()
                exitProcess(0)
            }
        }
        //侧滑菜单关闭
        mDrawerLayout.closeDrawers()
        return true
    }

    override fun onBackPressed() {
        //关闭侧滑菜单
        if (mDrawerLayout.isOpen) {
            showHideNavigation(false)
            return
        }

        if (mViewPager.currentItem == 0 && mMainTitlebarFragment.onBackPressed()) {
            return
        }

        if (mViewPager.currentItem == 0 && mMainFileShowFragment.onBackPressed()) {
            return
        }

        if (mViewPager.currentItem == 1 && mMainCompleteFragment.onBackPressed()) {
            return
        }

        //按两次返回键退出程序
        if (System.currentTimeMillis() - firstBackTime > 2000) {
            Mtools.toast("再按一次返回键退出程序")
            firstBackTime = System.currentTimeMillis()
            return
        }

        super.onBackPressed()
    }

    override fun handleShowHide(isShow: Boolean) {
        //加载handle
        FragmentTools.fragmentShowHide(
            supportFragmentManager,
            R.id.frameLayout_main_handle_area,
            mMainHandleFragment,
            LConstants.TAG_FRAGMENT_MAIN_HANDLE,
            isShow
        )
    }

    override fun onStart() {
        super.onStart()
        GeneralTools.getShizukuPermission(this)

        if (ConfigData.isAgreeTerm()) {
            UriTool.grantedUriPermission(ConfigData.getCacheFilePath(), this)
        }
    }

    override fun onPageSelected(position: Int) {
        when (position) {
            0 -> {
                showHideImgView(true)
                handleShowHide(isMultipleSelectionMode())
            }

            1 -> {
                showHideSearchView(false)
                showTitleImgView(ImgView.REFRESH)
                handleShowHide(false)
            }

            2 -> {
                showHideSearchView(false)
                showHideImgView(false)
                handleShowHide(false)
            }

            else -> {}
        }

        val item = mBottomNavigView.menu.getItem(position)
        setMainTitle(item.title.toString())
        item.isChecked = true
    }


    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun getBottomNavigView(): BottomNavigationView {
        return mBottomNavigView
    }

    override fun getDrawerLayout(): DrawerLayout {
        return mDrawerLayout
    }

    override fun getNavigationView(): NavigationView {
        return mNavigationView
    }

    override fun getViewPager(): ViewPager {
        return mViewPager
    }

    override fun getMainFileShowFragment(): AbstractMainFileShowFragment {
        return mMainFileShowFragment
    }

    override fun getMainTitlebarFragment(): AbstractMainTitlebarFragment {
        return mMainTitlebarFragment
    }

    override fun getMainFfmpegFragment(): AbstractMainFfmpegFragment {
        return mMainFfmpegFragment
    }

    override fun getMainCompleteFragment(): MainCompleteFragment {
        return mMainCompleteFragment
    }

    override fun getMainHandleFragment(): AbstractMainHandleFragment {
        return mMainHandleFragment
    }

    override fun getCompletePathSelectFragment(): PathSelectFragment {
        return mMainCompleteFragment.pathSelectFragment
    }

    override fun refreshCompleteFileList() {
        mMainCompleteFragment.refreshFileList()
    }

    override fun showHideNavigation(status: Boolean) {
        if (status) {
            mDrawerLayout.openDrawer(GravityCompat.START)
        } else {
            mDrawerLayout.close()
        }
    }

    override fun setMainTitle(text: String) {
        mMainTitlebarFragment.setMainTitle(text)
    }

    override fun showHideImgView(status: Boolean) {
        mMainTitlebarFragment.showHideImgView(status)
    }

    override fun showTitleImgView(showImg: ImgView) {
        mMainTitlebarFragment.showTitleImgView(showImg)
    }


    override fun showHideSearchView(status: Boolean) {
        mMainTitlebarFragment.showHideSearchView(status)
    }

    override fun getSearchView(): MaterialSearchView {
        return mMainTitlebarFragment.getSearchView()
    }

    override fun updateCollectionFileList(): MutableList<CacheFile> {
        return mMainFileShowFragment.updateCollectionFileList()
    }

    override fun updateChapterFileList(): MutableList<CacheFile> {
        return mMainFileShowFragment.updateChapterFileList()
    }

    override fun updateChapterFileList(collectionPath: String): MutableList<CacheFile> {
        return mMainFileShowFragment.updateChapterFileList(collectionPath)
    }

    override fun getSelectedCacheFileList(): MutableList<CacheFile> {
        return mMainFileShowFragment.getSelectedCacheFileList()
    }

    override fun getAllCacheFileList(): MutableList<CacheFile> {
        return mMainFileShowFragment.getAllCacheFileList()
    }

    override fun getCacheFileListAdapter(): CacheFileListAdapter {
        return mMainFileShowFragment.getCacheFileListAdapter()
    }

    override fun getPathCacheFileManager(): ICacheFileManager {
        return mMainFileShowFragment.getPathCacheFileManager()
    }

    override fun selectAllCacheFile(status: Boolean) {
        mMainFileShowFragment.selectAllCacheFile(status)
    }

    override fun openCloseMultipleMode(cacheFile: CacheFile, status: Boolean) {
        mMainFileShowFragment.openCloseMultipleMode(cacheFile, status)
    }

    override fun openCloseMultipleMode(status: Boolean) {
        mMainFileShowFragment.openCloseMultipleMode(status)
    }

    override fun isMultipleSelectionMode(): Boolean {
        return mMainFileShowFragment.isMultipleSelectionMode()
    }

    override fun refreshCacheFileList() {
        mMainFileShowFragment.refreshCacheFileList()
    }

    override fun setWholeVisible(state: Boolean): MutableList<CacheFile> {
        return mMainFileShowFragment.setWholeVisible(state)
    }
}