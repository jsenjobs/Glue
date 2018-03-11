package app.chaosstudio.com.glue.unit.js_native

import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import app.chaosstudio.com.glue.App
import app.chaosstudio.com.glue.eventb.VideoAction
import app.chaosstudio.com.glue.eventb.WebViewAction
import app.chaosstudio.com.glue.greendb.gen.PageSourceDao
import app.chaosstudio.com.glue.greendb.model.PageSource
import app.chaosstudio.com.glue.ui.ToastWithEdit
import app.chaosstudio.com.glue.unit.BrowserUnit
import app.chaosstudio.com.glue.utils.OKManager
import app.chaosstudio.com.glue.webconfig.WebViewManager
import com.alibaba.fastjson.JSONArray
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.apache.commons.lang.StringEscapeUtils
import org.json.JSONObject
import java.io.IOException
import java.net.URI
import java.net.URLDecoder
import java.util.regex.Matcher
import java.util.regex.Pattern





/**
 * Created by jsen on 2018/1/24.
 *
 */

class MixUnit {
    companion object {
        var toastWithEdit:ToastWithEdit? = null
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
        fun HTTP_GET_SOURCE(id:String):String {
            // return WebViewManager.getCurrentActive().sourceData
            return try {
                val lid = id.toLong()
                val item = App.instances.daoSession.pageSourceDao.load(lid)
                item.source
            } catch (e:Exception) {
                "EMPTY"
            }
        }
        fun HTTP_GET_IMAGE_LIST(id:String):String {
            return try {
                val lid = id.toLong()
                val item = App.instances.daoSession.pageSourceDao.load(lid)
                getImgUrls(item.url, item.source)
            } catch (e:Exception) {
                "[]"
            }
        }
        fun BEFOREVIEWSOURCE(content:String) {
            val cc = URLDecoder.decode(content, "utf-8") // 中文乱码问题
            val ccc = StringEscapeUtils.unescapeHtml(cc) // html转义问题
            val pageSource = PageSource()
            pageSource.url = WebViewManager.getCurrentActive().url
            pageSource.source = ccc
            App.instances.daoSession.pageSourceDao.save(pageSource)
            // WebViewManager.getCurrentActive().sourceData = ccc
            if (toastWithEdit != null && !toastWithEdit!!.isCancel) {
                WebViewAction.fire(WebViewAction.ACTION.GO, "file:///android_asset/plugin_source/code_viewer.html?url=" + pageSource.id)
                if (toastWithEdit!!.isShowing) toastWithEdit!!.dismiss()
            }
            toastWithEdit = null
        }
        fun BEFORELISTIMAGE(content:String) {
            val cc = URLDecoder.decode(content, "utf-8") // 中文乱码问题
            val ccc = StringEscapeUtils.unescapeHtml(cc) // html转义问题
            val pageSource = PageSource()
            pageSource.url = WebViewManager.getCurrentActive().url
            pageSource.source = ccc
            App.instances.daoSession.pageSourceDao.save(pageSource)
            // WebViewManager.getCurrentActive().sourceData = ccc
            if (toastWithEdit != null && !toastWithEdit!!.isCancel) {
                WebViewAction.fire(WebViewAction.ACTION.GO, "file:///android_asset/picture_viewer.html?url=" + pageSource.id)
                if (toastWithEdit!!.isShowing) toastWithEdit!!.dismiss()
            }
        }

        fun SEARCH(url:String) {
            WebViewAction.fire(WebViewAction.ACTION.GO, url)
        }
        fun filter(uri:Uri):String? {
            if (uri.scheme == "jjs") {
                when {
                    uri.authority == "BEFOREVIEWSOURCE" -> { // 网页加载结束调用此方法返回网页源码
                        MixUnit.BEFOREVIEWSOURCE(uri.fragment)
                        return "OK"
                    }
                    uri.authority == "HTTP_GET_SOURCE" -> { // 源码页发起获取源码请求 code_viewer
                        return MixUnit.HTTP_GET_SOURCE(uri.fragment)
                    }
                    uri.authority == "BEFORELISTIMAGE" -> { // 网页加载结束调用此方法返回网页源码
                        MixUnit.BEFORELISTIMAGE(uri.fragment)
                        return "OK"
                    }
                    uri.authority == "HTTP_GET_IMAGE_LIST" -> { // 分析源码获取所有图片url
                        return HTTP_GET_IMAGE_LIST(uri.fragment)
                    }
                    uri.authority == "INJECTFULLSCREEN" -> {
                        VideoAction.fire(VideoAction.ACTION.FULLSCREEN_INJECT)
                        return "OK"
                    }
                    uri.authority == "HOMECUSTOMER" -> { // 主页获取定制信息
                        return BrowserUnit.getHomeCustomer(App.instances)
                    }
                    uri.authority == "SEARCH" -> {  // 主页发起搜索
                        if (uri.fragment != null) {
                            SEARCH(uri.fragment)
                            return "OK"
                        }
                        return null
                    }
                    uri.authority == "HTTP_GET" -> {
                        if (uri.fragment != null) {
                            MixUnit.HTTP_GET(uri.authority, uri.fragment)
                            return "OK"
                        }
                        return null
                    }
                    else -> return null
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
                    wv.post({
                        wv.evaluateJavascript("javascript:nativeBack($json)") { }
                    })
            }
        }

        private fun getImgUrls(urlBase:String, htmlStr: String): String {
            val base = URI(urlBase)
            val pics = JSONArray()
            var img: String
            val p_image: Pattern
            val m_image: Matcher
            //     String regEx_img = "<img.*src=(.*?)[^>]*?>"; //图片链接地址
            val regEx_img = "<img.+src\\s*=\\s*(.*?)[^>]*?>"
            p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE)
            m_image = p_image.matcher(htmlStr)
            while (m_image.find()) {
                // 得到<img />数据
                img = m_image.group()
                // 匹配<img>中的src数据
                val m = Pattern.compile("<img.+?src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img)
                while (m.find()) {
                    try {
                        val src = base.resolve(m.group(1)).toString()
                        if (!pics.contains(src)) {
                            pics.add(base.resolve(m.group(1)).toString())
                        }
                    } catch (e:Exception) {
                        e.printStackTrace()
                        pics.add(m.group(1))
                    }
                }
            }
            return pics.toJSONString()
        }
    }
}
