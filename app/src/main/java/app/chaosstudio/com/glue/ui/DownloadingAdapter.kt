package app.chaosstudio.com.glue.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.activity.download.ActivityDownload
import app.chaosstudio.com.glue.download.DownloadManager

/**
 * Created by jsen on 2018/2/1.
 */

class DownloadingAdapter(private val context0: Context, private val list: List<ActivityDownload.DownloadingMode>) : ArrayAdapter<ActivityDownload.DownloadingMode>(context0, R.layout.list_fragment_downloading_item, list) {
    private val layoutResId: Int = R.layout.list_fragment_downloading_item

    private class Holder {
        internal var title: TextView? = null
        internal var progressBar: ProgressBar? = null
        internal var icon: ImageView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: Holder
        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(context0).inflate(layoutResId, parent, false)
            holder = Holder()
            holder.title = view!!.findViewById(R.id.download_title)
            holder.progressBar = view.findViewById(R.id.download_progress)
            holder.icon = view.findViewById(R.id.icon)
            view.tag = holder
        } else {
            holder = view.tag as Holder
        }

        val bookMark = list[position]
        holder.title!!.text = bookMark.url
        holder.icon!!.tag = bookMark.isDownloading
        if (bookMark.isDownloading) {
            holder.icon!!.setImageResource(R.mipmap.icon_progress_stop)
        } else {
            holder.icon!!.setImageResource(R.mipmap.icon_progress_go)
        }
        holder.icon!!.setOnClickListener{v ->
            if (v.tag as Boolean) {
                (v as ImageView).setImageResource(R.mipmap.icon_progress_go)
                SimpleToast.makeToast(context, "暂停下载", Toast.LENGTH_LONG).show()
                list[position].isDownloading = false
                v.tag = false
                DownloadManager.downloadManager!!.stopTask(bookMark.url)
            } else {
                Thread({
                    DownloadManager.downloadManager!!.startTask(bookMark.url)
                }).start()
            }
        }
        holder.progressBar!!.progress = bookMark.progress
        view.setTag(R.id.list_item_custom, position)
        return view
    }
}
