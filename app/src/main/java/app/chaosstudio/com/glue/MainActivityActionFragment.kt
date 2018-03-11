package app.chaosstudio.com.glue

import android.animation.ValueAnimator
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import app.chaosstudio.com.glue.activity.GoAddress
import app.chaosstudio.com.glue.activity.ListBookMarksFragment
import app.chaosstudio.com.glue.activity.ListHistoryFragment
import app.chaosstudio.com.glue.activity.SimpleContainer
import app.chaosstudio.com.glue.eventb.ActionBarAction
import app.chaosstudio.com.glue.eventb.AnimationTag
import app.chaosstudio.com.glue.eventb.FragmentAction
import app.chaosstudio.com.glue.eventb.WebViewAction
import app.chaosstudio.com.glue.greendb.gen.BookMarkDao
import app.chaosstudio.com.glue.greendb.model.BookMark
import app.chaosstudio.com.glue.ui.PopupMini
import app.chaosstudio.com.glue.ui.PopupMore
import app.chaosstudio.com.glue.ui.PopupPages
import app.chaosstudio.com.glue.ui.SimpleToast
import app.chaosstudio.com.glue.unit.BrowserUnit
import app.chaosstudio.com.glue.webconfig.WebViewManager
import kotlinx.android.synthetic.main.fragment_action_bar.*
import kotlinx.android.synthetic.main.fragment_web_title.*
import kotlinx.android.synthetic.main.popup_search_box.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Created by jsen on 2018/1/21.
 */

class MainActivityActionFragment : Fragment() {
    var hiddenAnim: Animation? = null
    var showAnim: Animation? = null

    var popupMore: PopupMore? = null
    var popupMini: PopupMini? = null
    var popupPages: PopupPages? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_action_bar, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        hiddenAnim = AnimationUtils.loadAnimation(activity, R.anim.bottom_popup_out)
        showAnim = AnimationUtils.loadAnimation(activity, R.anim.bottom_popup_in)
        hiddenAnim?.fillAfter = true
        showAnim?.fillAfter = true

        init()

        val lis:View.OnClickListener = View.OnClickListener { view ->
            val id:Int = view.id
            when (id) {
                R.id.searchBox_last -> {
                    WebViewManager.getCurrentActive().findNext(false)
                    return@OnClickListener
                }
                R.id.searchBox_next -> {
                    WebViewManager.getCurrentActive().findNext(true)
                    return@OnClickListener
                }
            }
            if (searchBox.visibility == View.VISIBLE) {
                searchBoxEdit.setText("")
                searchBox.visibility = View.GONE
            } else {
                when (id) {
                    // R.id.fun_back -> EventBus.getDefault().post(WebViewAction(WebViewAction.ACTION.GOBACK))
                    R.id.fun_back -> mockEvent(GPre.actionItem1)
                    // R.id.fun_forw -> EventBus.getDefault().post(WebViewAction(WebViewAction.ACTION.GOFORWARD))
                    R.id.fun_forw -> mockEvent(GPre.actionItem2)
                    R.id.fun_home -> mockEvent(GPre.actionItem3)
                    R.id.fun_num -> mockEvent(GPre.actionItem4)
                    R.id.fun_more -> mockEvent(GPre.actionItem5)
                    /*
                    R.id.fun_home -> {
                        startActivity(Intent(activity, GoAddress::class.java))
                        activity.overridePendingTransition(R.anim.bottom_activity_in, R.anim.hold_activity)
                    }
                    R.id.fun_num -> {
                        hiddenFun()
                        showPopupPages()
                    }
                    R.id.fun_more -> {
                        hiddenFun()
                        showPopupMore()
                    }
                    */
                }
            }
        }
        /*
        * Snackbar.make(view, "func num", Snackbar.LENGTH_LONG)
                        .setAction("Action", View.OnClickListener {  }).show()
        * */
        val lisl: View.OnLongClickListener = View.OnLongClickListener { view ->
            if (searchBox.visibility == View.VISIBLE) {
                searchBoxEdit.setText("")
                searchBox.visibility = View.GONE
                true
            } else {
                val id:Int = view.id
                when (id) {
                    R.id.fun_back -> mockEvent(GPre.actionItemLong1)
                    R.id.fun_forw -> mockEvent(GPre.actionItemLong2)
                    R.id.fun_home -> mockEvent(GPre.actionItemLong3)
                    R.id.fun_num -> mockEvent(GPre.actionItemLong4)
                    R.id.fun_more -> mockEvent(GPre.actionItemLong5)
                    /*
                    R.id.fun_back -> {
                        SimpleToast.makeToast(context, "暂未实现", Toast.LENGTH_LONG).show()
                        /*
                        WebViewManager.getCurrentActive().post({
                            WebViewManager.getCurrentActive().loadUrl("javascript:callJS()")
                        })
                        SimpleToast.makeToast(activity, "JSCALL", Toast.LENGTH_LONG).show()
                        */
                        // EventBus.getDefault().post(WebViewAction(WebViewAction.ACTION.GOBACK))
                        true
                    }
                    R.id.fun_forw -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            WebViewManager.getCurrentActive().evaluateJavascript("javascript:callJS()", object : ValueCallback<String> {
                                override fun onReceiveValue(value: String?) {

                                }
                            })
                            SimpleToast.makeToast(activity, "JSCALL", Toast.LENGTH_LONG).show()
                        }
                        true
                    }
                    R.id.fun_home -> {
                        val ac = WebViewAction(WebViewAction.ACTION.GO)
                        ac.url = BrowserUnit.getHome(activity)
                        EventBus.getDefault().post(ac)
                        true
                    }
                    R.id.fun_num -> {
                        WebViewAction.fire(WebViewAction.ACTION.CREATEPAGE, BrowserUnit.getHome(activity))
                        true
                    }
                    R.id.fun_more -> {
                        EventBus.getDefault().post(WebViewAction(WebViewAction.ACTION.REFRESH))
                        true
                    }
                    else -> {
                        true
                    }
                    */
                }
                true
            }
        }


        searchBox_last.setOnClickListener(lis)
        searchBox_next.setOnClickListener(lis)

        fun_back.setOnClickListener(lis)
        fun_back.setOnLongClickListener(lisl)
        fun_forw.setOnClickListener(lis)
        fun_forw.setOnLongClickListener(lisl)
        fun_home.setOnClickListener(lis)
        fun_home.setOnLongClickListener(lisl)
        fun_num.setOnClickListener(lis)
        fun_num.setOnLongClickListener(lisl)
        fun_more.setOnClickListener(lis)
        fun_more.setOnLongClickListener(lisl)
        action_container.post({
            val acVHeight = action_container.measuredHeight.toFloat()
            hiddenHeightAnimation = ValueAnimator.ofFloat(acVHeight, 0f)
            showHeightAnimation = ValueAnimator.ofFloat(0f, acVHeight)
            hiddenHeightAnimation!!.duration = 280
            hiddenHeightAnimation!!.startDelay = 10
            showHeightAnimation!!.duration = 280
            showHeightAnimation!!.startDelay = 10

            val updateListener = ValueAnimator.AnimatorUpdateListener{ animation ->
                val vv = (animation.animatedValue as Float).toInt()
                setViewHeight(action_container, vv)
            }
            hiddenHeightAnimation!!.addUpdateListener(updateListener)
            showHeightAnimation!!.addUpdateListener(updateListener)
        })
    }

    fun init() {
        searchBoxEdit.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    WebViewManager.getCurrentActive().findAllAsync(textView.text.toString())
                } else {
                    WebViewManager.getCurrentActive().findAll(textView.text.toString())
                }
            }
            true
        }
        popupMore = PopupMore(activity, R.style.BottomPopup)
        popupMore!!.setOnDismissListener { var1: DialogInterface ->
            if(shouldShowFun) {
                showFun()
            }
            shouldShowFun = true
        }
        popupMini = PopupMini(activity, R.style.BottomPopup)
        popupMini!!.setOnDismissListener { var1: DialogInterface ->
            if(shouldShowFun) {
                showFun()
            }
            shouldShowFun = true
        }
        popupPages = PopupPages(activity, R.style.BottomPopup)
        popupPages!!.setOnDismissListener { var1: DialogInterface ->
            if(shouldShowFun) {
                showFun()
            }
            shouldShowFun = true
        }
    }
    private fun showPopupMore() {
        if(popupMore != null && !popupMore!!.isShowing) {
            popupMore!!.show()
        }
    }
    private fun hiddenPopupMore() {
        if(popupMore != null && popupMore!!.isShowing) {
            popupMore!!.dismiss()
        }

    }
    private fun showPopupMini() {
        if(popupMini != null && !popupMini!!.isShowing) {
            popupMini!!.show()
        }
    }
    private fun hiddenPopupMini() {
        if(popupMini != null && popupMini!!.isShowing) {
            popupMini!!.dismiss()
        }

    }
    private fun showPopupPages() {
        if(popupPages != null && !popupPages!!.isShowing) {
            popupPages!!.show()
        }
    }
    private fun hiddenPopupPages() {
        if(popupPages != null && popupPages!!.isShowing) {
            popupPages!!.dismiss()
        }

    }

    private fun hiddenFun() {
        action_container.startAnimation(hiddenAnim)
    }
    private fun showFun() {
        action_container.startAnimation(showAnim)
    }

    @Subscribe
    fun onAnimationTag(animationTag: AnimationTag) {
        when(animationTag.action) {
            AnimationTag.ACTION.SHOW -> showFun()
            AnimationTag.ACTION.HIDDEN -> hiddenFun()
        }
    }

    @Subscribe
    fun onActionBarAction(actionBarAction: ActionBarAction) {
        when(actionBarAction.action) {
            ActionBarAction.ACTION.PAGESCHANGED -> totalPages.text = actionBarAction.pages.toString()
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
    var shouldShowFun = true
    var hiddenHeightAnimation: ValueAnimator? = null
    var showHeightAnimation: ValueAnimator? = null
    @Subscribe
    fun onFragmentAction(action: FragmentAction) {
        when(action.action) {
            FragmentAction.ACTION.FULLSCREEN -> {
                if (action.tag == 0) {//hidden
                    if (action_container.visibility == View.VISIBLE) {
                        hiddenHeightAnimation!!.start()
                    }
                } else {
                    if (action_container.visibility == View.GONE) {
                        showHeightAnimation!!.start()
                    }

                }
            }
            FragmentAction.ACTION.SHOW_TOOLS -> {
                shouldShowFun = false
                hiddenPopupMore()
                showPopupMini()
            }
            FragmentAction.ACTION.SEARCH -> {
                searchBox.visibility = View.VISIBLE
            }
            FragmentAction.ACTION.ON_FIND_NUM -> {
                searchBox_matches.text = action.tag.toString()
            }
            FragmentAction.ACTION.HIDDEN_UI -> {
                action_container.tag = action_container.visibility
                action_container.visibility = View.GONE
                searchBox.tag = searchBox.visibility
                searchBox.visibility = View.GONE
            }
            FragmentAction.ACTION.SHOW_UI -> {
                action_container.visibility = action_container.tag as Int
                searchBox.visibility = searchBox.tag as Int
            }
        }
    }

    override fun startActivity(intent: Intent?) {
        activity.startActivity(intent)
    }

    private fun mockEvent(tag:String) {
        when(tag) {
            "1" -> {
                SimpleToast.makeToast(activity, "无操作", Toast.LENGTH_LONG).show()
            }
            "2" -> {
                EventBus.getDefault().post(WebViewAction(WebViewAction.ACTION.REFRESH))
            }
            "3" -> {
                val wv = WebViewManager.getCurrentActive()
                wv?.loadUrl("javascript:scrollTo(0,0)")
            }
            "4" -> {
                val wv = WebViewManager.getCurrentActive()
                wv?.loadUrl("javascript:scrollTo(0,document.getElementsByTagName('body')[0].scrollHeight)")

            }
            "5" -> {
                startActivity(Intent(activity, GoAddress::class.java))
                activity.overridePendingTransition(R.anim.bottom_activity_in, R.anim.hold_activity)
            }
            "6" -> {
                WebViewAction.fire(WebViewAction.ACTION.CREATEPAGE, BrowserUnit.getHome(activity))
            }
            "7" -> {
                val wv = WebViewManager.getCurrentActive()
                if (wv != null) {
                    val dao = App.instances.daoSession.bookMarkDao
                    val count = dao.queryBuilder().where(BookMarkDao.Properties.Url.eq(wv.url)).limit(1).count()
                    if (count > 0) {
                        SimpleToast.makeToast(context, "书签已存在", Toast.LENGTH_LONG).show()
                    } else {
                        val bm = BookMark()
                        bm.date = System.currentTimeMillis()
                        bm.name = wv.title
                        bm.url = wv.url
                        App.instances.daoSession.bookMarkDao.save(bm)
                        SimpleToast.makeToast(context, "添加书签成功", Toast.LENGTH_LONG).show()
                        FragmentAction.fire(FragmentAction.ACTION.URL_MARKED, 1)
                    }
                }
            }
            "8" -> {
                val intent = Intent(context, SimpleContainer::class.java)
                intent.putExtra("fragment", ListBookMarksFragment::class.java.name)
                context.startActivity(intent)
            }
            "9" -> {
                val intent = Intent(context, SimpleContainer::class.java)
                intent.putExtra("fragment", ListHistoryFragment::class.java.name)
                context.startActivity(intent)
            }
            "10" -> {
                val index = WebViewManager.getCurrentActiveIndex()
                WebViewAction.fire(WebViewAction.ACTION.CLOSEPAGE, index)
            }
            "11" -> {
                var index = WebViewManager.getCurrentActiveIndex() - 1
                if (index < 0) {
                    index = WebViewManager.getSize() - 1
                }
                WebViewAction.fire(WebViewAction.ACTION.CHANGEPAGE, index)
            }
            "12" -> {
                var index = WebViewManager.getCurrentActiveIndex() + 1
                if (index >= WebViewManager.getSize()) {
                    index = 0
                }
                WebViewAction.fire(WebViewAction.ACTION.CHANGEPAGE, index)
            }
            "13" -> {
                EventBus.getDefault().post(WebViewAction(WebViewAction.ACTION.GOBACK))
            }
            "14" -> {
                EventBus.getDefault().post(WebViewAction(WebViewAction.ACTION.GOFORWARD))
            }
            "15" -> {
                FragmentAction.fire(FragmentAction.ACTION.SEARCH)
            }
            "16" -> {
                WebViewAction.fire(WebViewAction.ACTION.GO, BrowserUnit.getHome(activity))
            }
            "17" -> {
                hiddenFun()
                showPopupPages()
            }
            "18" -> {
                hiddenFun()
                showPopupMore()
            }
        }
    }
}
