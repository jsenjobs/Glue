package app.chaosstudio.com.glue.webconfig

import android.Manifest
import android.app.Activity
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.support.v4.content.ContextCompat
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.unit.BrowserUnit
import app.chaosstudio.com.glue.utils.PermissionHelp

/**
 * Created by jsen on 2018/1/21.
 */
class WVConfig(val wvClient:WVClient, val wcClient:WCClient, val sp: SharedPreferences) {



    fun config(webView: NWebView) {
        initWebView(webView)
        initWebSettings(webView)
        initPreferences(webView)
    }

    @Synchronized private fun initWebView(w : NWebView) {
        w.drawingCacheBackgroundColor = 0x00000000
        w.isDrawingCacheEnabled = true
        w.setWillNotCacheDrawing(false)
        w.isSaveEnabled = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            w.background = null
            w.rootView.background = null
        }
        w.setBackgroundColor(ContextCompat.getColor(w.context, R.color.colorPrimary))

        w.isFocusable = true
        w.isFocusableInTouchMode = true
        w.isHorizontalScrollBarEnabled = false
        w.isVerticalScrollBarEnabled = false
        w.isScrollbarFadingEnabled = true


        w.webViewClient = wvClient
        w.webChromeClient = wcClient
        w.setDownloadListener(NDownloadListener(w))

    }

    var userAgentOriginal:String = BrowserUnit.UA_DESKTOP
    @Synchronized private fun initWebSettings(w : WebView) {
        val settings:WebSettings = w .settings
        userAgentOriginal = settings.userAgentString

        settings.allowContentAccess = true
        settings.allowFileAccess = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true
        }
        // settings.setSupportMultipleWindows(true)

        settings.setAppCacheEnabled(true)
        settings.setAppCachePath(w.context.cacheDir.toString())
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.databaseEnabled = true
        settings.domStorageEnabled = true

        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        // settings.defaultZoom = WebSettings.ZoomDensity.FAR
        //settings.setRenderPriority(WebSettings.RenderPriority.HIGH)

        settings.defaultTextEncodingName = BrowserUnit.URL_ENCODING
        settings.loadsImagesAutomatically = true

        settings.loadWithOverviewMode = true
        settings.textZoom = 100
        settings.useWideViewPort = true

        settings.fixedFontFamily = null
        settings.standardFontFamily = null
        settings.cursiveFontFamily = null
        settings.fantasyFontFamily = null
        settings.sansSerifFontFamily = null
        settings.serifFontFamily = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW;
        }
    }

    @Synchronized fun initPreferences(w : NWebView) {
        val settings = w.settings
        val context = w.context

        w.crashClose = sp.getBoolean(context.getString(R.string.sp_crash), false)

        settings.blockNetworkImage = !sp.getBoolean(context.getString(R.string.sp_images), true)
        settings.javaScriptEnabled = sp.getBoolean(context.getString(R.string.sp_javascript), true)
        settings.javaScriptCanOpenWindowsAutomatically = sp.getBoolean(context.getString(R.string.sp_javascript), true)

        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
            } catch (e: Exception) {
                Log.w("BGlue", "Error setting TEXT_AUTOSIZING")
            }
        }

        // UA
        val deskMode = sp.getBoolean(context.getString(R.string.sp_user_agent_desk_mode), false)
        if (deskMode) {
            settings.userAgentString = BrowserUnit.UA_DESKTOP
        } else {
            val userAgent = sp.getString(context.getString(R.string.sp_user_agent), "3").toInt()
            when (userAgent) {
                1 -> settings.userAgentString = BrowserUnit.UA_DESKTOP
                2 -> settings.userAgentString = sp.getString(context.getString(R.string.sp_user_agent_custom), userAgentOriginal)
                else -> settings.userAgentString = userAgentOriginal
            }
        }

        wvClient.enableAdBlock = sp.getBoolean(context.getString(R.string.sp_ad_block), true)

        val cm = CookieManager.getInstance()
        cm.setAcceptCookie(sp.getBoolean(context.getString(R.string.sp_cookies), true))

        if (sp.getBoolean(context.getString(R.string.sp_location), false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val hasAccess_FINE_LOCATION = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                if (hasAccess_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
                    if (context is Activity) {
                        PermissionHelp.grantPermissionsLoc(context)
                    }
                } else {
                    settings.setGeolocationEnabled(sp.getBoolean(context.getString(R.string.sp_location), true))
                }
            } else {
                settings.setGeolocationEnabled(sp.getBoolean(context.getString(R.string.sp_location), true))
            }
        }
    }


}