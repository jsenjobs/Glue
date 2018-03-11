package app.chaosstudio.com.glue.webconfig

import android.view.GestureDetector
import android.view.MotionEvent


class NGestureListener(private val webView: NWebView) : GestureDetector.SimpleOnGestureListener() {
    private var longPress = true

    override fun onLongPress(e: MotionEvent) {
        if (longPress) {
            webView.onLongPress()
        }
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        longPress = false
        return false
    }

    override fun onShowPress(e: MotionEvent) {
        longPress = true
    }


    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        val disX = e1.x - e2.x
        val disY = e1.y - e2.y
        if (Math.abs(disX) > Math.abs(disY) * 1.2) {
            // check x
            if (Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                if (disX > FLING_MIN_DISTANCE) {
                    // left
                } else if (-disX > FLING_MIN_DISTANCE) {
                    // right
                }
            }
        } else {
            // check y
            if (Math.abs(velocityY) > FLING_MIN_VELOCITY) {
                if (disY > FLING_MIN_DISTANCE) {
                    // up
                    webView.onFling?.up()
                } else if (-disY > FLING_MIN_DISTANCE) {
                    // down
                    webView.onFling?.down()
                }
            }
        }
        return false
    }

    companion object {

        private val FLING_MIN_DISTANCE = 0
        private val FLING_MIN_VELOCITY = 0
    }
}
