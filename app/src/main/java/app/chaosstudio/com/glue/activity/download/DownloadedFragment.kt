package app.chaosstudio.com.glue.activity.download

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import app.chaosstudio.com.glue.App
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.activity.set.FragmentBase
import app.chaosstudio.com.glue.download.DownloadManager
import app.chaosstudio.com.glue.greendb.gen.DownloadModeDao
import app.chaosstudio.com.glue.ui.*
import kotlinx.android.synthetic.main.list_fragment_history.*
import android.media.MediaMetadataRetriever
import android.util.Log
import app.chaosstudio.com.glue.activity.media.AudioPlay
import app.chaosstudio.com.glue.activity.media.VideoPlay
import java.io.File


/**
 * Created by jsen on 2018/1/25.
 */

class DownloadedFragment : FragmentBase() {

    var data = App.instances.daoSession.downloadModeDao.queryBuilder().where(DownloadModeDao.Properties.IsFinished.eq(true)).build().list()
    var adapter: DownloadedAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.simple_listview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        checkView()
        adapter = DownloadedAdapter(activity, data)
        adapter?.notifyDataSetChanged()
        list_history.adapter = adapter

        list_history.setOnItemClickListener { parent, view, position, id ->
            val mod = App.instances.daoSession.downloadModeDao.load(data[position].id)
            if (mod != null && !TextUtils.isEmpty(mod.path)) {
                if (File(mod.path).exists()) {
                    val mimetype = getType(mod.path)
                    if (mimetype == "video/*") {
                        val intent = Intent(activity, VideoPlay::class.java)
                        intent.putExtra("path", "file://" + mod.path)
                        activity.startActivity(intent)
                    } else if (mimetype == "audio/*") {
                        val intent = Intent(activity, AudioPlay::class.java)
                        intent.putExtra("path", "file://" + mod.path)
                        activity.startActivity(intent)
                    } else {
                        val intent = Intent(Intent.ACTION_VIEW)
                        val mt = getMimeType(mod.path)
                        if (mt != null) {
                            Log.e("MIMETYPE", mod.path)
                            Log.e("MIMETYPE", mt)
                            intent.setDataAndType(Uri.parse(mod.path), mt)
                            if (intent.resolveActivity(activity.packageManager) != null) {
                                activity.startActivity(intent)
                                activity.overridePendingTransition(0, 0)
                            }
                        }
                    }

                }
            }
        }

        list_history.setOnItemLongClickListener { parent, view, position, id ->
            val build = SimpleAlert.Build(activity, R.style.SimpleAlert)
            build.content = "删除该记录？"
            build.showTitle = true
            build.title = "删除记录"
            build.onPos = View.OnClickListener { _ ->
                val m = data.removeAt(position)
                DownloadManager.downloadManager!!.stopTask(m.url)
                App.instances.daoSession.downloadModeDao.deleteByKey(m.id)
                adapter!!.notifyDataSetChanged()
            }
            build.build().show()
            true
        }
    }

    fun reloadData() {
        data.clear()
        data.addAll(App.instances.daoSession.downloadModeDao.queryBuilder().where(DownloadModeDao.Properties.IsFinished.eq(true)).build().list())
        adapter!!.notifyDataSetChanged()
        checkView()
    }
    fun checkView() {
        if (data.isEmpty()) {
            record_list_empty.visibility = View.VISIBLE
        }
    }

    fun getType(p:String):String? {
        val path = p.toLowerCase()
        return if (path.endsWith(".mp3") ||
                path.endsWith(".wav") ||
                path.endsWith(".midi") ||
                path.endsWith(".cda") ||
                path.endsWith(".flac")
        ) {
            "audio/*"
        } else if (path.endsWith(".mp4") ||
                path.endsWith(".avi") ||
                path.endsWith(".rmvb") ||
                path.endsWith(".rm") ||
                path.endsWith(".asf") ||
                path.endsWith(".divx") ||
                path.endsWith(".ts") ||
                path.endsWith(".wmv") ||
                path.endsWith(".mkv") ||
                path.endsWith(".m3u8") ||
                path.endsWith(".vob") ||
                path.endsWith(".mpg")
        ) {
            "video/*"
        }  else if (path.endsWith(".mpg") ||
                path.endsWith(".mpeg") ||
                path.endsWith(".rmvb") ||
                path.endsWith(".rm") ||
                path.endsWith(".asf") ||
                path.endsWith(".divx") ||
                path.endsWith(".mpg")
        ) {
            "image/*"
        } else {
            null
        }
    }

    fun getMimeType(filePath: String?): String? {
        val mmr = MediaMetadataRetriever()
        var mime:String? = null
        if (!TextUtils.isEmpty(filePath)) {
            try {
                mmr.setDataSource(filePath)
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
            } catch (e: IllegalStateException) {
                return mime
            } catch (e: IllegalArgumentException) {
                return mime
            } catch (e: RuntimeException) {
                return mime
            }

        }
        return mime
    }
}
