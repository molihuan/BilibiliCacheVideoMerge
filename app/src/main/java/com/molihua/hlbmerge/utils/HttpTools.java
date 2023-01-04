package com.molihua.hlbmerge.utils;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.model.HttpMethod;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.zip.Inflater;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpTools {
    public final static int FILETYPE_XML=0;
    public final static int FILETYPE_PIC=1;
    public final static int FILETYPE_APK=2;

    /**
     * 下载弹幕文件并解压
     * @param context
     * @param url
     * @param dowmloadFilePath
     */
    public static void downloadFile(AppCompatActivity context, String url, String dowmloadFilePath){
        EasyHttp.download(context)
                .method(HttpMethod.GET)
                .file(new File(dowmloadFilePath))
                .url(url)
                //.url("http://dldir1.qq.com/weixin/android/weixin708android1540.apk")
                //.md5("2E8BDD7686474A7BC4A51ADC3667CABF")
                .listener(new OnDownloadListener() {

                    @Override
                    public void onStart(File file) {

                    }

                    @Override
                    public void onProgress(File file, int progress) {

                    }

                    @Override
                    public void onComplete(File file) {
                        if (FileUtils.getFileExtension(dowmloadFilePath).equals("xml")){
                            //ToastUtils.make().show("下载完成：" + file.getPath());
                            //ToastUtils.make().show("正在解压deflate数据请稍后");
                            byte[] bytes =decompress(FileIOUtils.readFile2BytesByStream(file));
                            FileUtils.delete(dowmloadFilePath);
                            FileIOUtils.writeFileFromBytesByStream(dowmloadFilePath,bytes);
                            //ToastUtils.make().show("处理成功");
                        }
                        ToastUtils.make().show("下载完成：" + file.getPath());

                    }

                    @Override
                    public void onError(File file, Exception e) {
                        ToastUtils.make().show("下载出错：" + e.getMessage());
                    }

                    @Override
                    public void onEnd(File file) {

                    }

                }).start();
    }

    /**
     * 解压deflate数据
     * @param data
     * @return
     */
    public static byte[] decompress(byte[] data) {
        byte[] output;
        Inflater decompresser = new Inflater(true);//这个true是关键
        decompresser.reset();
        decompresser.setInput(data);
        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                o.write(buf, 0, i);
            }
            output = o.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                o.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        decompresser.end();
        return output;
    }


    /**
     * 根据av或bv号下载文件
     * @param context
     * @param AVBV
     * @param fileType
     */

    public static void downloadFileFromCidByAV(AppCompatActivity context,String AVBV,int fileType) {
        String url;
        if (AVBV.matches("[0-9]+")){
            url= "http://api.bilibili.com/x/web-interface/view?aid=";
        }else {
            url= "http://api.bilibili.com/x/web-interface/view?bvid=";
        }
        LogUtils.e(url+AVBV);

        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .get()
                //.url("https://www.bilibili.com/video/av/" + avNumber)
                .url(url + AVBV)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonContent = response.body().string();
                if (StringUtils.isEmpty(jsonContent)){
                    ToastUtils.make().show("请求接口失败,获取网络json文件失败");
                    return;
                }

                Map<String, String> jsonDataMap = JsonTools.parsWebJson(jsonContent);
                if (jsonDataMap==null){
                    ToastUtils.make().show("解析网络json文件失败");
                    return;
                }

                String title =jsonDataMap.get("title");
                String pic =jsonDataMap.get("pic");
                LogUtils.e(pic);

                switch (fileType){
                    case FILETYPE_XML :
                        String cid ="https://comment.bilibili.com/"+jsonDataMap.get("cid")+".xml" ;
                        if (StringUtils.isSpace(cid)){
                            ToastUtils.make().show("下载弹幕失败");
                        }else {
                            String allDownloadXml=PathTools.getOutputTempPath()+"/XML/";
                            FileUtils.createOrExistsDir(allDownloadXml);
                            downloadFile(context,cid,allDownloadXml+title+".xml");
                        }
                        break;
                    case FILETYPE_PIC :
                        if (StringUtils.isSpace(pic)){
                            ToastUtils.make().show("下载封面失败");
                        }else {
                            String allDownloadXml=PathTools.getOutputTempPath()+"/PIC/";
                            FileUtils.createOrExistsDir(allDownloadXml);
                            downloadFile(context,pic,allDownloadXml+title+".jpg");
                        }
                        break;
                }


            }
        });
    }






}
