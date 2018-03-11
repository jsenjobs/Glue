package app.chaosstudio.com.glue.m3u8test

import app.chaosstudio.com.glue.utils.OKManager
import app.chaosstudio.com.glue.video.M3U8
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.junit.Test
import java.io.*
import app.chaosstudio.com.glue.video.sec.MoreAES






/**
 * Created by jsen on 2018/1/31.
 */
class M3U8Test {

    @Test
    fun test1() {
        val m3u8File = M3U8.buildM3U8("http://download.avzyk.com/20180110/zgn055zajur460mp4/index.m3u8")
        // System.out.println(m3u8File?.toString()?:"null")
        if (m3u8File != null) {
            M3U8.download(m3u8File, "/Users/jsen/Downloads/m3u8/cache", "/Users/jsen/Downloads/m3u8/target.ts")
        }
        Thread.sleep(1000 * 60 * 60)
    }

    @Test fun downloadFile() {
        val request = Request.Builder().url("http://download.avzyk.com/20180110/zgn055zajur460mp4/1600kb/hls/1MlSbOY1929000.ts").build()
        OKManager.okHttpClient.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call?, e: IOException?) {

            }

            override fun onResponse(call: Call?, response: Response?) {
                val inputStream = response?.body()?.byteStream()
                System.out.println("http://download.avzyk.com/20180110/zgn055zajur460mp4/1600kb/hls/1MlSbOY1929000.ts" + response?.body()?.contentLength())
                val outputStream = FileOutputStream("/Users/jsen/Downloads/m3u8/tes33.ts")
                try {
                    var len:Int
                    val buf = ByteArray(1024)
                    len = inputStream!!.read(buf)
                    while (len != -1) {
                        outputStream.write(buf, 0, len)
                        len = inputStream.read(buf)
                    }
                } catch (e:Exception) {
                    e.printStackTrace()
                } finally {
                    outputStream.close()
                    inputStream?.close()
                }
            }

        })
        Thread.sleep(1000 * 60 )
    }

    @Test
    @Throws(Exception::class)
    fun decrypt() {

        val key = getBytesFromDat("/Users/jsen/Downloads/m3u8/key.key") ?: return
        val raw = getBytesFromDat("/Users/jsen/Downloads/m3u8/1MlSbOY1929000.ts") ?: return
        val iv = ByteArray(16)
        val data = ByteArray(raw.size - 16)
        for (i in 0..15) {
            System.out.println(i)
            iv[i] = raw[i]
        }
        for (i in 16..(raw.size-1)) {
            data[i - 16] = raw[i]
        }
        System.out.println(String(iv))
        System.out.println(raw.size)
        val aes = MoreAES.getInstances()
        aes.decrypt(data, "/Users/jsen/Downloads/m3u8/1MlSbOY1929000-out-java.ts", key,iv)
        /*

val result = decrypt(rawKey, enc)

val file = File("/Users/jsen/Downloads/m3u8/1MlSbOY1929000-out.ts")

val output = FileOutputStream(file)

val bufferedOutput = BufferedOutputStream(output)

bufferedOutput.write(result)
bufferedOutput.flush()
bufferedOutput.close()
output.close()
*/
    }

    /**
     * 获取密钥的byte数组
     * @param keyPath
     * @return
     */
    private fun getBytesFromDat(keyPath: String): ByteArray? {
        var fis: FileInputStream? = null
        var baos: ByteArrayOutputStream? = null
        try {
            fis = FileInputStream(keyPath)
            val b = ByteArray(1024)
            var len: Int = fis.read(b)
            baos = ByteArrayOutputStream()
            while (len != -1) {
                baos.write(b, 0, len)
                len = fis.read(b)
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
}