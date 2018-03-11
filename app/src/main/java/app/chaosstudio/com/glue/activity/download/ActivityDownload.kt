package app.chaosstudio.com.glue.activity.download

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import app.chaosstudio.com.glue.App
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.download.DownloadManager
import app.chaosstudio.com.glue.eventb.FragmentAction
import app.chaosstudio.com.glue.greendb.gen.DownloadModeDao
import app.chaosstudio.com.glue.ui.EditAlert
import app.chaosstudio.com.glue.ui.SimpleToast
import app.chaosstudio.com.glue.utils.CustomTheme
import kotlinx.android.synthetic.main.activity_download.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.EventBusException

/**
 * Created by jsen on 2018/2/1.
 */

class ActivityDownload : AppCompatActivity() {

    var fragmentDownloading: DownloadingFragment? = null
    var fragmentDownloaded: DownloadedFragment? = null
    var fragmentPlayList: PlayListFragment? = null

    enum class SHOW {
        DOWNLOADED,
        DOWNLOADING,
        PLAYLIST
    }
    var show = SHOW.DOWNLOADING
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (CustomTheme.hiddenStatus) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        setContentView(R.layout.activity_download)
        setSupportActionBar(toolbar)
        acb_title.text = "正在下载"

        initFragment(savedInstanceState)

        try {
            EventBus.getDefault().register(fragmentDownloading)
        } catch (e : EventBusException) {
        }
        try {
            EventBus.getDefault().register(fragmentDownloaded)
        } catch (e : EventBusException) {
        }
        try {
            EventBus.getDefault().register(fragmentPlayList)
        } catch (e : EventBusException) {
        }

        download_switch_downloading.setOnClickListener { _ ->
            supportFragmentManager.beginTransaction().hide(fragmentDownloaded).hide(fragmentPlayList).show(fragmentDownloading).commit()
            show = SHOW.DOWNLOADING
            acb_title.text = "正在下载"
        }
        download_switch_downloaded.setOnClickListener { _ ->
            fragmentDownloaded!!.reloadData()
            supportFragmentManager.beginTransaction().hide(fragmentDownloading).hide(fragmentPlayList).show(fragmentDownloaded).commit()
            show = SHOW.DOWNLOADED
            acb_title.text = "已完成"
        }
        download_switch_playlist.setOnClickListener { _ ->
            fragmentDownloaded!!.reloadData()
            supportFragmentManager.beginTransaction().hide(fragmentDownloading).hide(fragmentDownloaded).show(fragmentPlayList).commit()
            show = SHOW.PLAYLIST
            acb_title.text = "播放列表"
        }
        createAlert()
    }

    fun initFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            fragmentDownloading = Fragment.instantiate(this, DownloadingFragment::class.java.name) as DownloadingFragment
            fragmentDownloaded = Fragment.instantiate(this, DownloadedFragment::class.java.name) as DownloadedFragment
            fragmentPlayList = Fragment.instantiate(this, PlayListFragment::class.java.name) as PlayListFragment
        } else {
            fragmentDownloading = supportFragmentManager.getFragment(savedInstanceState, DownloadingFragment::class.java.name) as DownloadingFragment
            fragmentDownloaded = supportFragmentManager.getFragment(savedInstanceState, DownloadedFragment::class.java.name) as DownloadedFragment
            fragmentPlayList = supportFragmentManager.getFragment(savedInstanceState, PlayListFragment::class.java.name) as PlayListFragment
        }
        supportFragmentManager.beginTransaction().replace(R.id.set_fragment_container, fragmentDownloading).add(R.id.set_fragment_container, fragmentPlayList).add(R.id.set_fragment_container, fragmentDownloaded).hide(fragmentDownloaded).hide(fragmentPlayList).show(fragmentDownloading).commit()
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        supportFragmentManager.putFragment(outState, fragmentDownloading!!.javaClass.name, fragmentDownloading)
        supportFragmentManager.putFragment(outState, fragmentDownloaded!!.javaClass.name, fragmentDownloaded)
        supportFragmentManager.putFragment(outState, fragmentPlayList!!.javaClass.name, fragmentPlayList)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }
    override fun onDestroy() {
        EventBus.getDefault().unregister(fragmentDownloading)
        EventBus.getDefault().unregister(fragmentDownloaded)
        EventBus.getDefault().unregister(fragmentPlayList)
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.popup_download,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when(item.itemId) {
            R.id.clear_tag -> {
                if (show == SHOW.DOWNLOADED) {
                    App.instances.daoSession.downloadModeDao.queryBuilder().where(DownloadModeDao.Properties.IsFinished.eq(true)).buildDelete().executeDeleteWithoutDetachingEntities()
                    fragmentDownloaded!!.reloadData()
                } else if (show == SHOW.DOWNLOADING) {
                    DownloadManager.downloadManager!!.stopAllTask()
                    App.instances.daoSession.downloadModeDao.queryBuilder().where(DownloadModeDao.Properties.IsFinished.eq(false)).buildDelete().executeDeleteWithoutDetachingEntities()
                    fragmentDownloading!!.reloadData()
                } else if (show == SHOW.PLAYLIST) {
                    App.instances.daoSession.playListDao.deleteAll()
                    fragmentPlayList!!.reloadData()
                }
            }
            R.id.add_tag -> {
                doEditAlert?.show()
            }
        }
        FragmentAction.Companion.fire(FragmentAction.ACTION.MENU_ITEM, item.itemId)
        return super.onOptionsItemSelected(item)
    }

    var doEditAlert:EditAlert? = null
    fun createAlert() {
        val build = EditAlert.Build(this, R.style.SimpleAlert)
        build.showTitle = true
        build.title = "资源下载"
        build.onPos = View.OnClickListener { view ->
            val url = doEditAlert!!.text?:""
            if (TextUtils.isEmpty(url)) {
                SimpleToast.makeToast(this@ActivityDownload, "请输入合法网址", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            try {
                Uri.parse(url)
                Thread({
                    if(DownloadManager.downloadManager!!.createTask(url)) {
                        fragmentDownloading!!.uiHandler!!.sendEmptyMessage(2)
                    }
                }).start()
            } catch (e:Exception) {
                SimpleToast.makeToast(this@ActivityDownload, "请输入合法网址", Toast.LENGTH_LONG).show()
            }
        }
        doEditAlert = build.build()
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
                path.endsWith(".vob")//  ||
        // path.endsWith(".m3u8")
    }

    class DownloadingMode(val id:Long, val url:String) {
        var progress = 0
        var isDownloading = false
    }
}
