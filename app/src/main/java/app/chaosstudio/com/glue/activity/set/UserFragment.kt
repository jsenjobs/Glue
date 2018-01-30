package app.chaosstudio.com.glue.activity.set

import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.eventb.ActionBarAction
import app.chaosstudio.com.glue.ui.SimpleAlert
import app.chaosstudio.com.glue.ui.SimpleToast
import app.chaosstudio.com.glue.unit.BrowserUnit
import app.chaosstudio.com.glue.webconfig.WebViewManager
import kotlinx.android.synthetic.main.set_fragment_user.*

/**
 * Created by jsen on 2018/1/29.
 */

class UserFragment : FragmentBase() {

    init {
        title = "用户信息"
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.set_fragment_user, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val username = PreferenceManager.getDefaultSharedPreferences(activity).getString("sp_username", "")
        if (TextUtils.isEmpty(username)) {
            activity.finish()
            return
        } else {
            user_username_t.text = username
        }
        user_username.setOnClickListener { v ->
            showSyncAlert()
        }

        logout.setOnClickListener { v ->
            showLogoutAlert()
        }


    }

    fun showSyncAlert() {
        val build = SimpleAlert.Build(activity, R.style.SimpleAlert)
        build.content = "同步本地信息"
        build.onPos = View.OnClickListener { view ->
            SimpleToast.makeToast(activity, "同步成功", Toast.LENGTH_LONG).show()
        }
        build.build().show()
    }

    fun showLogoutAlert() {
        val build = SimpleAlert.Build(activity, R.style.SimpleAlert)
        build.content = "退出登入"
        build.onPos = View.OnClickListener { view ->
            PreferenceManager.getDefaultSharedPreferences(activity).edit().remove("sp_username").apply()
            SimpleToast.makeToast(activity, "退出成功", Toast.LENGTH_LONG).show()
        }
        build.build().show()
    }
}

