package app.chaosstudio.com.glue.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import app.chaosstudio.com.glue.R

/**
 * Created by jsen on 2018/2/3.
 */
class PopupSimpleAdapter internal constructor(private val context0: Context, private val list: List<String>) : ArrayAdapter<String>(context0, R.layout.popup_simple_item, list) {
    private val layoutResId = R.layout.popup_simple_item

    private class Holder {
        internal var textView: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: Holder
        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(context).inflate(layoutResId, parent, false)
            holder = Holder()
            holder.textView = view!!.findViewById(R.id.popup_simple_item_txt)
            view.tag = holder
        } else {
            holder = view.tag as Holder
        }

        holder.textView!!.text = list[position]

        return view
    }
}