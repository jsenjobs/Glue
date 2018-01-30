package app.chaosstudio.com.glue.eventb

import org.greenrobot.eventbus.EventBus

/**
 * Created by jsen on 2018/1/21.
 */

class FragmentAction(val action: ACTION) {
    enum class ACTION {
        FULLSCREEN,
        FULL_STATUS_CHANGE,
        URL_BELIEVE,
        URL_SECURITY,
        URL_MARKED,
        MENU_ITEM,
        SHOW_TOOLS,
        SEARCH,

        ON_FIND_NUM,

        FULL_MODE_CHANGE
    }
    var tag:Int = -1
    var disX:Float = 1f
    var disY:Float = 1f


    companion object {
        fun fire(action: ACTION) {
            EventBus.getDefault().post(FragmentAction(action))
        }
        fun fire(action: ACTION, tag:Int) {
            val w = FragmentAction(action)
            w.tag = tag
            EventBus.getDefault().post(w)
        }
        fun fire(action: ACTION, disX:Float, disY:Float) {
            val w = FragmentAction(action)
            w.disX = disX
            w.disY = disY
            EventBus.getDefault().post(w)
        }
    }
}