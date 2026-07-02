package com.molihua.hlbmerge.activity.impl

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.molihua.hlbmerge.R
import com.molihua.hlbmerge.activity.BaseActivity
import com.molihua.hlbmerge.databinding.ActivityHtmlBinding
import com.molihua.hlbmerge.fragment.impl.BackTitlebarFragment
import com.molihua.hlbmerge.utils.FragmentTools

class HtmlActivity : BaseActivity<ActivityHtmlBinding>() {
    private var htmlUrl: String = ""
    private var title: String? = null
    override fun getContentViewBinding(): ActivityHtmlBinding {
        return ActivityHtmlBinding.inflate(layoutInflater)
    }

    override fun getComponents() {

    }

    override fun initData() {
        htmlUrl = intent.getStringExtra("url").toString()
        title = intent.getStringExtra("title")
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        FragmentTools.fragmentReplace(
            getSupportFragmentManager(),
            R.id.titlebar_show_area,
            BackTitlebarFragment(title),
            "html_back_titlebar"
        )

        binding.apply {

            //激活WebView为活跃状态，能正常执行网页的响应
            asstm.onResume()
            asstm.getSettings().javaScriptEnabled = true

            asstm.loadUrl(htmlUrl)
            asstm.setWebViewClient(object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url)
                    return true
                }

                //开始载入页面时调用此方法，在这里我们可以设定一个loading的页面，告诉用户程序正在等待网络响应。
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    //设定加载开始的操作
                }

                //在页面加载结束时调用。我们可以关闭loading 条，切换程序动作。
                override fun onPageFinished(view: WebView?, url: String?) {
                    //设定加载结束的操作
                }

                //在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
                override fun onLoadResource(view: WebView?, url: String?) {
                    //设定加载资源的操作
                }

                //处理https请求
                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler,
                    error: SslError?
                ) {
                    handler.proceed() //表示等待证书响应
                    // handler.cancel(); //表示挂起连接，为默认方式
                    // handler.handleMessage(null); //可做其他处理
                }
            })

            //声明WebSettings子类
            val webSettings = asstm.getSettings()

            //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
            webSettings.javaScriptEnabled = true


            //设置自适应屏幕，两者合用
            //webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
            //webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
            //缩放操作
            webSettings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
            webSettings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放
            webSettings.displayZoomControls = false //隐藏原生的缩放控件

            //其他细节操作
            webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK //关闭webview中缓存
            webSettings.allowFileAccess = true //设置可以访问文件
            webSettings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
            webSettings.loadsImagesAutomatically = true //支持自动加载图片
            webSettings.defaultTextEncodingName = "utf-8" //设置编码格式
        }


    }

    override fun onBackPressed() {
        if (binding.asstm.canGoBack()) {
            binding.asstm.goBack()
            return
        }

        super.onBackPressed()
    }
}