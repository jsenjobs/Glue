package app.chaosstudio.com.glue.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.TextView
import android.widget.Toast
import app.chaosstudio.com.glue.App
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.activity.set.FragmentBase
import app.chaosstudio.com.glue.activity.set.UrlRuleFragment
import app.chaosstudio.com.glue.eventb.ActionBarAction
import app.chaosstudio.com.glue.eventb.FragmentAction
import app.chaosstudio.com.glue.eventb.WebViewAction
import app.chaosstudio.com.glue.greendb.model.BlackUrl
import app.chaosstudio.com.glue.greendb.model.LogMode
import app.chaosstudio.com.glue.greendb.model.WhiteDomain
import app.chaosstudio.com.glue.ui.*
import app.chaosstudio.com.glue.unit.BrowserUnit
import app.chaosstudio.com.glue.webconfig.AdBlock
import app.chaosstudio.com.glue.webconfig.Logs
import app.chaosstudio.com.glue.webconfig.WebViewManager
import kotlinx.android.synthetic.main.list_fragment_history.*
import org.greenrobot.eventbus.Subscribe

/**
 * Created by jsen on 2018/1/23.
 */

class ListLogsFragment : FragmentBase() {
    companion object {
        var isResources = false
    }
    init {
        title = if (isResources) {
            "资源嗅探"
        } else {
            "日志记录"
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_fragment_history, container, false)
    }

    var data:List<LogMode> ? = null
    var adapter: LogsAdapter? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val wv =WebViewManager.getCurrentActive()
        if (wv == null) {
            activity.finish()
            return
        }
        data = if (isResources) {
            wv.logs.findResources(wv.uuid)
        } else {
            wv.logs.getLogs(wv.uuid)
        }

        if (data == null || data!!.isEmpty()) {
            record_list_empty.visibility = View.VISIBLE
            return
        }
        adapter = LogsAdapter(activity, data!!)
        adapter?.notifyDataSetChanged()
        list_history.adapter = adapter

        list_history.setOnItemClickListener { parent, view, position, id ->
            val url = view.findViewById<TextView>(R.id.log_item_url).text.toString()
            BrowserUnit.copyURL(activity, url)
        }

        list_history.setOnItemLongClickListener { parent, view, position, id ->
            val url = view.findViewById<TextView>(R.id.log_item_url).text.toString()
            val build = SimpleAlert.Build(activity, R.style.SimpleAlert)
            build.content = url
            build.showTitle = true
            build.title = "添加到黑名单？"
            build.onPos = View.OnClickListener { _ ->
                ruleEditPopup?.urlEditText?.setText(url)
                ruleEditPopup?.urlTagEditText?.setText("log标记")
                ruleEditPopup?.show()
            }
            build.build().show()
            true
        }
        createAddAlert()
    }

    var ruleEditPopup:RuleEditPopup? = null
    fun createAddAlert() {
        val build = RuleEditPopup.Build(activity, R.style.SimpleAlert)
        build.onPos = View.OnClickListener { _ ->
            if (!TextUtils.isEmpty(ruleEditPopup?.urlText) && !TextUtils.isEmpty(ruleEditPopup?.urlTagText)) {
                val w = BlackUrl()
                w.domain = ruleEditPopup?.urlText
                w.tag = ruleEditPopup?.urlTagText
                App.instances.daoSession.blackUrlDao.save(w)
                AdBlock.loadDomains(activity)
                SimpleToast.makeToast(activity, "标记成功", Toast.LENGTH_LONG).show()
            }
            // add data
            // sp!!.edit().putString(context.getString(R.string.sp_user_agent_custom), uaEditAlert!!.text).apply()
        }
        ruleEditPopup = build.build()
    }
}
