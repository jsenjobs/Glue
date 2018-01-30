package app.chaosstudio.com.glue.ui

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import app.chaosstudio.com.glue.R

/**
 * Created by jsen on 2018/1/21.
 */

class RuleEditPopup(build:Build) : AlertDialog(build.context, build.themeResId) {
    class Build(val context: Context, val themeResId: Int) {
        var onPos: View.OnClickListener? = null

        var url:String = ""
        var tag:String = ""

        fun build(): RuleEditPopup {
            return RuleEditPopup(this)
        }

    }
    var root: View? = null
    var urlText: String? = null
    var urlTagText: String? = null
    var urlEditText: EditText? = null
    var urlTagEditText: EditText? = null
    init {
        root = View.inflate(context, R.layout.alert_rule_edit, null)
        val con = root!!.findViewById<EditText>(R.id.alert_url)
        urlTagEditText = root!!.findViewById(R.id.alert_url_tag)
        urlEditText = con
        con.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                urlText = s.toString()
            }
        })
        urlTagEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                urlTagText = s.toString()
            }
        })
        urlTagEditText?.setText(build.tag)
        urlEditText?.setText(build.url)

        val con2 = root!!.findViewById<TextView>(R.id.alert_sub)
        con2.setOnClickListener { view ->
            dismiss()
            if(build.onPos != null) build.onPos!!.onClick(view)
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
            urlEditText!!.post({
                val inputManager = urlEditText!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.showSoftInput(urlEditText, InputMethodManager.SHOW_IMPLICIT)
            })
        }
    }

    fun clear() {
        urlEditText?.setText("")
        urlTagEditText?.setText("")
    }
    fun setUp(url:String, urlTag:String) {
        urlEditText?.setText(url)
        urlTagEditText?.setText(urlTag)
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