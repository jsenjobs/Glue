package app.chaosstudio.com.glue.ui

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import app.chaosstudio.com.glue.R

/**
 * Created by jsen on 2018/1/21.
 */

class EditAlertLogin(build:Build) : AlertDialog(build.context, build.themeResId) {
    interface OnClick {
        fun pos(username:String, password:String)
        fun nag()
    }
    class Build(val context: Context, val themeResId: Int) {
        var pos:String = "登入"
        var onClick: OnClick? = null

        fun build(): EditAlertLogin {
            return EditAlertLogin(this)
        }

    }
    var root: View? = null
    var userName: EditText? = null
    var password: EditText? = null
    init {
        root = View.inflate(context, R.layout.alert_login, null)
        userName = root!!.findViewById(R.id.alert_login_username)
        password = root!!.findViewById(R.id.alert_login_password)

        val con2 = root!!.findViewById<TextView>(R.id.alert_login_pos)
        con2.text = build.pos
        con2.setOnClickListener { view ->
            dismiss()
            if(build.onClick != null) build.onClick!!.pos(userName?.text.toString(), password?.text.toString())
        }

        val con3 = root!!.findViewById<TextView>(R.id.alert_login_nag)
        con3.setOnClickListener { view ->
            dismiss()
            if(build.onClick != null) build.onClick!!.nag()
        }

        setCancelable(true)
        setCanceledOnTouchOutside(true)
        window.setDimAmount(0.0f)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val lp = window.attributes
        lp.y = - getScreenHeight(context) / 4
        lp.alpha = 1f
        window.attributes = lp
        setOnShowListener { _ ->
            userName!!.post({
                val inputManager = userName!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.showSoftInput(userName, InputMethodManager.SHOW_IMPLICIT)
            })
        }
    }

    override fun show() {
        super.show()
        window.setContentView(root)
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
    }

    /**
     * 获取屏幕高度(px)
     */
    private fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }
}