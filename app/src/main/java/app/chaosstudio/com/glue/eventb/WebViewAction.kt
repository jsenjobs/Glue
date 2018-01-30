package app.chaosstudio.com.glue.eventb

import org.greenrobot.eventbus.EventBus

/**
 * Created by jsen on 2018/1/21.
 */

class WebViewAction(val action: ACTION) {
    enum class ACTION {
        GOBACK,
        GOFORWARD,
        REFRESH,
        GO,
        CHANGEPAGE,
        AFTERCHANGEPAGE,
        CREATEPAGE, // 创建新页面
        CREATEPAGEBACK, // 创建新页面
        CLOSEPAGE,
        CLOSEPAGES,
        ONLONGCLICK
    }
    var url:String = ""
    var page:Int = -1


    companion object {
        fun fire(action: ACTION) {
            EventBus.getDefault().post(WebViewAction(action))
        }
        fun fire(action: ACTION, url:String) {
            val w = WebViewAction(action)
            w.url = url
            EventBus.getDefault().post(w)
        }
        fun fire(action: ACTION, page:Int) {
            val w = WebViewAction(action)
            w.page = page
            EventBus.getDefault().post(w)
        }
    }
}
