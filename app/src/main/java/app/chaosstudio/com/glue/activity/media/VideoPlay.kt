package app.chaosstudio.com.glue.activity.media

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import app.chaosstudio.com.glue.R
import io.vov.vitamio.Vitamio
import io.vov.vitamio.utils.Log
import io.vov.vitamio.widget.MediaController
import kotlinx.android.synthetic.main.video_play.*

/**
 * Created by jsen on 2018/2/2.
 */

class VideoPlay : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Vitamio.isInitialized(applicationContext)

        setContentView(R.layout.video_play)

        if (intent.hasExtra("path") && !TextUtils.isEmpty(intent.getStringExtra("path"))) {
            playfunction(intent.getStringExtra("path"))
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    internal fun playfunction(path:String) {
        // path = "http://gslb.miaopai.com/stream/3D~8BM-7CZqjZscVBEYr5g__.mp4"
        Log.e("PATH", path)
        /*
         * Alternatively,for streaming media you can use
         * mVideoView.setVideoURI(Uri.parse(URLstring));
         */
        surface_view.setVideoPath(path)
        surface_view.setMediaController(MediaController(this))
        surface_view.requestFocus()

        surface_view.setOnPreparedListener { mediaPlayer ->
            // optional need Vitamio 4.0
            mediaPlayer.setPlaybackSpeed(1.0f)
        }

        surface_view.setOnBufferingUpdateListener { mp, percent ->
            video_hold.visibility = View.GONE
        }

    }
}
