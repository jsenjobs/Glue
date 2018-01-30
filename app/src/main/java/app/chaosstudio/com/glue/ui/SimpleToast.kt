package app.chaosstudio.com.glue.ui

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast

import app.chaosstudio.com.glue.R

/**
 * Created by jsen on 2018/1/22.
 */

class SimpleToast {
    companion object {

        fun makeToast(context: Context, stringResId:Int):Toast {
            return makeToast(context, context.getString(stringResId), Toast.LENGTH_LONG)
        }

        fun makeToast(context: Context, message: String, duration: Int):Toast {
            val root = View.inflate(context, R.layout.toast_simple, null)
            root.findViewById<TextView>(R.id.toast_message).text = message
            val toast = Toast(context)
            val wm:WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val h = wm.defaultDisplay.height
            toast.setGravity(Gravity.TOP, 0, h * 2 / 3)
            toast.duration = duration
            toast.view = root
            return toast
        }

    }
}
