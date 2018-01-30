package app.chaosstudio.com.glue.ui

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.widget.*

import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.eventb.FragmentAction
import app.chaosstudio.com.glue.eventb.WebViewAction
import app.chaosstudio.com.glue.unit.BrowserUnit
import app.chaosstudio.com.glue.utils.CustomTheme
import app.chaosstudio.com.glue.utils.ScreenshotTask
import app.chaosstudio.com.glue.webconfig.WebViewManager
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList
import android.content.pm.ShortcutManager
import android.content.pm.ShortcutInfo
import android.graphics.drawable.Icon
import app.chaosstudio.com.glue.activity.ListHistoryFragment
import app.chaosstudio.com.glue.activity.ListLogsFragment
import app.chaosstudio.com.glue.activity.SimpleContainer
import java.net.URLEncoder


/**
 * Created by jsen on 2018/1/21.
 */

class PopupMini(context: Context, themeResId: Int) : Dialog(context, themeResId) {

    var spp:SharedPreferences? = null
    var popUAModels: ArrayList<PopModel>
    var tX = 0f
    var tY = 0f
    var ttY = 0f
    init {

        val root = LayoutInflater.from(context).inflate(R.layout.popup_mini_tools, null) as LinearLayout
        setContentView(root)
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        spp = sp

        popUAModels = ArrayList()
        initValue(sp, root)

        val window = window
        window!!.setGravity(Gravity.BOTTOM)
        window.setDimAmount(0.0f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val lp = window.attributes
        lp.x = 0
        // lp.y = (int)context.getResources().getDimension(R.dimen.action_bar_height);
        lp.y = 0
        lp.width = context.resources.displayMetrics.widthPixels
        root.measure(0, 0)
        lp.height = root.measuredHeight
        lp.alpha = 0.95f

        val lis = View.OnClickListener{ view ->
            when(view.id) {
                R.id.mini_bt_pic_model -> {
                    if (sp.getBoolean(context.getString(R.string.sp_images), true)) {
                        sp.edit().putBoolean(context.getString(R.string.sp_images), false).apply()
                        WebViewManager.getCurrentActive().clearCache(true)
                        root.findViewById<TextView>(R.id.mini_bt_pic_model_txt).text = "无图模式"
                    } else {
                        sp.edit().putBoolean(context.getString(R.string.sp_images), true).apply()
                        root.findViewById<TextView>(R.id.mini_bt_pic_model_txt).text = "有图模式"
                    }
                    WebViewManager.reloadPreferences()
                }
                R.id.mini_bt_ua_mode -> {
                    val deskMode = sp.getBoolean(context.getString(R.string.sp_user_agent_desk_mode), false)
                    if (deskMode) {
                        sp.edit().putBoolean(context.getString(R.string.sp_user_agent_desk_mode), false).apply()
                        root.findViewById<TextView>(R.id.mini_bt_ua_mode_txt).text = "非电脑模式"
                        root.findViewById<ImageView>(R.id.mini_bt_ua_mode_icon).setImageResource(R.mipmap.icon_phone)
                    } else {
                        sp.edit().putBoolean(context.getString(R.string.sp_user_agent_desk_mode), true).apply()
                        root.findViewById<TextView>(R.id.mini_bt_ua_mode_txt).text = "电脑模式"
                        root.findViewById<ImageView>(R.id.mini_bt_ua_mode_icon).setImageResource(R.mipmap.icon_desktop)
                    }
                    WebViewManager.reloadPreferences()
                }
                R.id.mini_bt_fullscreen -> {
                    val fullscreen = sp.getBoolean(context.getString(R.string.sp_hidden_status), false)
                    if(fullscreen) {
                        sp.edit().putBoolean(context.getString(R.string.sp_hidden_status), false).apply()
                        CustomTheme.hiddenStatus = false
                        root.findViewById<TextView>(R.id.mini_bt_fullscreen_txt).text = "非全屏"
                        root.findViewById<ImageView>(R.id.mini_bt_fullscreen_icon).setImageResource(R.mipmap.icon_no_fullscreen)
                    } else {
                        sp.edit().putBoolean(context.getString(R.string.sp_hidden_status), true).apply()
                        CustomTheme.hiddenStatus = true
                        root.findViewById<TextView>(R.id.mini_bt_fullscreen_txt).text = "全屏"
                        root.findViewById<ImageView>(R.id.mini_bt_fullscreen_icon).setImageResource(R.mipmap.icon_fullscreen)
                    }
                    FragmentAction.fire(FragmentAction.ACTION.FULL_STATUS_CHANGE)
                }
                R.id.mini_bt_ua -> {
                    uaPopWindow!!.updateAnchor(root, tX.toInt(), tY.toInt())
                    uaPopWindow!!.showAtLocation(Gravity.TOP or Gravity.START)
                }
                R.id.mini_bt_copy_url -> {
                    dismiss()
                    BrowserUnit.copyURL(context, WebViewManager.getCurrentActive().url)
                }

                R.id.mini_bt_save_page -> {
                    dismiss()
                    val uri = WebViewManager.getCurrentActive().url
                    val i = Uri.parse(uri)
                    if (i.scheme != "http" && i.scheme != "https") {
                        SimpleToast.makeToast(context, "只支持离线http和https网页", Toast.LENGTH_LONG).show()
                    } else {
                        BrowserUnit.download(context, WebViewManager.getCurrentActive().url, WebViewManager.getCurrentActive().url, BrowserUnit.MIME_TYPE_TEXT_HTML)
                    }
                }
                R.id.mini_bt_saved_page -> {
                    dismiss()
                    SimpleToast.makeToast(context, "暂未实现", Toast.LENGTH_LONG).show()
                }
                R.id.mini_bt_screenshot -> {
                    dismiss()
                    sp.edit().putInt("screenshot", 1).apply()
                    ScreenshotTask(context, WebViewManager.getCurrentActive()).execute()
                }
                R.id.mini_bt_translate -> {
                    dismiss()
                    val wv = WebViewManager.getCurrentActive()
                    if(wv != null) {
                        val url = "http://translate.baiducontent.com/transpage?from=auto&to=en&source=url&query="
                        val api = url + URLEncoder.encode(wv.url, "utf-8")
                        WebViewAction.fire(WebViewAction.ACTION.GO, api)
                    }
                    // SimpleToast.makeToast(context, "暂未实现", Toast.LENGTH_LONG).show()
                }
                R.id.mini_bt_send_to_desktop -> {
                    dismiss()
                    //添加书签的意图
                    val wv = WebViewManager.getCurrentActive()
                    if (wv != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val shortcutManager = context.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager
                            if (shortcutManager.isRequestPinShortcutSupported) {
                                val intent = Intent(Intent.ACTION_MAIN)
                                // val content_url = Uri.parse(wv.url)
                                // intent.data = content_url
                                intent.action = Intent.ACTION_VIEW //action必须设置，不然报错
                                intent.putExtra("SURL", wv.url)
                                intent.setClassName("app.chaosstudio.com.glue", "app.chaosstudio.com.glue.MainActivity") //调用系统浏览器
                                intent.addCategory(Intent.CATEGORY_LAUNCHER)

                                val info = ShortcutInfo.Builder(context, "app.chaosstudio.com.glue")
                                        .setIcon(Icon.createWithResource(context, R.mipmap.ic_launcher))
                                        .setShortLabel(wv.title)
                                        .setIntent(intent)
                                        .build()

                                //当添加快捷方式的确认弹框弹出来时，将被回调
                                // val shortcutCallbackIntent = PendingIntent.getBroadcast(context, 0, Intent(context, MyReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

                                // shortcutManager.requestPinShortcut(info, shortcutCallbackIntent.intentSender)
                                shortcutManager.requestPinShortcut(info, null)
                            }
                        } else {
                            val shortcutintent = Intent("com.android.launcher.action.INSTALL_SHORTCUT")
                            shortcutintent.putExtra("duplicate", false)
                            shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, wv.title)
                            val icon = Intent.ShortcutIconResource.fromContext(context, R.mipmap.ic_launcher)
                            shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon)

                            val intent = Intent(Intent.ACTION_MAIN)
                            // val content_url = Uri.parse(wv.url)
                            // intent.data = content_url
                            intent.putExtra("SURL", wv.url)
                            intent.setClassName("app.chaosstudio.com.glue", "app.chaosstudio.com.glue.MainActivity") //调用系统浏览器
                            intent.addCategory(Intent.CATEGORY_LAUNCHER)
                            shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent)
                            context.sendBroadcast(shortcutintent)
                        }
                    }
                }

                R.id.mini_bt_code -> {
                    dismiss()
                    WebViewAction.fire(WebViewAction.ACTION.GO, "file:///android_asset/plugin_source/code_viewer.html")
                }
                R.id.mini_bt_search -> {
                    dismiss()
                    FragmentAction.fire(FragmentAction.ACTION.SEARCH)
                }
                R.id.mini_bt_resources -> {
                    dismiss()
                    ListLogsFragment.isResources = true
                    val intent = Intent(context, SimpleContainer::class.java)
                    intent.putExtra("fragment", ListLogsFragment::class.java.name)
                    context.startActivity(intent)
                }
                R.id.mini_bt_logs -> {
                    dismiss()
                    ListLogsFragment.isResources = false
                    val intent = Intent(context, SimpleContainer::class.java)
                    intent.putExtra("fragment", ListLogsFragment::class.java.name)
                    context.startActivity(intent)
                }
                R.id.mini_bt_other_application -> {
                    dismiss()
                    //添加书签的意图
                    val wv = WebViewManager.getCurrentActive()
                    if (wv != null) {
                        //Uri uri = Uri.parse("file://"+file.getAbsolutePath());
                        val intent = Intent()
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        //设置intent的Action属性
                        intent.action = Intent.ACTION_VIEW
                        intent.data = Uri.parse(wv.url)
                        //设置intent的data和Type属性。
                        // intent.setDataAndType(/*uri*/Uri.parse(wv.url), BrowserUnit.MIME_TYPE_TEXT_HTML)
                        //跳转
                        context.startActivity(intent)
                        (context as Activity?)?.overridePendingTransition(0, 0)
                    }
                }


                R.id.mini_bt_dis -> dismiss()
                R.id.mini_bt_quit -> {
                    dismiss()
                    (context as? Activity)?.finish()
                }
                R.id.mini_bt_refresh -> {
                    dismiss()
                    EventBus.getDefault().post(WebViewAction(WebViewAction.ACTION.REFRESH))
                }
            }
        }


        /*
        root.findViewById<View>(R.id.mini_bt_add_marks).setOnClickListener(lis)
        root.findViewById<View>(R.id.mini_bt_his).setOnClickListener(lis)
        root.findViewById<View>(R.id.mini_bt_download).setOnClickListener(lis)
        root.findViewById<View>(R.id.mini_bt_share).setOnClickListener(lis)
        root.findViewById<View>(R.id.mini_bt_marks).setOnClickListener(lis)
        root.findViewById<View>(R.id.mini_bt_tools).setOnClickListener(lis)
        root.findViewById<View>(R.id.mini_bt_set).setOnClickListener(lis)
        */
        root.findViewById<View>(R.id.mini_bt_pic_model).setOnClickListener(lis)
        root.findViewById<View>(R.id.mini_bt_ua_mode).setOnClickListener(lis)
        root.findViewById<View>(R.id.mini_bt_fullscreen).setOnClickListener(lis)
        root.findViewById<View>(R.id.mini_bt_ua).setOnClickListener(lis)
        root.findViewById<View>(R.id.mini_bt_copy_url).setOnClickListener(lis)

        root.findViewById<View>(R.id.mini_bt_save_page).setOnClickListener(lis)
        root.findViewById<View>(R.id.mini_bt_saved_page).setOnClickListener(lis)
        root.findViewById<View>(R.id.mini_bt_screenshot).setOnClickListener(lis)
        root.findViewById<View>(R.id.mini_bt_translate).setOnClickListener(lis)
        root.findViewById<View>(R.id.mini_bt_send_to_desktop).setOnClickListener(lis)

        root.findViewById<View>(R.id.mini_bt_code).setOnClickListener(lis)
        root.findViewById<View>(R.id.mini_bt_search).setOnClickListener(lis)
        root.findViewById<View>(R.id.mini_bt_resources).setOnClickListener(lis)
        root.findViewById<View>(R.id.mini_bt_logs).setOnClickListener(lis)
        root.findViewById<View>(R.id.mini_bt_other_application).setOnClickListener(lis)

        root.findViewById<View>(R.id.mini_bt_dis).setOnClickListener(lis)
        root.findViewById<View>(R.id.mini_bt_quit).setOnClickListener(lis)
        root.findViewById<View>(R.id.mini_bt_refresh).setOnClickListener(lis)

        root.post({
            ttY = context.resources.displayMetrics.heightPixels.toFloat() - root.height
        })
        root.findViewById<View>(R.id.mini_bt_ua).setOnTouchListener { v, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    tX = event.rawX
                    tY = event.rawY - ttY
                }
                MotionEvent.ACTION_MOVE-> {
                    tX = event.rawX
                    tY = event.rawY - ttY
                }
                MotionEvent.ACTION_UP -> {
                    tX = event.rawX
                    tY = event.rawY - ttY
                }
            }
            false
        }
        window.attributes = lp
    }

    fun initValue(sp:SharedPreferences, root:View) {
        root.findViewById<TextView>(R.id.mini_bt_pic_model_txt).text = if (sp.getBoolean(context.getString(R.string.sp_images), true)) {
            "有图模式"
        } else {
            "无图模式"
        }
        val deskMode = sp.getBoolean(context.getString(R.string.sp_user_agent_desk_mode), false)
        if(deskMode) {
            root.findViewById<TextView>(R.id.mini_bt_ua_mode_txt).text = "电脑模式"
            root.findViewById<ImageView>(R.id.mini_bt_ua_mode_icon).setImageResource(R.mipmap.icon_desktop)
        } else {
            root.findViewById<TextView>(R.id.mini_bt_ua_mode_txt).text = "非电脑模式"
            root.findViewById<ImageView>(R.id.mini_bt_ua_mode_icon).setImageResource(R.mipmap.icon_phone)
        }
        val fullscreen = sp.getBoolean(context.getString(R.string.sp_hidden_status), false)
        if(fullscreen) {
            root.findViewById<TextView>(R.id.mini_bt_fullscreen_txt).text = "全屏"
            root.findViewById<ImageView>(R.id.mini_bt_fullscreen_icon).setImageResource(R.mipmap.icon_fullscreen)
        } else {
            root.findViewById<TextView>(R.id.mini_bt_fullscreen_txt).text = "非全屏"
            root.findViewById<ImageView>(R.id.mini_bt_fullscreen_icon).setImageResource(R.mipmap.icon_no_fullscreen)
        }
        createUAPop(sp, root)
    }

    var uaPopWindow:SimplePopupWindow? = null
    var uaEditAlert:EditAlert? = null
    fun createUAPop(sp:SharedPreferences, root0:View) {
        val cc = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            when(buttonView.id) {
                R.id.ua_edit_desk_r -> CheckPop(popUAModels, R.id.ua_edit_desk_r, isChecked, uaPopWindow!!, R.string.sp_user_agent)
                R.id.ua_edit_default_r -> CheckPop(popUAModels, R.id.ua_edit_default_r, isChecked, uaPopWindow!!, R.string.sp_user_agent)
                R.id.ua_edit_custom_r -> {
                    CheckPop(popUAModels, R.id.ua_edit_custom_r, isChecked, uaPopWindow!!, R.string.sp_user_agent)
                    if (isChecked && uaEditAlert != null) {
                        uaEditAlert!!.show()
                    }
                }
            }
        }
        val lis = View.OnClickListener{ view ->
            when(view.id) {
                R.id.ua_edit_desk -> setChecked(popUAModels, R.id.ua_edit_desk)
                R.id.ua_edit_default -> setChecked(popUAModels, R.id.ua_edit_default)
                R.id.ua_edit_custom -> {
                    if (!setChecked(popUAModels, R.id.ua_edit_custom)) {
                        uaPopWindow!!.dismiss()
                        uaEditAlert!!.show()
                    }
                }
            }
        }
        val root = View.inflate(context, R.layout.popup_ua_edit, null)
        popUAModels.clear()
        popUAModels.add(PopModel("1", "桌面", root.findViewById(R.id.ua_edit_desk_r), root.findViewById(R.id.ua_edit_desk)))
        popUAModels.add(PopModel("2", "自定义", root.findViewById(R.id.ua_edit_custom_r), root.findViewById(R.id.ua_edit_custom)))
        popUAModels.add(PopModel("3", "默认", root.findViewById(R.id.ua_edit_default_r), root.findViewById(R.id.ua_edit_default)))
        for (model: PopModel in popUAModels) {
            model.checkBox.setOnCheckedChangeListener(cc)
            model.view.setOnClickListener(lis)
        }

        uaPopWindow = SimplePopupWindow(root0.findViewById(R.id.mini_bt_ua), root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        uaPopWindow!!.animationStyle = R.style.popwin_anim_style
        val userAgent = sp.getString(context.getString(R.string.sp_user_agent), "3").toInt()
        popUAModels[userAgent - 1].checkBox.isChecked = true

        val build = EditAlert.Build(context, R.style.SimpleAlert)
        build.onPos = View.OnClickListener { view ->
            sp.edit().putString(context.getString(R.string.sp_user_agent_custom), uaEditAlert!!.text).apply()
        }
        build.content = sp.getString(context.getString(R.string.sp_user_agent_custom), WebViewManager.getCurrentActive().wvConfig!!.userAgentOriginal)
        uaEditAlert = build.build()

    }
    private fun CheckPop(list:ArrayList<PopModel>, checkBoxId:Int, isChecked:Boolean, popupView:SimplePopupWindow, storeKeyId:Int) {

        if (isChecked) {
            popupView.dismiss()
            for (model: PopModel in list) {
                if (checkBoxId == model.checkBox.id) {
                    spp!!.edit().putString(context.getString(storeKeyId), model.value).apply()
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
