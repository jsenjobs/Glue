package app.chaosstudio.com.glue.activity.set

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.Toast
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.ui.SimplePopupWindow
import app.chaosstudio.com.glue.ui.SimpleToast
import kotlinx.android.synthetic.main.set_fragment_set_common.*
import java.io.File
import android.view.Gravity
import android.webkit.CookieManager
import android.webkit.WebViewDatabase
import android.widget.TextView
import app.chaosstudio.com.glue.App
import app.chaosstudio.com.glue.activity.SimpleContainer
import app.chaosstudio.com.glue.eventb.FragmentAction
import app.chaosstudio.com.glue.ui.EditAlert
import app.chaosstudio.com.glue.unit.BrowserUnit
import app.chaosstudio.com.glue.unit.BrowserUnit.SEARCH_ENGINE_BAIDU
import app.chaosstudio.com.glue.utils.CustomTheme
import app.chaosstudio.com.glue.webconfig.NWebView
import app.chaosstudio.com.glue.webconfig.WebViewManager
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList






/**
 * Created by jsen on 2018/1/22.
 */

class SetCommonFragment : FragmentBase() {

    val base = Environment.getExternalStorageDirectory().toString() + File.separator
    var dir:String = ""

    init {
        title = "通用"
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.set_fragment_set_common, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sp = PreferenceManager.getDefaultSharedPreferences(context)

        val lis = View.OnClickListener{ view ->
            when(view.id) {
                R.id.set_common_tag -> {
                    if (!TextUtils.isEmpty(sp!!.getString("sp_username", ""))) {
                        val intent = Intent(activity, SimpleContainer::class.java)
                        intent.putExtra("fragment", UserFragment::class.java.name)
                        startActivity(intent)
                    } else {
                        val intent = Intent(activity, SimpleContainer::class.java)
                        intent.putExtra("fragment", LoginFragment::class.java.name)
                        startActivity(intent)
                    }
                }
                R.id.set_common_ua -> {
                    // SimpleToast.makeToast(activity, "暂未实现", Toast.LENGTH_LONG).show()
                    uaPopWindow!!.updateAnchor(view)
                    uaPopWindow!!.showAtLocation(Gravity.TOP or Gravity.START)
                }
                R.id.set_common_cc -> {
                    clearCacheExpand.toggleExpand()
                }
                R.id.set_common_adblock -> set_common_adblock_r.isChecked = !set_common_adblock_r.isChecked
                R.id.set_common_ad_white -> {
                    UrlRuleFragment.isWhite = true
                    val intent = Intent(activity, SimpleContainer::class.java)
                    intent.putExtra("fragment", UrlRuleFragment::class.java.name)
                    startActivity(intent)
                }
                R.id.set_common_ad_black -> {
                    UrlRuleFragment.isWhite = false
                    val intent = Intent(activity, SimpleContainer::class.java)
                    intent.putExtra("fragment", UrlRuleFragment::class.java.name)
                    startActivity(intent)
                }
                R.id.set_common_fullscreen -> set_common_fullscreen_r.isChecked = !set_common_fullscreen_r.isChecked
                R.id.set_common_hidden_status -> set_common_hidden_status_r.isChecked = !set_common_hidden_status_r.isChecked
                R.id.set_common_home -> {
                    urlEditAlert?.editText!!.setText(BrowserUnit.getHome(context))
                    urlEditAlert?.show()
                }
                R.id.set_common_se -> {
                    // SimpleToast.makeToast(activity, "暂未实现", Toast.LENGTH_LONG).show()
                    searchEnginePopup!!.updateAnchor(view)
                    searchEnginePopup!!.showAtLocation(Gravity.TOP or Gravity.START)
                }
                R.id.set_common_dd -> {
                    downloadDirAlert?.editText!!.setSelection(downloadDirAlert?.editText!!.text.length)
                    downloadDirAlert?.show()
                }
                R.id.set_common_cd -> clearCacheExpand_exit.toggleExpand()
                R.id.set_common_quick -> {
                    val intent = Intent(activity, SimpleContainer::class.java)
                    intent.putExtra("fragment", SetQuickFragment::class.java.name)
                    startActivity(intent)
                }

                R.id.set_common_cc_cache -> {
                    set_common_cc_cache_r.isChecked = !set_common_cc_cache_r.isChecked
                }
                R.id.set_common_cc_form -> {
                    set_common_cc_form_r.isChecked = !set_common_cc_form_r.isChecked
                }
                R.id.set_common_cc_his -> {
                    set_common_cc_his_r.isChecked = !set_common_cc_his_r.isChecked
                }
                R.id.set_common_cc_web -> {
                    set_common_cc_web_r.isChecked = !set_common_cc_web_r.isChecked
                }
                R.id.set_common_cc_cookies -> {
                    set_common_cc_cookies_r.isChecked = !set_common_cc_cookies_r.isChecked
                }
                R.id.set_common_cc_ok -> doClear()

                R.id.set_common_cc_cache_exit -> {
                    set_common_cc_cache_exit_r.isChecked = !set_common_cc_cache_exit_r.isChecked
                }
                R.id.set_common_cc_form_exit -> {
                    set_common_cc_form_exit_r.isChecked = !set_common_cc_form_exit_r.isChecked
                }
                R.id.set_common_cc_his_exit -> {
                    set_common_cc_his_exit_r.isChecked = !set_common_cc_his_exit_r.isChecked
                }
                R.id.set_common_cc_web_exit -> {
                    set_common_cc_web_exit_r.isChecked = !set_common_cc_web_exit_r.isChecked
                }
                R.id.set_common_cc_cookies_exit -> {
                    set_common_cc_cookies_exit_r.isChecked = !set_common_cc_cookies_exit_r.isChecked
                }

                R.id.ua_edit_desk -> setChecked(popUAModels, R.id.ua_edit_desk)
                R.id.ua_edit_default -> setChecked(popUAModels, R.id.ua_edit_default)
                R.id.ua_edit_custom -> {
                    if (!setChecked(popUAModels, R.id.ua_edit_custom)) {
                        uaPopWindow!!.dismiss()
                        uaEditAlert!!.show()
                    }
                }

                R.id.search_engine_google -> setChecked(popSEModels, R.id.search_engine_google)
                R.id.search_engine_ddgo -> setChecked(popSEModels, R.id.search_engine_ddgo)
                R.id.search_engine_spage -> setChecked(popSEModels, R.id.search_engine_spage)
                R.id.search_engine_bing -> setChecked(popSEModels, R.id.search_engine_bing)
                R.id.search_engine_baidu -> setChecked(popSEModels, R.id.search_engine_baidu)
                R.id.search_engine_shenma -> setChecked(popSEModels, R.id.search_engine_shenma)
                R.id.search_engine_sogou -> setChecked(popSEModels, R.id.search_engine_sogou)
                R.id.search_engine_360 -> setChecked(popSEModels, R.id.search_engine_360)
                R.id.search_engine_custom -> {
                    if(!setChecked(popSEModels, R.id.search_engine_custom)) {
                        searchEnginePopup!!.dismiss()
                        searchEngineEditAlert!!.show()
                    }
                }
            }
        }
        val cc = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            when(buttonView.id) {
                R.id.set_common_cc_cache_r -> sp!!.edit().putBoolean(context.getString(R.string.sp_clear_cache), isChecked).apply()
                R.id.set_common_cc_form_r -> sp!!.edit().putBoolean(context.getString(R.string.sp_clear_form), isChecked).apply()
                R.id.set_common_cc_his_r -> sp!!.edit().putBoolean(context.getString(R.string.sp_clear_history), isChecked).apply()
                R.id.set_common_cc_web_r -> sp!!.edit().putBoolean(context.getString(R.string.sp_clear_web), isChecked).apply()
                R.id.set_common_cc_cookies_r -> sp!!.edit().putBoolean(context.getString(R.string.sp_clear_cookie), isChecked).apply()

                R.id.set_common_cc_cache_exit_r -> sp!!.edit().putBoolean(context.getString(R.string.sp_clear_cache_e), isChecked).apply()
                R.id.set_common_cc_form_exit_r -> sp!!.edit().putBoolean(context.getString(R.string.sp_clear_form_e), isChecked).apply()
                R.id.set_common_cc_his_exit_r -> sp!!.edit().putBoolean(context.getString(R.string.sp_clear_history_e), isChecked).apply()
                R.id.set_common_cc_web_exit_r -> sp!!.edit().putBoolean(context.getString(R.string.sp_clear_web_e), isChecked).apply()
                R.id.set_common_cc_cookies_exit_r -> sp!!.edit().putBoolean(context.getString(R.string.sp_clear_cookie_e), isChecked).apply()

                R.id.set_common_adblock_r -> {
                    if (isChecked) {
                        set_common_adblock_tag.text = "已开启"
                    } else {
                        set_common_adblock_tag.text = "未开启"
                    }
                    sp!!.edit().putBoolean(context.getString(R.string.sp_ad_block), isChecked).apply()
                }
                R.id.set_common_fullscreen_r -> {
                    sp!!.edit().putBoolean(context.getString(R.string.sp_omnibox_control), isChecked).apply()
                    if (isChecked) {
                        set_common_fullscreen_tag.text = "已开启"
                        FragmentAction.fire(FragmentAction.ACTION.FULL_MODE_CHANGE, 1)
                    } else {
                        set_common_fullscreen_tag.text = "未开启"
                        FragmentAction.fire(FragmentAction.ACTION.FULL_MODE_CHANGE, 0)
                    }
                }
                R.id.set_common_hidden_status_r -> {
                    sp!!.edit().putBoolean(context.getString(R.string.sp_hidden_status), isChecked).apply()
                    CustomTheme.hiddenStatus = isChecked
                    FragmentAction.fire(FragmentAction.ACTION.FULL_STATUS_CHANGE)
                }

                R.id.ua_edit_desk_r -> CheckPop(popUAModels, R.id.ua_edit_desk_r, isChecked, uaPopWindow!!, R.string.sp_user_agent, set_common_ua_tag)
                R.id.ua_edit_default_r -> CheckPop(popUAModels, R.id.ua_edit_default_r, isChecked, uaPopWindow!!, R.string.sp_user_agent, set_common_ua_tag)
                R.id.ua_edit_custom_r -> {
                    CheckPop(popUAModels, R.id.ua_edit_custom_r, isChecked, uaPopWindow!!, R.string.sp_user_agent, set_common_ua_tag)
                    if (isChecked && uaEditAlert != null) {
                        uaEditAlert!!.show()
                    }
                }

                R.id.search_engine_google_r -> CheckPop(popSEModels, R.id.search_engine_google_r, isChecked, searchEnginePopup!!, R.string.sp_search_engine, set_common_se_tag)
                R.id.search_engine_ddgo_r -> CheckPop(popSEModels, R.id.search_engine_ddgo_r, isChecked, searchEnginePopup!!, R.string.sp_search_engine, set_common_se_tag)
                R.id.search_engine_spage_r-> CheckPop(popSEModels, R.id.search_engine_spage_r, isChecked, searchEnginePopup!!, R.string.sp_search_engine, set_common_se_tag)
                R.id.search_engine_bing_r -> CheckPop(popSEModels, R.id.search_engine_bing_r, isChecked, searchEnginePopup!!, R.string.sp_search_engine, set_common_se_tag)
                R.id.search_engine_baidu_r -> CheckPop(popSEModels, R.id.search_engine_baidu_r, isChecked, searchEnginePopup!!, R.string.sp_search_engine, set_common_se_tag)
                R.id.search_engine_shenma_r -> CheckPop(popSEModels, R.id.search_engine_shenma_r, isChecked, searchEnginePopup!!, R.string.sp_search_engine, set_common_se_tag)
                R.id.search_engine_sogou_r -> CheckPop(popSEModels, R.id.search_engine_sogou_r, isChecked, searchEnginePopup!!, R.string.sp_search_engine, set_common_se_tag)
                R.id.search_engine_360_r -> CheckPop(popSEModels, R.id.search_engine_360_r, isChecked, searchEnginePopup!!, R.string.sp_search_engine, set_common_se_tag)
                R.id.search_engine_custom_r -> {
                    CheckPop(popSEModels, R.id.search_engine_custom_r, isChecked, searchEnginePopup!!, R.string.sp_search_engine, set_common_se_tag)
                    // show view
                    if (isChecked && searchEngineEditAlert != null) {
                        searchEngineEditAlert!!.show()
                    }
                }
            }
        }
        set_common_tag.setOnClickListener(lis)
        set_common_ua.setOnClickListener(lis)
        set_common_cc.setOnClickListener(lis)
        set_common_adblock.setOnClickListener(lis)
        set_common_ad_white.setOnClickListener(lis)
        set_common_ad_black.setOnClickListener(lis)
        set_common_fullscreen.setOnClickListener(lis)
        set_common_hidden_status.setOnClickListener(lis)
        set_common_home.setOnClickListener(lis)
        set_common_se.setOnClickListener(lis)
        set_common_dd.setOnClickListener(lis)
        set_common_cd.setOnClickListener(lis)
        set_common_quick.setOnClickListener(lis)

        set_common_cc_cache.setOnClickListener(lis)
        set_common_cc_form.setOnClickListener(lis)
        set_common_cc_his.setOnClickListener(lis)
        set_common_cc_web.setOnClickListener(lis)
        set_common_cc_cookies.setOnClickListener(lis)

        set_common_cc_ok.setOnClickListener(lis)

        set_common_cc_cache_exit.setOnClickListener(lis)
        set_common_cc_form_exit.setOnClickListener(lis)
        set_common_cc_his_exit.setOnClickListener(lis)
        set_common_cc_web_exit.setOnClickListener(lis)
        set_common_cc_cookies_exit.setOnClickListener(lis)

        set_common_cc_cache_r.setOnCheckedChangeListener(cc)
        set_common_cc_form_r.setOnCheckedChangeListener(cc)
        set_common_cc_his_r.setOnCheckedChangeListener(cc)
        set_common_cc_web_r.setOnCheckedChangeListener(cc)
        set_common_cc_cookies_r.setOnCheckedChangeListener(cc)

        set_common_cc_cache_exit_r.setOnCheckedChangeListener(cc)
        set_common_cc_form_exit_r.setOnCheckedChangeListener(cc)
        set_common_cc_his_exit_r.setOnCheckedChangeListener(cc)
        set_common_cc_web_exit_r.setOnCheckedChangeListener(cc)
        set_common_cc_cookies_exit_r.setOnCheckedChangeListener(cc)

        set_common_adblock_r.setOnCheckedChangeListener(cc)
        set_common_fullscreen_r.setOnCheckedChangeListener(cc)
        set_common_hidden_status_r.setOnCheckedChangeListener(cc)

        createUAPop(lis, cc)
        createSEPop(lis, cc)
        createUrlPop()
        createDownloadDirAlert()

        initUI()
    }


    var sp:SharedPreferences? = null
    private fun initUI() {
        // UA
        val userAgent = sp!!.getString(context.getString(R.string.sp_user_agent), "3").toInt()
        when (userAgent) {
            1 -> set_common_ua_tag.text = "桌面"// settings.userAgentString = BrowserUnit.UA_DESKTOP
            2 -> set_common_ua_tag.text = "自定义"// settings.userAgentString = sp.getString(context.getString(R.string.sp_user_agent_custom), userAgentOriginal)
            else -> set_common_ua_tag.text = "默认"// settings.userAgentString = userAgentOriginal
        }

        // clear cache
        set_common_cc_cache_r.isChecked = sp!!.getBoolean(context.getString(R.string.sp_clear_cache), true)
        set_common_cc_form_r.isChecked = sp!!.getBoolean(context.getString(R.string.sp_clear_form), true)
        set_common_cc_his_r.isChecked = sp!!.getBoolean(context.getString(R.string.sp_clear_history), true)
        set_common_cc_web_r.isChecked = sp!!.getBoolean(context.getString(R.string.sp_clear_web), false)
        set_common_cc_cookies_r.isChecked = sp!!.getBoolean(context.getString(R.string.sp_clear_cookie), false)

        set_common_cc_cache_exit_r.isChecked = sp!!.getBoolean(context.getString(R.string.sp_clear_cache_e), true)
        set_common_cc_form_exit_r.isChecked = sp!!.getBoolean(context.getString(R.string.sp_clear_form_e), true)
        set_common_cc_his_exit_r.isChecked = sp!!.getBoolean(context.getString(R.string.sp_clear_history_e), true)
        set_common_cc_web_exit_r.isChecked = sp!!.getBoolean(context.getString(R.string.sp_clear_web_e), false)
        set_common_cc_cookies_exit_r.isChecked = sp!!.getBoolean(context.getString(R.string.sp_clear_cookie_e), false)

        // ad block
        if(sp!!.getBoolean(context.getString(R.string.sp_ad_block), true))  {
            set_common_adblock_tag.text = "已开启"
            set_common_adblock_r.isChecked = true
        } else {
            set_common_adblock_tag.text = "未开启"
            set_common_adblock_r.isChecked = false
        }
        if(sp!!.getBoolean(context.getString(R.string.sp_omnibox_control), false))  {
            set_common_fullscreen_tag.text = "已开启"
            set_common_fullscreen_r.isChecked = true
        } else {
            set_common_fullscreen_tag.text = "未开启"
            set_common_fullscreen_r.isChecked = false
        }
        set_common_hidden_status_r.isChecked = sp!!.getBoolean(context.getString(R.string.sp_hidden_status), false)


        dir = sp!!.getString(context.getString(R.string.sp_file_download_dir), Environment.DIRECTORY_DOWNLOADS)

        set_common_dd_dir.text = base + dir

        clearCacheExpand.initExpand(false)
        clearCacheExpand_exit.initExpand(false)

        if(BrowserUnit.defaultHome == BrowserUnit.getHome(context)) {
            set_common_now_home.text = "默认"
        } else {
            set_common_now_home.text = BrowserUnit.getHome(context)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(sp!!.getBoolean(context.getString(R.string.sp_omnibox_control), false)) {
            for (w:NWebView in WebViewManager.getAllNWebView()) {
                initScrollChange(w)
            }
        } else {
            for (w:NWebView in WebViewManager.getAllNWebView()) {
                // w.onScrollChangeListener = null
                w.onFling = null
            }
        }
    }
    private fun doClear() {
        /**
         * 清除WebView缓存
         */
        //清理Webview缓存数据库
        if(sp!!.getBoolean(context.getString(R.string.sp_clear_cache), true)) {
            //清理Webview缓存数据库
            try {
                activity.deleteDatabase("webview.db")
                activity.deleteDatabase("webviewCache.db")
            } catch (e : Exception) {
                e.printStackTrace()
            }
            // val webviewCacheDir = File(getCacheDir().getAbsolutePath() + "/webviewCache")
            // Log.e(TAG, "webviewCacheDir path=" + webviewCacheDir.getAbsolutePath())
            val webviewCacheDir = context.cacheDir

            // 删除webview 缓存目录
            if(webviewCacheDir.exists()){
                deleteFile(webviewCacheDir);
            }

            WebViewManager.getCurrentActive().clearCache(true)
        }

        if(sp!!.getBoolean(context.getString(R.string.sp_clear_form), true)) {
            WebViewManager.getCurrentActive().clearFormData()
            WebViewDatabase.getInstance(context).clearHttpAuthUsernamePassword()
        }

        if(sp!!.getBoolean(context.getString(R.string.sp_clear_history), true)) {
            App.instances.daoSession.historyDao.deleteAll()
        }

        if(sp!!.getBoolean(context.getString(R.string.sp_clear_web), false)) {
            //WebView 缓存文件
            // val appCacheDir = File(getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME)
            // Log.e(TAG, "appCacheDir path=" + appCacheDir.getAbsolutePath())


            //删除webview 缓存 缓存目录
            // if(appCacheDir.exists()){
            //     deleteFile(appCacheDir);
            // }

        }

        if(sp!!.getBoolean(context.getString(R.string.sp_clear_cookie), false)) {
            val cookieManager = CookieManager.getInstance()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.flush()
                cookieManager.removeAllCookies { }
            } else {
                cookieManager.removeAllCookie()
            }
        }

        SimpleToast.makeToast(context, "清理成功", Toast.LENGTH_LONG).show()
    }

    /**
     * 递归删除 文件/文件夹
     *
     * @param file
     */
    private fun deleteFile(file: File) {

        Log.i("SetCommonFragment", "delete file path=" + file.absolutePath)

        if (file.exists()) {
            if (file.isFile) {
                file.delete()
            } else if (file.isDirectory) {
                val files = file.listFiles()
                for (i in files!!.indices) {
                    deleteFile(files[i])
                }
            }
            file.delete()
        } else {
            Log.e("SetCommonFragment", "delete file no exists " + file.absolutePath)
        }
    }

    private fun initScrollChange(w: NWebView) {
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
                    }, 1000)
                    Handler().postDelayed({
                        hideOmnibox()
                    }, 250)
                } else if (scrollY < oldScrollY) {

                    w.onScrollChangeListener = null
                    Handler().postDelayed({
                        initScrollChange(w)
                    }, 1000)
                    Handler().postDelayed({
                        showOmnibox()
                    }, 250)
                }
            }

        }
        */
    }
    private fun hideOmnibox() {
        FragmentAction.fire(FragmentAction.ACTION.FULLSCREEN, 0)
    }
    private fun showOmnibox() {
        FragmentAction.fire(FragmentAction.ACTION.FULLSCREEN, 1)
    }

    var urlEditAlert:EditAlert? = null
    fun createUrlPop() {
        val build = EditAlert.Build(activity, R.style.SimpleAlert)
        build.content = BrowserUnit.getHome(context)
        build.onPos = View.OnClickListener {_ ->
            val  url = urlEditAlert!!.text
            if (TextUtils.isEmpty(url)) {
                sp!!.edit().remove(context.getString(R.string.sp_home)).apply()
                BrowserUnit.setHome(null)
            } else {
                sp!!.edit().putString(context.getString(R.string.sp_home), url).apply()
                BrowserUnit.setHome(url)
            }

            if(BrowserUnit.defaultHome == BrowserUnit.getHome(context)) {
                set_common_now_home.text = "默认"
            } else {
                set_common_now_home.text = BrowserUnit.getHome(context)
            }
        }
        urlEditAlert = build.build()
    }
    var downloadDirAlert:EditAlert? = null
    fun createDownloadDirAlert() {
        val build = EditAlert.Build(context, R.style.SimpleAlert)
        build.content = sp!!.getString(context.getString(R.string.sp_file_download_dir), Environment.DIRECTORY_DOWNLOADS)
        build.onPos = View.OnClickListener { view ->
            val p = downloadDirAlert!!.text
            val fs = File(base + p)
            try{
                fs.mkdirs()
                sp!!.edit().putString(context.getString(R.string.sp_file_download_dir), downloadDirAlert!!.text).apply()
                dir = p?:dir
                set_common_dd_dir.text = base + dir
            } catch (e:Exception) {
                e.printStackTrace()
            }
        }
        downloadDirAlert = build.build()
    }
    var uaPopWindow:SimplePopupWindow? = null
    var uaEditAlert:EditAlert? = null
    var searchEnginePopup:SimplePopupWindow? = null
    var searchEngineEditAlert:EditAlert? = null
    var popUAModels: ArrayList<PopModel> = ArrayList()
    var popSEModels: ArrayList<PopModel> = ArrayList()
    fun createUAPop(lis:View.OnClickListener, cc: CompoundButton.OnCheckedChangeListener) {
        val root = View.inflate(activity, R.layout.popup_ua_edit, null)
        popUAModels.clear()
        popUAModels.add(PopModel("1", "桌面", root.findViewById(R.id.ua_edit_desk_r), root.findViewById(R.id.ua_edit_desk)))
        popUAModels.add(PopModel("2", "自定义", root.findViewById(R.id.ua_edit_custom_r), root.findViewById(R.id.ua_edit_custom)))
        popUAModels.add(PopModel("3", "默认", root.findViewById(R.id.ua_edit_default_r), root.findViewById(R.id.ua_edit_default)))
        for (model:PopModel in popUAModels) {
            model.checkBox.setOnCheckedChangeListener(cc)
            model.view.setOnClickListener(lis)
        }

        uaPopWindow = SimplePopupWindow(view, root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        uaPopWindow!!.animationStyle = R.style.popwin_anim_style
        val userAgent = sp!!.getString(context.getString(R.string.sp_user_agent), "3").toInt()
        popUAModels[userAgent - 1].checkBox.isChecked = true

        val build = EditAlert.Build(activity, R.style.SimpleAlert)
        build.onPos = View.OnClickListener { view ->
            sp!!.edit().putString(context.getString(R.string.sp_user_agent_custom), uaEditAlert!!.text).apply()
        }
        build.content = sp!!.getString(context.getString(R.string.sp_user_agent_custom), WebViewManager.getCurrentActive().wvConfig!!.userAgentOriginal)
        uaEditAlert = build.build()

    }
    private fun createSEPop(lis:View.OnClickListener, cc: CompoundButton.OnCheckedChangeListener) {

        val root = View.inflate(activity, R.layout.popup_search_engine, null)
        popSEModels.clear()
        popSEModels.add(PopModel("0", "谷歌", root.findViewById(R.id.search_engine_google_r), root.findViewById(R.id.search_engine_google)))
        popSEModels.add(PopModel("1", "duckduckgo", root.findViewById(R.id.search_engine_ddgo_r), root.findViewById(R.id.search_engine_ddgo)))
        popSEModels.add(PopModel("2", "startpage", root.findViewById(R.id.search_engine_spage_r), root.findViewById(R.id.search_engine_spage)))
        popSEModels.add(PopModel("3", "必应", root.findViewById(R.id.search_engine_bing_r), root.findViewById(R.id.search_engine_bing)))
        popSEModels.add(PopModel("4", "百度", root.findViewById(R.id.search_engine_baidu_r), root.findViewById(R.id.search_engine_baidu)))
        popSEModels.add(PopModel("5", "神马", root.findViewById(R.id.search_engine_shenma_r), root.findViewById(R.id.search_engine_shenma)))
        popSEModels.add(PopModel("6", "搜狗", root.findViewById(R.id.search_engine_sogou_r), root.findViewById(R.id.search_engine_sogou)))
        popSEModels.add(PopModel("7", "360", root.findViewById(R.id.search_engine_360_r), root.findViewById(R.id.search_engine_360)))
        popSEModels.add(PopModel("8", "自定义", root.findViewById(R.id.search_engine_custom_r), root.findViewById(R.id.search_engine_custom)))

        for (model:PopModel in popSEModels) {
            model.checkBox.setOnCheckedChangeListener(cc)
            model.view.setOnClickListener(lis)
        }


        searchEnginePopup = SimplePopupWindow(view, root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        searchEnginePopup!!.animationStyle = R.style.popwin_anim_style
        // val custom = sp!!.getString(context.getString(R.string.sp_search_engine_custom), SEARCH_ENGINE_BAIDU)
        val i = sp!!.getString(context.getString(R.string.sp_search_engine), "4").toInt()
        popSEModels[i].checkBox.isChecked = true


        val build = EditAlert.Build(activity, R.style.SimpleAlert)
        build.onPos = View.OnClickListener { view ->
            sp!!.edit().putString(context.getString(R.string.sp_search_engine_custom), searchEngineEditAlert!!.text).apply()
        }
        build.content = sp!!.getString(context.getString(R.string.sp_search_engine_custom), SEARCH_ENGINE_BAIDU)
        searchEngineEditAlert = build.build()
    }
    private fun CheckPop(list:ArrayList<PopModel>, checkBoxId:Int, isChecked:Boolean, popupView:SimplePopupWindow, storeKeyId:Int, tagTextView:TextView) {

        if (isChecked) {
            popupView.dismiss()
            for (model:PopModel in list) {
                if (checkBoxId == model.checkBox.id) {
                    sp!!.edit().putString(context.getString(storeKeyId), model.value).apply()
                    tagTextView.text = model.showName
                } else if (model.checkBox.isChecked) {
                    model.checkBox.isChecked = false
                }
            }
        }
    }
    private fun setChecked(list:ArrayList<PopModel>, textViewId:Int): Boolean {
        for (model:PopModel in list) {
            if (model.view.id == textViewId) {
                if (model.checkBox.isChecked) return false
                model.checkBox.isChecked = true
                return true
            }
        }
        return true
    }

    class PopModel(val value:String, val showName:String, val checkBox: CheckBox, val view: View)
}
