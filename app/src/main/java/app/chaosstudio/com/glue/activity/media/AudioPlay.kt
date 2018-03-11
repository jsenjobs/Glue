package app.chaosstudio.com.glue.activity.media

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.activity.media.CustomVideoView.VIDEO_LAYOUT_MINI
import io.vov.vitamio.Vitamio
import io.vov.vitamio.widget.MediaController
import kotlinx.android.synthetic.main.audio_play.*

/**
 * Created by jsen on 2018/2/2.
 */

class AudioPlay : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Vitamio.isInitialized(applicationContext)

        setContentView(R.layout.audio_play)

        surface_view.setVideoLayout(VIDEO_LAYOUT_MINI, 0F)
        if (intent.hasExtra("path") && !TextUtils.isEmpty(intent.getStringExtra("path"))) {
            playfunction(intent.getStringExtra("path"))
        }

        touch_layer.setOnTouchListener { v, event ->
            if (mediaController!!.isShowing) {
                mediaController!!.hide()
            } else {
                mediaController!!.show()
            }
            false
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    var mediaController:MediaController? = null
    fun playfunction(path:String) {
        // path = "http://gslb.miaopai.com/stream/3D~8BM-7CZqjZscVBEYr5g__.mp4"
        Log.e("PATHssss", path)
        /*
         * Alternatively,for streaming media you can use
         * mVideoView.setVideoURI(Uri.parse(URLstring));
         */
        mediaController = MediaController(this)
        surface_view.setVideoPath(path)
        surface_view.setMediaController(mediaController)
        surface_view.requestFocus()

        surface_view.setOnPreparedListener { mediaPlayer ->
            // optional need Vitamio 4.0
            mediaPlayer.setPlaybackSpeed(1.0f)
        }
    }
}
