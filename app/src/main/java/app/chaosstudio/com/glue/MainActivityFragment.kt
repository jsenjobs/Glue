package app.chaosstudio.com.glue

import android.content.SharedPreferences
import android.support.v4.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.chaosstudio.com.glue.eventb.ActionBarAction
import app.chaosstudio.com.glue.eventb.FragmentAction
import app.chaosstudio.com.glue.eventb.WebViewAction
import app.chaosstudio.com.glue.eventb.WebViewTitle
import app.chaosstudio.com.glue.greendb.gen.OpendedUrlDao
import app.chaosstudio.com.glue.ui.SimpleAlert
import app.chaosstudio.com.glue.unit.BrowserUnit
import app.chaosstudio.com.glue.webconfig.*
import kotlinx.android.synthetic.main.fragment_action_bar.*
import kotlinx.android.synthetic.main.fragment_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * A placeholder fragment containing a simple view.
 */
class MainActivityFragment : Fragment() {
    var sp:SharedPreferences? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sp = PreferenceManager.getDefaultSharedPreferences(activity)
        // WVConfig(WVClient(webView), WCClient()).config(webView)
        // webView.loadUrl("http://www.baidu.com")

        WebViewManager.setNoHis(sp!!.getBoolean(activity.getString(R.string.sp_no_his_mode), true))
        WebViewManager.setIsNight(sp!!.getBoolean(activity.getString(R.string.sp_night), false))

        if (WebViewManager.getCurrentActive() == null) {
            if(sp!!.getBoolean(context.getString(R.string.sp_crash), false)) {
                val dS = App.instances.daoSession.opendedUrlDao
                val pages = dS.loadAll()
                for (page in pages) {
                    val wv = createWebPage()
                    if (wv != null) {
                        wv.uuid = page.uuid
                        wv.loadUrl(page.domain)
                    } else {
                        dS.delete(page)
                    }
                }
                if (WebViewManager.getAllNWebView().isEmpty()) {
                    createWebPage()
                }
                showWebPage(WebViewManager.getAllNWebView().size - 1)
                if (activity.intent.hasExtra("SURL")) {
                    val wv = createWebPage()
                    if(wv == null) {
                        WebViewManager.getAllNWebView()[WebViewManager.getSize() - 1].loadUrl(activity.intent.getStringExtra("SURL"))
                    } else {
                        wv.loadUrl(activity.intent.getStringExtra("SURL"))
                    }
                    showWebPage(WebViewManager.getSize() - 1)
                }
                ActionBarAction.fire(ActionBarAction.ACTION.PAGESCHANGED, WebViewManager.getSize())
            } else {
                createWebPage()
                showWebPage(0)
                if (activity.intent.hasExtra("URL")) {
                    WebViewManager.getCurrentActive().loadUrl(activity.intent.getStringExtra("URL"))
                } else {
                    WebViewManager.getCurrentActive().loadUrl(BrowserUnit.getHome(activity))
                    if (activity.intent.hasExtra("SURL")) {
                        WebViewManager.getAllNWebView()[WebViewManager.getSize() - 1].loadUrl(activity.intent.getStringExtra("SURL"))
                        showWebPage(WebViewManager.getSize() - 1)
                    }
                }
            }
        } else {
            if (activity.intent.hasExtra("SURL")) {
                WebViewManager.getCurrentActive().loadUrl(activity.intent.getStringExtra("SURL"))
            } else {
                WebViewManager.getCurrentActive().loadUrl(BrowserUnit.getHome(activity))
            }
        }
    }

    @Subscribe
    fun onWebViewAction(action: WebViewAction) {
        val webView: NWebView = WebViewManager.getCurrentActive() ?: return
        when(action.action) {
            WebViewAction.ACTION.GOFORWARD -> webView.goForward()
            WebViewAction.ACTION.GOBACK -> webView.goBack()
            WebViewAction.ACTION.REFRESH -> webView.reload()
            WebViewAction.ACTION.GO -> webView.loadUrl(action.url)
            WebViewAction.ACTION.CHANGEPAGE -> showWebPage(action.page)
            WebViewAction.ACTION.CREATEPAGE -> {
                val nWebView = createWebPage()
                if (nWebView != null) {
                    showWebPage(nWebView)
                    WebViewManager.getCurrentActive().loadUrl(action.url)
                    ActionBarAction.fire(ActionBarAction.ACTION.PAGESCHANGED, WebViewManager.getSize())
                }
            }
            WebViewAction.ACTION.CREATEPAGEBACK -> {
                createWebPage()?.loadUrl(action.url)
                ActionBarAction.fire(ActionBarAction.ACTION.PAGESCHANGED, WebViewManager.getSize())
            }
            WebViewAction.ACTION.CLOSEPAGE -> {
                val page = WebViewManager.get(action.page)
                WebViewManager.remove(page)
                ActionBarAction.fire(ActionBarAction.ACTION.PAGESCHANGED, WebViewManager.getSize())
                val index:Int = WebViewManager.rebuildIndex(action.page)
                if (index >= 0) {
                    showWebPage(index)
                } else {
                    val nWebView = createWebPage()
                    if (nWebView != null) {
                        showWebPage(nWebView)
                        WebViewManager.getCurrentActive().loadUrl(BrowserUnit.getHome(activity))
                        ActionBarAction.fire(ActionBarAction.ACTION.PAGESCHANGED, WebViewManager.getSize())
                    }
                }
            }
            WebViewAction.ACTION.CLOSEPAGES -> {
                val build = SimpleAlert.Build(activity, R.style.SimpleAlert)
                build.content = "全部关闭？"
                build.onPos = View.OnClickListener { view ->
                    WebViewManager.clear()
                    ActionBarAction.fire(ActionBarAction.ACTION.PAGESCHANGED, WebViewManager.getSize())
                    val index:Int = WebViewManager.rebuildIndex(action.page)
                    if (index >= 0) {
                        showWebPage(index)
                    } else {
                        val nWebView = createWebPage()
                        if (nWebView != null) {
                            showWebPage(nWebView)
                            WebViewManager.getCurrentActive().loadUrl(BrowserUnit.getHome(activity))
                            ActionBarAction.fire(ActionBarAction.ACTION.PAGESCHANGED, WebViewManager.getSize())
                        }
                    }
                }
                build.build().show()
            }
        }
    }

    private fun createWebPage(): NWebView? {
        if(WebViewManager.getSize() >= 9) {
            return null
        }
        val webView = NWebView(context)
        initScrollChange(webView)
        WebViewManager.add(webView)
        return webView
    }
    private fun showWebPage(i:Int) {
        WebViewManager.active(i)
        val nWebView = WebViewManager.getCurrentActive()
        webViewContainer.removeAllViews()
        webViewContainer.addView(nWebView)

        WebViewTitle.fire(nWebView.title)
        WebViewAction.fire(WebViewAction.ACTION.AFTERCHANGEPAGE)
    }
    private fun showWebPage(nWebView:NWebView) {
        WebViewManager.active(nWebView)
        val nWebView2 = WebViewManager.getCurrentActive()
        webViewContainer.removeAllViews()
        webViewContainer.addView(nWebView2)

        WebViewTitle.fire(nWebView2.title)
    }

    private fun initScrollChange(w:NWebView) {
        if (sp!!.getBoolean(activity.getString(R.string.sp_omnibox_control), false)) {
            w.onFling = object : NWebView.OnFling {
                override fun up() {
                    w.onFling = null
                    Handler().postDelayed({
                        initScrollChange(w)
                    }, 250)
                    hideOmnibox()
                }

                override fun down() {
                    w.onFling = null
                    Handler().postDelayed({
                        initScrollChange(w)
                    }, 250)
                    showOmnibox()
                }

            }
            /*
            w.onScrollChangeListener = object : NWebView.OnScrollChangeListener {
                override fun onScrollChange(scrollY: Int, oldScrollY: Int) {
                    if (scrollY > oldScrollY) {
                        w.onScrollChangeListener = null
                        Handler().postDelayed({
                            initScrollChange(w)
                        }, 250)
                        hideOmnibox()
                    } else if (scrollY < oldScrollY) {

                        w.onScrollChangeListener = null
                        Handler().postDelayed({
                            initScrollChange(w)
                        }, 250)
                        showOmnibox()
                    }
                }
            }
            */
        }
    }
    private fun hideOmnibox() {
        FragmentAction.fire(FragmentAction.ACTION.FULLSCREEN, 0)
    }
    private fun showOmnibox() {
        FragmentAction.fire(FragmentAction.ACTION.FULLSCREEN, 1)
    }
}
