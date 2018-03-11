package app.chaosstudio.com.glue.activity.set

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.activity.Set
import app.chaosstudio.com.glue.activity.SimpleContainer
import app.chaosstudio.com.glue.eventb.WebViewAction
import app.chaosstudio.com.glue.ui.SimpleToast
import kotlinx.android.synthetic.main.set_fragment_set.*

/**
 * Created by jsen on 2018/1/22.
 */

class SetFragment : FragmentBase() {

    init {
        title = "设置"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.set_fragment_set, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val lis = View.OnClickListener{view ->
            when(view.id) {
                R.id.set_common -> {
                    val intent = Intent(activity, Set::class.java)
                    intent.putExtra("fragment", SetCommonFragment::class.java.name)
                    startActivity(intent)
                }
                R.id.set_self -> {
                    val intent = Intent(activity, SimpleContainer::class.java)
                    intent.putExtra("fragment", SetSelfFragment::class.java.name)
                    startActivity(intent)
                }
                R.id.set_private -> {
                    val intent = Intent(activity, Set::class.java)
                    intent.putExtra("fragment", SetPrivateFragment::class.java.name)
                    startActivity(intent)
                }
                R.id.set_more -> {
                    val intent = Intent(activity, Set::class.java)
                    intent.putExtra("fragment", SetMoreFragment::class.java.name)
                    startActivity(intent)
                }
                R.id.set_plugin -> {
                    val intent = Intent(activity, SimpleContainer::class.java)
                    intent.putExtra("fragment", SetPluginFragment::class.java.name)
                    startActivity(intent)
                }
                R.id.set_about -> {
                    WebViewAction.fire(WebViewAction.ACTION.GO, "file:///android_asset/about.html")
                    activity.finish()
                }
            }
        }
        set_common.setOnClickListener(lis)
        set_self.setOnClickListener(lis)
        set_private.setOnClickListener(lis)
        set_more.setOnClickListener(lis)
        set_plugin.setOnClickListener(lis)
        set_about.setOnClickListener(lis)
    }
}
