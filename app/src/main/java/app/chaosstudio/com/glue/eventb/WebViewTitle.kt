package app.chaosstudio.com.glue.eventb

import org.greenrobot.eventbus.EventBus

/**
 * Created by jsen on 2018/1/21.
 */

class WebViewTitle(val title: String) {
    companion object {
        fun fire(title: String) {
            EventBus.getDefault().post(WebViewTitle(title))
        }
    }
}
