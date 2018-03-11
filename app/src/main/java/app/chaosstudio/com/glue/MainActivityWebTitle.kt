package app.chaosstudio.com.glue

import android.animation.ValueAnimator
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import app.chaosstudio.com.glue.eventb.*
import app.chaosstudio.com.glue.greendb.gen.BookMarkDao
import app.chaosstudio.com.glue.greendb.model.BookMark
import app.chaosstudio.com.glue.ui.SimpleAlert
import app.chaosstudio.com.glue.ui.SimpleToast
import app.chaosstudio.com.glue.unit.BrowserUnit
import app.chaosstudio.com.glue.utils.CustomTheme
import app.chaosstudio.com.glue.webconfig.WebViewManager
import kotlinx.android.synthetic.main.fragment_web_title.*
import org.greenrobot.eventbus.Subscribe

/**
 * Created by jsen on 2018/1/21.
 */

class MainActivityWebTitle : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_web_title, container, false)
    }

    var orHeight:Int = 0
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // updateTheme()

        webView_container.post({
            val acVHeight = webView_container.measuredHeight.toFloat()
            hiddenHeightAnimation = ValueAnimator.ofFloat(acVHeight, 0f)
            showHeightAnimation = ValueAnimator.ofFloat(0f, acVHeight)
            hiddenHeightAnimation!!.duration = 280
            hiddenHeightAnimation!!.startDelay = 10
            showHeightAnimation!!.duration = 280
            showHeightAnimation!!.startDelay = 10

            val updateListener = ValueAnimator.AnimatorUpdateListener{ animation ->
                val vv = (animation.animatedValue as Float).toInt()
                setViewHeight(webView_container, vv)
            }
            hiddenHeightAnimation!!.addUpdateListener(updateListener)
            showHeightAnimation!!.addUpdateListener(updateListener)
        })

        val lis = View.OnClickListener{view ->
            if (webView_mark.tag as Boolean) {
                val build = SimpleAlert.Build(activity, R.style.SimpleAlert)
                build.content = "删除书签？"
                build.onPos = View.OnClickListener { view ->
                    val dao = App.instances.daoSession.bookMarkDao
                    dao.queryBuilder().where(BookMarkDao.Properties.Url.eq(WebViewManager.getCurrentActive().url)).buildDelete().executeDeleteWithoutDetachingEntities()
                    SimpleToast.makeToast(activity, "清除书签成功", Toast.LENGTH_LONG).show()
                    webView_mark.setImageResource(R.mipmap.icon_no_mark)
                    webView_mark.tag = true
                }
                build.build().show()
            } else {
                val cW = WebViewManager.getCurrentActive()
                if (cW != null) {
                    val dao = App.instances.daoSession.bookMarkDao
                    val count = dao.queryBuilder().where(BookMarkDao.Properties.Url.eq(cW.url)).limit(1).count()
                    if (count > 0) {
                        SimpleToast.makeToast(context, "书签已存在", Toast.LENGTH_LONG).show()
                    } else {
                        val bm = BookMark()
                        bm.date = System.currentTimeMillis()
                        bm.name = cW.title
                        bm.url = cW.url
                        App.instances.daoSession.bookMarkDao.save(bm)
                        SimpleToast.makeToast(context, "添加书签成功", Toast.LENGTH_LONG).show()
                        webView_mark.setImageResource(R.mipmap.icon_marked)
                        webView_mark.tag = true
                    }
                } else {
                    SimpleToast.makeToast(context, "添加书签失败", Toast.LENGTH_LONG).show()
                }
            }
        }
        val lisL = View.OnLongClickListener{ view ->
            when(view.id) {
                R.id.webView_title -> {
                    val wM = WebViewManager.getCurrentActive()
                    if (wM != null) {
                        val build = SimpleAlert.Build(activity, R.style.SimpleAlert)
                        build.content = wM.url
                        build.showTitle = true
                        build.title = "复制地址"
                        build.onPos = View.OnClickListener { _ ->
                            BrowserUnit.copyURL(activity, wM.url)
                        }
                        build.build().show()
                    }

                }
                R.id.webView_bel -> SimpleToast.makeToast(activity, belTXT, Toast.LENGTH_LONG).show()
                R.id.webView_sec -> SimpleToast.makeToast(activity, secTXT, Toast.LENGTH_LONG).show()
            }

            true
        }
        webView_title.setOnLongClickListener(lisL)
        webView_bel.setOnLongClickListener(lisL)
        webView_sec.setOnLongClickListener(lisL)

        webView_mark.setOnClickListener(lis)


        webView_mark.tag = false
    }

    /*
    @Subscribe
    fun onThemeAction(action: ThemeAction) {
        updateTheme()
    }
    fun updateTheme() {
        webView_container.setBackgroundColor(CustomTheme.colorPrimary)
    }
    */
    @Subscribe
    fun onWebViewAction(action: WebViewAction) {
        when(action.action) {
            WebViewAction.ACTION.AFTERCHANGEPAGE -> {
                val w = WebViewManager.getCurrentActive()
                if (w != null) {
                    when(w.isBel) {
                        0 -> {
                            webView_bel.setImageResource(R.mipmap.icon_no_bel)
                            belTXT = "不可信网站"
                        }
                        1 -> {
                            webView_bel.setImageResource(R.mipmap.icon_bel)
                            belTXT = "可信网站"
                        }
                        else -> {
                            webView_bel.setImageResource(R.mipmap.icon_def_bel)
                            belTXT = "本地访问"
                        }
                    }


                    secTXT = if (!w.isSec) {
                        webView_sec.setImageResource(R.mipmap.icon_no_sec)
                        "普通访问"
                    } else {
                        webView_sec.setImageResource(R.mipmap.icon_sec)
                        "加密访问"
                    }


                    if (w.isBookMark) {
                        webView_mark.setImageResource(R.mipmap.icon_marked)
                        webView_mark.tag = true
                    } else {
                        webView_mark.setImageResource(R.mipmap.icon_no_mark)
                        webView_mark.tag = false
                    }

                }
            }
        }
    }


    @Subscribe
    fun onWebViewTitle(title: WebViewTitle) {
        webView_title.text = WebViewManager.getCurrentActive().title
    }

    @Subscribe
    fun onWebViewProgress(progress: WebViewProgress) {
        val p = progress.progress
        if (p >= 0 && p < 100) {
            webView_progress.progress = p
            webView_progress.visibility = View.VISIBLE
        } else {
            webView_progress.progress = 0
            webView_progress.visibility = View.GONE
        }
    }

    fun setViewHeight(view: View, height: Int) {
        val params = view.layoutParams
        params.height = height
        view.requestLayout()
        if (height == 0) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
        }
    }
    var hiddenHeightAnimation: ValueAnimator? = null
    var showHeightAnimation: ValueAnimator? = null

    var belTXT = "不可信网站"
    var secTXT = "普通访问"
    @Subscribe
    fun onFragmentAction(action: FragmentAction) {
        when(action.action) {
            FragmentAction.ACTION.FULLSCREEN -> {
                if (action.tag == 0) {//hidden
                    if (webView_container.visibility == View.VISIBLE) {
                        hiddenHeightAnimation!!.start()
                        // action_container.visibility = View.GONE
                        // activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    }
                } else {
                    if (webView_container.visibility == View.GONE) {
                        showHeightAnimation!!.start()
                        // action_container.visibility = View.VISIBLE
                        // activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    }

                }
            }
            FragmentAction.ACTION.URL_BELIEVE -> {
                when {
                    action.tag == 0 -> {
                        webView_bel.setImageResource(R.mipmap.icon_no_bel)
                        belTXT = "不可信网站"
                    }
                    action.tag == 1 -> {
                        webView_bel.setImageResource(R.mipmap.icon_bel)
                        belTXT = "可信网站"
                    }
                    else -> {
                        webView_bel.setImageResource(R.mipmap.icon_def_bel)
                        secTXT = "本地访问"
                    }
                }
            }
            FragmentAction.ACTION.URL_SECURITY -> {
                secTXT = if (action.tag == 0) {
                    webView_sec.setImageResource(R.mipmap.icon_no_sec)
                    "普通访问"
                } else {
                    webView_sec.setImageResource(R.mipmap.icon_sec)
                    "加密访问"
                }
            }
            FragmentAction.ACTION.URL_MARKED -> {
                if (action.tag == 1) {
                    webView_mark.setImageResource(R.mipmap.icon_marked)
                    webView_mark.tag = true
                } else {
                    webView_mark.setImageResource(R.mipmap.icon_no_mark)
                    webView_mark.tag = false
                }
            }
            FragmentAction.ACTION.HIDDEN_UI -> {
                webView_container.tag = webView_container.visibility
                webView_container.visibility = View.GONE
            }
            FragmentAction.ACTION.SHOW_UI -> {
                webView_container.visibility = webView_container.tag as Int
            }
        }
    }
}