package app.chaosstudio.com.glue

import android.content.Context
import android.preference.PreferenceManager

/**
 * Created by jsen on 2018/1/28.
 */

class GPre{
    companion object {
        var actionItem1 = ""
        var actionItem2 = ""
        var actionItem3 = ""
        var actionItem4 = ""
        var actionItem5 = ""

        var actionItemLong1 = ""
        var actionItemLong2 = ""
        var actionItemLong3 = ""
        var actionItemLong4 = ""
        var actionItemLong5 = ""

        var volumeScroll = false
        var flingPage = false

        fun init(context: Context) {
            val sp = PreferenceManager.getDefaultSharedPreferences(context)
            val prefix = context.getString(R.string.sp_qquick)
            actionItem1 = sp.getString(prefix + "1", "13")
            actionItem2 = sp.getString(prefix + "2", "14")
            actionItem3 = sp.getString(prefix + "3", "5")
            actionItem4 = sp.getString(prefix + "4", "17")
            actionItem5 = sp.getString(prefix + "5", "18")

            actionItemLong1 = sp.getString(prefix + "1l", "1")
            actionItemLong2 = sp.getString(prefix + "2l", "1")
            actionItemLong3 = sp.getString(prefix + "3l", "16")
            actionItemLong4 = sp.getString(prefix + "4l", "6")
            actionItemLong5 = sp.getString(prefix + "5l", "2")


            volumeScroll = sp.getBoolean(context.getString(R.string.sp_volume_scroll), false)
            flingPage = sp.getBoolean(context.getString(R.string.sp_fling_page), true)
        }
    }
}
