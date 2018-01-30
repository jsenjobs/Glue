package app.chaosstudio.com.glue.activity.set

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.ui.SimpleToast
import kotlinx.android.synthetic.main.set_fragment_login.*

/**
 * Created by jsen on 2018/1/29.
 */

class LoginFragment : FragmentBase() {

    init {
        title = "登入"
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.set_fragment_login, container, false)
    }


    var username = ""
    var password = ""
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        login_username.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                username = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        login_password.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                password = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        login.setOnClickListener { v ->
            if (TextUtils.isEmpty(username)) {
                SimpleToast.makeToast(activity, "请输入用户名", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                SimpleToast.makeToast(activity, "请输入密码", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // do login
        }
    }
}
