package com.molihua.hlbmerge.utils;

import android.content.Context;
import android.net.Uri;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.molihua.hlbmerge.dialogs.MoreProgressCallBack;
import com.molihua.hlbmerge.dialogs.SingleProgressCallBack;
import com.molihua.hlbmerge.entities.ListItemMain;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import io.microshow.rxffmpeg.RxFFmpegInvoke;

public class RxFfmpegTools {
    public static final int TYPE_SINGLE=0;//单个
    public static final int TYPE_MORE=1;//多个
    public static final int TYPE_ASYNC=2;//异步执行








    /**
     * 注意路径有空格的情况
     * @param videoPath
     * @param audioPath
     * @param outPutPath
     * @param type
     */
    public static void execStatement(String videoPath, String audioPath,String outPutPath,int type){
        String cmd = "ffmpeg -i %s -i %s -c copy %s.mp4";
        cmd= String.format(cmd, videoPath, audioPath, outPutPath);
        LogUtils.e(cmd);
        String[] cmds = cmd.split(" ");
        switch (type){
            case TYPE_SINGLE:
                RxFFmpegInvoke.getInstance().runCommandRxJava(cmds).subscribe();//同步线程不安全
                break;
            case TYPE_MORE:
                RxFFmpegInvoke.getInstance().runCommand(cmds, null);//同步线程安全
                break;
        }

    }

    public static String getFileMessage(String filePath){
        return RxFFmpegInvoke.getInstance().getMediaInfo(filePath);
    }

    /**
     * 自定义ffmpeg命令
     * @param cmd
     */
    public static void execStatement(String cmd){
        String[] cmds = cmd.split(" ");
        RxFFmpegInvoke.getInstance().runCommandAsync(cmds, null);
    }

    public static void execStatement(List<ListItemMain> listItemMains,Context context,int type){
        for (int i = 0; i < listItemMains.size(); i++) {
            execStatement(listItemMains.get(i),context,TYPE_MORE);
        }
    }

    public static void execStatement(ListItemMain listItemMain, Context context, int type){
        if (listItemMain.getName().equals("返回上一级"))return;//如果是返回上一级则跳过
        List<String> blvPathList = listItemMain.getBlvPathList();
        String fullOutputPath = listItemMain.getFullOutputPath();
        String fullOutputDirPath = listItemMain.getFullOutputDirPath();
        boolean orExistsDir = FileUtils.createOrExistsDir(fullOutputDirPath);//创建文件夹
        String cmd=null;
        String tagerMp4File = fullOutputPath + ".mp4";//最终生成的Mp4文件

        tagerMp4File=FileTools.produceNoRepeatFilePath(tagerMp4File, FileTools.FLAG_FILENAME_INDEX);//对mp4文件名进行重复进行处理

        
        if (orExistsDir) {//文件夹是否存在或者创建成功
            String currentDirAbsolutePath = PathTools.getCurrentDirAbsolutePath(tagerMp4File);
            String fileNameNoExtension = FileUtils.getFileNameNoExtension(tagerMp4File);

            if (listItemMain.isExportXml()){//判断是否要导出弹幕文件
                FileUtils.copy(listItemMain.getDanmakuXmlPath(),currentDirAbsolutePath+fileNameNoExtension+".xml");
            }

            switch (listItemMain.getExportType()){
                case MLHInitConfig.TYPE_EXPORT_VIDEO ://导出无声视频
                    FileUtils.copy(listItemMain.getVideoPath(),tagerMp4File);
                    ToastUtils.make().show("导出无声视频完成");
                    return;
                case MLHInitConfig.TYPE_EXPORT_AUDIO ://导出音频
                    FileUtils.copy(listItemMain.getAudioPath(),currentDirAbsolutePath+fileNameNoExtension+".mp3");
                    ToastUtils.make().show("导出音频完成");
                    return;
            }


            if (blvPathList.size()==0) {//如果blv没有则是M4S合并模式
                String audioPath = listItemMain.getAudioPath();
                String videoPath = listItemMain.getVideoPath();
                cmd = "ffmpeg -i %s -i %s -c copy %s";
                if (StringUtils.isEmpty(videoPath)||StringUtils.isEmpty(audioPath)){
                    ToastUtils.make().show("videoPath或者audioPath为空");
                    return;
                }
                cmd = String.format(cmd, videoPath, audioPath, tagerMp4File);
            }else {
                /**blv合并模式
                 * blv.txt文件内容（不是绝对路径）:
                 * file '0.blv'
                 * file '1.blv'
                 */
                String concatContent="";
                for (int i = 0; i < blvPathList.size(); i++) {
                    concatContent+="file '"+i+".blv'\n";
                }
                //blv.txt文件路径
                String parentPath = blvPathList.get(0);
                int last = parentPath.lastIndexOf('/');
                parentPath=parentPath.substring(0,last+1);
                parentPath=parentPath+"blv.txt";

                FileIOUtils.writeFileFromString(parentPath, concatContent);//把字符串写进文件

                cmd = "ffmpeg -f concat -i %s -c copy %s";
                cmd = String.format(cmd,parentPath, tagerMp4File);

            }


            LogUtils.e(cmd);
            String[] cmds = cmd.split(" ");
            //显示弹窗执行命令
            switch (type) {
                case TYPE_SINGLE:
                    //RxFFmpegInvoke.getInstance().runCommandRxJava(cmds).subscribe();//同步线程不安全
                    //RxFFmpegInvoke.getInstance().runCommand(cmds, null);//同步线程安全

                    SingleProgressCallBack singleProgressCallBack = new SingleProgressCallBack();//实例化一个进度回调处理对象
                    singleProgressCallBack.openProgressDialog(context, tagerMp4File);//打开处理进度ProgressDialog对话框
                    //实例化一个RxFFmpegSubscriber添加回调处理对象
                    SingleProgressCallBack.MyRxFFmpegSubscriber myRxFFmpegSubscriber = new SingleProgressCallBack.MyRxFFmpegSubscriber(singleProgressCallBack);
                    //执行ffmpeg命令
                    RxFFmpegInvoke.getInstance().runCommandRxJava(cmds).subscribe(myRxFFmpegSubscriber);//同步线程不安全

                    break;
                case TYPE_MORE:
                    RxFFmpegInvoke.getInstance().runCommand(cmds, null);//同步线程安全
                    break;
                case TYPE_ASYNC:
                    RxFFmpegInvoke.getInstance().runCommandAsync(cmds, null);
                    break;
                default:
                    ;
            }


            
            
            
        } else {

        }
    }


    public static List<String> moreMergeTagerMp4FileList;//合并后的mp4文件集合

    public static void execStatement(List<ListItemMain> listItemMains,Context context){
        if (moreMergeTagerMp4FileList!=null){
            moreMergeTagerMp4FileList.clear();
        }
        moreMergeTagerMp4FileList=new ArrayList<String>();
        MoreProgressCallBack moreProgressCallBack = new MoreProgressCallBack();
        MaterialDialog openProgressDialog = moreProgressCallBack.openProgressDialog(listItemMains, context);
        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < listItemMains.size(); i++) {
                    ListItemMain listItemMain = listItemMains.get(i);

                    if (listItemMain.getName().equals("返回上一级"))continue;//如果是返回上一级则跳过

                    if (VersionTools.isAndroid11AndNull()){//复制所有文件
                        PathTools.clearAudioVideoPath();//清除缓存
                        String tempOnePath = listItemMain.getPath();//获取tempP路径
                        Uri copyAllUri = UriTools.path2Uri(tempOnePath);//根据路径计算出Android/data对应位置的uri
                        UriTools.copyAllPathAndJson(context.getContentResolver(),context,copyAllUri,1);//复制uri下面的所有文件
                        PathTools.getAudioVideoJsonPath(tempOnePath);
                        listItemMain.setAudioPath(PathTools.getAudioPath());//设置路径AudioPath
                        listItemMain.setVideoPath(PathTools.getVideoPath());//设置路径VideoPath
                        listItemMain.setDanmakuXmlPath(PathTools.getDanmakuXmlPath());//设置路径DanmakuXmlPath
                        listItemMain.setBlvPathList(PathTools.getBlvPathList());//设置路径BlvPathList
                    }



                    List<String> blvPathList = listItemMain.getBlvPathList();
                    String cmd=null;
                    String fullOutputPath = listItemMain.getFullOutputPath();
                    String fullOutputDirPath = listItemMain.getFullOutputDirPath();
                    boolean orExistsDir = FileUtils.createOrExistsDir(fullOutputDirPath);//创建文件夹
                    String tagerMp4File = fullOutputPath + ".mp4";//最终生成的Mp4文件
                    tagerMp4File=FileTools.produceNoRepeatFilePath(tagerMp4File, FileTools.FLAG_FILENAME_INDEX);//对mp4文件名进行重复进行处理

                    moreMergeTagerMp4FileList.add(tagerMp4File);

                    if (orExistsDir) {
                        String currentDirAbsolutePath = PathTools.getCurrentDirAbsolutePath(tagerMp4File);
                        String fileNameNoExtension = FileUtils.getFileNameNoExtension(tagerMp4File);


                        if (listItemMain.isExportXml()){//判断是否要导出弹幕文件
                            FileUtils.copy(listItemMain.getDanmakuXmlPath(),currentDirAbsolutePath+fileNameNoExtension+".xml");
                        }

                        switch (listItemMain.getExportType()){
                            case MLHInitConfig.TYPE_EXPORT_VIDEO :
                                FileUtils.copy(listItemMain.getVideoPath(),tagerMp4File);
                                continue;
                            case MLHInitConfig.TYPE_EXPORT_AUDIO :
                                FileUtils.copy(listItemMain.getAudioPath(),currentDirAbsolutePath+fileNameNoExtension+".mp3");
                                continue;
                        }

                        if (blvPathList.size()==0) {//如果blv没有则是M4S合并模式
                            String audioPath = listItemMain.getAudioPath();
                            String videoPath = listItemMain.getVideoPath();
                            if (StringUtils.isEmpty(videoPath)||StringUtils.isEmpty(audioPath)){
                                ToastUtils.make().show("videoPath或者audioPath为空");
                                return;
                            }
                            cmd = "ffmpeg -i %s -i %s -c copy %s";
                            cmd= String.format(cmd, videoPath, audioPath, tagerMp4File);
                        }else {//blv合并模式
                            /**
                             * blv.txt文件内容:
                             * file '0.blv'
                             * file '1.blv'
                             *
                             */
                            String concatContent="";
                            for (int n = 0; n < blvPathList.size(); n++) {
                                concatContent+="file '"+n+".blv'\n";
                            }
                            //blv.txt文件路径
                            String parentPath = blvPathList.get(0);
                            int last = parentPath.lastIndexOf('/');
                            parentPath=parentPath.substring(0,last+1);
                            parentPath=parentPath+"blv.txt";

                            FileIOUtils.writeFileFromString(parentPath, concatContent);//把字符串写进文件

                            cmd = "ffmpeg -f concat -i %s -c copy %s";
                            cmd = String.format(cmd,parentPath, tagerMp4File);

                        }



                        LogUtils.e(cmd);
                        String[] cmds = cmd.split(" ");
                        RxFFmpegInvoke.getInstance().runCommand(cmds, moreProgressCallBack);//同步线程安全


                    } else {
                    }



                }

                openProgressDialog.dismiss();//关闭弹窗
                ToastUtils.make().show("处理完成");
            }
        }).start();
    }









}
