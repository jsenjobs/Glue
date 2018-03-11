package app.chaosstudio.com.glue.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.*
import android.webkit.URLUtil
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import app.chaosstudio.com.glue.App
import app.chaosstudio.com.glue.GPre
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.activity.media.AudioPlay
import app.chaosstudio.com.glue.activity.media.VideoPlay
import app.chaosstudio.com.glue.activity.set.FragmentBase
import app.chaosstudio.com.glue.download.DownloadManager
import app.chaosstudio.com.glue.greendb.model.BlackUrl
import app.chaosstudio.com.glue.greendb.model.LogMode
import app.chaosstudio.com.glue.greendb.model.PlayList
import app.chaosstudio.com.glue.ui.*
import app.chaosstudio.com.glue.unit.BrowserUnit
import app.chaosstudio.com.glue.utils.MediaUtil
import app.chaosstudio.com.glue.video.M3U8
import app.chaosstudio.com.glue.webconfig.AdBlock
import app.chaosstudio.com.glue.webconfig.WebViewManager
import kotlinx.android.synthetic.main.list_fragment_history.*
import java.io.File
import java.util.*

/**
 * Created by jsen on 2018/1/23.
 */

class ListResourcesFragment : FragmentBase() {
    init {
        title = "资源嗅探"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_fragment_history, container, false)
    }

    var data:List<LogMode> ? = null
    var adapter: LogsAdapter? = null
    var tX = 0f
    var tY = 0f
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val wv =WebViewManager.getCurrentActive()
        if (wv == null) {
            activity.finish()
            return
        }
        data = wv.logs.findResources(wv.uuid)

        if (data == null || data!!.isEmpty()) {
            record_list_empty.visibility = View.VISIBLE
            return
        }
        adapter = LogsAdapter(activity, data!!)
        adapter?.notifyDataSetChanged()
        list_history.adapter = adapter

        list_history.setOnItemClickListener { parent, view, position, id ->
            selectedUrl = view.findViewById<TextView>(R.id.log_item_url).text.toString()
            popup!!.updateAnchor(view, tX.toInt(), tY.toInt())
            popup!!.showAtLocation(Gravity.TOP or Gravity.START)
        }

        /*
        val m3u8File = M3U8.buildM3U8(url)
        if (m3u8File != null) {
            M3U8.download(m3u8File, Environment.getExternalStorageDirectory().toString() + File.separator + GPre.downloadDir + File.separator + "tmp",
                    Environment.getExternalStorageDirectory().toString() + File.separator + GPre.downloadDir + File.separator + URLUtil.guessFileName(url, url, "text/texmacs"))
        } else {
            SimpleToast.makeToast(activity, "无法获取文件信息", Toast.LENGTH_LONG).show()
        }
        */
        list_history.setOnItemLongClickListener { parent, view, position, id ->
            val url = view.findViewById<TextView>(R.id.log_item_url).text.toString()
            val build = SimpleAlert.Build(activity, R.style.SimpleAlert)
            build.content = url
            build.showTitle = true
            build.title = "添加到黑名单？"
            build.onPos = View.OnClickListener { _ ->
                ruleEditPopup?.urlEditText?.setText(url)
                ruleEditPopup?.urlTagEditText?.setText("log标记")
                ruleEditPopup?.show()
            }
            build.build().show()
            true
        }

        list_history.setOnTouchListener { v, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    tX = event.rawX
                    tY = event.rawY
                }
                MotionEvent.ACTION_MOVE-> {
                    tX = event.rawX
                    tY = event.rawY
                }
                MotionEvent.ACTION_UP -> {
                    tX = event.rawX
                    tY = event.rawY
                }
            }
            false
        }
        createAddAlert()
        createPopupMenu()
    }



    fun filterCommonResource(uri: Uri):Boolean {
        val path = uri.path.toLowerCase()
        return path.endsWith(".mp4") ||
                path.endsWith(".mp3") ||
                path.endsWith(".wav") ||
                path.endsWith(".midi") ||
                path.endsWith(".cda") ||
                path.endsWith(".wma") ||
                path.endsWith(".flac") ||
                path.endsWith(".avi") ||
                path.endsWith(".rmvb") ||
                path.endsWith(".rm") ||
                path.endsWith(".asf") ||
                path.endsWith(".divx") ||
                path.endsWith(".mpg") ||
                path.endsWith(".mpeg") ||
                path.endsWith(".wmv") ||
                path.endsWith(".mkv") ||
                path.endsWith(".m3u8") ||
                path.endsWith(".vob")//  ||
                // path.endsWith(".m3u8")
    }

    var ruleEditPopup:RuleEditPopup? = null
    fun createAddAlert() {
        val build = RuleEditPopup.Build(activity, R.style.SimpleAlert)
        build.onPos = View.OnClickListener { _ ->
            if (!TextUtils.isEmpty(ruleEditPopup?.urlText) && !TextUtils.isEmpty(ruleEditPopup?.urlTagText)) {
                val w = BlackUrl()
                w.domain = ruleEditPopup?.urlText
                w.tag = ruleEditPopup?.urlTagText
                App.instances.daoSession.blackUrlDao.save(w)
                AdBlock.loadDomains(activity)
                SimpleToast.makeToast(activity, "标记成功", Toast.LENGTH_LONG).show()
            }
            // add data
            // sp!!.edit().putString(context.getString(R.string.sp_user_agent_custom), uaEditAlert!!.text).apply()
        }
        ruleEditPopup = build.build()
    }

    var popup:SimplePopupWindow? = null
    var selectedUrl = ""
    fun createPopupMenu() {
        val strings = resources.getStringArray(R.array.popup_resource)
        val list = ArrayList<String>()
        list.addAll(Arrays.asList(*strings))
        val root = View.inflate(activity, R.layout.popup_simple, null)


        val listView = root.findViewById<ListView>(R.id.popup_simple_container)
        val adapter = PopupSimpleAdapter(activity, list)
        listView.adapter = adapter
        adapter.notifyDataSetChanged()


        listView.setOnItemClickListener { parent, view, position, id ->
            popup?.dismiss()
            when(position) {
                0 -> {
                    val mimetype = MediaUtil.getType(Uri.parse(selectedUrl).path)
                    when (mimetype) {
                        "audio/*" -> {
                            val intent = Intent(activity, AudioPlay::class.java)
                            intent.putExtra("path", selectedUrl)
                            activity.startActivity(intent)
                        }
                        "video/*" -> {
                            val intent = Intent(activity, VideoPlay::class.java)
                            intent.putExtra("path", selectedUrl)
                            activity.startActivity(intent)
                        }
                        else -> {
                            SimpleToast.makeToast(activity, "无法播放该资源", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                1 -> {
                    if (filterCommonResource(Uri.parse(selectedUrl))) {
                        Thread({
                            DownloadManager.downloadManager!!.createTask(selectedUrl)
                        }).start()
                    } else {
                        SimpleToast.makeToast(activity, "不支持该资源的下载", Toast.LENGTH_LONG).show()
                    }
                }
                2 -> {
                    val playList = PlayList()
                    playList.time = System.currentTimeMillis()
                    playList.url = selectedUrl
                    App.instances.daoSession.playListDao.save(playList)
                    SimpleToast.makeToast(activity, "添加成功", Toast.LENGTH_LONG).show()
                }
                3 -> {
                    BrowserUnit.copyURL(activity, selectedUrl)
                }
            }
        }
        popup = SimplePopupWindow(view, root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popup?.animationStyle = R.style.popwin_anim_style
    }
}
