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
 * Created by jsen on 2018/1/22.
 */

class ToastWithEdit(build:Build) : AlertDialog(build.context, build.themeResId) {
    class Build(val context: Context, val themeResId: Int) {
        var message:String = ""
        var pos:String = "取消"
        var listener:View.OnClickListener? = null

        fun build():ToastWithEdit {
            return ToastWithEdit(this)
        }

    }


    var root:View? = null
    init {
        root = View.inflate(context, R.layout.toast_with_edit, null)
        root!!.findViewById<TextView>(R.id.alert_content)
        root!!.findViewById<TextView>(R.id.toast_message).text = build.message
        root!!.findViewById<TextView>(R.id.toast_pos).text = build.pos
        root!!.findViewById<TextView>(R.id.toast_pos).setOnClickListener { v ->
            dismiss()
            build.listener?.onClick(v)
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
