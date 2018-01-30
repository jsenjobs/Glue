package app.chaosstudio.com.glue.webconfig

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.widget.FrameLayout

/**
 * Created by jsen on 2018/1/26.
 */

class FullscreenHolder(context: Context) : FrameLayout(context) {
    init {
        this.setBackgroundColor(ContextCompat.getColor(context, android.R.color.black))
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return true
    }
}
