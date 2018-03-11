package app.chaosstudio.com.glue.ok

import android.net.Uri
import app.chaosstudio.com.glue.utils.OKManager
import app.chaosstudio.com.glue.video.M3U8
import app.chaosstudio.com.glue.video.sec.MoreAES
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.*
import java.util.*

/**
 * Created by jsen on 2018/1/31.
 */
class M3U8SaveFileCallBack(val m3U8: M3U8, val ts:M3U8.Companion.Ts, val cachePath:String, val targetFile:String):Callback {
    override fun onFailure(call: Call?, e: IOException?) {
        System.out.println("Failed download ts file - 1:" + ts.url)
    }

    override fun onResponse(call: Call?, response: Response?) {
        val inputStream = response?.body()?.byteStream()
        if (inputStream == null) {
            System.out.println("Failed download ts file - 1:" + ts.url)
            return
        }
        System.out.println(m3U8.tsList.size.toString() + "  " + m3U8.totalLeft)
        if (response.isSuccessful) {

            if (ts.key != null && ts.key != "") {
                val aes = MoreAES.getInstances()
                if (ts.iv != null && ts.iv != "") {
                    aes.decrypt(inputStream, getFileName(ts.url, cachePath), ts.key!!.toByteArray(), ts.iv!!.toByteArray())
                } else {
                    val raw = getBytesFromInputStream(inputStream)
                    if (raw == null) {
                        System.out.println("Failed download ts file - 1:" + ts.url)
                    } else {
                        val iv = ByteArray(16)
                        val data = ByteArray(raw.size - 16)
                        for (i in 0..15) {
                            iv[i] = raw[i]
                        }
                        for (i in 16..(raw.size-1)) {
                            data[i - 16] = raw[i]
                        }
                        aes.decrypt(data, getFileName(ts.url, cachePath), ts.key!!.toByteArray(), iv)
                    }
                }
            } else {
                val outputStream = FileOutputStream(getFileName(ts.url, cachePath))
                var len:Int
                val buf = ByteArray(1024)
                len = inputStream!!.read(buf)
                while (len != -1) {
                    outputStream.write(buf, 0, len)
                    len = inputStream.read(buf)
                }
                outputStream.close()
            }
            m3U8.totalLeft--
            if (m3U8.totalLeft == 0) {
                m3U8.endDownloadTime = System.currentTimeMillis()
                System.out.println(mergeTsFile())
                System.out.println(m3U8)
            }
        } else {
            val request = Request.Builder().url(ts.url).build()
            // System.out.println(t.url)
            // val request = Request.Builder().url("http://download.avzyk.com/20180110/zgn055zajur460mp4/1600kb/hls/1MlSbOY1929000.ts").build()
            val call = OKManager.okHttpClient.newCall(request)
            call.enqueue(M3U8SaveFileCallBack(m3U8, ts, cachePath, targetFile))
        }
        inputStream.close()

    }

    fun mergeTsFile():Boolean {
        if (m3U8.tsList.isEmpty()) {
            return false
        }
        if (m3U8.tsList.size == 1) {
            return File(getFileName(m3U8.tsList[0].url, cachePath)).renameTo(File(targetFile))
        }

        val resultFile = File(targetFile)

        try {
            val fs = FileOutputStream(resultFile, true)
            val resutFileChannel = fs.channel
            var fis:FileInputStream
            for (ts in m3U8.tsList) {
                fis = FileInputStream(getFileName(ts.url, cachePath))
                val blk = fis.channel
                resutFileChannel.transferFrom(blk, resutFileChannel.size(), blk.size())
                fis.close()
                blk.close()
            }
            fs.close()
            resutFileChannel.close()
        } catch (e:Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }


    /**
     * 获取密钥的byte数组
     * @param inputStream
     * @return
     */
    private fun getBytesFromInputStream(inputStream: InputStream): ByteArray? {
        var fis: FileInputStream? = null
        var baos: ByteArrayOutputStream? = null
        try {
            val b = ByteArray(1024)
            var len: Int = inputStream.read(b)
            baos = ByteArrayOutputStream()
            while (len != -1) {
                baos.write(b, 0, len)
                len = inputStream.read(b)
            }
            return baos.toByteArray()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (fis != null) {
                try {
                    fis.close()
                } catch (e: IOException) {
                }

            }

            if (baos != null) {
                try {
                    baos.flush()
                } catch (e: IOException) {
                }

                try {
                    baos.close()
                } catch (e: IOException) {
                }

            }
        }
        return null
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
}