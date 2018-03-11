package app.chaosstudio.com.glue.download

import android.content.Context
import android.net.Uri
import android.util.Log
import app.chaosstudio.com.glue.App
import app.chaosstudio.com.glue.download.parse.M3U8Parse
import app.chaosstudio.com.glue.download.parse.MPParse
import app.chaosstudio.com.glue.download.parse.Parse
import app.chaosstudio.com.glue.greendb.gen.DownloadModeDao
import app.chaosstudio.com.glue.greendb.model.DownloadMode
import okhttp3.Call

/**
 * Created by jsen on 2018/2/1.
 */

class DownloadManager private constructor() {
    companion object {
        var downloadManager: DownloadManager? = null
        get() {
            if(field == null) {
                field = DownloadManager()
            }
            return field
        }
    }

    private val parses:HashMap<String, Parse> = HashMap()

    init {
        parses["m3u8"] = M3U8Parse()
        parses["mp"] = MPParse()
    }

    // 创建新的下载任务
    fun createTask(url:String):Boolean {
        val key = urlToKey(url) ?: return false
        parses[key] ?: return false
        if(App.instances.daoSession.downloadModeDao.queryBuilder().where(DownloadModeDao.Properties.Url.eq(url)).limit(1).count() > 0) {
            return false
        }
        val downloadMode = DownloadMode()
        downloadMode.url = url
        downloadMode.isFinished = false
        App.instances.daoSession.downloadModeDao.save(downloadMode)
        // val modes = App.instances.daoSession.downloadModeDao.queryBuilder().where(DownloadModeDao.Properties.Url.eq(url)).limit(1).build().list()
        // if (modes.isEmpty()) return false
        // val mode = modes[0]

        // try {
        //     parse.parse(mode.id, url)
        // } catch (e:Exception) {
        //     return false
        // }
        // return true
        return startTask(url)
    }
    // 点击开始按钮 开始任务
    fun startTask(url: String):Boolean {
        val key = urlToKey(url) ?: return false
        val parse = parses[key] ?: return false
        val modes = App.instances.daoSession.downloadModeDao.queryBuilder().where(DownloadModeDao.Properties.Url.eq(url)).limit(1).build().list()
        if (modes.isEmpty()) return false
        val mode = modes[0]
        try {
            parse.parse(mode.id, url)
        } catch (e:Exception) {
            return false
        }
        return true
    }

    val calls = HashMap<String, Call>()
    fun addCall(url: String, call: Call):Boolean {
        if (calls.containsKey(url)) return false
        calls[url] = call
        return true
    }
    fun removeCall(url: String) {
        val call = calls[url]
        if (call != null) {
            if (!call.isCanceled) call.cancel()
        }
        calls.remove(url)
    }

    // @start task
    fun stopTask(url: String) {
        val key = urlToKey(url) ?: return
        val parse = parses[key] ?: return
        parse.stop(url)
    }
    fun stopAllTask() {
        for (call in calls) {
            if (!call.value.isCanceled) call.value.cancel()
        }
        calls.clear()
    }
    fun isTaskExist(url:String):Boolean {
        for (parse in parses) {
            if (parse.value.isTaskExist(url)) {
                return true
            }
        }
        return false
    }
    // @end task





    private fun urlToKey(url:String):String? {
        return try {
            val uri = Uri.parse(url)
            val path = uri.path
            if (path.toLowerCase().endsWith(".m3u8")) {
                "m3u8"
            } else {
                "mp"
            }
        } catch (e:Exception) {
            null
        }
    }

}
