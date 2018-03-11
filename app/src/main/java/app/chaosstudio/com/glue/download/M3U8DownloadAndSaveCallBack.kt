package app.chaosstudio.com.glue.download

import android.text.TextUtils
import app.chaosstudio.com.glue.App
import app.chaosstudio.com.glue.eventb.DownloadAction
import app.chaosstudio.com.glue.video.M3U8
import app.chaosstudio.com.glue.video.sec.MoreAES

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream

/**
 * Created by jsen on 2018/2/1.
 *
 */

class M3U8DownloadAndSaveCallBack(val id:Long, val url:String, val trueSaveDir:String, val targetFileName:String, val m3U8: M3U8, val ts:M3U8.Companion.Ts) : Callback {

    override fun onFailure(call: Call, e: IOException) {
        m3U8.faildItem ++
        DownloadManager.downloadManager!!.removeCall(ts.url)
        onFinished()
    }

    @Throws(IOException::class)
    override fun onResponse(call: Call, response: Response) {
        if (!response.isSuccessful || response.body() == null) {
            m3U8.faildItem ++
            DownloadManager.downloadManager!!.removeCall(ts.url)
            onFinished()
            return
        }
        val inputStream = response.body()!!.byteStream()

        if (!TextUtils.isEmpty(ts.key)) {
            if (!TextUtils.isEmpty(ts.iv)) {


                var fis: CipherInputStream? = null
                var fos: FileOutputStream? = null
                try {
                    val deCipher = MoreAES.getCipher(Cipher.DECRYPT_MODE, ts.key!!.toByteArray(), ts.iv!!.toByteArray())

                    fis = CipherInputStream(inputStream, deCipher)
                    fos = FileOutputStream(File(trueSaveDir, ts.fileName))

                    val buf = ByteArray(2048)
                    var len = fis.read(buf)
                    while (len != -1) {
                        fos.write(buf, 0, len)
                        len = fis.read(buf)
                    }
                    fos.flush()
                    m3U8.totalLeft--
                    DownloadManager.downloadManager!!.removeCall(ts.url)
                    onFinished()
                } catch (e:Exception) {
                    e.printStackTrace()
                    if(!call.isCanceled) {
                        m3U8.faildItem ++
                        File(trueSaveDir, ts.fileName).delete()
                        DownloadManager.downloadManager!!.removeCall(ts.url)
                        onFinished()
                    }
                } finally {
                    inputStream?.close()
                    fis?.close()
                    fos?.close()
                }

            } else {

                var baos: ByteArrayOutputStream? = null
                var fis: CipherInputStream? = null
                var fos: FileOutputStream? = null
                try {
                    baos = ByteArrayOutputStream()

                    val buf = ByteArray(2048)
                    var len = inputStream.read(buf)
                    while (len != -1) {
                        baos.write(buf, 0, len)
                        len = inputStream.read(buf)

                    }
                    baos.flush()
                    val raw = baos.toByteArray()

                    val iv = ByteArray(16)
                    val data = ByteArray(raw.size - 16)
                    for (i in 0..15) {
                        iv[i] = raw[i]
                    }
                    for (i in 16..(raw.size-1)) {
                        data[i - 16] = raw[i]
                    }



                    val deCipher = MoreAES.getCipher(Cipher.DECRYPT_MODE, ts.key!!.toByteArray(), iv) ?: return
                    fis = CipherInputStream(ByteArrayInputStream(data), deCipher)
                    fos = FileOutputStream(File(trueSaveDir, ts.fileName))
                    len = fis.read(buf)
                    while (len > 0) {
                        fos.write(buf, 0, len)
                        len = fis.read(buf)
                    }
                    fos.flush()
                    m3U8.totalLeft--
                    DownloadManager.downloadManager!!.removeCall(ts.url)
                    onFinished()
                } catch (e:Exception) {
                    e.printStackTrace()
                    if(!call.isCanceled) {
                        m3U8.faildItem ++
                        File(trueSaveDir, ts.fileName).delete()
                        DownloadManager.downloadManager!!.removeCall(ts.url)
                        onFinished()
                    }
                } finally {
                    inputStream?.close()
                    baos?.close()
                    fis?.close()
                    fos?.close()
                }

            }
        } else {
            var outputStream: FileOutputStream? = null
            try {
                outputStream = FileOutputStream(File(trueSaveDir, ts.fileName))

                val buf = ByteArray(2048)
                var len:Int = inputStream!!.read(buf)
                while (len != -1) {
                    outputStream.write(buf, 0, len)
                    len = inputStream.read(buf)
                }
                outputStream.flush()
                m3U8.totalLeft--
                DownloadManager.downloadManager!!.removeCall(ts.url)
                onFinished()
            } catch (e:Exception) {
                e.printStackTrace()
                if(!call.isCanceled) {
                    m3U8.faildItem ++
                    File(trueSaveDir, ts.fileName).delete()
                    DownloadManager.downloadManager!!.removeCall(ts.url)
                    onFinished()
                }
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        }
    }

    fun onFinished() {
        DownloadAction.fire(id, DownloadAction.ACTION.ON_PROGRESS, (((m3U8.totalItem - m3U8.totalLeft) * 100) / m3U8.totalItem))
        if (m3U8.totalLeft == 0) {
            DownloadManager.downloadManager!!.stopTask(url)

            m3U8.endDownloadTime = System.currentTimeMillis()
            if (mergeTsFile()) {
                DownloadAction.fire(id, DownloadAction.ACTION.ON_TOAST, "下载成功")
                DownloadAction.fire(id, DownloadAction.ACTION.ON_FINISHED)
                val mode = App.instances.daoSession.downloadModeDao.load(id)
                mode.isFinished = true
                App.instances.daoSession.downloadModeDao.update(mode)
            }
        } else if (m3U8.totalLeft == m3U8.faildItem) {
            DownloadManager.downloadManager!!.stopTask(url)
            DownloadAction.fire(id, DownloadAction.ACTION.ON_TOAST, "下载失败")
            DownloadAction.fire(id, DownloadAction.ACTION.ON_FAIL)
        }
    }

    fun mergeTsFile():Boolean {
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

        val resultFile = File(File(trueSaveDir).parentFile.absolutePath, targetFileName)

        try {
            val fs = FileOutputStream(resultFile, true)
            val resultFileChannel = fs.channel
            var tsFile:File
            var fis: FileInputStream
            for (ts in m3U8.tsList) {
                tsFile = File(trueSaveDir, ts.fileName)
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
