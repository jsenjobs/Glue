package app.chaosstudio.com.glue.download.parse

import android.net.Uri
import android.text.TextUtils
import app.chaosstudio.com.glue.App
import app.chaosstudio.com.glue.GPre
import app.chaosstudio.com.glue.download.DownloadManager
import app.chaosstudio.com.glue.download.M3U8DownloadAndSaveCallBack
import app.chaosstudio.com.glue.eventb.DownloadAction
import app.chaosstudio.com.glue.utils.OKManager
import app.chaosstudio.com.glue.video.M3U8
import okhttp3.Request
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by jsen on 2018/2/1.
 *
 */

class M3U8Parse:ParseBase() {
    override fun isTaskExist(url: String): Boolean {
        return taskMap.containsKey(url)
    }

    private val taskMap = HashMap<String, M3U8>()
    override fun stop(url: String) {
        val m3U8 = taskMap[url]
        val d = DownloadManager.downloadManager!!
        if (m3U8!= null) {
            for (ts in m3U8.tsList) {
                d.removeCall(ts.url)
            }
        }
        taskMap.remove(url)
    }

    override fun parse(id:Long, url: String) {
        if (taskMap.containsKey(url)) {
            DownloadAction.fire(id, DownloadAction.ACTION.ON_TOAST, "任务存在")
            return
        }
        var base = GPre.downloadDir
        if (!base.endsWith(File.separator)) {
            base = base + File.separator + "tmp"
        } else {
            base += "tmp"
        }
        val trueSaveDir = checkDirectory(base)
        val m3U8 = parseM3U8(url, trueSaveDir)

        for (ts in m3U8.tsList) {
            if (File(trueSaveDir, ts.fileName).exists()) {
                m3U8.totalLeft --
            } else {
                val request = Request.Builder().url(ts.url).build()
                val call = OKManager.okHttpClient.newCall(request)
                if (DownloadManager.downloadManager!!.addCall(ts.url, call)) {
                    call.enqueue(M3U8DownloadAndSaveCallBack(id, url, trueSaveDir, guessFileNameUnique(url, trueSaveDir), m3U8, ts))
                }
            }
        }
        DownloadAction.fire(id, DownloadAction.ACTION.ON_PROGRESS, (((m3U8.totalItem - m3U8.totalLeft) * 100) / m3U8.totalItem))
        if (m3U8.totalLeft == 0) {
            m3U8.endDownloadTime = System.currentTimeMillis()
            if (mergeTsFile(id, m3U8, trueSaveDir, guessFileNameUnique(url, trueSaveDir))) {
                DownloadAction.fire(id, DownloadAction.ACTION.ON_TOAST, "下载成功")
                DownloadAction.fire(id, DownloadAction.ACTION.ON_FINISHED)
                val mode = App.instances.daoSession.downloadModeDao.load(id)
                mode.isFinished = true
                App.instances.daoSession.downloadModeDao.update(mode)

            }
        } else {
            taskMap[url] = m3U8
            DownloadAction.fire(id, DownloadAction.ACTION.ON_TOAST, "开始下载")
            DownloadAction.fire(id, DownloadAction.ACTION.ON_START)
        }
    }

    private fun parseM3U8(url: String, saveDir: String): M3U8 {

        val m3U8 = M3U8()
        val reader = ParseUtils.getBufferedReader(url)?:return m3U8
        m3U8.basepath = url.substring(0, url.lastIndexOf("/") + 1)

        var line = reader.readLine()
        var key:String? = null
        var iv:String? = null
        while (line != null) {
            if (line.startsWith("#EXTINF:")) {
                line = line.substring(8)
                val index = line.indexOf(",")
                if (index != -1) {
                    line = line.substring(0, index) // time
                    val seconds = try {
                        line.toFloat()
                    } catch (e:Exception) {
                        e.printStackTrace()
                        0F
                    }
                    line = reader.readLine()
                    if (line == null) break
                    if (!line.startsWith("http")) {
                        line = m3U8.basepath + line
                    }
                    m3U8.addTs(M3U8.Companion.Ts(line, getFileName(line, saveDir), seconds, key, iv))
                }

                line = reader.readLine()
            } else if (line.startsWith("#EXT-X-KEY:")) {
                line = line.substring(11)
                val items = line.split(",")
                if (items.size >= 2) {
                    line = items[1]
                    line = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")) // key
                    if (!line.startsWith("http")) {
                        line = m3U8.basepath + line
                    }
                    key = ParseUtils.getString(line)
                    if (items.size == 3) {
                        line = items[2]
                        line = line.substring(line.indexOf("=") + 1) // iv
                        iv = if (!TextUtils.isEmpty(line)) {
                            line
                        } else {
                            null
                        }
                    }
                }


                line = reader.readLine()
            } else if (line.startsWith("#EXT-X-STREAM-INF:")) {
                line = reader.readLine()
                if (line == null) break
                if (line.toLowerCase().endsWith(".m3u8")) {
                    if (!line.startsWith("http")) {
                        line = m3U8.basepath + line
                    }
                    m3U8.merge(parseM3U8(line, saveDir))


                    line = reader.readLine()
                }
            } else {
                line = reader.readLine()
            }
        }

        return m3U8
    }
    private fun guessFileNameUnique(url: String, trueSaveDir:String):String {
        try {
            val uri = Uri.parse(url)
            val name = uri.path.substring(url.lastIndexOf("/") + 1)
            if (File(trueSaveDir, name).exists()) {
                while (true) {
                    val uuid = UUID.randomUUID().toString()
                    if (!File(trueSaveDir, uuid + name).exists()) {
                        return uuid + name + ".ts"
                    }
                }
            } else {
                return name + ".ts"
            }
        } catch (e:Exception) {
            while (true) {
                val uuid = UUID.randomUUID().toString()
                if (!File(trueSaveDir, uuid).exists()) {
                    return uuid + ".ts"
                }
            }
        }
    }

    private fun mergeTsFile(id:Long, m3U8: M3U8, trueSaveDir: String, targetFileName: String):Boolean {
        if (m3U8.tsList.isEmpty()) {
            return false
        }
        val file = File(trueSaveDir, targetFileName)
        val mod = App.instances.daoSession.downloadModeDao.load(id)
        if (m3U8.tsList.size == 1) {
            if(File(trueSaveDir,  m3U8.tsList[0].fileName).renameTo(file)) {
                if (mod != null) {
                    mod.path = file.absolutePath
                }
                App.instances.daoSession.downloadModeDao.update(mod)
                return true
            }
            return false
        }

        val resultFile = File(trueSaveDir, targetFileName)

        try {
            val fs = FileOutputStream(resultFile, true)
            val resultFileChannel = fs.channel
            var tsFile:File
            var fis: FileInputStream
            for (ts in m3U8.tsList) {
                tsFile = File(File(trueSaveDir).parentFile.absolutePath, ts.fileName)
                fis = FileInputStream(tsFile)
                val blk = fis.channel
                resultFileChannel.transferFrom(blk, resultFileChannel.size(), blk.size())
                fis.close()
                blk.close()
                tsFile.delete()
            }
            fs.close()
            resultFileChannel.close()
            if (mod != null) {
                mod.path = resultFile.absolutePath
            }
            App.instances.daoSession.downloadModeDao.update(mod)
        } catch (e:Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }
}
