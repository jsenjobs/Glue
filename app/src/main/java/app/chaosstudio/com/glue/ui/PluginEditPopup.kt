package app.chaosstudio.com.glue.ui

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import app.chaosstudio.com.glue.R

/**
 * Created by jsen on 2018/1/21.
 */

class PluginEditPopup(build:Build) : AlertDialog(build.context, build.themeResId) {
    class Build(val context: Context, val themeResId: Int) {
        var onPos: View.OnClickListener? = null

        var tag:String = ""
        var filter:String = ""
        var js:String = ""

        fun build(): PluginEditPopup {
            return PluginEditPopup(this)
        }

    }
    var root: View? = null
    var tagT: String? = null
    var filterT: String? = null
    var jsT: String? = null

    var tag: EditText? = null
    var filter: EditText? = null
    var js: EditText? = null
    init {
        root = View.inflate(context, R.layout.alert_plugin_edit, null)

        tag = root!!.findViewById(R.id.alert_tag)
        filter = root!!.findViewById(R.id.alert_filter)
        js = root!!.findViewById(R.id.alert_js)

        tag!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                tagT = s.toString()
            }
        })
        filter!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterT = s.toString()
            }
        })
        js!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                jsT = s.toString()
            }
        })
        tag?.setText(build.tag)
        filter?.setText(build.filter)
        js?.setText(build.js)

        root!!.findViewById<View>(R.id.alert_sub).setOnClickListener { view ->
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
            tag!!.post({
                val inputManager = tag!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.showSoftInput(tag, InputMethodManager.SHOW_IMPLICIT)
            })
        }
    }

    fun clear() {
        tag?.setText("")
        filter?.setText("")
        js?.setText("")
    }
    fun setUp(tagT:String, filterT:String, jsT:String) {
        tag?.setText(tagT)
        filter?.setText(filterT)
        js?.setText(jsT)
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