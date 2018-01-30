package app.chaosstudio.com.glue.eventb

import org.greenrobot.eventbus.EventBus

/**
 * Created by jsen on 2018/1/25.
 */

class ThemeAction {
    companion object {
        fun fire() {
            EventBus.getDefault().post(ThemeAction())
        }
    }
}
