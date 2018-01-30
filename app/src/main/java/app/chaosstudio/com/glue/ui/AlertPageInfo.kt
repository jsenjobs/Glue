package app.chaosstudio.com.glue.ui

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.eventb.WebViewAction
import app.chaosstudio.com.glue.unit.BrowserUnit
import app.chaosstudio.com.glue.utils.DensityUtil

/**
 * Created by jsen on 2018/1/21.
 */

class AlertPageInfo(build:Build) : AlertDialog(build.context, build.themeResId) {
    class Build(val context: Context, val themeResId: Int) {


        var title:String = ""
        var url:String = ""
        var goUrl:String = ""

        fun build(): AlertPageInfo {
            return AlertPageInfo(this)
        }

    }
    var root:View? = null
    init {
        root = View.inflate(context, R.layout.alert_page_info, null)

        root!!.findViewById<TextView>(R.id.alert_content_title).text = build.title
        root!!.findViewById<TextView>(R.id.alert_content_url).text = build.url
        root!!.findViewById<TextView>(R.id.alert_content_select_url).text = build.goUrl

        root!!.findViewById<TextView>(R.id.alert_pos).setOnClickListener { view ->
            dismiss()
        }

        root!!.findViewById<TextView>(R.id.alert_content_url).setOnLongClickListener { view ->
            BrowserUnit.copyURL(context, (view as TextView?)?.text?.toString() ?: "")
            true
        }

        root!!.findViewById<TextView>(R.id.alert_content_select_url).setOnLongClickListener { view ->
            BrowserUnit.copyURL(context, (view as TextView?)?.text?.toString() ?: "")
            true
        }

        setCancelable(true)
        setCanceledOnTouchOutside(true)
        val window = window
        window.setGravity(Gravity.BOTTOM)
        window.setDimAmount(0.0f)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val lp = window.attributes
        lp.y = DensityUtil.dip2px(context, 56f)
        lp.alpha = 1f
        window.attributes = lp

    }

    override fun show() {
        super.show()
        window.setContentView(root)
    }
}
