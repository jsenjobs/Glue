package app.chaosstudio.com.glue.activity.set

import android.app.Service
import android.content.Intent
import android.os.IBinder

import app.chaosstudio.com.glue.MainActivity
import app.chaosstudio.com.glue.eventb.WebViewAction
import app.chaosstudio.com.glue.unit.RecordUnit

/**
 * Created by jsen on 2018/1/23.
 */

class DefService : Service() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val toActivity = Intent(this@DefService, MainActivity::class.java)
        toActivity.putExtra("URL", RecordUnit.getHolder().url)
        toActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(toActivity)

        WebViewAction.fire(WebViewAction.ACTION.CREATEPAGE, RecordUnit.getHolder().url)
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
