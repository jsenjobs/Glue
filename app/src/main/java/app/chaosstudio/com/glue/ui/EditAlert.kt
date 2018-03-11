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

class EditAlert(build:Build) : AlertDialog(build.context, build.themeResId) {
    class Build(val context: Context, val themeResId: Int) {
        var content:String = ""
        var pos:String = "确定"

        var showNag = false
        var nag:String = "取消"
        var showTitle = false
        var title = ""

        var onPos: View.OnClickListener? = null
        var onNag: View.OnClickListener? = null

        fun build(): EditAlert {
            return EditAlert(this)
        }

    }
    var root: View? = null
    var text: String? = null
    var editText: EditText? = null
    var tag = false
    init {
        root = View.inflate(context, R.layout.alert_with_edit, null)
        val con = root!!.findViewById<EditText>(R.id.alert_content)
        editText = con
        con.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                text = s.toString()
            }
        })
        con.setText(build.content)

        val con2 = root!!.findViewById<TextView>(R.id.alert_pos)
        con2.text = build.pos
        con2.visibility = View.VISIBLE
        con2.setOnClickListener { view ->
            tag = true
            dismiss()
            if(build.onPos != null) build.onPos!!.onClick(view)
        }
        if (build.showNag) {
            val nagB = root!!.findViewById<TextView>(R.id.alert_nag)
            nagB.text = build.nag
            nagB.visibility = View.VISIBLE
            nagB.setOnClickListener { view ->
                dismiss()
                if(build.onNag != null) build.onNag!!.onClick(view)
            }
        }
        if (build.showTitle) {
            val titleT = root!!.findViewById<TextView>(R.id.alert_title)
            titleT.visibility = View.VISIBLE
            titleT.text = build.title
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
        setOnShowListener { dialog ->
            editText!!.post({
                val inputManager = editText!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
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