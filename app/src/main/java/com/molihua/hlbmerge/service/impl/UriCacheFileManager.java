package com.molihua.hlbmerge.service.impl;

import android.net.Uri;
import android.view.View;

import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import com.molihua.hlbmerge.dao.ConfigData;
import com.molihua.hlbmerge.entity.CacheDo;
import com.molihua.hlbmerge.entity.CacheFile;
import com.molihua.hlbmerge.entity.CacheSrc;
import com.molihua.hlbmerge.service.BaseCacheFileManager;
import com.molihua.hlbmerge.utils.FileTool;
import com.molihua.hlbmerge.utils.UriTool;
import com.tencent.mmkv.MMKV;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName: UriCacheFileManager
 * @Author: molihuan
 * @Date: 2022/12/22/20:18
 * @Description:
 */
public class UriCacheFileManager extends BaseCacheFileManager {


    private Fragment fragment;

    public UriCacheFileManager(Fragment fragment) {
        super(fragment.getContext());
        this.fragment = fragment;
    }

    @Override
    public List<CacheFile> updateCollectionFileList(String path, List<CacheFile> cacheFileList) {
        //初始化列表
        cacheFileList = initCollectionFileList(path, cacheFileList);

        CacheSrc<Uri> needUri = new CacheSrc<>();
        String[] names = new String[3];


        //获取所有的合集路径
        List<DocumentFile> collectionFiles = UriTool.getCollectionChapterFile(fragment, path);
        //显示进度弹窗
        showLoadingDialog();


        for (int i = 0; i < collectionFiles.size(); i++) {

            DocumentFile[] allDocumentFiles = collectionFiles.get(i).listFiles();


            if (allDocumentFiles.length == 0) {
                continue;
            }
            //进度加载
            int finalI = i + 1;
            //更新弹窗信息
            updateLoadingDialogMsg(finalI + " / " + collectionFiles.size());


            String prePathName = collectionFiles.get(i).getName();

            //获取每一个集合中的第一个章节路径
            DocumentFile oneChapterPath = allDocumentFiles[0];
//            Mtools.log(oneChapterPath.getUri().toString());
            getCacheMsgByMMKV(oneChapterPath, prePathName, needUri, names);


            //item显示路径
            String itemShowPath = path + File.separator + prePathName;

            cacheFileList.add(
                    new CacheFile()
                            .setFlag(BaseCacheFileManager.FLAG_CACHE_FILE_COLLECTION)
                            .setWholeVisibility(View.VISIBLE)
                            .setCollectionPath(itemShowPath)
                            .setCollectionName(names[0])
                            .setChapterName(names[1])
                            .setJsonPath(needUri.getJson() == null ? null : needUri.getJson().toString())
                            .setAudioPath(needUri.getAudio() == null ? null : needUri.getAudio().toString())
                            .setVideoPath(needUri.getVideo() == null ? null : needUri.getVideo().toString())
                            .setDanmakuPath(needUri.getDanmaku() == null ? null : needUri.getDanmaku().toString())
                            .setBoxVisibility(View.INVISIBLE)
                            .setBoxCheck(false)
                            .setUseUri(true)
                            .setCoverUrl(names[2])
            );


        }

        //关闭弹窗
        dismissLoadingDialog();

        return cacheFileList;
    }

    @Override
    public List<CacheFile> initChapterFileList(String collectionPath, List<CacheFile> cacheFileList) {
        if (cacheFileList == null) {
            cacheFileList = new ArrayList<>();
        }

        cacheFileList.clear();

        cacheFileList.add(
                new CacheFile()
                        .setFlag(BaseCacheFileManager.FLAG_CACHE_FILE_BACK)
                        .setWholeVisibility(View.VISIBLE)
                        .setCollectionPath(collectionPath)
                        .setChapterName("返回上一级")
                        .setBoxVisibility(View.INVISIBLE)
                        .setBoxCheck(false)
                        .setUseUri(true)
        );

        return cacheFileList;
    }

    @Override
    public List<CacheFile> updateChapterFileList(String collectionPath, List<CacheFile> cacheFileList) {
        cacheFileList = initChapterFileList(collectionPath, cacheFileList);

        CacheSrc<Uri> needUri = new CacheSrc<>();
        String[] names = new String[3];

        showLoadingDialog();

        //获取一个合集下面所有的章节
        List<DocumentFile> chapterFile = UriTool.getCollectionChapterFile(fragment, collectionPath);

        for (int i = 0; i < chapterFile.size(); i++) {

            int finalI = i + 1;
            updateLoadingDialogMsg(finalI + " / " + chapterFile.size());
            String prePathName = chapterFile.get(i).getName();

            boolean cacheMsgByMMKVresult = getCacheMsgByMMKV(chapterFile.get(i), prePathName, needUri, names);

            if (cacheMsgByMMKVresult) {
                names[0] = FileTool.getName(collectionPath);
            }


            //获取章节里需要的Uri
//            needUri = UriTool.getNeedUri(chapterFile.get(i), needUri);


            //校验needUri
//            String srcErrorMsg = FileTool.needSrcErrorHandle(needUri, prePathName);
//            if (srcErrorMsg != null) {
//                names[0] = FileTool.getName(collectionPath);
//                names[1] = srcErrorMsg;
//            } else {
//                //获取合集名称和章节名称
//                names = UriTool.getCollectionChapterName(needUri.getJson(), names);
//            }

            cacheFileList.add(
                    new CacheFile()
                            .setFlag(BaseCacheFileManager.FLAG_CACHE_FILE_CHAPTER)
                            .setWholeVisibility(View.VISIBLE)
                            .setCollectionPath(collectionPath)
                            .setCollectionName(names[0])
                            .setChapterPath(collectionPath + File.separator + prePathName)
                            .setChapterName(names[1])
                            .setJsonPath(needUri.getJson() == null ? null : needUri.getJson().toString())
                            .setAudioPath(needUri.getAudio() == null ? null : needUri.getAudio().toString())
                            .setVideoPath(needUri.getVideo() == null ? null : needUri.getVideo().toString())
                            .setDanmakuPath(needUri.getDanmaku() == null ? null : needUri.getDanmaku().toString())
                            .setBoxVisibility(View.INVISIBLE)
                            .setBoxCheck(false)
                            .setUseUri(true)
                            .setCoverUrl(names[2])
            );
        }
        dismissLoadingDialog();
        return cacheFileList;
    }

    /**
     * 进行数据缓存
     *
     * @param documentFile
     * @param prePathName
     * @param needUri
     * @param names
     * @return
     */
    public boolean getCacheMsgByMMKV(DocumentFile documentFile, String prePathName, CacheSrc<Uri> needUri, String[] names) {
        String key = ConfigData.TEMP_DATA_PERFIX + documentFile.getUri();
        boolean result = false;
        //判断kv中是否存在
        if (ConfigData.containsKey(key)) {
            CacheDo cacheDo = ConfigData.getInstance(key, CacheDo.class);
            //获取章节里需要的Uri
            needUri.setAudio(Uri.parse(cacheDo.getAudio() == null ? "" : cacheDo.getAudio()));
            needUri.setVideo(Uri.parse(cacheDo.getVideo() == null ? "" : cacheDo.getVideo()));
            needUri.setJson(Uri.parse(cacheDo.getJson() == null ? "" : cacheDo.getJson()));
            needUri.setDanmaku(Uri.parse(cacheDo.getDanmaku() == null ? "" : cacheDo.getDanmaku()));

            names[0] = cacheDo.getTitle();
            names[1] = cacheDo.getSubTitle();
            names[2] = cacheDo.getCoverUrl();


        } else {
            CacheDo cacheDo = new CacheDo();
            //获取章节里需要的Uri
            needUri = UriTool.getNeedUri(documentFile, needUri);
            cacheDo.setJson(needUri.getJson() == null ? "" : needUri.getJson().toString())
                    .setAudio(needUri.getAudio() == null ? "" : needUri.getAudio().toString())
                    .setVideo(needUri.getVideo() == null ? "" : needUri.getVideo().toString())
                    .setDanmaku(needUri.getDanmaku() == null ? "" : needUri.getDanmaku().toString());

            //校验needUri
            String srcErrorMsg = FileTool.needSrcErrorHandle(needUri, prePathName);

            if (srcErrorMsg != null) {
                names[0] = srcErrorMsg;
                names[1] = srcErrorMsg;
                result = true;
            } else {
                //获取合集名称和章节名称
//                names = UriTool.getCollectionChapterName(needUri.getJson(), names);
                try {
                    names = UriTool.uriToJsonString(mContext, needUri.getJson(), names);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            cacheDo.setTitle(names[0]);
            cacheDo.setSubTitle(names[1]);
            cacheDo.setCoverUrl(names[2]);

            ConfigData.saveInstance(key, cacheDo, MMKV.ExpireInMinute);
        }

        return result;

    }


    /**
     * 将合集item转换为章节item
     *
     * @param collectionCacheFileList
     * @return
     */
    public static List<CacheFile> collection2ChapterCacheFileList(Fragment fragment, List<CacheFile> collectionCacheFileList) {
        Objects.requireNonNull(collectionCacheFileList, "collectionCacheFileList is null");

        //如果已经是章节了就直接返回
        if (collectionCacheFileList.get(0).getFlag() == BaseCacheFileManager.FLAG_CACHE_FILE_CHAPTER) {
            return collectionCacheFileList;
        }

        List<CacheFile> tempList = new ArrayList<>();

        CacheSrc<Uri> needUri = new CacheSrc<>();
        String[] names = new String[3];

        String collectionPath;
        //遍历所有合集
        for (int n = 0; n < collectionCacheFileList.size(); n++) {
            //获取一个合集路径
            collectionPath = collectionCacheFileList.get(n).getCollectionPath();
            //获取一个合集下面所有的章节
            List<DocumentFile> chapterFile = UriTool.getCollectionChapterFile(fragment, collectionPath);
            for (int i = 0; i < chapterFile.size(); i++) {

                //获取章节里需要的Uri
                needUri = UriTool.getNeedUri(chapterFile.get(i), needUri);

                String prePathName = chapterFile.get(i).getName();

                //校验needUri
                String srcErrorMsg = FileTool.needSrcErrorHandle(needUri, prePathName);
                if (srcErrorMsg != null) {
                    names[0] = FileTool.getName(collectionPath);
                    names[1] = srcErrorMsg;
                } else {
                    //获取合集名称和章节名称
                    names = UriTool.getCollectionChapterName(needUri.getJson(), names);
                }

                tempList.add(
                        new CacheFile()
                                .setFlag(BaseCacheFileManager.FLAG_CACHE_FILE_CHAPTER)
                                .setWholeVisibility(View.VISIBLE)
                                .setCollectionPath(collectionPath)
                                .setCollectionName(names[0])
                                .setChapterPath(collectionPath + File.separator + chapterFile.get(i).getName())
                                .setChapterName(names[1])
                                .setJsonPath(needUri.getJson() == null ? null : needUri.getJson().toString())
                                .setAudioPath(needUri.getAudio() == null ? null : needUri.getAudio().toString())
                                .setVideoPath(needUri.getVideo() == null ? null : needUri.getVideo().toString())
                                .setDanmakuPath(needUri.getDanmaku() == null ? null : needUri.getDanmaku().toString())
                                .setBoxVisibility(View.INVISIBLE)
                                .setBoxCheck(false)
                                .setUseUri(true)
                                .setCoverUrl(names[2])
                );


            }

        }

        return tempList;
    }

}
