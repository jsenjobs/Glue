package app.chaosstudio.com.glue.activity.download

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import app.chaosstudio.com.glue.App
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.activity.set.FragmentBase
import app.chaosstudio.com.glue.download.DownloadManager
import app.chaosstudio.com.glue.download.parse.M3U8Parse
import app.chaosstudio.com.glue.eventb.DownloadAction
import app.chaosstudio.com.glue.greendb.gen.DownloadModeDao
import app.chaosstudio.com.glue.ui.*
import kotlinx.android.synthetic.main.list_fragment_history.*
import org.greenrobot.eventbus.Subscribe
import java.lang.ref.WeakReference

/**
 * Created by jsen on 2018/1/25.
 *
 */

class DownloadingFragment : FragmentBase() {

    var data = ArrayList<ActivityDownload.DownloadingMode>()
    var uiHandler:UIHandler? = null
    init {
        val d = App.instances.daoSession.downloadModeDao.queryBuilder().where(DownloadModeDao.Properties.IsFinished.eq(false)).build().list()
        for (item in d) {
            val dm = ActivityDownload.DownloadingMode(item.id, item.url)
            if (DownloadManager.downloadManager!!.isTaskExist(item.url)) {
                dm.isDownloading = true
            }
            data.add(dm)
        }
    }
    var adapter: DownloadingAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.simple_listview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        checkView()
        adapter = DownloadingAdapter(activity, data)
        adapter?.notifyDataSetChanged()
        list_history.adapter = adapter

        /*
        list_history.setOnItemClickListener { parent, view, position, id ->
            if (data[position].isDownloading) {
                SimpleToast.makeToast(activity, "取消下载", Toast.LENGTH_LONG).show()
                data[position].isDownloading = false
                adapter?.notifyDataSetChanged()
                DownloadManager.downloadManager!!.stopTask(data[position].url)
            } else {
                DownloadManager.downloadManager!!.startTask(data[position].url)
            }
        }
        */

        list_history.setOnItemLongClickListener { parent, view, position, id ->
            val build = SimpleAlert.Build(activity, R.style.SimpleAlert)
            build.content = "确认取消该下载吗？"
            build.showTitle = true
            build.title = "取消下载"
            build.onPos = View.OnClickListener { _ ->
                val m = data.removeAt(position)
                DownloadManager.downloadManager!!.stopTask(m.url)
                App.instances.daoSession.downloadModeDao.deleteByKey(m.id)
                adapter!!.notifyDataSetChanged()
            }
            build.build().show()
            true
        }
        uiHandler = UIHandler(this)
    }

    @Subscribe
    fun onDownloadAction(action: DownloadAction) {
        val msg = Message()
        msg.what = 1
        msg.obj = action
        uiHandler!!.sendMessage(msg)
    }
    fun findItemByID(id:Long):ActivityDownload.DownloadingMode? {
        return data.firstOrNull { it.id == id }
    }
    fun findItemIndex(id:Long):Int {
        return data.indexOfFirst { it.id == id }
    }

    class UIHandler:Handler{
        val ref:WeakReference<DownloadingFragment>
        constructor(fragment: DownloadingFragment):super() {
            ref = WeakReference(fragment)
        }
        override fun handleMessage(msg: Message?) {
            val fragment = ref.get() ?: return
            when(msg?.what) {
                1 -> {
                    val action = msg.obj as DownloadAction

                    when(action.action) {
                        DownloadAction.ACTION.ON_START -> {
                            val item = fragment.findItemByID(action.id)
                            if (item != null) {
                                item.isDownloading = true
                                fragment.adapter?.notifyDataSetChanged()
                            }
                        }
                        DownloadAction.ACTION.ON_TOAST -> SimpleToast.makeToast(fragment.activity, action.message, Toast.LENGTH_LONG).show()
                        DownloadAction.ACTION.ON_PROGRESS -> {
                            val item = fragment.findItemByID(action.id)
                            if (item != null) {
                                item.progress = action.progress
                                val index = fragment.findItemIndex(action.id)
                                if (index>=0){
                                    val view = fragment.getViewByPosition(index)
                                    if (view != null) {
                                        view.findViewById<ProgressBar>(R.id.download_progress).progress = action.progress
                                    }
                                }
                            }
                            /*
                            val item = fragment.findItemByID(action.id)
                            if (item != null) {
                                item.isDownloading = true
                                item.progress = action.progress
                                fragment.adapter?.notifyDataSetChanged()
                            }
                            */
                        }
                        DownloadAction.ACTION.ON_FINISHED -> {
                            val item = fragment.findItemByID(action.id)
                            if (item != null) {
                                val mode = App.instances.daoSession.downloadModeDao.load(item.id)
                                mode.isFinished = true
                                App.instances.daoSession.downloadModeDao.update(mode)
                                fragment.data.remove(item)
                                fragment.adapter?.notifyDataSetChanged()
                                fragment.checkView()
                            }
                        }
                        DownloadAction.ACTION.ON_FAIL -> {
                            val item = fragment.findItemByID(action.id)
                            if (item != null) {
                                item.isDownloading = false
                                fragment.adapter?.notifyDataSetChanged()
                            }
                        }
                    }


                }

                2 -> {
                    fragment.reloadData()
                }
            }
        }
    }

    fun reloadData() {
        data.clear()
        val d = App.instances.daoSession.downloadModeDao.queryBuilder().where(DownloadModeDao.Properties.IsFinished.eq(false)).build().list()
        for (item in d) {
            val dm = ActivityDownload.DownloadingMode(item.id, item.url)
            if (DownloadManager.downloadManager!!.isTaskExist(item.url)) {
                dm.isDownloading = true
            }
            data.add(dm)
        }
        adapter!!.notifyDataSetChanged()
        checkView()
    }
    fun checkView() {
        if (data.isEmpty()) {
            record_list_empty.visibility = View.VISIBLE
        }
    }

    fun getViewByPosition(position:Int):View? {
        return list_history?.getChildAt(position)
    }
}
