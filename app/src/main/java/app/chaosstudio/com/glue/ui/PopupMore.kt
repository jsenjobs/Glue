package app.chaosstudio.com.glue.ui

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.preference.PreferenceManager
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat.startActivity
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import app.chaosstudio.com.glue.App

import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.activity.ListBookMarksFragment
import app.chaosstudio.com.glue.activity.ListHistoryFragment
import app.chaosstudio.com.glue.activity.Set
import app.chaosstudio.com.glue.activity.SimpleContainer
import app.chaosstudio.com.glue.activity.set.SetCommonFragment
import app.chaosstudio.com.glue.eventb.ActivityAction
import app.chaosstudio.com.glue.eventb.FragmentAction
import app.chaosstudio.com.glue.eventb.WebViewAction
import app.chaosstudio.com.glue.greendb.gen.BookMarkDao
import app.chaosstudio.com.glue.greendb.model.BookMark
import app.chaosstudio.com.glue.webconfig.WebViewManager
import org.greenrobot.eventbus.EventBus
import app.chaosstudio.com.glue.R.drawable.file
import java.io.File


/**
 * Created by jsen on 2018/1/21.
 */

class PopupMore(context: Context, themeResId: Int) : Dialog(context, themeResId) {

    init {

        val root = LayoutInflater.from(context).inflate(R.layout.popup_more, null) as LinearLayout
        setContentView(root)

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
            dismiss()
            when(view.id) {
                R.id.more_bt_add_marks -> {
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
                            FragmentAction.fire(FragmentAction.ACTION.URL_MARKED, 1)
                        }
                    } else {
                        SimpleToast.makeToast(context, "添加书签失败", Toast.LENGTH_LONG).show()
                    }
                }
                R.id.more_bt_his -> {
                    val intent = Intent(context, SimpleContainer::class.java)
                    intent.putExtra("fragment", ListHistoryFragment::class.java.name)
                    context.startActivity(intent)
                }
                R.id.more_bt_download -> {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.setDataAndType(Uri.parse(Environment.getExternalStorageDirectory().toString() + File.separator
                            + PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.sp_file_download_dir), Environment.DIRECTORY_DOWNLOADS)), "*/*")
                    context.startActivity(intent)
                }
                R.id.more_bt_share -> {
                    val cW = WebViewManager.getCurrentActive()
                    if (cW != null) {
                        val textIntent = Intent(Intent.ACTION_SEND);
                        textIntent.type = "text/plain";
                        textIntent.putExtra(Intent.EXTRA_TEXT, cW.url)
                        context.startActivity(Intent.createChooser(textIntent, "Glue"))
                        (context as Activity?)?.overridePendingTransition(0, 0)
                    } else {
                        SimpleToast.makeToast(context, "分享失败", Toast.LENGTH_LONG).show()
                    }
                }
                R.id.more_bt_day -> {
                    val res = if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.sp_night), false)) {
                        root.findViewById<TextView>(R.id.more_bt_day_txt).text = "白天"
                        WebViewManager.setIsNight(false)
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(context.getString(R.string.sp_night), false).apply()
                        R.mipmap.more_day
                    } else {
                        root.findViewById<TextView>(R.id.more_bt_day_txt).text = "夜晚"
                        WebViewManager.setIsNight(true)
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(context.getString(R.string.sp_night), true).apply()
                        R.mipmap.more_night
                    }
                    root.findViewById<ImageView>(R.id.more_bt_day_icon).setImageResource(res)
                }
                R.id.more_bt_marks -> {
                    val intent = Intent(context, SimpleContainer::class.java)
                    intent.putExtra("fragment", ListBookMarksFragment::class.java.name)
                    context.startActivity(intent)
                }
                R.id.more_bt_tools -> FragmentAction.fire(FragmentAction.ACTION.SHOW_TOOLS)
                R.id.more_bt_set -> ActivityAction.fire(ActivityAction.ACTION.Set)
                R.id.more_bt_quit -> (context as? Activity)?.finish()
                R.id.more_bt_refresh -> EventBus.getDefault().post(WebViewAction(WebViewAction.ACTION.REFRESH))
            }
        }


        root.findViewById<View>(R.id.more_bt_add_marks).setOnClickListener(lis)
        root.findViewById<View>(R.id.more_bt_his).setOnClickListener(lis)
        root.findViewById<View>(R.id.more_bt_download).setOnClickListener(lis)
        root.findViewById<View>(R.id.more_bt_share).setOnClickListener(lis)
        root.findViewById<View>(R.id.more_bt_day).setOnClickListener(lis)
        root.findViewById<View>(R.id.more_bt_marks).setOnClickListener(lis)
        root.findViewById<View>(R.id.more_bt_tools).setOnClickListener(lis)
        root.findViewById<View>(R.id.more_bt_set).setOnClickListener(lis)

        root.findViewById<View>(R.id.more_bt_dis).setOnClickListener(lis)
        root.findViewById<View>(R.id.more_bt_quit).setOnClickListener(lis)
        root.findViewById<View>(R.id.more_bt_refresh).setOnClickListener(lis)

        val res = if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.sp_night), false)) {
            root.findViewById<TextView>(R.id.more_bt_day_txt).text = "夜晚"
            R.mipmap.more_night
        } else {
            root.findViewById<TextView>(R.id.more_bt_day_txt).text = "白天"
            R.mipmap.more_day
        }
        root.findViewById<ImageView>(R.id.more_bt_day_icon).setImageResource(res)
        window.attributes = lp
    }


}
