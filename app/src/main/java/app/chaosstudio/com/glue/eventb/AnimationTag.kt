package app.chaosstudio.com.glue.eventb

/**
 * Created by jsen on 2018/1/21.
 */

class AnimationTag(val action: ACTION) {
    enum class ACTION {
        HIDDEN,
        SHOW
    }
}