package app.chaosstudio.com.glue.activity.set

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.TextView

import java.util.ArrayList
import java.util.Arrays
import java.util.Timer
import java.util.TimerTask

import app.chaosstudio.com.glue.MainActivity
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.greendb.model.Record
import app.chaosstudio.com.glue.unit.BrowserUnit
import app.chaosstudio.com.glue.unit.IntentUnit
import app.chaosstudio.com.glue.unit.RecordUnit

/**
 * Created by jsen on 2018/1/23.
 */

class DefActivity : AppCompatActivity() {

    private var first: Record? = null
    private var second: Record? = null
    private var timer: Timer? = null
    private var background = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (intent == null || intent.data == null) {
            finish()
            overridePendingTransition(0, 0)
            return
        }

        first = Record()
        first!!.title = "页面"
        first!!.url = intent.data!!.toString()
        first!!.time = System.currentTimeMillis()
        val task = object : TimerTask() {
            override fun run() {
                if (first != null && second == null) {
                    val toService = Intent(this@DefActivity, DefService::class.java)
                    RecordUnit.setHolder(first)
                    startService(toService)
                    background = true
                }
                this@DefActivity.finish()
                overridePendingTransition(0, 0)
            }
        }
        timer = Timer()
        timer!!.schedule(task, TIMER_SCHEDULE_DEFAULT.toLong())
    }

    public override fun onNewIntent(intent: Intent?) {
        if (intent == null || intent.data == null || first == null) {
            finish()
            overridePendingTransition(0, 0)
            return
        }

        if (timer != null) {
            timer!!.cancel()
        }

        second = Record()
        second!!.title = "页面"
        second!!.url = intent.data!!.toString()
        second!!.time = System.currentTimeMillis()

        if (first!!.url == second!!.url) {
            showHolderDialog()
        } else {
            val toService = Intent(this@DefActivity, DefService::class.java)
            RecordUnit.setHolder(second)
            startService(toService)
            background = true
            finish()
            overridePendingTransition(0, 0)
        }
    }

    private fun showHolderDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)

        @SuppressLint("InflateParams")
        val linearLayout = layoutInflater.inflate(R.layout.def_dialog_list, null, false) as FrameLayout
        builder.setView(linearLayout)

        val strings = resources.getStringArray(R.array.holder_menu)
        val list = ArrayList<String>()
        list.addAll(Arrays.asList(*strings))

        val listView = linearLayout.findViewById<ListView>(R.id.dialog_list)
        val adapter = DialogAdapter(this, list)
        listView.adapter = adapter
        adapter.notifyDataSetChanged()

        val dialog = builder.create()
        dialog.setOnCancelListener { this@DefActivity.finish() }
        dialog.show()

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> {
                    val toActivity = Intent(this@DefActivity, MainActivity::class.java)
                    toActivity.putExtra("URL", first!!.url)
                    toActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(toActivity)
                    overridePendingTransition(0, 0)
                }
                1 -> BrowserUnit.copyURL(this@DefActivity, first!!.url)
                2 -> IntentUnit.share(this@DefActivity, first!!.title, first!!.url)
                else -> {
                }
            }
            dialog.hide()
            dialog.dismiss()
            finish()
            overridePendingTransition(0, 0)
        }
    }

    private class DialogAdapter internal constructor(private val context0: Context, private val list: List<String>) : ArrayAdapter<String>(context0, R.layout.def_dialog_list_item, list) {
        private val layoutResId: Int

        init {
            this.layoutResId = R.layout.def_dialog_list_item
        }

        private class Holder {
            internal var textView: TextView? = null
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val holder: Holder
            var view = convertView

            if (view == null) {
                view = LayoutInflater.from(context).inflate(layoutResId, parent, false)
                holder = Holder()
                holder.textView = view!!.findViewById(R.id.dialog_text_item)
                view.tag = holder
            } else {
                holder = view.tag as Holder
            }

            holder.textView!!.text = list[position]

            return view
        }
    }

    companion object {
        private val TIMER_SCHEDULE_DEFAULT = 512
    }
}
