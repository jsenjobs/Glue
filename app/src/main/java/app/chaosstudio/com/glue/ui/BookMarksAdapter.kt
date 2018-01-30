package app.chaosstudio.com.glue.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import com.github.curioustechizen.ago.RelativeTimeTextView

import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.greendb.model.BookMark
import app.chaosstudio.com.glue.greendb.model.History

class BookMarksAdapter(private val context0: Context, private val list: List<BookMark>) : ArrayAdapter<BookMark>(context0, R.layout.list_fragment_record_item, list) {
    private val layoutResId: Int = R.layout.list_fragment_record_item

    private class Holder {
        internal var title: TextView? = null
        internal var time: RelativeTimeTextView? = null
        internal var url: TextView? = null
        internal var icon: ImageView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: Holder
        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(context0).inflate(layoutResId, parent, false)
            holder = Holder()
            holder.title = view!!.findViewById(R.id.record_item_title)
            holder.time = view.findViewById(R.id.record_item_time)
            holder.url = view.findViewById(R.id.record_item_url)
            holder.icon = view.findViewById(R.id.icon)
            view.tag = holder
        } else {
            holder = view.tag as Holder
        }

        val bookMark = list[position]
        holder.title!!.text = bookMark.name
        holder.time!!.setReferenceTime(bookMark.date)
        holder.url!!.text = bookMark.url
        holder.icon!!.setImageResource(R.mipmap.icon_book_mark)

        return view
    }
}