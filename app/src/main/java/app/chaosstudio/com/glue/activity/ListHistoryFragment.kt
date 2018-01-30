package app.chaosstudio.com.glue.activity

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import app.chaosstudio.com.glue.App
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.activity.set.FragmentBase
import app.chaosstudio.com.glue.eventb.FragmentAction
import app.chaosstudio.com.glue.eventb.WebViewAction
import app.chaosstudio.com.glue.ui.HistoryAdapter
import app.chaosstudio.com.glue.ui.SimpleAlert
import app.chaosstudio.com.glue.ui.SimplePopupWindow
import app.chaosstudio.com.glue.ui.SimpleToast
import kotlinx.android.synthetic.main.list_fragment_history.*
import org.greenrobot.eventbus.Subscribe

/**
 * Created by jsen on 2018/1/23.
 */

class ListHistoryFragment : FragmentBase() {
    init {
        title = "历史记录"
        showMenu = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_fragment_history, container, false)
    }

    var data = App.instances.daoSession.historyDao.loadAll()
    var adapter: HistoryAdapter? = null
    var tX = 0f
    var tY = 0f
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (data == null || data.isEmpty()) {
            record_list_empty.visibility = View.VISIBLE
        }
        adapter = HistoryAdapter(activity, data)
        adapter?.notifyDataSetChanged()
        list_history.adapter = adapter

        list_history.setOnItemClickListener { parent, view, position, id ->
            WebViewAction.fire(WebViewAction.ACTION.GO, view.findViewById<TextView>(R.id.record_item_url).text.toString())
            activity.finish()
        }

        list_history.setOnItemLongClickListener { parent, view, position, id ->
            selectedIndex = position

            // popup!!.updateAnchor(view)
            popup!!.updateAnchor(view, tX.toInt(), tY.toInt())
            popup!!.showAtLocation(Gravity.TOP or Gravity.START)
            true
        }

        list_history.setOnTouchListener { v, event ->
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

    }



    var popup: SimplePopupWindow? = null
    var selectedIndex = -1
    fun createPopup() {
        val root = View.inflate(activity, R.layout.popup_his_edit, null)
        val lis = View.OnClickListener{ view ->
            popup?.dismiss()
            when(view.id) {
                R.id.book_mark_edit_del -> {
                    App.instances.daoSession.historyDao.delete(data[selectedIndex])
                    data.clear()
                    data.addAll(App.instances.daoSession.historyDao.loadAll())
                    adapter?.notifyDataSetChanged()
                    SimpleToast.makeToast(activity, "删除成功", Toast.LENGTH_LONG).show()
                }
                R.id.book_mark_edit_go -> {
                    WebViewAction.fire(WebViewAction.ACTION.GO, data[selectedIndex].url)
                    activity.finish()
                }
            }
        }
        root.findViewById<View>(R.id.book_mark_edit_go).setOnClickListener(lis)
        root.findViewById<View>(R.id.book_mark_edit_del).setOnClickListener(lis)
        popup = SimplePopupWindow(view, root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popup?.animationStyle = R.style.popwin_anim_style
    }

    @Subscribe
    fun onFragmentAction(action:FragmentAction) {
        if (action.action == FragmentAction.ACTION.MENU_ITEM) {
            when(action.tag) {
                R.id.clear_tag -> {

                    val build = SimpleAlert.Build(activity, R.style.SimpleAlert)
                    build.content = "全部清除？"
                    build.onPos = View.OnClickListener { view ->
                        App.instances.daoSession.historyDao.deleteAll()
                        data.clear()
                        data.addAll(App.instances.daoSession.historyDao.loadAll())
                        adapter?.notifyDataSetChanged()
                        SimpleToast.makeToast(activity, "清除成功", Toast.LENGTH_LONG).show()
                    }
                    build.build().show()
                }
            }
        }
    }
}
