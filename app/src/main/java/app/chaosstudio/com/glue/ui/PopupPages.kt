package app.chaosstudio.com.glue.ui

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.preference.Preference
import android.preference.PreferenceManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*

import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.eventb.WebViewAction
import app.chaosstudio.com.glue.unit.BrowserUnit
import app.chaosstudio.com.glue.webconfig.WebViewManager

/**
 * Created by jsen on 2018/1/21.
 */

class PopupPages(context: Context, themeResId: Int) : Dialog(context, themeResId) {
    var listView:ListView? = null
    private var lAdapter:ListAdapter? = null
    init {
        val root = View.inflate(context, R.layout.popup_pages, null)
        listView = root.findViewById(R.id.lst_pages)
        lAdapter = ListAdapter(context, this)
        listView!!.adapter = lAdapter
        listView!!.setOnItemClickListener { parent, view, position, id ->
            dismiss()
            WebViewAction.fire(WebViewAction.ACTION.CHANGEPAGE, position)
        }

        setContentView(root)

        val window = window
        window!!.setGravity(Gravity.BOTTOM)
        window.setDimAmount(0.0f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val lp = window.attributes
        lp.x = 0
        // lp.y = (int)context.getResources().getDimension(R.dimen.action_bar_height);
        lp.y = 0
        lp.width = context.resources.displayMetrics.widthPixels
        // root.measure(0, 0)
        // lp.height = root.measuredHeight
        val lis = View.OnClickListener{view ->
            dismiss()
            when(view.id) {
                R.id.pages_add_page -> WebViewAction.fire(WebViewAction.ACTION.CREATEPAGE, BrowserUnit.getHome(context))
                R.id.pages_close_all_pages -> WebViewAction.fire(WebViewAction.ACTION.CLOSEPAGES)
                R.id.pages_his_browser -> {
                    WebViewManager.setNoHis(!WebViewManager.isNoHis())
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(context.getString(R.string.sp_no_his_mode), WebViewManager.isNoHis()).apply()
                    if (WebViewManager.isNoHis()) {
                        root.findViewById<ImageView>(R.id.pages_his_browser).setImageResource(R.mipmap.action_no_his)
                        SimpleToast.makeToast(context, "已开启无痕模式", Toast.LENGTH_LONG).show()
                    } else {
                        root.findViewById<ImageView>(R.id.pages_his_browser).setImageResource(R.mipmap.action_his)
                        SimpleToast.makeToast(context, "已关闭无痕模式", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        if (WebViewManager.isNoHis()) {
            root.findViewById<ImageView>(R.id.pages_his_browser).setImageResource(R.mipmap.action_no_his)
        } else {
            root.findViewById<ImageView>(R.id.pages_his_browser).setImageResource(R.mipmap.action_his)
        }
        root.findViewById<View>(R.id.more_bt_dis).setOnClickListener(lis)
        root.findViewById<View>(R.id.pages_add_page).setOnClickListener(lis)
        root.findViewById<View>(R.id.pages_close_all_pages).setOnClickListener(lis)
        root.findViewById<View>(R.id.pages_his_browser).setOnClickListener(lis)
        lp.alpha = 0.95f
        window.attributes = lp
    }

    override fun show() {
        lAdapter!!.notifyDataSetChanged()
        super.show()
    }

    private class ListAdapter(val context: Context, val popupPages: PopupPages) : BaseAdapter() {

        override fun getCount(): Int {
            return WebViewManager.getSize()
        }

        override fun getItem(position: Int): Any? {
            return null
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            var c = convertView
            if (c == null) {
                c = View.inflate(context, R.layout.popup_page_item, null)
            }
            if (position != WebViewManager.getCurrentActiveIndex()) {
                c!!.setBackgroundResource(R.color.colorPrimary)
            } else {
                c!!.setBackgroundResource(R.color.colorLightGray)
            }
            val icon = c.findViewById<ImageView>(R.id.page_item_icon)
            // val num = c.findViewById<TextView>(R.id.page_item_num)
            val title = c.findViewById<TextView>(R.id.page_item_title)
            val closeBtn = c.findViewById<View>(R.id.page_item_close_page)
            closeBtn.tag = position
            c.findViewById<View>(R.id.page_item_close_page).setOnClickListener{view ->
                val pos = view.tag
                if (pos is Int) {
                    WebViewAction.fire(WebViewAction.ACTION.CLOSEPAGE, pos)
                }
                notifyDataSetChanged()
            }
            val data = WebViewManager.get(position)
            title.text = data.title
            if (data.url.startsWith("http")) {
                val bmp = data.favicon
                if (bmp != null) {
                    icon.setImageBitmap(bmp)
                } else {
                    icon.setImageResource(R.mipmap.default_fav)
                }
            } else {
                icon.setImageResource(R.mipmap.default_fav)
            }
            // num.text = (position + 1).toString()
            return c
        }
    }
}
