package app.chaosstudio.com.glue.ui

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.HashSet

import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.greendb.model.BookMark
import app.chaosstudio.com.glue.greendb.model.History

/**
 * Created by jsen on 2018/1/23.
 */

class CompleteAdapter(private val context0: Context, hisList: List<History>, markList: List<BookMark>) : BaseAdapter(), Filterable {
    private val layoutResId: Int = R.layout.complete_item
    private val originalList: MutableList<CompleteItem>
    private val resultList: MutableList<CompleteItem>
    private val filter = CompleteFilter()

    private inner class CompleteFilter : Filter() {
        override fun performFiltering(prefix: CharSequence?): Filter.FilterResults {
            if (prefix == null) {
                return Filter.FilterResults()
            }

            resultList.clear()
            for (item in originalList) {
                if (item.title!!.contains(prefix) || item.url!!.contains(prefix)) {
                    if (item.title.contains(prefix)) {
                        item.index = item.title.indexOf(prefix.toString())
                    } else if (item.url!!.contains(prefix)) {
                        item.index = item.url.indexOf(prefix.toString())
                    }
                    resultList.add(item)
                }
            }

            Collections.sort(resultList) { first, second ->
                when {
                    first.index < second.index -> -1
                    first.index > second.index -> 1
                    else -> 0
                }
            }

            val results = Filter.FilterResults()
            results.values = resultList
            results.count = resultList.size

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults) {
            notifyDataSetChanged()
        }
    }

    private inner class CompleteItem internal constructor(internal val title: String?, internal val url: String?, val typeIconRes:Int) {

        internal var index = Integer.MAX_VALUE

        override fun equals(`object`: Any?): Boolean {
            if (`object` !is CompleteItem) {
                return false
            }

            val item = `object` as CompleteItem?
            return item!!.title == title && item.url == url
        }

        override fun hashCode(): Int {
            return if (title == null || url == null) {
                0
            } else title.hashCode() and url.hashCode()

        }
    }

    private class Holder {
        internal var titleView: TextView? = null
        internal var urlView: TextView? = null
        internal var icon: ImageView? = null
    }

    init {
        this.originalList = ArrayList()
        this.resultList = ArrayList()
        deDup(hisList, markList)
    }

    private fun deDup(hisList: List<History>, markList: List<BookMark>) {
        for (his in hisList) {
            if (!TextUtils.isEmpty(his.name) && !TextUtils.isEmpty(his.url)) {
                originalList.add(CompleteItem(his.name, his.url, R.mipmap.icon_his))
            }
        }
        for (mark in markList) {
            if (!TextUtils.isEmpty(mark.name) && !TextUtils.isEmpty(mark.url)) {
                originalList.add(CompleteItem(mark.name, mark.url, R.mipmap.icon_book_mark))
            }
        }

        val set = HashSet(originalList)
        originalList.clear()
        originalList.addAll(set)
    }

    override fun getCount(): Int {
        return resultList.size
    }

    override fun getFilter(): Filter {
        return filter
    }

    override fun getItem(position: Int): Any {
        return resultList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View? = convertView
        val holder: Holder

        if (view == null) {
            view = LayoutInflater.from(context0).inflate(layoutResId, null, false)
            holder = Holder()
            holder.titleView = view!!.findViewById(R.id.complete_item_title)
            holder.urlView = view.findViewById(R.id.complete_item_url)
            holder.icon = view.findViewById(R.id.complete_item_icon)
            view.tag = holder
        } else {
            holder = view.tag as Holder
        }

        val item = resultList[position]
        holder.titleView!!.text = item.title
        if (item.url != null) {
            holder.urlView!!.text = item.url
        } else {
            holder.urlView!!.text = item.url
        }
        holder.icon?.setImageResource(item.typeIconRes)

        return view
    }
}
