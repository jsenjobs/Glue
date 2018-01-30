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
import app.chaosstudio.com.glue.greendb.model.BookMark
import app.chaosstudio.com.glue.ui.BookMarksAdapter
import app.chaosstudio.com.glue.ui.SimplePopupWindow
import app.chaosstudio.com.glue.ui.SimpleToast
import kotlinx.android.synthetic.main.list_fragment_book_marks.*

/**
 * Created by jsen on 2018/1/23.
 */

class ListBookMarksFragment : FragmentBase() {
    init {
        title = "书签"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_fragment_book_marks, container, false)
    }


    var data = App.instances.daoSession.bookMarkDao.loadAll()
    var adapter:BookMarksAdapter? = null
    var tX = 0f
    var tY = 0f
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        if (data == null || data.isEmpty()) {
            record_list_empty.visibility = View.VISIBLE
        }
        adapter = BookMarksAdapter(activity, data)
        adapter?.notifyDataSetChanged()
        list_book_marks.adapter = adapter

        list_book_marks.setOnItemClickListener { parent, view, position, id ->
            WebViewAction.fire(WebViewAction.ACTION.GO, view.findViewById<TextView>(R.id.record_item_url).text.toString())
            activity.finish()
        }

        list_book_marks.setOnItemLongClickListener { parent, view, position, id ->
            selectedIndex = position

            popup!!.updateAnchor(view, tX.toInt(), tY.toInt())
            popup!!.showAtLocation(Gravity.TOP or Gravity.START)
            true
        }

        list_book_marks.setOnTouchListener { v, event ->
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
        val root = View.inflate(activity, R.layout.popup_book_mark_edit, null)
        val lis = View.OnClickListener{ view ->
            popup?.dismiss()
            when(view.id) {
                R.id.book_mark_edit_del -> {
                    App.instances.daoSession.bookMarkDao.delete(data[selectedIndex])
                    data.clear()
                    data.addAll(App.instances.daoSession.bookMarkDao.loadAll())
                    adapter?.notifyDataSetChanged()
                    SimpleToast.makeToast(activity, "删除成功", Toast.LENGTH_LONG).show()
                    FragmentAction.fire(FragmentAction.ACTION.URL_MARKED, 0)
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
}