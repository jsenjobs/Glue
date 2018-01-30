package app.chaosstudio.com.glue.ui

import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.utils.DensityUtil

/**
 * Created by jsen on 2018/1/21.
 */

class SimpleAlert(build:Build) : AlertDialog(build.context, build.themeResId) {
    class Build(val context: Context, val themeResId: Int) {
        var showTitle:Boolean = false
        var title:String = ""
        var showNag:Boolean = true
        var nag:String = "取消"
        var showPos:Boolean = true
        var pos:String = "确定"
        var showContent:Boolean = true
        var content:String = ""
        var onNag:View.OnClickListener? = null
        var onPos:View.OnClickListener? = null

        fun build():SimpleAlert {
            return SimpleAlert(this)
        }

    }
    var root:View? = null
    var tag = false
    init {
        root = View.inflate(context, R.layout.alert_simple, null)
        if (build.showTitle) {
            val con = root!!.findViewById<TextView>(R.id.alert_title)
            con.visibility = View.VISIBLE
            con.text = build.title
        } else {
            root!!.findViewById<View>(R.id.alert_title).visibility = View.GONE
        }
        if (build.showContent) {
            val con = root!!.findViewById<TextView>(R.id.alert_content)
            con.visibility = View.VISIBLE
            con.text = build.content
        } else {
            root!!.findViewById<View>(R.id.alert_content).visibility = View.GONE
        }
        if (!build.showNag && !build.showPos) {
            root!!.findViewById<View>(R.id.alert_bottom).visibility = View.GONE
        } else {
            if (build.showNag) {
                val con = root!!.findViewById<TextView>(R.id.alert_nag)
                con.visibility = View.VISIBLE
                con.text = build.nag
                root!!.findViewById<View>(R.id.alert_nag).visibility = View.VISIBLE
                con.setOnClickListener { view ->
                    dismiss()
                    if(build.onNag != null) build.onNag!!.onClick(view)
                }
            } else {
                root!!.findViewById<View>(R.id.alert_nag).visibility = View.GONE
            }
            if (build.showPos) {
                val con = root!!.findViewById<TextView>(R.id.alert_pos)
                con.visibility = View.VISIBLE
                con.text = build.pos
                root!!.findViewById<View>(R.id.alert_pos).visibility = View.VISIBLE
                con.setOnClickListener { view ->
                    tag = true
                    dismiss()
                    if(build.onPos != null) build.onPos!!.onClick(view)
                }
            } else {
                root!!.findViewById<View>(R.id.alert_pos).visibility = View.GONE
            }
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
