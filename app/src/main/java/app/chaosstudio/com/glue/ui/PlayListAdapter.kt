package app.chaosstudio.com.glue.ui

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import app.chaosstudio.com.glue.GPre
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.greendb.model.DownloadMode
import app.chaosstudio.com.glue.greendb.model.PlayList
import java.io.File
import java.util.*

/**
 * Created by jsen on 2018/2/1.
 *
 */

class PlayListAdapter(private val context0: Context, private val list: List<PlayList>) : ArrayAdapter<PlayList>(context0, R.layout.list_fragment_playlist_item, list) {
    private val layoutResId: Int = R.layout.list_fragment_playlist_item
    private val trueDir = checkDirectory(GPre.downloadDir)

    private class Holder {
        internal var url: TextView? = null
        internal var title: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: Holder
        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(context0).inflate(layoutResId, parent, false)
            holder = Holder()
            holder.title = view!!.findViewById(R.id.playlist_title)
            holder.url = view.findViewById(R.id.playlist_url)
            view.tag = holder
        } else {
            holder = view.tag as Holder
        }

        val bookMark = list[position]
        holder.title!!.text = getFileName(bookMark.url, trueDir)
        holder.url!!.text = bookMark.url
        return view
    }

    private fun checkDirectory(saveDir:String):String {
        val file = File(Environment.getExternalStorageDirectory(), saveDir)
        if (!file.mkdirs()) {
            file.createNewFile()
        }
        return file.absolutePath
    }
    private fun getFileName(url: String, trueSaveDir:String):String {
        try {
            val uri = Uri.parse(url)
            return uri.path.substring(url.lastIndexOf("/") + 1)
        } catch (e:Exception) {
            while (true) {
                val uuid = UUID.randomUUID().toString()
                if (!File(trueSaveDir, uuid).exists()) {
                    return uuid
                }
            }
        }
    }
}
