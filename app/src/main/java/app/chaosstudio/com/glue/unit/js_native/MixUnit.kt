package app.chaosstudio.com.glue.unit.js_native

import android.net.Uri
import android.os.Build
import android.util.Log
import app.chaosstudio.com.glue.App
import app.chaosstudio.com.glue.eventb.WebViewAction
import app.chaosstudio.com.glue.unit.BrowserUnit
import app.chaosstudio.com.glue.utils.OKManager
import app.chaosstudio.com.glue.webconfig.WebViewManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.net.URLDecoder

/**
 * Created by jsen on 2018/1/24.
 */

class MixUnit {
    companion object {
        fun HTTP_GET(action:String, url:String) {

            val request = Request.Builder().url(url).build()
            val call = OKManager.okHttpClient.newCall(request)
            call.enqueue(object : Callback {
                override fun onResponse(call: Call?, response: Response?) {
                    MixUnit._CALLBACK(action, response?.body()!!.string(), url)
                }

                override fun onFailure(call: Call?, e: IOException?) {
                    MixUnit._CALLBACK(action, "", url)
                }
            })
        }
        fun HTTP_GET_SOURCE(action: String):String {
            // HTTP_GET(action, sourceUrl)
            return WebViewManager.getCurrentActive().sourceData
        }
        fun OnSourceGet(action: String, content:String) {
            // Log.e("MARKSOURCE", content)
            WebViewManager.getCurrentActive().sourceData = content

        }
        fun SEARCH(url:String) {
            WebViewAction.fire(WebViewAction.ACTION.GO, url)
        }
        fun filter(uri:Uri):String {
            if (uri.scheme == "jjs") {
                when {
                    uri.authority == "HTTP_GET_SOURCE" -> { // 源码页发起获取源码请求
                        return MixUnit.HTTP_GET_SOURCE(uri.authority)
                    }
                    uri.authority == "HOMECUSTOMER" -> { // 主页获取定制信息
                        return BrowserUnit.getHomeCustomer(App.instances)
                    }
                    uri.authority == "ONSOURCEGET" -> { // 网页加载结束调用此方法返回网页源码
                        MixUnit.OnSourceGet(uri.authority, uri.fragment)
                        return "OK"
                    }
                    uri.authority == "SEARCH" -> {  // 主页发起搜索
                        if (uri.fragment != null) {
                            SEARCH(uri.fragment)
                            return "OK"
                        }
                        return ""
                    }
                    uri.authority == "HTTP_GET" -> {
                        if (uri.fragment != null) {
                            MixUnit.HTTP_GET(uri.authority, uri.fragment)
                            return "OK"
                        }
                        return ""
                    }
                    else -> return ""
                }
            }
            return ""
        }
        fun _CALLBACK(action:String, result:String, url: String) {
            val wv = WebViewManager.getCurrentActive()
            if (wv != null) {
                val json = JSONObject()
                json.put("action", action)
                json.put("data", result)
                json.put("url", url)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    wv.post({
                        wv.evaluateJavascript("javascript:nativeBack($json)") { }
                    })
                } else {
                    wv.post({
                        wv.loadUrl("javascript:nativeBack($json)")
                    })
                }
            }
        }
    }
}
