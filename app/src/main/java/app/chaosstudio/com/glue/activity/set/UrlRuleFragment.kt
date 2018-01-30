package app.chaosstudio.com.glue.activity.set

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.TextView
import android.widget.Toast
import app.chaosstudio.com.glue.App
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.eventb.FragmentAction
import app.chaosstudio.com.glue.eventb.WebViewAction
import app.chaosstudio.com.glue.greendb.model.BlackUrl
import app.chaosstudio.com.glue.greendb.model.WhiteDomain
import app.chaosstudio.com.glue.ui.*
import app.chaosstudio.com.glue.webconfig.AdBlock
import kotlinx.android.synthetic.main.list_fragment_history.*
import org.greenrobot.eventbus.Subscribe

/**
 * Created by jsen on 2018/1/25.
 */

class UrlRuleFragment:FragmentBase() {
    companion object {
        var isWhite = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.simple_listview, container, false)
    }

    var data = if (isWhite) {
        App.instances.daoSession.whiteDomainDao.loadAll()
    } else {
        App.instances.daoSession.blackUrlDao.loadAll()
    }
    var adapterW: WhiteRuleEditAdapter? = null
    var adapterB: BlackRuleEditAdapter? = null
    var tX = 0f
    var tY = 0f
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (data == null || data.isEmpty()) {
            record_list_empty.visibility = View.VISIBLE
        }
        if (isWhite) {
            adapterW = WhiteRuleEditAdapter(activity, data as ArrayList<WhiteDomain>)
            adapterW?.notifyDataSetChanged()
            list_history.adapter = adapterW
        } else {
            adapterB = BlackRuleEditAdapter(activity, data as ArrayList<BlackUrl>)
            adapterB?.notifyDataSetChanged()
            list_history.adapter = adapterB

        }

        list_history.setOnItemClickListener { parent, view, position, id ->
            selectedIndex = position
            val d = data[selectedIndex]
            if (isWhite) {
                d as WhiteDomain
                ruleEditPopup?.setUp(d.domain, d.tag)
            } else {
                d as BlackUrl
                ruleEditPopup?.setUp(d.domain, d.tag)
            }
            ruleEditPopup?.show()
        }

        list_history.setOnItemLongClickListener { parent, view, position, id ->
            selectedIndex = position

            // popup!!.updateAnchor(view)
            popup!!.updateAnchor(view, tX.toInt(), tY.toInt())
            popup!!.showAtLocation(Gravity.TOP or Gravity.START)
            true
        }

        list_history.setOnTouchListener { _, event ->
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

    init {
        title = if (isWhite) {
            "白名单"
        } else {
            "黑名单"
        }
        showMenu = true
        defaultMenu = R.menu.popup_menu_rule
    }

    var ruleEditPopup:RuleEditPopup? = null
    fun createAddAlert() {
        val build = RuleEditPopup.Build(activity, R.style.SimpleAlert)
        build.onPos = View.OnClickListener { _ ->
            if (selectedIndex == -1) {
                // add
                if (!TextUtils.isEmpty(ruleEditPopup?.urlText) && !TextUtils.isEmpty(ruleEditPopup?.urlTagText)) {
                    if (isWhite) {
                        val w = WhiteDomain()
                        w.domain = ruleEditPopup?.urlText
                        w.tag = ruleEditPopup?.urlTagText
                        App.instances.daoSession.whiteDomainDao.save(w)
                        AdBlock.loadDomains(activity)
                        SimpleToast.makeToast(activity, "添加规则成功", Toast.LENGTH_LONG).show()
                    } else {
                        val w = BlackUrl()
                        w.domain = ruleEditPopup?.urlText
                        w.tag = ruleEditPopup?.urlTagText
                        App.instances.daoSession.blackUrlDao.save(w)
                        AdBlock.loadDomains(activity)
                        SimpleToast.makeToast(activity, "添加规则成功", Toast.LENGTH_LONG).show()
                    }
                    reloadData()
                }
            } else {
                val d = data[selectedIndex]
                if (isWhite) {
                    d as WhiteDomain
                    if (!TextUtils.isEmpty(ruleEditPopup?.urlText) && !TextUtils.isEmpty(ruleEditPopup?.urlTagText)) {
                        d.domain = ruleEditPopup?.urlText
                        d.tag = ruleEditPopup?.urlTagText
                        App.instances.daoSession.whiteDomainDao.update(d)
                        AdBlock.loadDomains(activity)
                        SimpleToast.makeToast(activity, "更新规则成功", Toast.LENGTH_LONG).show()
                    }
                } else {
                    d as BlackUrl
                    if (!TextUtils.isEmpty(ruleEditPopup?.urlText) && !TextUtils.isEmpty(ruleEditPopup?.urlTagText)) {
                        d.domain = ruleEditPopup?.urlText
                        d.tag = ruleEditPopup?.urlTagText
                        App.instances.daoSession.blackUrlDao.update(d)
                        AdBlock.loadDomains(activity)
                        SimpleToast.makeToast(activity, "更新规则成功", Toast.LENGTH_LONG).show()
                    }
                }
                reloadData()
            }
            // add data
            // sp!!.edit().putString(context.getString(R.string.sp_user_agent_custom), uaEditAlert!!.text).apply()
        }
        ruleEditPopup = build.build()
    }

    fun reloadData() {
        data.clear()
        if (isWhite) {
            (data as ArrayList<WhiteDomain>).addAll(App.instances.daoSession.whiteDomainDao.loadAll())
            adapterW?.notifyDataSetChanged()
        } else {
            (data as ArrayList<BlackUrl>).addAll(App.instances.daoSession.blackUrlDao.loadAll())
            adapterB?.notifyDataSetChanged()
        }
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
                    if (isWhite) {
                        d as WhiteDomain
                        App.instances.daoSession.whiteDomainDao.delete(d)
                    } else {
                        d as BlackUrl
                        App.instances.daoSession.blackUrlDao.delete(d)
                    }
                    reloadData()
                    AdBlock.loadDomains(activity)
                    SimpleToast.makeToast(activity, "删除成功", Toast.LENGTH_LONG).show()
                }
                R.id.rule_edit_edit -> {
                    val d = data[selectedIndex]
                    if (isWhite) {
                        d as WhiteDomain
                        ruleEditPopup?.setUp(d.domain, d.tag)
                    } else {
                        d as BlackUrl
                        ruleEditPopup?.setUp(d.domain, d.tag)
                    }
                    ruleEditPopup?.show()
                }
            }
        }
        root.findViewById<View>(R.id.rule_edit_del).setOnClickListener(lis)
        root.findViewById<View>(R.id.rule_edit_edit).setOnClickListener(lis)
        popup = SimplePopupWindow(view, root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popup?.animationStyle = R.style.popwin_anim_style
    }

    @Subscribe
    fun onFragmentAction(action: FragmentAction) {
        if (action.action == FragmentAction.ACTION.MENU_ITEM) {
            when(action.tag) {
                R.id.clear_tag -> {

                    val build = SimpleAlert.Build(activity, R.style.SimpleAlert)
                    build.content = "全部清除？"
                    build.onPos = View.OnClickListener { view ->
                        if (isWhite) {
                            App.instances.daoSession.whiteDomainDao.deleteAll()
                        } else {
                            App.instances.daoSession.blackUrlDao.deleteAll()
                        }
                        reloadData()
                        AdBlock.loadDomains(activity)
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
