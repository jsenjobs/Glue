package app.chaosstudio.com.glue.video

import android.net.Uri
import app.chaosstudio.com.glue.GPre
import app.chaosstudio.com.glue.ok.M3U8SaveFileCallBack
import app.chaosstudio.com.glue.utils.OKManager
import okhttp3.Request
import java.io.BufferedReader
import java.io.File
import java.io.StringReader
import java.util.*

/**
 * Created by jsen on 2018/1/31.
 *
 */
class M3U8 {
    var basepath = ""
    val tsList = ArrayList<Ts>()
    var startTime = 0L              // 开始时间
    get() {
        return 0L
    }
    var endTime = 0L                // 结束时间
    get() {
        var total = tsList
                .map { it.seconds }
                .sum()
        total *= 1000
        return total.toLong()
    }
    var startDownloadTime = 0L      // 开始下载时间
    var endDownloadTime = 0L        // 结束下载时间

    var totalLeft:Int = 0
    var faildItem:Int = 0
    var totalItem:Int = 0

    fun addTs(ts:Ts) {
        tsList.add(ts)
        totalLeft = tsList.size
        totalItem = totalLeft
    }

    fun merge(m3U8:M3U8?):M3U8 {
        if (m3U8 != null) {
            // debug
            // System.out.println("merge:" + m3U8.toString())
            tsList.addAll(m3U8.tsList)
        }
        totalLeft = tsList.size
        totalItem = totalLeft
        return this
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("basepath: " + basepath)
        for (ts in tsList) {
            sb.append("\nts_file_name = " + ts)
        }
        sb.append("\n\nstartTime = " + startTime)
        sb.append("\n\nendTime = " + endTime)
        sb.append("\n\nstartDownloadTime = " + startDownloadTime)
        sb.append("\n\nendDownloadTime = " + endDownloadTime)
        return sb.toString()
    }

    companion object {
        fun buildM3U8(url:String):M3U8? {
            val request = Request.Builder().url(url).build()
            val call = OKManager.okHttpClient.newCall(request)
            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    val result = response.body()?.string()
                    if (result != null && result != "") {
                        val ret = M3U8()
                        ret.basepath = request.url().toString()
                        ret.basepath = ret.basepath.substring(0, ret.basepath.lastIndexOf("/") + 1)
                        val reader = BufferedReader(StringReader(result))
                        var line:String? = reader.readLine()
                        var searchIndex:Int
                        var seconds = 0F
                        var key:String? = null
                        var iv:String? = null
                        while (line != null) {
                            if (line.startsWith("#")) {
                                if (line.startsWith("#EXT-X-KEY:")) {
                                    // load key
                                    line = line.substring(11)
                                    val items = line.split(",")
                                    if (items.size >= 2) {
                                        line = items[1]
                                        line = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""))
                                        if (!line.startsWith("http")) {
                                            line = ret.basepath + line
                                        }
                                        val req = Request.Builder().url(line).build()
                                        val cal = OKManager.okHttpClient.newCall(req)
                                        try {
                                            val res = cal.execute()
                                            key = res.body()?.string()
                                        } catch (e:Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                    if (items.size == 3) {
                                        line = items[2]
                                        line = line.substring(line.indexOf("=") + 1)
                                        // System.out.println(line)
                                        iv = line
                                    }
                                    line = reader.readLine()
                                    continue
                                }
                                if (line.startsWith("#EXTINF:")) {
                                    line = line.substring(8)
                                    searchIndex = line.indexOf(",")
                                    if (searchIndex != -1) {
                                        line = line.substring(0, searchIndex)
                                    }
                                    seconds = try {
                                        line.toFloat()
                                    } catch (e:Exception) {
                                        e.printStackTrace()
                                        0F
                                    }
                                }
                                line = reader.readLine()
                                continue
                            }
                            if (line.toLowerCase().endsWith(".m3u8")) {
                                return ret.merge(buildM3U8(ret.basepath + line))
                            }

                            ret.addTs(Ts(ret.basepath + line, getFileName(ret.basepath + line, GPre.downloadDir + "/tmp"), seconds, key, iv))
                            line = reader.readLine()
                        }
                        return ret
                    } else {
                        return null
                    }
                } else {
                    return null
                }
            } catch (e:Exception) {
                e.printStackTrace()
                return null
            }
        }
        fun download(m3U8: M3U8, cacheDir:String, targetFile:String) {
            val ok = OKManager.okHttpClient
            val f = File(cacheDir)
            if (!f.exists()) f.mkdirs()
            m3U8.startDownloadTime = System.currentTimeMillis()
            m3U8.tsList.forEach { t: Ts ->
                val file = File(getFileName(t.url, cacheDir))
                if (!file.exists()) {
                    val request = Request.Builder().url(t.url).build()
                    val call = ok.newCall(request)
                    call.enqueue(M3U8SaveFileCallBack(m3U8, t, cacheDir, targetFile))
                } else {
                    m3U8.totalLeft--
                }
            }
        }
        private fun getFileName(url: String, trueSaveDir:String):String {
            try {
                val uri = Uri.parse(url)
                return uri.path.substring(url.lastIndexOf("/") + 1)
            } catch (e:Exception) {
                while (true) {
                    val uuid = UUID.randomUUID().toString()
                    if (!File(trueSaveDir, uuid).exists()) {
                        return uuid
                    }
                }
            }
        }
        class Ts(var url:String, val fileName:String, var seconds:Float, var key:String?, var iv:String?) : Comparable<Ts> {
            override fun toString(): String {
                return fileName + " (" + seconds + "sec)"
            }

            override fun compareTo(other: Ts): Int {
                return fileName.compareTo(other.fileName)
            }
        }
    }
}