package com.molihua.hlbmerge.entity

import androidx.documentfile.provider.DocumentFile
import java.io.Serializable

data class CacheFile(
    //如果是合集则为0，如果是章节则是1
    var flag: Int? = null,
    //整体是否可见
    var wholeVisibility: Int? = null,
    //合集路径
    var collectionPath: String? = null,
    //合集名
    var collectionName: String? = null,
    //章节路径
    var chapterPath: String? = null,
    //章节名
    var chapterName: String? = null,
    //章节下audio路径
    var audioPath: String? = null,
    //章节下video路径
    var videoPath: String? = null,
    //章节下json路径
    var jsonPath: String? = null,
    //弹幕文件路径
    var danmakuPath: String? = null,
    //checkBox是否可见
    var boxVisibility: Int? = null,
    //checkBox是否选中
    var boxCheck: Boolean? = null,
    //存储blv格式的文件路径
    var blvPathList: MutableList<String?>? = null,
    //是否使用uri地址
    var useUri: Boolean? = null,
    //document
    var documentFile: DocumentFile? = null,
    //图片封面地址
    var coverUrl: String? = null,
    //bvid
    var bvId: String? = null
) : Serializable, Cloneable {
    public override fun clone(): CacheFile  {
        val cloneCacheFile = super.clone() as CacheFile
        if (blvPathList != null) {
            cloneCacheFile.blvPathList = ArrayList(blvPathList)
        }
        return cloneCacheFile
    }
}
