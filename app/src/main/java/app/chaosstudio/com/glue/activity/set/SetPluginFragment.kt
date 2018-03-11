package app.chaosstudio.com.glue.activity.set

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import app.chaosstudio.com.glue.App
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.eventb.FragmentAction
import app.chaosstudio.com.glue.greendb.model.Plugin
import app.chaosstudio.com.glue.ui.*
import app.chaosstudio.com.glue.webconfig.PluginFilter
import kotlinx.android.synthetic.main.set_fragment_set_plugin.*
import org.greenrobot.eventbus.Subscribe

/**
 * Created by jsen on 2018/1/22.
 */

class SetPluginFragment : FragmentBase() {

    init {
        title = "插件"
        showMenu = true
        defaultMenu = R.menu.popup_menu_rule
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.set_fragment_set_plugin, container, false)
    }


    var adapter:PluginAdapter? = null
    var data:ArrayList<Plugin> = ArrayList()
    var tX = 0f
    var tY = 0f
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

        data.addAll(App.instances.daoSession.pluginDao.loadAll())
        if (data.isEmpty()) {
            plugin_list_empty.visibility = View.VISIBLE
        }

        list_plugin.setOnItemLongClickListener { _, view, position, _ ->
            selectedIndex = position

            // popup!!.updateAnchor(view)
            popup!!.updateAnchor(view, tX.toInt(), tY.toInt())
            popup!!.showAtLocation(Gravity.TOP or Gravity.START)
            true
        }
        adapter = PluginAdapter(activity, data)
        list_plugin.adapter = adapter
        adapter!!.notifyDataSetChanged()


        list_plugin.setOnTouchListener { _, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    tX = event.rawX
                    tY = event.rawY
                }
                MotionEvent.ACTION_MOVE-> {
                    tX = event.rawX
                    tY = event.rawY
                }
                MotionEvent.ACTION_UP -> {
                    tX = event.rawX
                    tY = event.rawY
                }
            }
            false
        }
        createPopup()
        createAddAlert()
    }


    var ruleEditPopup: PluginEditPopup? = null
    fun createAddAlert() {
        val build = PluginEditPopup.Build(activity, R.style.SimpleAlert)
        build.onPos = View.OnClickListener { _ ->
            if (selectedIndex == -1) {
                // add
                if (!TextUtils.isEmpty(ruleEditPopup?.tagT) && !TextUtils.isEmpty(ruleEditPopup?.filterT) && !TextUtils.isEmpty(ruleEditPopup?.jsT)) {
                    val w = Plugin()
                    w.tag = ruleEditPopup?.tagT
                    w.filter = ruleEditPopup?.filterT
                    w.js = ruleEditPopup?.jsT
                    App.instances.daoSession.pluginDao.save(w)
                    PluginFilter.loadPlugins(activity)
                    SimpleToast.makeToast(activity, "添加插件成功", Toast.LENGTH_LONG).show()
                    reloadData()
                }
            } else {
                val d = data[selectedIndex]
                if (!TextUtils.isEmpty(ruleEditPopup?.tagT) && !TextUtils.isEmpty(ruleEditPopup?.filterT) && !TextUtils.isEmpty(ruleEditPopup?.jsT)) {
                    d.tag = ruleEditPopup?.tagT
                    d.filter = ruleEditPopup?.filterT
                    d.js = ruleEditPopup?.jsT
                    App.instances.daoSession.pluginDao.update(d)
                    PluginFilter.loadPlugins(activity)
                    SimpleToast.makeToast(activity, "更新插件成功", Toast.LENGTH_LONG).show()
                    reloadData()
                }
            }
            // add data
            // sp!!.edit().putString(context.getString(R.string.sp_user_agent_custom), uaEditAlert!!.text).apply()
        }
        ruleEditPopup = build.build()
    }

    var popup: SimplePopupWindow? = null
    var selectedIndex = -1
    fun createPopup() {
        val root = View.inflate(activity, R.layout.popup_rule_edit, null)
        val lis = View.OnClickListener{ view ->
            popup?.dismiss()
            when(view.id) {
                R.id.rule_edit_del -> {
                    val d = data[selectedIndex]
                    App.instances.daoSession.pluginDao.delete(d)
                    reloadData()
                    PluginFilter.loadPlugins(activity)
                    SimpleToast.makeToast(activity, "删除插件成功", Toast.LENGTH_LONG).show()
                }
                R.id.rule_edit_edit -> {
                    val d = data[selectedIndex]
                    ruleEditPopup?.setUp(d.tag, d.filter, d.js)
                    ruleEditPopup?.show()
                }
            }
        }
        root.findViewById<View>(R.id.rule_edit_del).setOnClickListener(lis)
        root.findViewById<View>(R.id.rule_edit_edit).setOnClickListener(lis)
        popup = SimplePopupWindow(view, root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popup?.animationStyle = R.style.popwin_anim_style
    }

    fun reloadData() {
        data.clear()
        data.addAll(App.instances.daoSession.pluginDao.loadAll())
        adapter!!.notifyDataSetChanged()
    }

    @Subscribe
    fun onFragmentAction(action: FragmentAction) {
        if (action.action == FragmentAction.ACTION.MENU_ITEM) {
            when(action.tag) {
                R.id.clear_tag -> {

                    val build = SimpleAlert.Build(activity, R.style.SimpleAlert)
                    build.content = "全部清除？"
                    build.onPos = View.OnClickListener { _ ->
                        App.instances.daoSession.pluginDao.deleteAll()
                        reloadData()
                        PluginFilter.loadPlugins(activity)
                        SimpleToast.makeToast(activity, "清除成功", Toast.LENGTH_LONG).show()
                    }
                    build.build().show()
                }
                R.id.add_tag -> {
                    selectedIndex = -1
                    ruleEditPopup?.clear()
                    ruleEditPopup?.show()
                }
            }
        }
    }
}