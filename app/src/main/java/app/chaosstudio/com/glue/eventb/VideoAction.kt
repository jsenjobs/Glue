package app.chaosstudio.com.glue.eventb

import android.view.View
import android.webkit.WebChromeClient
import org.greenrobot.eventbus.EventBus

/**
 * Created by jsen on 2018/1/26.
 */

class VideoAction(val action: ACTION) {
    enum class ACTION {
        FULLSCREEN,
        HIDDEN
    }
    var view:View? = null
    var callback:WebChromeClient.CustomViewCallback? = null
    companion object {
        fun fire(action: ACTION, view: View, callback:WebChromeClient.CustomViewCallback) {
            val ac = VideoAction(action)
            ac.view = view
            ac.callback = callback
            EventBus.getDefault().post(ac)
        }
        fun fire(action: ACTION) {
            val ac = VideoAction(action)
            EventBus.getDefault().post(ac)
        }
    }
}