package com.molihua.hlbmerge.service.impl

import android.content.Context
import android.view.View
import com.molihua.hlbmerge.entity.CacheFile
import com.molihua.hlbmerge.entity.CacheSrc
import com.molihua.hlbmerge.service.BaseCacheFileManager
import com.molihua.hlbmerge.utils.FileTool

class PathCacheFileManager(context: Context) : BaseCacheFileManager(context) {
    override fun updateCollectionFileList(
        path: String,
        rawCacheFileList: MutableList<CacheFile>
    ): MutableList<CacheFile> {

        //初始化列表
        val cacheFileList = initCollectionFileList(path, rawCacheFileList)

        var needPath = CacheSrc<String>()

        var names = arrayOfNulls<String>(4)

        //获取所有的合集
        val collectionFile = FileTool.getCollectionChapterFile(path)


        if (collectionFile == null || collectionFile.size == 0) {
            return cacheFileList
        }


        for (i in collectionFile.indices) {
            val allFiles = collectionFile[i]!!.listFiles()


            if (allFiles == null || allFiles.size == 0) {
                continue
            }

            //获取每一个集合中的第一个章节
            val oneChapterPath = allFiles[0]
            //获取章节里需要的路径
            needPath = FileTool.getNeedPath(oneChapterPath, needPath)

            val prePathName = collectionFile[i]!!.getName()

            //校验needPath
            val srcErrorMsg = FileTool.needSrcErrorHandle(needPath, prePathName)
            if (srcErrorMsg != null) {
                names[0] = srcErrorMsg
                names[1] = srcErrorMsg
            } else {
                //获取合集名称和章节名称
                names = FileTool.getCollectionChapterName(needPath.getJson(), names)
            }


            if (names == null) {
                return cacheFileList
            }

            cacheFileList.add(
                CacheFile(
                    flag = FLAG_CACHE_FILE_COLLECTION,
                    wholeVisibility = View.VISIBLE,
                    collectionPath = collectionFile[i]!!.absolutePath,
                    collectionName = names[0],
                    chapterName = names[1],
                    audioPath = needPath.getAudio(),
                    videoPath = needPath.getVideo(),
                    jsonPath = needPath.getJson(),
                    danmakuPath = needPath.getDanmaku(),
                    boxVisibility = View.INVISIBLE,
                    boxCheck = false,
                    useUri = false,
                    coverUrl = names[2],
                    bvId = names[3]
                )
            )
        }

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
                useUri = false
            )
        )

        return cacheFileList
    }

    override fun updateChapterFileList(
        collectionPath: String,
        rawCacheFileList: MutableList<CacheFile>
    ): MutableList<CacheFile> {
        val cacheFileList = initChapterFileList(collectionPath, rawCacheFileList)

        var needPath = CacheSrc<String?>()
        var names = arrayOfNulls<String>(4)

        //获取一个合集下面所有的章节
        val chapterFile = FileTool.getCollectionChapterFile(collectionPath)
        for (i in chapterFile.indices) {
            //获取章节里需要的路径
            needPath = FileTool.getNeedPath(chapterFile[i], needPath)

            val prePathName = chapterFile[i]!!.getName()
            //校验needPath
            val srcErrorMsg = FileTool.needSrcErrorHandle(needPath, prePathName)
            if (srcErrorMsg != null) {
                names[0] = FileTool.getName(collectionPath)
                names[1] = srcErrorMsg
            } else {
                //获取合集名称和章节名称
                names = FileTool.getCollectionChapterName(needPath.getJson(), names)
            }
            cacheFileList.add(
                CacheFile(
                    flag = FLAG_CACHE_FILE_CHAPTER,
                    wholeVisibility = View.VISIBLE,
                    collectionPath = collectionPath,
                    collectionName = names[0],
                    chapterPath = chapterFile[i]!!.absolutePath,
                    chapterName = names[1],
                    audioPath = needPath.getAudio(),
                    videoPath = needPath.getVideo(),
                    jsonPath = needPath.getJson(),
                    danmakuPath = needPath.getDanmaku(),
                    boxVisibility = View.INVISIBLE,
                    boxCheck = false,
                    useUri = false,
                    coverUrl = names[2],
                    bvId = names[3]
                )
            )
        }

        return cacheFileList
    }


    companion object {
        /**
         * 将合集item转换为章节item
         *
         * @param collectionCacheFileList
         * @return
         */
        @JvmStatic
        fun collection2ChapterCacheFileList(collectionCacheFileList: MutableList<CacheFile>): MutableList<CacheFile> {

            //如果已经是章节了就直接返回
            if (collectionCacheFileList[0].flag == FLAG_CACHE_FILE_CHAPTER) {
                return collectionCacheFileList
            }

            val tempList: MutableList<CacheFile> = ArrayList()

            var needPath = CacheSrc<String>()
            var names = arrayOfNulls<String>(4)

            var collectionPath: String?
            //遍历所有合集
            for (n in collectionCacheFileList.indices) {
                //获取一个合集路径
                collectionPath = collectionCacheFileList[n].collectionPath
                //获取一个合集下面所有的章节
                val chapterFile = FileTool.getCollectionChapterFile(collectionPath)
                for (i in chapterFile.indices) {
                    //获取章节里需要的路径
                    needPath = FileTool.getNeedPath(chapterFile[i], needPath)

                    val prePathName = chapterFile[i]!!.getName()
                    //校验needPath
                    val srcErrorMsg = FileTool.needSrcErrorHandle(needPath, prePathName)
                    if (srcErrorMsg != null) {
                        names[0] = FileTool.getName(collectionPath)
                        names[1] = srcErrorMsg
                    } else {
                        //获取合集名称和章节名称
                        names = FileTool.getCollectionChapterName(needPath.getJson(), names)
                    }
                    tempList.add(
                        CacheFile(
                            flag = FLAG_CACHE_FILE_CHAPTER,
                            wholeVisibility = View.VISIBLE,
                            collectionPath = collectionPath,
                            collectionName = names[0],
                            chapterPath = chapterFile[i]!!.absolutePath,
                            chapterName = names[1],
                            audioPath = needPath.getAudio(),
                            videoPath = needPath.getVideo(),
                            jsonPath = needPath.getJson(),
                            danmakuPath = needPath.getDanmaku(),
                            boxVisibility = View.INVISIBLE,
                            boxCheck = false,
                            useUri = false,
                            coverUrl = names[2],
                            bvId = names[3]
                        )
                    )
                }
            }

            return tempList
        }
    }


}