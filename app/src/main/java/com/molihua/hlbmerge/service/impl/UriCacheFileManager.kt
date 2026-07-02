package com.molihua.hlbmerge.service.impl

import android.net.Uri
import android.view.View
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import com.molihua.hlbmerge.dao.ConfigData
import com.molihua.hlbmerge.dao.ConfigData.clearTempData
import com.molihua.hlbmerge.dao.ConfigData.containsKey
import com.molihua.hlbmerge.dao.ConfigData.saveInstance
import com.molihua.hlbmerge.entity.CacheDo
import com.molihua.hlbmerge.entity.CacheFile
import com.molihua.hlbmerge.entity.CacheSrc
import com.molihua.hlbmerge.service.BaseCacheFileManager
import com.molihua.hlbmerge.utils.FileTool
import com.molihua.hlbmerge.utils.UriTool
import com.tencent.mmkv.MMKV
import java.io.File
import java.io.IOException

class UriCacheFileManager(val fragment: Fragment) :
    BaseCacheFileManager(fragment.requireContext()) {
    override fun updateCollectionFileList(
        path: String,
        rawCacheFileList: MutableList<CacheFile>
    ): MutableList<CacheFile> {
        //初始化列表
        val cacheFileList = initCollectionFileList(path, rawCacheFileList)

        val needUri = CacheSrc<Uri>()
        val names = arrayOfNulls<String>(4)


        //获取所有的合集路径
        val collectionFiles = UriTool.getCollectionChapterFile(fragment, path)

        //显示进度弹窗
        showLoadingDialog()


        for (i in collectionFiles.indices) {
            val allDocumentFiles = collectionFiles.get(i)!!.listFiles()


            if (allDocumentFiles.size == 0) {
                continue
            }
            //进度加载
            val finalI = i + 1
            //更新弹窗信息
            updateLoadingDialogMsg(finalI.toString() + " / " + collectionFiles.size)


            val prePathName = collectionFiles.get(i)!!.getName()

            //获取每一个集合中的第一个章节路径
            val oneChapterPath = allDocumentFiles[0]
            //            Mtools.log(oneChapterPath.getUri().toString());
            getCacheMsgByMMKV(oneChapterPath, prePathName, needUri, names)


            //item显示路径
            val itemShowPath = path + File.separator + prePathName

            cacheFileList.add(
                CacheFile(
                    flag = FLAG_CACHE_FILE_COLLECTION,
                    wholeVisibility = View.VISIBLE,
                    collectionPath = itemShowPath,
                    collectionName = names[0],
                    chapterName = names[1],
                    jsonPath = if (needUri.getJson() == null) null else needUri.getJson()
                        .toString(),
                    audioPath = if (needUri.getAudio() == null) null else needUri.getAudio()
                        .toString(),
                    videoPath = if (needUri.getVideo() == null) null else needUri.getVideo()
                        .toString(),
                    danmakuPath = if (needUri.getDanmaku() == null) null else needUri.getDanmaku()
                        .toString(),
                    boxVisibility = View.INVISIBLE,
                    boxCheck = false,
                    useUri = true,
                    coverUrl = names[2],
                    bvId = names[3],
                )
            )
        }

        //关闭弹窗
        dismissLoadingDialog()

        return cacheFileList
    }

    override fun initChapterFileList(
        collectionPath: String,
        cacheFileList: MutableList<CacheFile>
    ): MutableList<CacheFile> {

        cacheFileList.clear()

        cacheFileList.add(
            CacheFile(
                flag = FLAG_CACHE_FILE_BACK,
                wholeVisibility = View.VISIBLE,
                collectionPath = collectionPath,
                chapterName = "返回上一级(长按多选)",
                boxVisibility = View.INVISIBLE,
                boxCheck = false,
                useUri = true
            )
        )

        return cacheFileList
    }

    override fun updateChapterFileList(
        collectionPath: String,
        rawCacheFileList: MutableList<CacheFile>
    ): MutableList<CacheFile> {
        val cacheFileList = initChapterFileList(collectionPath, rawCacheFileList)

        val needUri = CacheSrc<Uri>()
        val names = arrayOfNulls<String>(4)

        showLoadingDialog()


        //获取一个合集下面所有的章节
        val chapterFile = UriTool.getCollectionChapterFile(fragment, collectionPath)

        for (i in chapterFile.indices) {
            val finalI = i + 1
            updateLoadingDialogMsg(finalI.toString() + " / " + chapterFile.size)
            val prePathName = chapterFile.get(i)!!.getName()

            val cacheMsgByMMKVresult =
                getCacheMsgByMMKV(chapterFile.get(i)!!, prePathName, needUri, names)

            if (cacheMsgByMMKVresult) {
                names[0] = FileTool.getName(collectionPath)
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
                CacheFile(
                    flag = FLAG_CACHE_FILE_CHAPTER,
                    wholeVisibility = View.VISIBLE,
                    collectionPath = collectionPath,
                    collectionName = names[0],
                    chapterPath = collectionPath + File.separator + prePathName,
                    chapterName = names[1],
                    jsonPath = if (needUri.getJson() == null) null else needUri.getJson()
                        .toString(),
                    audioPath = if (needUri.getAudio() == null) null else needUri.getAudio()
                        .toString(),
                    videoPath = if (needUri.getVideo() == null) null else needUri.getVideo()
                        .toString(),
                    danmakuPath = if (needUri.getDanmaku() == null) null else needUri.getDanmaku()
                        .toString(),
                    boxVisibility = View.INVISIBLE,
                    boxCheck = false,
                    useUri = true,
                    coverUrl = names[2],
                    bvId = names[3],
                )
            )
        }
        dismissLoadingDialog()
        return cacheFileList
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
    fun getCacheMsgByMMKV(
        documentFile: DocumentFile,
        prePathName: String?,
        needUri: CacheSrc<Uri>,
        names: Array<String?>
    ): Boolean {
        var needUri = needUri
        var names = names
        val key = ConfigData.TEMP_DATA_PERFIX + documentFile.getUri()
        var result = false
        //判断kv中是否存在
        if (containsKey(key)) {
            var cacheDo: CacheDo? = null
            try {
                cacheDo = ConfigData.getInstance(key, CacheDo::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (cacheDo == null) {
                clearTempData()
                return getCacheMsgByMMKV(documentFile, prePathName, needUri, names)
            }

            //获取章节里需要的Uri
            needUri.setAudio(Uri.parse(if (cacheDo.getAudio() == null) "" else cacheDo.getAudio()))
            needUri.setVideo(Uri.parse(if (cacheDo.getVideo() == null) "" else cacheDo.getVideo()))
            needUri.setJson(Uri.parse(if (cacheDo.getJson() == null) "" else cacheDo.getJson()))
            needUri.setDanmaku(Uri.parse(if (cacheDo.getDanmaku() == null) "" else cacheDo.getDanmaku()))

            names[0] = cacheDo.getTitle()
            names[1] = cacheDo.getSubTitle()
            names[2] = cacheDo.getCoverUrl()
            names[3] = cacheDo.getBvId()
        } else {
            val cacheDo = CacheDo()
            //获取章节里需要的Uri
            needUri = UriTool.getNeedUri(documentFile, needUri)
            cacheDo.setJson(if (needUri.getJson() == null) "" else needUri.getJson().toString())
                .setAudio(if (needUri.getAudio() == null) "" else needUri.getAudio().toString())
                .setVideo(if (needUri.getVideo() == null) "" else needUri.getVideo().toString())
                .setDanmaku(
                    if (needUri.getDanmaku() == null) "" else needUri.getDanmaku().toString()
                )

            //校验needUri
            val srcErrorMsg = FileTool.needSrcErrorHandle(needUri, prePathName)

            if (srcErrorMsg != null) {
                names[0] = srcErrorMsg
                names[1] = srcErrorMsg
                result = true
            } else {
                //获取合集名称和章节名称
//                names = UriTool.getCollectionChapterName(needUri.getJson(), names);
                try {
                    names = UriTool.uriToJsonString(mContext, needUri.getJson(), names)
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            }

            cacheDo.setTitle(names[0])
            cacheDo.setSubTitle(names[1])
            cacheDo.setCoverUrl(names[2])
            cacheDo.setBvId(names[3])

            saveInstance(key, cacheDo, MMKV.ExpireInMinute)
        }

        return result
    }

    companion object{

        /**
         * 将合集item转换为章节item
         *
         * @param collectionCacheFileList
         * @return
         */
        @JvmStatic
        fun collection2ChapterCacheFileList(
            fragment: Fragment,
            collectionCacheFileList: MutableList<CacheFile?>
        ): MutableList<CacheFile?> {
            //如果已经是章节了就直接返回
            if (collectionCacheFileList[0]!!.flag == FLAG_CACHE_FILE_CHAPTER) {
                return collectionCacheFileList
            }

            val tempList: MutableList<CacheFile?> = ArrayList()

            var needUri = CacheSrc<Uri?>()
            var names = arrayOfNulls<String>(4)

            var collectionPath: String?
            //遍历所有合集
            for (n in collectionCacheFileList.indices) {
                //获取一个合集路径
                collectionPath = collectionCacheFileList.get(n)!!.collectionPath
                //获取一个合集下面所有的章节
                val chapterFile = UriTool.getCollectionChapterFile(fragment, collectionPath)
                for (i in chapterFile.indices) {
                    //获取章节里需要的Uri

                    needUri = UriTool.getNeedUri(chapterFile.get(i), needUri)

                    val prePathName = chapterFile.get(i)!!.getName()

                    //校验needUri
                    val srcErrorMsg = FileTool.needSrcErrorHandle(needUri, prePathName)
                    if (srcErrorMsg != null) {
                        names[0] = FileTool.getName(collectionPath)
                        names[1] = srcErrorMsg
                    } else {
                        //获取合集名称和章节名称
                        names = UriTool.getCollectionChapterName(needUri.getJson(), names)
                    }

                    tempList.add(
                        CacheFile(
                            flag = FLAG_CACHE_FILE_CHAPTER,
                            wholeVisibility = View.VISIBLE,
                            collectionPath = collectionPath,
                            collectionName = names[0],
                            chapterPath = collectionPath + File.separator + chapterFile[i]!!.name,
                            chapterName = names[1],
                            audioPath = if (needUri.getAudio() == null) null else needUri.getAudio()
                                .toString(),
                            videoPath = if (needUri.getVideo() == null) null else needUri.getVideo()
                                .toString(),
                            jsonPath = if (needUri.getJson() == null) null else needUri.getJson()
                                .toString(),
                            danmakuPath = if (needUri.getDanmaku() == null) null else needUri.getDanmaku()
                                .toString(),
                            boxVisibility = View.INVISIBLE,
                            boxCheck = false,
                            useUri = true,
                            coverUrl = names[2],
                            bvId = names[3],
                        )
                    )
                }
            }

            return tempList
        }

    }




}