package app.chaosstudio.com.glue.activity.set

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import app.chaosstudio.com.glue.App
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.greendb.model.OpendedUrl
import app.chaosstudio.com.glue.ui.SimpleToast
import app.chaosstudio.com.glue.webconfig.WebViewManager
import kotlinx.android.synthetic.main.set_fragment_set_more.*

/**
 * Created by jsen on 2018/1/22.
 */

class SetMoreFragment : FragmentBase() {

    init {
        title = "高级"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.set_fragment_set_more, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sp = PreferenceManager.getDefaultSharedPreferences(context)

        val lis = View.OnClickListener{ view ->
            when(view.id) {
                R.id.set_more_force_scale -> set_more_force_scale_r.isChecked = !set_more_force_scale_r.isChecked
                R.id.set_more_popup -> set_more_popup_r.isChecked = !set_more_popup_r.isChecked
                R.id.set_more_js -> set_more_js_r.isChecked = !set_more_js_r.isChecked
                R.id.set_more_crash -> set_more_crash_r.isChecked = !set_more_crash_r.isChecked
                R.id.set_more_reload -> set_more_reload_r.isChecked = !set_more_reload_r.isChecked
            }
        }
        val cc = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            when(buttonView.id) {
                R.id.set_more_force_scale_r -> sp!!.edit().putBoolean(context.getString(R.string.sp_force_scale), isChecked).apply()
                R.id.set_more_popup_r -> sp!!.edit().putBoolean(context.getString(R.string.sp_popup), isChecked).apply()
                R.id.set_more_js_r -> sp!!.edit().putBoolean(context.getString(R.string.sp_javascript), isChecked).apply()
                R.id.set_more_crash_r -> {
                    sp!!.edit().putBoolean(context.getString(R.string.sp_crash), isChecked).apply()
                    val list = WebViewManager.getAllNWebView()
                    val dS = App.instances.daoSession.opendedUrlDao
                    if (isChecked) {
                        dS.deleteAll()
                        for (item in list) {
                            item.crashClose = isChecked
                            val dao = OpendedUrl()
                            dao.domain = item.url
                            dao.uuid  = item.toString()
                            dS.insert(dao)
                        }
                    } else {
                        dS.deleteAll()
                        for (item in list) {
                            item.crashClose = isChecked
                        }
                    }
                }
                R.id.set_more_reload_r -> sp!!.edit().putBoolean(context.getString(R.string.sp_resume), isChecked).apply()
            }

        }
        set_more_force_scale.setOnClickListener(lis)
        set_more_popup.setOnClickListener(lis)
        set_more_js.setOnClickListener(lis)
        set_more_crash.setOnClickListener(lis)
        set_more_reload.setOnClickListener(lis)

        set_more_force_scale_r.setOnCheckedChangeListener(cc)
        set_more_popup_r.setOnCheckedChangeListener(cc)
        set_more_js_r.setOnCheckedChangeListener(cc)
        set_more_crash_r.setOnCheckedChangeListener(cc)
        set_more_reload_r.setOnCheckedChangeListener(cc)

        initUI()
    }

    var sp: SharedPreferences? = null
    fun initUI() {
        set_more_force_scale_r.isChecked = sp!!.getBoolean(context.getString(R.string.sp_force_scale), false)
        set_more_popup_r.isChecked = sp!!.getBoolean(context.getString(R.string.sp_popup), false)
        set_more_js_r.isChecked = sp!!.getBoolean(context.getString(R.string.sp_javascript), true)
        set_more_crash_r.isChecked = sp!!.getBoolean(context.getString(R.string.sp_crash), false)
        set_more_reload_r.isChecked = sp!!.getBoolean(context.getString(R.string.sp_resume), false)
    }
}