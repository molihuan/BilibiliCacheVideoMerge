package com.molihua.hlbmerge.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.fragments.BackToolbarFragment;
import com.molihua.hlbmerge.interfaces.IActivityAndFragment;

/**
 * 加载html页面
 */
public class LoadingAssetsHTMLActivity extends AppCompatActivity implements View.OnClickListener, IActivityAndFragment {

    private BackToolbarFragment backToolbarFragment;
    private WebView asstm;
    private String title;
    private String htmlUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updata_log);

        getComponents();//获取组件
        setListeners();//设置监听
        initData();//初始化数据
    }

    private void initData() {
        Intent intent = getIntent();
        htmlUrl = intent.getStringExtra("HTMLUrl");
        title = intent.getStringExtra("title");
        fragmentInit();
        //激活WebView为活跃状态，能正常执行网页的响应
        asstm.onResume();
        asstm.getSettings().setJavaScriptEnabled(true);

        asstm.loadUrl(htmlUrl);
        asstm.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            //开始载入页面时调用此方法，在这里我们可以设定一个loading的页面，告诉用户程序正在等待网络响应。
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //设定加载开始的操作
            }
            //在页面加载结束时调用。我们可以关闭loading 条，切换程序动作。
            @Override
            public void onPageFinished(WebView view, String url) {
                //设定加载结束的操作
            }
            //在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
            @Override
            public void onLoadResource(WebView view, String url) {
                //设定加载资源的操作
            }
            @Override
            //处理https请求
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); //表示等待证书响应
                // handler.cancel(); //表示挂起连接，为默认方式
                // handler.handleMessage(null); //可做其他处理
            }
        });
        //声明WebSettings子类
        WebSettings webSettings = asstm.getSettings();
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        //设置自适应屏幕，两者合用
        //webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        //webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
    }

    private void fragmentInit() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();//开启事务
        backToolbarFragment = new BackToolbarFragment(title);//实例化fragment

        FragmentTransaction transaction = fragmentTransaction.add(R.id.toolbar_area, backToolbarFragment);
        transaction.commitAllowingStateLoss();

    }

    private void setListeners() {
    }

    private void getComponents() {

        asstm=findViewById(R.id.asstm);
    }

    @Override
    public void onBackPressed() {
        if (asstm.canGoBack()) {
            asstm.goBack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public Object invokeFuncAiF(int functionCode) {
        return null;
    }
}