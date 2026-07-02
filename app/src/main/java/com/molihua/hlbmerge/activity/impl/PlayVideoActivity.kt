package com.molihua.hlbmerge.activity.impl

import com.blankj.molihuan.utilcode.util.ClipboardUtils
import com.molihua.hlbmerge.activity.BaseActivity
import com.molihua.hlbmerge.controller.videocontroller.DKVideoController
import com.molihua.hlbmerge.databinding.ActivityPlayVideoBinding
import com.molihua.hlbmerge.utils.FileTool
import com.molihua.hlbmerge.utils.GeneralTools
import com.molihua.hlbmerge.utils.LConstants
import com.molihuan.pathselector.utils.Mtools
import master.flame.danmaku.ui.widget.DanmakuView
import java.io.File

class PlayVideoActivity : BaseActivity<ActivityPlayVideoBinding>() {
    private val context = this

    //DK控制器
    private var videoController: DKVideoController? = null

    //视频路径
    private var videoPath: String? = null

    fun getDanmakuView(): DanmakuView? {
        return videoController!!.danmakuView
    }

    override fun getContentViewBinding(): ActivityPlayVideoBinding {
        return ActivityPlayVideoBinding.inflate(layoutInflater)
    }

    override fun getComponents() {

    }

    override fun initData() {

        //获取播放视频的路径
        videoPath = intent.getStringExtra("videoPath")

        //初始化视频控制器
        videoController = DKVideoController(this, videoPath)


        binding.playVideoView.apply {
            //全屏模式
            //ScreenUtils.setFullScreen(this);
            //设置视频地址
            setUrl(videoPath)
            //设置控制器
            setVideoController(videoController)
            //开始播放
            start()
        }

    }

    override fun initView() {
        binding.btnCopypath.text = "路径:$videoPath"
    }

    override fun setListeners() {
        binding.apply {
            btnCopypath.setOnClickListener {
                ClipboardUtils.copyText(videoPath)
                Mtools.toast("文件路径已复制到剪贴板")
            }

            btnJumpSourceVedio.setOnClickListener {
                val bvId = FileTool.getVedioMetadataTitle(videoPath)
                if (bvId == null) {
                    Mtools.toast("无法获取bvid,请重新导出,如果重新导出也无法获取则缓存文件不完整导致。")
                    return@setOnClickListener
                }
                GeneralTools.jumpBrowser(context, LConstants.URL_BILIBILI_VIDEO_PRE + bvId)
            }

            btnUpdataxml.setOnClickListener {
                Mtools.toast("还在开发中...")
            }

            btnShare.setOnClickListener {
                if (videoPath == null) {
                    Mtools.toast("文件不存在")
                    return@setOnClickListener
                }
                FileTool.shareFile(context, File(videoPath))
            }


        }
    }

    override fun onBackPressed() {
        //先让videoView处理返回事件
        if (videoController?.onBackPressed() == true) {
            return
        }

        super.onBackPressed()
    }

    public override fun onResume() {
        super.onResume()
        //恢复播放
        binding.playVideoView.resume()
        getDanmakuView()?.resume()
    }

    public override fun onPause() {
        super.onPause()
        //暂停视频
        binding.playVideoView.pause()
        getDanmakuView()?.pause()
    }

    public override fun onDestroy() {
        //销毁播放器
        releaseVideoViewDanmakuView()
        super.onDestroy()
    }

    /**
     * 销毁播放器
     */
    private fun releaseVideoViewDanmakuView() {
        binding.playVideoView.release()
        getDanmakuView()?.release()
        Mtools.log("释放videoView和danmakuView")
    }

}