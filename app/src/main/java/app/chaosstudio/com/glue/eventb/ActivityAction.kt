package app.chaosstudio.com.glue.eventb

import org.greenrobot.eventbus.EventBus

/**
 * Created by jsen on 2018/1/21.
 */

class ActivityAction(val action: ACTION) {
    enum class ACTION {
        Set,
    }

    companion object {
        fun fire(action:ACTION) {
            EventBus.getDefault().post(ActivityAction(action))
        }
    }
}
