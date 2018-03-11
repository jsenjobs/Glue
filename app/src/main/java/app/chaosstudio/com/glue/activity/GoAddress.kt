package app.chaosstudio.com.glue.activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import app.chaosstudio.com.glue.App
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.eventb.WebViewAction
import app.chaosstudio.com.glue.ui.CompleteAdapter
import app.chaosstudio.com.glue.ui.SimpleToast
import app.chaosstudio.com.glue.utils.CustomTheme
import kotlinx.android.synthetic.main.activity_go_address.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by jsen on 2018/1/21.
 */

class GoAddress: AppCompatActivity() {
    var url:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or  View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (CustomTheme.hiddenStatus) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        setContentView(R.layout.activity_go_address)

        search_address.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_GO || (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)) {
                goAddress()
            }
            true
        }

        search_address.addTextChangedListener(object : TextWatcher{
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                url = p0.toString()
            }

        })
        search_address.post({
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(search_address, InputMethodManager.SHOW_IMPLICIT)
        })
        outl.setOnClickListener {
            finish()
        }

        val hisDao = App.instances.daoSession.historyDao
        val bkDao = App.instances.daoSession.bookMarkDao

        val adapter = CompleteAdapter(this, hisDao.loadAll(), bkDao.loadAll())
        adapter.notifyDataSetChanged()
        search_address.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val url = view.findViewById<TextView>(R.id.complete_item_url).text.toString()
            search_address.setText(url)
            search_address.selectAll()
        }
        search_address.setAdapter(adapter)
    }

    fun goAddress() {
        if (TextUtils.isEmpty(url)) {
            SimpleToast.makeToast(this@GoAddress, "请输入地址", Toast.LENGTH_SHORT).show()
        } else {
            val wa = WebViewAction(WebViewAction.ACTION.GO)
            wa.url = url
            EventBus.getDefault().post(wa)
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition( R.anim.hold_activity, R.anim.bottom_popup_out)
    }
}
