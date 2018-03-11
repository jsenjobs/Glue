package app.chaosstudio.com.glue.webconfig

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.MailTo
import android.os.Build
import android.preference.PreferenceManager
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.webkit.WebView
import android.widget.Toast
import app.chaosstudio.com.glue.GPre
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.eventb.FragmentAction
import app.chaosstudio.com.glue.ui.SimpleToast
import app.chaosstudio.com.glue.unit.BrowserUnit
import app.chaosstudio.com.glue.unit.IntentUnit
import app.chaosstudio.com.glue.unit.js_native.MixUnit
import app.chaosstudio.com.glue.utils.OKManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.URISyntaxException
import java.util.*

/**
 * Created by jsen on 2018/1/21.
 */

class NWebView : WebView {

    var sourceData = "" // 网页源码
    var crashClose = false // 是否需要恢复未关闭页面
    var imageGetBaseUrl = "" //获取图片的网址

    var intentTask = false
    var taskTag = 0L

    var isBookMark = false
    var isSec = false
    var isBel = 0

    var sp:SharedPreferences? = null

    var adBlock: AdBlock? = null
    var jsBlock: JsBlock? = null
    var wvClient:WVClient? = null
    var wcClient:WCClient? = null

    val logs = Logs()

    private var clickHandler: NClickHandler? = null
    private var gestureDetector: GestureDetector? = null


    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, privateBrowsing: Boolean) : super(context, attrs, defStyleAttr, privateBrowsing) {
        init(context)
    }

    var wvConfig:WVConfig? = null
    private fun init(context: Context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context)
        adBlock = AdBlock(context)
        jsBlock = JsBlock(context)
        wvClient = WVClient(this)
        wcClient = WCClient(this)
        wvConfig = WVConfig(wvClient!!, wcClient!!, sp!!)
        wvConfig!!.config(this)

        this.clickHandler = NClickHandler()
        this.gestureDetector = GestureDetector(context, NGestureListener(this))

        setOnTouchListener(touchLis)
        if (android.os.Build.VERSION_CODES.JELLY_BEAN <= Build.VERSION.SDK_INT)
            setFindListener(NFindListener())

        PluginFilter.init(context)
    }

    override fun findAll(find: String?): Int {
        val matches = super.findAll(find)
        FragmentAction.fire(FragmentAction.ACTION.ON_FIND_NUM, matches)
        return matches
    }

    // var pageFirst = true
    override fun loadUrl(oUrl: String?) {
        if (oUrl == null || oUrl.trim().isEmpty()) {
            SimpleToast.makeToast(context, "Can 't load this URL", Toast.LENGTH_LONG).show()
            return
        }

        val nUrl = BrowserUnit.queryWrapper(context, oUrl.trim())
        if (nUrl.startsWith(BrowserUnit.URL_SCHEME_MAIL_TO)) {
            val intent = IntentUnit.getEmailIntent(MailTo.parse(nUrl))
            context.startActivity(intent)
            reload()
            return
        } else if (nUrl.startsWith(BrowserUnit.URL_SCHEME_INTENT)) {
            try {
                val intent = Intent.parseUri(nUrl, Intent.URI_INTENT_SCHEME)
                context.startActivity(intent)
            } catch (e: URISyntaxException) {
                Log.w("glue", "Error parsing URL")
            }
            return
        }

        if (!sp!!.getBoolean(context.getString(R.string.sp_javascript), true)) {
            if (jsBlock!!.isWhite(nUrl)) {
                Log.w("glue", "isWhite")
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.javaScriptEnabled = true
            } else {
                Log.w("glue", "not isWhite")
                settings.javaScriptCanOpenWindowsAutomatically = false
                settings.javaScriptEnabled = false
            }
        }

        // wvClient!!.isWhite = adBlock!!.isWhite(nUrl)
        // pageFirst = false
        super.loadUrl(nUrl)
    }
    // var checkUI = true
    override fun reload() {
        // wvClient!!.isWhite = adBlock!!.isWhite(url)
        super.reload()
    }

    /*
    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if(checkUI && onScrollChangeListener != null) {
            if (Math.abs(t - oldt) < 500)
                onScrollChangeListener!!.onScrollChange(t, oldt)
        }
        checkUI = true
    }

    var onScrollChangeListener: OnScrollChangeListener? = null
    interface OnScrollChangeListener {
        /**
         * Called when the scroll position of a view changes.
         * @param scrollY    Current vertical scroll origin.
         * @param oldScrollY Previous vertical scroll origin.
         */
        fun onScrollChange(scrollY: Int, oldScrollY: Int)
    }
    */
    var onFling: OnFling? = null
    interface OnFling {
        fun up()
        fun down()
    }

    var startX:Float = 0f
    var endX:Float = 0f
    var minTranslate = 120f
    var rawX:Float = 0f
    var rawY:Float = 0f
    val touchLis = OnTouchListener { v, event ->
        gestureDetector?.onTouchEvent(event)
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                rawX = event.rawX
                rawY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                rawX = event.rawX
                rawY = event.rawY
            }
            MotionEvent.ACTION_UP -> {
                rawX = event.rawX
                rawY = event.rawY
                endX = event.x
                if (startX < 80 && endX - startX > minTranslate && canGoBack() && GPre.flingPage) {
                    goBack()
                } else if (measuredWidth - startX < 80 && startX - endX > minTranslate && canGoForward() && GPre.flingPage) {
                    goForward()
                }
            }
        }
        false
    }

    fun onLongPress() {
        val click = clickHandler?.obtainMessage()
        if (click != null) {
            click.target = clickHandler
        }
        requestFocusNodeHref(click)
    }

    var uuid = UUID.randomUUID().toString()
    override fun toString(): String {
        return uuid
    }
}
