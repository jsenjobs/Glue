package app.chaosstudio.com.glue.activity.set

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.ui.SimpleToast
import kotlinx.android.synthetic.main.set_fragment_set_plugin.*

/**
 * Created by jsen on 2018/1/22.
 */

class SetPluginFragment : FragmentBase() {

    init {
        title = "插件"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.set_fragment_set_plugin, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val lis = View.OnClickListener{ view ->
            when(view.id) {
                R.id.set_plugin_online -> SimpleToast.makeToast(activity, "暂未实现", Toast.LENGTH_LONG).show()
                // R.id.set_plugin_download -> SimpleToast.makeToast(activity, "暂未实现", Toast.LENGTH_LONG)
                // R.id.set_plugin_qrcode -> SimpleToast.makeToast(activity, "暂未实现", Toast.LENGTH_LONG)
            }
        }
        set_plugin_online.setOnClickListener(lis)
        // set_plugin_download.setOnClickListener(lis)
        // set_plugin_qrcode.setOnClickListener(lis)
    }
}