package app.chaosstudio.com.glue.webconfig

import android.util.Log
import android.view.View
import android.webkit.DownloadListener
import android.webkit.URLUtil
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.ui.AlertDownload
import app.chaosstudio.com.glue.unit.BrowserUnit

/**
 * Created by jsen on 2018/1/21.
 */

class NDownloadListener(val w: NWebView) : DownloadListener {


    var dA:AlertDownload? = null
    override fun onDownloadStart(url: String, userAgent: String, contentDisposition: String, mimetype: String, contentLength: Long) {
        if (w.intentTask && System.currentTimeMillis() - w.taskTag <= 2000) {
            w.intentTask = false
            return
        }

        val build =  AlertDownload.Build(w.context, R.style.SimpleAlert)
        build.url = url
        build.name = URLUtil.guessFileName(url, contentDisposition, mimetype)
        build.size = "文件大小：" + getSize(contentLength)
        build.onPos = View.OnClickListener { _ ->
            BrowserUnit.download(w.context, url, contentDisposition, mimetype)
        }

        dA?.dismiss()
        dA = build.build()
        dA?.show()
    }

    fun getSize(leng:Long):String {
        return if (leng >= 1024) {
            if (leng >= 1024 * 1024) {
                if (leng >= 1024 * 1024 * 1024) {
                    String.format("%.2f", leng.toFloat() / (1024f * 1024f * 1024f)) + "GB"
                } else {
                    String.format("%.2f", leng.toFloat() / (1024f * 1024f)) + "MB"
                }
            } else {
                String.format("%.2f", leng.toFloat() / 1024f) + "KB"
            }
        } else {
            leng.toString() + "B"
        }
    }
}
