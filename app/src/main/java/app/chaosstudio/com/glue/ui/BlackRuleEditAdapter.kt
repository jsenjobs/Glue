package app.chaosstudio.com.glue.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.greendb.model.BlackUrl
import app.chaosstudio.com.glue.greendb.model.WhiteDomain

class BlackRuleEditAdapter(private val context0: Context, private val list: List<BlackUrl>) : ArrayAdapter<BlackUrl>(context0, R.layout.ad_rule_item, list) {
    private val layoutResId: Int = R.layout.ad_rule_item

    private class Holder {
        internal var tag: TextView? = null
        internal var url: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: Holder
        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(context).inflate(layoutResId, parent, false)
            holder = Holder()
            holder.tag = view!!.findViewById(R.id.ad_rule_url_tag)
            holder.url = view.findViewById(R.id.ad_rule_url)
            view.tag = holder
        } else {
            holder = view.tag as Holder
        }

        val domain = list[position]
        holder.tag!!.text = domain.tag
        holder.url!!.text = domain.domain

        return view
    }
}