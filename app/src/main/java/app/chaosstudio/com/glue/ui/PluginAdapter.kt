package app.chaosstudio.com.glue.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.greendb.model.BlackUrl
import app.chaosstudio.com.glue.greendb.model.Plugin
import app.chaosstudio.com.glue.greendb.model.WhiteDomain

class PluginAdapter(private val context0: Context, private val list: List<Plugin>) : ArrayAdapter<Plugin>(context0, R.layout.plugin_item, list) {
    private val layoutResId: Int = R.layout.plugin_item

    private class Holder {
        internal var tag: TextView? = null
        internal var filter: TextView? = null
        internal var js: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: Holder
        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(context).inflate(layoutResId, parent, false)
            holder = Holder()
            holder.tag = view!!.findViewById(R.id.plugin_tag)
            holder.filter = view.findViewById(R.id.plugin_filter)
            holder.js = view.findViewById(R.id.plugin_js)
            view.tag = holder
        } else {
            holder = view.tag as Holder
        }

        val domain = list[position]
        holder.tag!!.text = domain.tag
        holder.filter!!.text = domain.filter
        holder.js!!.text = domain.js

        return view
    }
}