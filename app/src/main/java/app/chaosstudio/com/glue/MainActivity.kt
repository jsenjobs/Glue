package app.chaosstudio.com.glue

import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.webkit.WebChromeClient
import android.webkit.WebIconDatabase
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.Toast
import android.widget.VideoView
import app.chaosstudio.com.glue.activity.ListResourcesFragment
import app.chaosstudio.com.glue.activity.SimpleContainer
import app.chaosstudio.com.glue.eventb.*
import app.chaosstudio.com.glue.greendb.gen.PageSourceDao
import app.chaosstudio.com.glue.greendb.model.BlackUrl
import app.chaosstudio.com.glue.ui.*
import app.chaosstudio.com.glue.unit.BrowserUnit
import app.chaosstudio.com.glue.unit.js_native.MixUnit
import app.chaosstudio.com.glue.utils.*
import app.chaosstudio.com.glue.webconfig.FullscreenHolder
import app.chaosstudio.com.glue.webconfig.WebViewManager

import kotlinx.android.synthetic.main.content_action_bar.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_web_title.*
import kotlinx.android.synthetic.main.fragment_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.lang.ref.WeakReference
import java.net.URLEncoder


class MainActivity : AppCompatActivity() {

    var uiHandler:UIHandler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or  View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw()
        }
        CustomTheme.hiddenStatus = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.sp_hidden_status), CustomTheme.hiddenStatus)
        if (CustomTheme.hiddenStatus) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        PermissionHelp.grantPermissionsStorage(this)
        setContentView(R.layout.activity_main)

        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.sp_omnibox_control), true))  {
            // full
            val lp = webViewContainer.layoutParams as FrameLayout.LayoutParams
            lp.bottomMargin = 0
            webViewContainer.layoutParams = lp
        } else {
            val lp = webViewContainer.layoutParams as FrameLayout.LayoutParams
            lp.bottomMargin = resources.getDimensionPixelOffset(R.dimen.action_bar_height)
            webViewContainer.layoutParams = lp
        }
        EventBus.getDefault().register(fra_webTitle)
        EventBus.getDefault().register(fra_webView)
        EventBus.getDefault().register(fra_action_bar)
        EventBus.getDefault().register(this)

        WebIconDatabase.getInstance().open(getDirs(cacheDir.absolutePath +"/icons/"))
        // setSupportActionBar(toolbar)

        /*
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        */

        // Example of a call to a native method
        // sample_text.text = stringFromJNI()

        createPopup()

        GPre.init(this)
        uiHandler = UIHandler(this)
        MainActivity.instance = this

        App.instances.daoSession.pageSourceDao.deleteAll()

        AndroidBug5497Workaround.assistActivity(this)

    }

    /*
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    */
    fun getDirs(path: String): String {
        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return path
    }
    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
    }

    /*
    override fun onNewIntent(intent: Intent?) {
        if (intent!!.hasExtra("SURL")) {
            WebViewAction.fire(WebViewAction.ACTION.GO, intent.getStringExtra("SURL"))
        }
    }
    */

    override fun onDestroy() {
        EventBus.getDefault().unregister(fra_webTitle)
        EventBus.getDefault().unregister(fra_webView)
        EventBus.getDefault().unregister(fra_action_bar)
        EventBus.getDefault().unregister(this)
        MainActivity.instance = null
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when(keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                if (isFullScreenMode) {
                    fullScreen()
                }
                if (customViewCallback != null) {
                    customViewCallback?.onCustomViewHidden()
                    return true
                }
                if (WebViewManager.getCurrentActive().canGoBack()) {
                    WebViewManager.getCurrentActive().goBack()
                } else {
                    moveTaskToBack(false)
                }
                return true
            }
            KeyEvent.KEYCODE_VOLUME_UP -> {
                if (GPre.volumeScroll) {
                    val wv = WebViewManager.getCurrentActive()
                    wv?.loadUrl("javascript:scrollBy(0, -200)")
                    return true
                }
            }
            KeyEvent.KEYCODE_VOLUME_DOWN-> {
                if (GPre.volumeScroll) {
                    val wv = WebViewManager.getCurrentActive()
                    wv?.loadUrl("javascript:scrollBy(0, 200)")
                    return true
                }
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    @Subscribe
    fun onActivityAction(action: ActivityAction) {
        when(action.action) {
            ActivityAction.ACTION.Set -> {
                startActivity(Intent(this@MainActivity, app.chaosstudio.com.glue.activity.Set::class.java))
            }
        }
    }


    var popup: SimplePopupWindow? = null
    var goUrl = ""
    fun createPopup() {
        val root = View.inflate(this, R.layout.popup_web_content_edit, null)
        val lis = View.OnClickListener{ view ->
            popup?.dismiss()
            when(view.id) {
                R.id.web_content_resource -> {
                    val intent = Intent(this@MainActivity, SimpleContainer::class.java)
                    intent.putExtra("fragment", ListResourcesFragment::class.java.name)
                    this@MainActivity.startActivity(intent)
                }
                R.id.web_content_new_back -> WebViewAction.fire(WebViewAction.ACTION.CREATEPAGEBACK, goUrl)
                R.id.web_content_new_for -> WebViewAction.fire(WebViewAction.ACTION.CREATEPAGE, goUrl)

                R.id.web_content_copy -> BrowserUnit.copyURL(this@MainActivity, goUrl)

                R.id.web_content_save -> BrowserUnit.download(this@MainActivity, goUrl, goUrl, BrowserUnit.MIME_TYPE_IMAGE)
                R.id.web_content_qrcode -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(QRUtil.result.toString())
                    val build = SimpleAlert.Build(this@MainActivity, R.style.SimpleAlert)
                    build.showTitle = true
                    build.title = "扫描结果"
                    build.content = QRUtil.result.text
                    if (intent.resolveActivity(this@MainActivity.packageManager) != null) {
                        build.nag = "复制"
                        build.pos = "打开"
                        build.onNag = View.OnClickListener {
                            BrowserUnit.copyURL(this@MainActivity, QRUtil.result.text)
                        }
                        build.onPos = View.OnClickListener {
                            startActivity(intent)
                            this@MainActivity.overridePendingTransition(0, 0)
                        }
                    } else {
                        build.showNag = false
                        build.pos = "复制"
                        build.onPos = View.OnClickListener {
                            BrowserUnit.copyURL(this@MainActivity, QRUtil.result.text)
                        }
                    }
                    build.build().show()
                }
                R.id.web_content_search_pic -> {
                    // http://image.baidu.com/pcdutu?queryImageUrl=
                    WebViewAction.fire(WebViewAction.ACTION.CREATEPAGE, "http://image.baidu.com/pcdutu?queryImageUrl=" + URLEncoder.encode(goUrl, "UTF-8"))
                }
                R.id.web_content_look_pic -> {

                    val wv = WebViewManager.getCurrentActive()
                    if(wv!=null) {
                        val mds = App.instances.daoSession.pageSourceDao.queryBuilder().where(PageSourceDao.Properties.Url.eq(wv.url)).list()
                        if (mds.size > 0) {
                            WebViewAction.fire(WebViewAction.ACTION.GO, "file:///android_asset/picture_viewer.html?url=" + mds[0].id)
                        } else {
                            val build = ToastWithEdit.Build(this@MainActivity, R.style.SimpleAlert)
                            build.pos = "取消"
                            build.message = "获取中"
                            if (MixUnit.toastWithEdit != null && MixUnit.toastWithEdit!!.isShowing) {
                                MixUnit.toastWithEdit!!.dismiss()
                                MixUnit.toastWithEdit = null
                            }
                            MixUnit.toastWithEdit = build.build()
                            MixUnit.toastWithEdit!!.show()

                            wv.loadUrl("javascript:var s = prompt('jjs://BEFORELISTIMAGE#' + " +"escape(encodeURIComponent('<html>'+" + "document.getElementsByTagName('html')[0].innerHTML+'</html>')));")
                        }
                    }

                    // wv?.imageGetBaseUrl = wv.url?:""
                    // WebViewAction.fire(WebViewAction.ACTION.GO, "file:///android_asset/picture_viewer.html")
                }

                R.id.web_content_page_info -> {
                    val wv = WebViewManager.getCurrentActive()
                    if (wv != null) {
                        val build =  AlertPageInfo.Build(this@MainActivity, R.style.SimpleAlert)
                        build.url = wv.url
                        build.goUrl = goUrl
                        build.title = wv.title
                        build.build().show()
                    }
                }
                R.id.web_content_ad_tag -> {
                    val build = SimpleAlert.Build(this@MainActivity, R.style.SimpleAlert)
                    build.showTitle = true
                    build.title = "标记广告"
                    build.content = "地址：" + goUrl
                    build.onPos = View.OnClickListener { view ->
                        WebViewManager.clear()
                        val model = BlackUrl()
                        model.domain = goUrl
                        model.tag = "标记广告"
                        App.instances.daoSession.blackUrlDao.save(model)
                        SimpleToast.makeToast(this@MainActivity, "标记成功", Toast.LENGTH_LONG).show()
                    }
                    build.build().show()
                }
            }
        }
        root.findViewById<View>(R.id.web_content_new_back).setOnClickListener(lis)
        root.findViewById<View>(R.id.web_content_new_for).setOnClickListener(lis)

        root.findViewById<View>(R.id.web_content_copy).setOnClickListener(lis)

        root.findViewById<View>(R.id.web_content_save).setOnClickListener(lis)
        root.findViewById<View>(R.id.web_content_qrcode).setOnClickListener(lis)
        root.findViewById<View>(R.id.web_content_search_pic).setOnClickListener(lis)
        root.findViewById<View>(R.id.web_content_look_pic).setOnClickListener(lis)

        root.findViewById<View>(R.id.web_content_page_info).setOnClickListener(lis)
        root.findViewById<View>(R.id.web_content_ad_tag).setOnClickListener(lis)

        root.findViewById<View>(R.id.web_content_resource).setOnClickListener(lis)

        popup = SimplePopupWindow(null, root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popup?.animationStyle = R.style.popwin_anim_style
    }
    private var timeStamp = 0L
    @Subscribe
    fun onWebViewAction(action: WebViewAction) {
        when(action.action) {
            WebViewAction.ACTION.ONLONGCLICK -> {
                val wv = WebViewManager.getCurrentActive()
                if (wv != null) {
                    goUrl = action.url
                    val result = wv.hitTestResult
                    popup?.contentView?.findViewById<View>(R.id.web_content_qrcode)!!.visibility = View.GONE
                    if (result != null && (result.type == WebView.HitTestResult.IMAGE_TYPE || result.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE)) {
                        popup?.contentView?.findViewById<View>(R.id.group_for_pic)!!.visibility = View.VISIBLE
                    } else {
                        popup?.contentView?.findViewById<View>(R.id.group_for_pic)!!.visibility = View.GONE
                    }
                    if (goUrl.startsWith("http://") || goUrl.startsWith("https://")) {
                        popup?.contentView?.findViewById<View>(R.id.group_for_http)!!.visibility = View.VISIBLE
                    } else {
                        popup?.contentView?.findViewById<View>(R.id.group_for_http)!!.visibility = View.GONE
                    }
                    popup!!.updateAnchor(fra_webView.view, wv.rawX.toInt(), wv.rawY.toInt(), 20, this@MainActivity.resources.getDimension(R.dimen.toolBarSize).toInt() + DensityUtil.dip2px(this@MainActivity, 40F))
                    popup!!.showAtLocation(Gravity.TOP or Gravity.START)
                    Thread({
                        timeStamp = System.currentTimeMillis()
                        if (QRUtil.isQRImage(assets, goUrl)) {
                            val sC = System.currentTimeMillis()
                            if (sC - timeStamp < 1001) {
                                uiHandler?.sendEmptyMessageDelayed(1, timeStamp + 1001 - sC)
                            } else {
                                uiHandler?.sendEmptyMessage(1)
                            }
                        } else {
                            uiHandler?.sendEmptyMessage(2)
                        }
                    }).start()
                }
            }
        }
    }
    @Subscribe
    fun onFragmentAction(action: FragmentAction) {
        when(action.action) {
            FragmentAction.ACTION.FULL_STATUS_CHANGE -> {
                if (CustomTheme.hiddenStatus) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                }
            }
            FragmentAction.ACTION.FULL_MODE_CHANGE -> {
                if (action.tag == 1) {
                    // full
                    val lp = webViewContainer.layoutParams as FrameLayout.LayoutParams
                    lp.bottomMargin = 0
                    webViewContainer.layoutParams = lp
                } else {
                    val lp = webViewContainer.layoutParams as FrameLayout.LayoutParams
                    lp.bottomMargin = resources.getDimensionPixelOffset(R.dimen.action_bar_height)
                    webViewContainer.layoutParams = lp
                }
            }
        }
    }

    var customView:View? = null
    var customViewCallback: WebChromeClient.CustomViewCallback? = null
    var videoView:VideoView? = null
    var fullscreenHolder: FullscreenHolder? = null
    var originalOrientation:Int = 0
    @Subscribe
    fun onVideoAction(action: VideoAction) {
        when(action.action) {
            VideoAction.ACTION.FULLSCREEN -> {
                if (action.view == null) return
                if (customView != null && action.callback != null) {
                    action.callback?.onCustomViewHidden()
                    return
                }

                customView = action.view
                originalOrientation = requestedOrientation
                fullscreenHolder = FullscreenHolder(this)
                fullscreenHolder?.addView(customView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))


                val decorView = window.decorView as FrameLayout
                decorView.addView(fullscreenHolder, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))

                customView?.keepScreenOn = true
                setCustomFullscreen(true)

                if (customView is FrameLayout) {
                    if ((customView as FrameLayout).focusedChild is VideoView) {
                        videoView = (customView as FrameLayout).focusedChild as VideoView
                        videoView?.setOnErrorListener(VideoCompletionListener())
                        videoView?.setOnCompletionListener(VideoCompletionListener())
                    }
                }
                customViewCallback = action.callback

                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            VideoAction.ACTION.HIDDEN -> doHideCustomView()
            VideoAction.ACTION.FULLSCREEN_INJECT -> {
                fullScreen()
            }
        }
    }

    fun doHideCustomView():Boolean {
        if (customView == null || customViewCallback == null) {
            return false
        }
        val decorView = window.decorView as FrameLayout
        decorView.removeView(fullscreenHolder)

        customView?.keepScreenOn = false
        setCustomFullscreen(false)

        fullscreenHolder = null
        customView = null
        customViewCallback = null
        if (videoView != null) {
            videoView?.setOnErrorListener(null)
            videoView?.setOnCompletionListener(null)
            videoView = null
        }
        CustomTheme.hiddenStatus = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.sp_hidden_status), CustomTheme.hiddenStatus)
        if (CustomTheme.hiddenStatus) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        isFullScreenMode = false
        requestedOrientation = originalOrientation
        webViewContainer.scrollTo(oldX, oldY)
        FragmentAction.fire(FragmentAction.ACTION.SHOW_UI)
        return true
    }

    var isFullScreenMode = false
    var oldX = 0
    var oldY = 0
    private fun fullScreen() {
        if (isFullScreenMode) {
            isFullScreenMode = false
            requestedOrientation = originalOrientation
            CustomTheme.hiddenStatus = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.sp_hidden_status), CustomTheme.hiddenStatus)
            if (CustomTheme.hiddenStatus) {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
            webViewContainer.scrollTo(oldX, oldY)
            FragmentAction.fire(FragmentAction.ACTION.SHOW_UI)
        } else {
            isFullScreenMode = true
            oldX = webViewContainer.x.toInt()
            oldY = webViewContainer.y.toInt()
            webViewContainer.scrollTo(0, 0)
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            originalOrientation = requestedOrientation
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            FragmentAction.fire(FragmentAction.ACTION.HIDDEN_UI)
        }
    }

    private inner class VideoCompletionListener : MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
        override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
            return false
        }

        override fun onCompletion(mp: MediaPlayer) {
            /*
            if (customViewCallback != null) {
                customViewCallback?.onCustomViewHidden()
            }
            */
        }
    }
    private fun setCustomFullscreen(fullscreen: Boolean) {
        val layoutParams = window.attributes
        /*
         * Can not use View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION,
         * so we can not hide NavigationBar :(
         */
        val bits = WindowManager.LayoutParams.FLAG_FULLSCREEN

        if (fullscreen) {
            layoutParams.flags = layoutParams.flags or bits
        } else {
            layoutParams.flags = layoutParams.flags and bits.inv()
            if (customView != null) {
                customView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            } else {
                webViewContainer.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
        window.attributes = layoutParams
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    class UIHandler:Handler{
        val weakRefrence:WeakReference<MainActivity>
        constructor(mainActivity: MainActivity):super() {
            weakRefrence = WeakReference(mainActivity)
        }

        override fun handleMessage(msg: Message?) {
            val activity = weakRefrence.get()
            if (activity != null) {
                when(msg?.what) {
                    1 -> activity.popup?.contentView?.findViewById<View>(R.id.web_content_qrcode)!!.visibility = View.VISIBLE
                    2 -> activity.popup?.contentView?.findViewById<View>(R.id.web_content_qrcode)!!.visibility = View.GONE
                }
            }
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        var instance:MainActivity? = null

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
