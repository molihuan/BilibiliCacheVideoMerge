package com.molihua.hlbmerge.activity.impl;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.molihuan.utilcode.util.TimeUtils;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.activity.BaseActivity;
import com.molihua.hlbmerge.dao.ConfigData;
import com.molihua.hlbmerge.fragment.impl.BackTitlebarFragment;
import com.molihua.hlbmerge.utils.FragmentTools;
import com.molihua.hlbmerge.utils.UpdataTools;
import com.molihuan.pathselector.PathSelector;
import com.molihuan.pathselector.entity.FileBean;
import com.molihuan.pathselector.entity.FontBean;
import com.molihuan.pathselector.fragment.BasePathSelectFragment;
import com.molihuan.pathselector.listener.CommonItemListener;
import com.molihuan.pathselector.utils.MConstants;
import com.molihuan.pathselector.utils.Mtools;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner;

import java.util.List;

/**
 * @ClassName: SettingsActivity
 * @Author: molihuan
 * @Date: 2022/12/26/19:56
 * @Description:
 */
public class SettingsActivity extends BaseActivity implements View.OnClickListener, MaterialSpinner.OnItemSelectedListener {
    private TextView cachePathShowTv;
    private MaterialSpinner biliVersionMs;
    private RelativeLayout customCachePathRela;
    private TextView outputPathShowTv;
    private RelativeLayout customOutputPathRela;
    private LinearLayout biliVersionLine;

    private MaterialSpinner autoUpdataFrequencyMs;


    @Override
    public int setContentViewID() {
        return R.layout.activity_settings;
    }

    @Override
    public void getComponents() {
        cachePathShowTv = findViewById(R.id.tv_cache_path_show);
        biliVersionMs = findViewById(R.id.ms_bilibili_version);
        customCachePathRela = findViewById(R.id.rela_custom_cache_path);
        outputPathShowTv = findViewById(R.id.tv_output_path_show);
        customOutputPathRela = findViewById(R.id.rela_custom_output_path);
        biliVersionLine = findViewById(R.id.line_switch_bilibili_app_version);
        autoUpdataFrequencyMs = findViewById(R.id.ms_auto_updata);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        //显示路径配置
        String cacheFilePath = ConfigData.getCacheFilePath();
        int updateFrequency = ConfigData.getUpdateFrequency();
        cachePathShowTv.setText(cacheFilePath);
        outputPathShowTv.setText(ConfigData.getOutputFilePath());

        autoUpdataFrequencyMs.setSelectedIndex(updateFrequency);

        if (cacheFilePath.equals(MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_INTERNAL)) {
            biliVersionMs.setSelectedIndex(0);
            customCachePathRela.setAlpha(0.2f);
            biliVersionLine.setAlpha(1f);
        } else if (cacheFilePath.equals(MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_ABROAD)) {
            biliVersionMs.setSelectedIndex(1);
            customCachePathRela.setAlpha(0.2f);
            biliVersionLine.setAlpha(1f);
        } else if (cacheFilePath.equals(MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_IPAD)) {
            biliVersionMs.setSelectedIndex(2);
            customCachePathRela.setAlpha(0.2f);
            biliVersionLine.setAlpha(1f);
        } else if (cacheFilePath.equals(MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_CONCEPT)) {
            biliVersionMs.setSelectedIndex(3);
            customCachePathRela.setAlpha(0.2f);
            biliVersionLine.setAlpha(1f);
        } else {
            biliVersionMs.setSelectedIndex(4);
            customCachePathRela.setAlpha(1f);
            biliVersionLine.setAlpha(0.4f);
        }

        FragmentTools.fragmentReplace(
                getSupportFragmentManager(),
                R.id.titlebar_show_area,
                new BackTitlebarFragment("设置").setRightOption("恢复默认", new BackTitlebarFragment.IClickListener() {
                    @Override
                    public void onClick(View v) {
                        //默认缓存路径
                        String cachePath = MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_INTERNAL;
                        ConfigData.setCacheFilePath(cachePath);
                        cachePathShowTv.setText(cachePath);
                        customCachePathRela.setAlpha(0.2f);
                        biliVersionMs.setSelectedIndex(0);
                        biliVersionLine.setAlpha(1f);
                        //默认输出路径
                        String path = MConstants.DEFAULT_ROOTPATH + ConfigData.TYPE_OUTPUT_FILE_PATH_COMPLETE;
                        ConfigData.setOutputFilePath(path);
                        outputPathShowTv.setText(path);
                        //默认更新设置
                        autoUpdataFrequencyMs.setSelectedIndex(1);
                        onItemSelected(autoUpdataFrequencyMs, 1, autoUpdataFrequencyMs.getId(), null);

                        Mtools.toast("恢复默认成功");
                    }
                }),
                "setting_back_titlebar"
        );
    }

    @Override
    public void setListeners() {
        customCachePathRela.setOnClickListener(this);
        customOutputPathRela.setOnClickListener(this);
        biliVersionMs.setOnItemSelectedListener(this);
        autoUpdataFrequencyMs.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rela_custom_cache_path) {
            PathSelector.build(this, MConstants.BUILD_ACTIVITY)
                    .setRequestCode(3660)
                    .setTitlebarMainTitle(new FontBean("选择缓存文件夹(download)"))
                    .setAlwaysShowHandleFragment(true)
                    .setMaxCount(1)
                    .setHandleItemListeners(
                            new CommonItemListener("选择") {
                                @Override
                                public boolean onClick(View v, TextView tv, List<FileBean> selectedFiles, String currentPath, BasePathSelectFragment pathSelectFragment) {
                                    if (selectedFiles == null || selectedFiles.size() == 0) {
                                        Mtools.toast("你还没有选择捏!请长按文件夹进行选择!");
                                    } else {
                                        String path = selectedFiles.get(0).getPath();
                                        boolean success = ConfigData.setCacheFilePath(path);
                                        cachePathShowTv.setText(path);
                                        biliVersionMs.setSelectedIndex(4);
                                        customCachePathRela.setAlpha(1f);
                                        biliVersionLine.setAlpha(0.4f);

                                        if (success) {
                                            Mtools.toast("设置成功");
                                        } else {
                                            Mtools.toast("设置失败");
                                        }

                                    }
                                    return false;
                                }
                            }
                    )
                    .show();
        } else if (id == R.id.rela_custom_output_path) {
            PathSelector.build(this, MConstants.BUILD_ACTIVITY)
                    .setRequestCode(3660)
                    .setShowSelectStorageBtn(false)
                    .setTitlebarMainTitle(new FontBean("选择输出路径"))
                    .setAlwaysShowHandleFragment(true)
                    .setMaxCount(1)
                    .setHandleItemListeners(
                            new CommonItemListener("选择") {
                                @Override
                                public boolean onClick(View v, TextView tv, List<FileBean> selectedFiles, String currentPath, BasePathSelectFragment pathSelectFragment) {
                                    if (selectedFiles == null || selectedFiles.size() == 0) {
                                        Mtools.toast("你还没有选择捏!请长按文件夹进行选择!");
                                    } else {
                                        String path = selectedFiles.get(0).getPath();
                                        boolean success = ConfigData.setOutputFilePath(path);
                                        outputPathShowTv.setText(path);
                                        if (success) {
                                            Mtools.toast("设置成功");
                                        } else {
                                            Mtools.toast("设置失败");
                                        }
                                    }
                                    return false;
                                }
                            }
                    )
                    .show();
        }
    }

    @Override
    public void onItemSelected(MaterialSpinner v, int position, long id, Object item) {
        int msid = v.getId();
        if (msid == R.id.ms_bilibili_version) {
            String cachePath;
            switch (position) {
                case 0:
                    cachePath = MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_INTERNAL;
                    break;
                case 1:
                    cachePath = MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_ABROAD;
                    break;
                case 2:
                    cachePath = MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_IPAD;
                    break;
                case 3:
                    cachePath = MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_CONCEPT;
                    break;
                case 4://空白什么都不做
                    return;
                default:
                    cachePath = MConstants.PATH_ANRROID_DATA + ConfigData.TYPE_CACHE_FILE_PATH_INTERNAL;
            }

            ConfigData.setCacheFilePath(cachePath);
            cachePathShowTv.setText(cachePath);
            customCachePathRela.setAlpha(0.2f);
            biliVersionLine.setAlpha(1f);

        } else if (msid == R.id.ms_auto_updata) {
            long nowMills = TimeUtils.getNowMills();
            long updateMills;
            switch (position) {
                case 0://一天
                    updateMills = nowMills + UpdataTools.TIMESTAMP_DAY;
                    break;
                case 2://一月
                    updateMills = nowMills + UpdataTools.TIMESTAMP_MONTH;
                    break;
                case 1://一周
                default:
                    updateMills = nowMills + UpdataTools.TIMESTAMP_WEEK;
            }
            ConfigData.setUpdateMills(updateMills);
            ConfigData.setUpdateFrequency(position);

        }

    }
}
