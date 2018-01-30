package app.chaosstudio.com.glue.eventb

import org.greenrobot.eventbus.EventBus

/**
 * Created by jsen on 2018/1/21.
 */

class ActionBarAction(val action: ACTION) {
    enum class ACTION {
        PAGESCHANGED
    }
    var pages:Int = 1
    companion object {
        fun fire(action: ACTION, pages: Int) {
            val ac = ActionBarAction(action)
            ac.pages = pages
            EventBus.getDefault().post(ac)
        }
    }
}