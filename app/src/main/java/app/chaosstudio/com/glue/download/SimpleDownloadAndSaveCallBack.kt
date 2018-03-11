package app.chaosstudio.com.glue.download

import app.chaosstudio.com.glue.App

import app.chaosstudio.com.glue.eventb.DownloadAction
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.*

/**
 * Created by jsen on 2018/2/1.
 *
 */

class SimpleDownloadAndSaveCallBack(val id:Long, val url:String, val trueSaveDir:String, val trueFileName:String, val skip:Long) : Callback {

    override fun onFailure(call: Call, e: IOException) {
        DownloadManager.downloadManager!!.removeCall(url)
        DownloadAction.fire(id, DownloadAction.ACTION.ON_TOAST, "下载失败-onFailure")
        DownloadAction.fire(id, DownloadAction.ACTION.ON_FAIL)
    }

    @Throws(IOException::class)
    override fun onResponse(call: Call, response: Response) {
        if (!response.isSuccessful || response.body() == null) {
            DownloadManager.downloadManager!!.removeCall(url)
            DownloadAction.fire(id, DownloadAction.ACTION.ON_TOAST, "下载失败-onResponse")
            DownloadAction.fire(id, DownloadAction.ACTION.ON_FAIL)
            return
        }
        val inputStream = response.body()!!.byteStream()
        var outputStream:RandomAccessFile? = null

        try {
            val total = response.body()!!.contentLength() + skip
            val file = File(trueSaveDir, trueFileName)
            outputStream = RandomAccessFile(file, "rw")
            outputStream.seek(skip)

            val buf = ByteArray(2048)
            var len:Int = inputStream!!.read(buf)
            var read = 0L
            while (len != -1) {
                outputStream.write(buf, 0, len)
                read += len
                DownloadAction.fire(id, DownloadAction.ACTION.ON_PROGRESS, (((read + skip) * 100) / total).toInt())
                len = inputStream.read(buf)
            }
            val mod = App.instances.daoSession.downloadModeDao.load(id)
            if (mod != null) {
                mod.path = file.absolutePath
            }
            App.instances.daoSession.downloadModeDao.update(mod)
            DownloadManager.downloadManager!!.removeCall(url)
            DownloadAction.fire(id, DownloadAction.ACTION.ON_TOAST, "下载成功")
            DownloadAction.fire(id, DownloadAction.ACTION.ON_FINISHED)
            val mode = App.instances.daoSession.downloadModeDao.load(id)
            mode.isFinished = true
            App.instances.daoSession.downloadModeDao.update(mode)
        } catch (e:Exception) {
            e.printStackTrace()
            if(!call.isCanceled) {
                DownloadManager.downloadManager!!.removeCall(url)
                DownloadAction.fire(id, DownloadAction.ACTION.ON_TOAST, "下载失败-Exception")
                DownloadAction.fire(id, DownloadAction.ACTION.ON_FAIL)
            }
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }

    /*
        @Throws(IOException::class)
    override fun onResponse(call: Call, response: Response) {
        if (!response.isSuccessful || response.body() == null) {
            DownloadManager.downloadManager!!.stopTask(url)
            DownloadAction.fire(id, DownloadAction.ACTION.ON_TOAST, "下载失败-onResponse")
            DownloadAction.fire(id, DownloadAction.ACTION.ON_FAIL)
            return
        }
        val inputStream = response.body()!!.byteStream()
        var outputStream:OutputStream? = null

        try {
            val total = response.body()!!.contentLength()
            outputStream = FileOutputStream(File(trueSaveDir, trueFileName))

            val buf = ByteArray(2048)
            var len:Int = inputStream!!.read(buf)
            var read = 0L
            while (len != -1) {
                outputStream.write(buf, 0, len)
                read += len
                DownloadAction.fire(id, DownloadAction.ACTION.ON_PROGRESS, ((read * 100) / total).toInt())
                len = inputStream.read(buf)
            }
            outputStream.flush()
            DownloadManager.downloadManager!!.stopTask(url)
            DownloadAction.fire(id, DownloadAction.ACTION.ON_TOAST, "下载成功")
            DownloadAction.fire(id, DownloadAction.ACTION.ON_FINISHED)
            val mode = App.instances.daoSession.downloadModeDao.load(id)
            mode.isFinished = true
            App.instances.daoSession.downloadModeDao.update(mode)
        } catch (e:Exception) {
            e.printStackTrace()
            if(!call.isCanceled) {
                DownloadManager.downloadManager!!.stopTask(url)
                DownloadAction.fire(id, DownloadAction.ACTION.ON_TOAST, "下载失败-Exception")
                DownloadAction.fire(id, DownloadAction.ACTION.ON_FAIL)
            }
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }
    * */
}
