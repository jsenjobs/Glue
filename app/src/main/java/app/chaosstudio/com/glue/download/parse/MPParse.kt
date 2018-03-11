package app.chaosstudio.com.glue.download.parse

import app.chaosstudio.com.glue.GPre
import app.chaosstudio.com.glue.download.DownloadManager
import app.chaosstudio.com.glue.download.SimpleDownloadAndSaveCallBack
import app.chaosstudio.com.glue.eventb.DownloadAction
import app.chaosstudio.com.glue.utils.OKManager
import okhttp3.Request
import java.io.File

/**
 * Created by jsen on 2018/2/1.
 *
 * mp3 mp4
 */

class MPParse : ParseBase() {
    override fun isTaskExist(url: String):Boolean {
        return DownloadManager.downloadManager!!.calls.containsKey(url)
    }

    override fun stop(url: String) {
        DownloadManager.downloadManager!!.removeCall(url)
    }
    override fun parse(id:Long, url: String) {
        val trueSaveDir = checkDirectory(GPre.downloadDir)
        val trueFileName = getFileName(url, trueSaveDir)
        val skip = getSkip(File(trueSaveDir, trueFileName))
        val request = Request.Builder().addHeader("RANGE", "bytes=$skip-").url(url).build()
        val call = OKManager.okHttpClient.newCall(request)
        if (DownloadManager.downloadManager!!.addCall(url, call)) {
            DownloadAction.fire(id, DownloadAction.ACTION.ON_TOAST, "开始下载")
            DownloadAction.fire(id, DownloadAction.ACTION.ON_START)
            call.enqueue(SimpleDownloadAndSaveCallBack(id, url, trueSaveDir, trueFileName, skip))
        }
    }
}
