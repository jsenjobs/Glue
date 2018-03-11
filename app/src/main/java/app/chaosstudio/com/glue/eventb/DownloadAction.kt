package app.chaosstudio.com.glue.eventb

import org.greenrobot.eventbus.EventBus

/**
 * Created by jsen on 2018/2/1.
 */

class DownloadAction(val id:Long, val action: ACTION) {
    enum class ACTION {
        ON_START,
        ON_PROGRESS,
        ON_FAIL,
        ON_FINISHED,

        ON_TOAST
    }
    var progress = 0
    var message:String = ""

    companion object {
        fun fire(id:Long, action: ACTION, progress:Int) {
            val d = DownloadAction(id, action)
            d.progress = progress
            EventBus.getDefault().post(d)
        }
        fun fire(id:Long, action: ACTION, message:String) {
            val d = DownloadAction(id, action)
            d.message = message
            EventBus.getDefault().post(d)
        }
        fun fire(id:Long, action: ACTION) {
            val d = DownloadAction(id, action)
            EventBus.getDefault().post(d)
        }
    }
}
