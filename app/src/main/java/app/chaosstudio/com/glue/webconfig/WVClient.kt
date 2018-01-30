package app.chaosstudio.com.glue.webconfig

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Message
import android.support.annotation.RequiresApi
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.Toast
import app.chaosstudio.com.glue.App
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.eventb.FragmentAction
import app.chaosstudio.com.glue.eventb.WebViewTitle
import app.chaosstudio.com.glue.greendb.gen.BookMarkDao
import app.chaosstudio.com.glue.greendb.gen.HistoryDao
import app.chaosstudio.com.glue.greendb.gen.OpendedUrlDao
import app.chaosstudio.com.glue.greendb.model.History
import app.chaosstudio.com.glue.greendb.model.OpendedUrl
import app.chaosstudio.com.glue.ui.EditAlertLogin
import app.chaosstudio.com.glue.ui.SimpleAlert
import app.chaosstudio.com.glue.ui.SimpleToast
import app.chaosstudio.com.glue.ui.ToastWithEdit
import app.chaosstudio.com.glue.unit.BrowserUnit
import app.chaosstudio.com.glue.utils.OKManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import java.io.ByteArrayInputStream
import java.io.IOException
import java.net.URISyntaxException
import java.net.URL

/**
 * Created by jsen on 2018/1/21.
 */

class WVClient(val webView: NWebView) : WebViewClient() {

    private val context:Context = webView.context
    private val adBlock:AdBlock = webView.adBlock!!

    var isWhite:Boolean = true
    var enableAdBlock:Boolean = true

    val api = "https://v.pinpaibao.com.cn/cert/site/?site="
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        webView.logs.clear(webView.uuid)
        val urlL = URL(url)
        if (urlL.protocol != "file") {
            if(urlL.protocol == "https") {
                FragmentAction.fire(FragmentAction.ACTION.URL_SECURITY, 1)
                webView.isSec = true
            } else {
                FragmentAction.fire(FragmentAction.ACTION.URL_SECURITY, 0)
                webView.isSec = false
            }
            val domain = urlL.host
            val request = Request.Builder().url(api + domain).build()
            val call = OKManager.okHttpClient.newCall(request)
            call.enqueue(object : Callback {
                override fun onResponse(call: Call?, response: Response?) {
                    if (response?.body()!!.string()!!.contains("安全联盟企业信誉评级证书")) {
                        // System.out.println("可信")
                        FragmentAction.fire(FragmentAction.ACTION.URL_BELIEVE, 1)
                        webView.isBel = 1
                    } else {
                        // System.out.println("不可信")
                        FragmentAction.fire(FragmentAction.ACTION.URL_BELIEVE, 0)
                        webView.isBel = 0
                    }
                }

                override fun onFailure(call: Call?, e: IOException?) {
                    FragmentAction.fire(FragmentAction.ACTION.URL_BELIEVE, 0)
                    webView.isBel = 0
                }
            })
        } else {
            FragmentAction.fire(FragmentAction.ACTION.URL_SECURITY, 1)
            webView.isSec = true
            FragmentAction.fire(FragmentAction.ACTION.URL_BELIEVE, -1)
            webView.isBel = -1
        }
        if (App.instances.daoSession.bookMarkDao.queryBuilder().where(BookMarkDao.Properties.Url.eq(url)).limit(1).count() > 0) {
            webView.isBookMark = true
            FragmentAction.fire(FragmentAction.ACTION.URL_MARKED, 1)
        } else {
            webView.isBookMark = false
            FragmentAction.fire(FragmentAction.ACTION.URL_MARKED, 0)
        }

        if (!TextUtils.isEmpty(view!!.title)) {
            if (WebViewManager.getCurrentActive() == view) {
                WebViewTitle.fire(view.title)
            }
        }
    }

    val css2="javascript: (function nnight() {\n" +
            "  \n" +
            "    css = document.createElement('link');\n" +
            "    css.id = 'xxx_browser_2014';\n" +
            "    css.rel = 'stylesheet';\n" +
            "    css.href = 'data:text/css,html,body,applet,object,h1,h2,h3,h4,h5,h6,blockquote,pre,abbr,acronym,address,big,cite,code,del,dfn,em,font,img,ins,kbd,q,p,s,samp,small,strike,strong,sub,sup,tt,var,b,u,i,center,dl,dt,dd,ol,ul,li,fieldset,form,label,legend,table,caption,tbody,tfoot,thead,th,td{background:rgba(0,0,0,0) !important;color:#fff !important;border-color:#A0A0A0 !important;}div,input,button,textarea,select,option,optgroup{background-color:#000 !important;color:#fff !important;border-color:#A0A0A0 !important;}a,a *{color:#ffffff !important; text-decoration:none !important;font-weight:bold !important;background-color:rgba(0,0,0,0) !important;}a:active,a:hover,a:active *,a:hover *{color:#1F72D0 !important;background-color:rgba(0,0,0,0) !important;}p,span{font color:#FF0000 !important;color:#ffffff !important;background-color:rgba(0,0,0,0) !important;}html{-webkit-filter: contrast(50%);}';\n" +
            "    document.getElementsByTagName('body')[0].appendChild(css);\n" +
            "  \n" +
            "})();"
    val css="javascript: (function() {\n" +
            "  \n" +
            "    var css = document.createElement('link');\n" +
            "    css.rel = 'stylesheet';\n" +
            "    css.href = 'data:text/css,body{}';\n" +
            "    document.getElementsByTagName('head')[0].appendChild(css);\n" +
            "  \n" +
            "})();"
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)

        if (WebViewManager.isIsNight()) {
				view?.loadUrl(css)
        }
        // view?.loadUrl("javascript:prompt('jjs://ONSOURCEGET#' + " +"encodeURIComponent('<head>'+" + "document.getElementsByTagName('html')[0].innerHTML+'</head>'));")
        view?.loadUrl("javascript:var s = prompt('jjs://ONSOURCEGET#' + " +"'<html>'+" + "document.getElementsByTagName('html')[0].innerHTML+'</html>');")

        if (!TextUtils.isEmpty(view!!.title)) {
            if (WebViewManager.getCurrentActive() == view) {
                WebViewTitle.fire(view.title)
            }
        }

        // 添加历史记录
        if (!WebViewManager.isNoHis()) {
            val hisDao = App.instances.daoSession.historyDao
            val hiss = hisDao.queryBuilder().where(HistoryDao.Properties.Url.eq(webView.url)).limit(1).list()
            if (hiss != null && hiss.size > 0) {
                val his:History = hiss[0]
                his.date = System.currentTimeMillis()
                his.name = webView.title
                hisDao.update(his)
            } else {
                val his = History()
                his.name = webView.title
                his.url = webView.url
                his.date = System.currentTimeMillis()
                hisDao.insert(his)
            }
        }
    }



    @SuppressWarnings("deprecation")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        // if (!webView.pageFirst) webView.checkUI = false

        EventBus.getDefault().post(WebViewTitle(url!!))
        return handleUri(view!!, Uri.parse(url))
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        // if (!webView.pageFirst) webView.checkUI = false
        EventBus.getDefault().post(WebViewTitle(request?.url.toString()))
        // return super.shouldOverrideUrlLoading(view, request)
        // return false
        return handleUri(view!!, request!!.url)
    }

    fun handleUri(w: WebView, uri: Uri) : Boolean {
        val url = uri.toString()
        isWhite = adBlock.isWhite(url)

        if (url.startsWith("http")) return false

        val packageManager : PackageManager = context.packageManager
        val browserIntent = Intent(Intent.ACTION_VIEW).setData(uri)
        if (browserIntent.resolveActivity(packageManager) != null) {
            // appmarket://details?id=com.baidu.haokan
            val build = ToastWithEdit.Build(webView.context, R.style.SimpleAlert)
            build.pos = "打开"
            build.message = "请求打开系统应用，点击打开"
            build.listener = View.OnClickListener { _ ->
                context.startActivity(browserIntent)
                (context as Activity?)?.overridePendingTransition(0, 0)
            }
            build.build().show()
            webView.intentTask = true
            webView.taskTag = System.currentTimeMillis()
            return true
        }
        if (url.startsWith("intent:")) {
            try {
                val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                if (intent.resolveActivity(packageManager) != null) {
                    try {
                        val build = ToastWithEdit.Build(webView.context, R.style.SimpleAlert)
                        build.pos = "打开"
                        build.message = "请求打开系统应用，点击打开"
                        build.listener = View.OnClickListener { _ ->
                            context.startActivity(intent)
                            (context as Activity?)?.overridePendingTransition(0, 0)
                        }
                        build.build().show()
                        webView.intentTask = true
                        webView.taskTag = System.currentTimeMillis()
                    } catch (e:Exception) {
                        SimpleToast.makeToast(context, "Can not load url", Toast.LENGTH_SHORT).show()
                    }
                    return true
                }

                val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                if (fallbackUrl != null) {
                    webView.loadUrl(fallbackUrl)
                    return true
                }
                val marketIntent = Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=" + intent.`package`!!))
                if (marketIntent.resolveActivity(packageManager) != null) {
                    val build = ToastWithEdit.Build(webView.context, R.style.SimpleAlert)
                    build.pos = "打开"
                    build.message = "请求打开系统应用，点击打开"
                    build.listener = View.OnClickListener { _ ->
                        context.startActivity(marketIntent)
                        (context as Activity?)?.overridePendingTransition(0, 0)
                    }
                    build.build().show()
                    webView.intentTask = true
                    webView.taskTag = System.currentTimeMillis()
                    return true
                }
            } catch (e : URISyntaxException) {
            }
        }
        return true
    }

    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
        if (enableAdBlock && ((!isWhite && adBlock.isAd(url)) || adBlock.isBlack(url))) {
            return WebResourceResponse(BrowserUnit.MIME_TYPE_TEXT_PLAIN, BrowserUnit.URL_ENCODING, ByteArrayInputStream("".toByteArray()))
        } else {
        }
        return super.shouldInterceptRequest(view, url)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        val url = request!!.url.toString()
        // || "https://gss0.bdstatic.com/5bd1bjqh_Q23odCf/static/wiseindex/js/package/newsActivity_f6d3b0f.js" == request!!.url.toString()
        if (enableAdBlock && ((!isWhite && adBlock.isAd(request!!.url.toString())) || adBlock.isBlack(url))) {
            webView.logs.addLog(webView.uuid, url, false)
            return WebResourceResponse(BrowserUnit.MIME_TYPE_TEXT_PLAIN, BrowserUnit.URL_ENCODING, ByteArrayInputStream("".toByteArray()))
        } else {
            webView.logs.addLog(webView.uuid, url, true)
        }
        return super.shouldInterceptRequest(view, request)
    }

    override fun onFormResubmission(view: WebView?, dontResend: Message?, resend: Message?) {
        if (context !is Activity) {
            return
        }

        val build = SimpleAlert.Build(context, R.style.SimpleAlert)
        build.showTitle = true
        build.title = "表单恢复"
        build.content = "要恢复表单数据吗？"
        build.pos = "恢复"
        build.onPos = View.OnClickListener { _ ->
            resend?.sendToTarget()
        }
        build.onNag = View.OnClickListener { _ ->
            dontResend?.sendToTarget()
        }
        build.build().show()
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        if (context !is Activity) {
            return
        }

        val build = SimpleAlert.Build(context, R.style.SimpleAlert)
        build.content = "The certificate of the site is not trusted. Proceed anyway？"
        build.pos = "继续"
        build.onPos = View.OnClickListener { _ ->
            handler?.proceed()
        }
        build.onNag = View.OnClickListener { _ ->
            handler?.cancel()
        }
        build.build().show()
    }

    override fun onReceivedHttpAuthRequest(view: WebView?, handler: HttpAuthHandler?, host: String?, realm: String?) {
        if (context !is Activity) {
            return
        }

        val build = EditAlertLogin.Build(context, R.style.SimpleAlert)
        build.onClick = object : EditAlertLogin.OnClick{
            override fun pos(username: String, password: String) {
                handler?.proceed(username, password)
            }

            override fun nag() {
                handler?.cancel()
            }

        }
    }

    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
        // view?.loadUrl(BrowserUnit.getHome(webView.context))
        super.onReceivedError(view, errorCode, description, failingUrl)
    }

    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        // view?.loadUrl(BrowserUnit.getHome(webView.context))
        super.onReceivedError(view, request, error)
    }
}