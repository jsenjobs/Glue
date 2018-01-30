package app.chaosstudio.com.glue.ui

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView


import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.greendb.model.LogMode
import java.text.SimpleDateFormat
import java.util.*

class LogsAdapter(private val context0: Context, private val list: List<LogMode>) : ArrayAdapter<LogMode>(context0, R.layout.list_fragment_logs_item, list) {
    private val layoutResId: Int = R.layout.list_fragment_logs_item

    var dateFm = SimpleDateFormat("yyyy/MM/dd HH:mm:ss:ms", Locale.getDefault())
    private class Holder {
        internal var icon: ImageView? = null
        internal var time: TextView? = null
        internal var url: TextView? = null
        internal var type: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: Holder
        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(context0).inflate(layoutResId, parent, false)
            holder = Holder()
            holder.icon = view.findViewById(R.id.icon)
            holder.time = view.findViewById(R.id.log_item_time)
            holder.url = view.findViewById(R.id.log_item_url)
            holder.type = view.findViewById(R.id.log_item_type)
            view.tag = holder
        } else {
            holder = view.tag as Holder
        }

        val bookMark = list[position]
        val res = if (bookMark.loaded) {
            R.mipmap.icon_his
        } else {
            R.mipmap.icon_bel
        }
        holder.icon!!.setImageResource(res)
        holder.time!!.text = dateFm.format(Date(bookMark.timestamp))
        holder.url!!.text = bookMark.url
        try {
            val path = Uri.parse(bookMark.url).path
            val sub = path.substring(path.lastIndexOf("."), path.length)
            holder.type!!.text = if (TextUtils.isEmpty(sub)) {
                "NULL"
            } else {
                sub
            }
        } catch (e:Exception) {
            e.printStackTrace()
            holder.type!!.text = "NULL"
        }

        return view!!
    }
}