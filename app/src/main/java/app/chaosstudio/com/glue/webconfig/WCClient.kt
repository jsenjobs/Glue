package app.chaosstudio.com.glue.webconfig

import android.graphics.Bitmap
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.*
import app.chaosstudio.com.glue.App
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.eventb.FragmentAction
import app.chaosstudio.com.glue.eventb.VideoAction
import app.chaosstudio.com.glue.eventb.WebViewProgress
import app.chaosstudio.com.glue.eventb.WebViewTitle
import app.chaosstudio.com.glue.greendb.gen.BookMarkDao
import app.chaosstudio.com.glue.greendb.gen.OpendedUrlDao
import app.chaosstudio.com.glue.greendb.model.OpendedUrl
import app.chaosstudio.com.glue.ui.EditAlert
import app.chaosstudio.com.glue.ui.SimpleAlert
import app.chaosstudio.com.glue.unit.js_native.MixUnit
import org.greenrobot.eventbus.EventBus

/**
 * Created by jsen on 2018/1/21.
 */

class WCClient(val webView: NWebView) : WebChromeClient() {


    override fun onReceivedTitle(view: WebView?, title: String?) {
        if (WebViewManager.getCurrentActive() == view && title != null)
            WebViewTitle.fire(title)

        if (App.instances.daoSession.bookMarkDao.queryBuilder().where(BookMarkDao.Properties.Url.eq(webView.url)).limit(1).count() > 0) {
            webView.isBookMark = true
            FragmentAction.fire(FragmentAction.ACTION.URL_MARKED, 1)
        } else {
            webView.isBookMark = false
            FragmentAction.fire(FragmentAction.ACTION.URL_MARKED, 0)
        }

        if (webView.crashClose) {
            val dS = App.instances.daoSession.opendedUrlDao
            val ds = dS.queryBuilder().where(OpendedUrlDao.Properties.Uuid.eq(webView.toString())).limit(1).list()
            if (ds != null && ds.size > 0) {
                val dao = ds[0]
                dao.domain = webView.url
                dS.update(dao)
            } else {
                val dao = OpendedUrl()
                dao.domain = webView.url
                dao.uuid  = webView.toString()
                dS.insert(dao)
            }
        }
    }

    override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
        super.onReceivedIcon(view, icon)
    }

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        if (WebViewManager.getCurrentActive() == view)
            EventBus.getDefault().post(WebViewProgress(newProgress))
    }

    override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
        callback!!.invoke(origin, true, false)
        super.onGeolocationPermissionsShowPrompt(origin, callback)
    }

    override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
        if (message == null) return super.onJsAlert(view, url, message, result)


        val build = SimpleAlert.Build(webView.context, R.style.SimpleAlert)
        build.showTitle = true
        build.title = webView.title
        build.content = message
        build.onPos = View.OnClickListener { _ ->
            result?.confirm()
        }
        val dialog = build.build()
        dialog.setOnDismissListener { _ ->
            if (!dialog.tag) result?.cancel()
        }
        dialog.show()
        return true
    }



    override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {
        val uri = Uri.parse(message)
        val f = MixUnit.filter(uri)
        if (!TextUtils.isEmpty(f)) {
            result?.confirm(f)
            return true
        }

        val build = EditAlert.Build(webView.context, R.style.SimpleAlert)
        build.showNag = true
        build.title = message?:"Prompt"
        build.content = defaultValue?:""
        if (defaultValue!=null) build.nag = defaultValue
        val dialog = build.build()
        dialog.setOnDismissListener { _ ->
            if (!dialog.tag) {
                result?.cancel()
            } else {
                result?.confirm(dialog.text)
            }
        }
        dialog.show()
        return true
    }

    override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
        if (message == null) return super.onJsAlert(view, url, message, result)
        val build = SimpleAlert.Build(webView.context, R.style.SimpleAlert)
        build.content = message
        val dialog = build.build()
        dialog.setOnDismissListener { _ ->
            if (!dialog.tag) {
                result?.cancel()
            } else {
                result?.confirm()
            }
        }
        dialog.show()
        return true
    }



    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        VideoAction.fire(VideoAction.ACTION.FULLSCREEN, view!!, callback!!)
        super.onShowCustomView(view, callback)
    }

    override fun onHideCustomView() {
        VideoAction.fire(VideoAction.ACTION.HIDDEN)
        super.onHideCustomView()
    }
}
