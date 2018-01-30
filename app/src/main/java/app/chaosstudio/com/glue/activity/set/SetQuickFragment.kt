package app.chaosstudio.com.glue.activity.set

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.Toast
import app.chaosstudio.com.glue.GPre
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.ui.SimplePopupWindow
import app.chaosstudio.com.glue.ui.SimpleToast
import kotlinx.android.synthetic.main.fragment_action_bar.*
import kotlinx.android.synthetic.main.set_fragment_quick.*
import java.util.ArrayList

/**
 * Created by jsen on 2018/1/28.
 */

class SetQuickFragment : FragmentBase() {
    init {
        title = "个性化"
    }

    var sp:SharedPreferences? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.set_fragment_quick, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sp = PreferenceManager.getDefaultSharedPreferences(activity)

        createUAPop()

        totalPages.visibility = View.GONE
        val lis = View.OnClickListener{view ->
            when(view.id) {
                R.id.fun_back -> showPopup(view, "1", GPre.actionItem1)
                R.id.fun_forw -> showPopup(view, "2", GPre.actionItem2)
                R.id.fun_home -> showPopup(view, "3", GPre.actionItem3)
                R.id.fun_num -> showPopup(view, "4", GPre.actionItem4)
                R.id.fun_more -> {
                    uaPopWindow?.dismiss()
                    SimpleToast.makeToast(activity, "不能修改该快捷方式", Toast.LENGTH_LONG).show()
                }

                R.id.set_quick_fling -> set_quick_fling_r.isChecked = !set_quick_fling_r.isChecked
                R.id.set_quick_vscroll -> set_quick_vscroll_r.isChecked = !set_quick_vscroll_r.isChecked
            }
        }
        val ll = View.OnLongClickListener{view ->
            when(view.id) {
                R.id.fun_back -> showPopup(view, "1l", GPre.actionItemLong1)
                R.id.fun_forw -> showPopup(view, "2l", GPre.actionItemLong2)
                R.id.fun_home -> showPopup(view, "3l", GPre.actionItemLong3)
                R.id.fun_num -> showPopup(view, "4l", GPre.actionItemLong4)
                R.id.fun_more -> showPopup(view, "5l", GPre.actionItemLong5)
            }
            true
        }
        val cc = CompoundButton.OnCheckedChangeListener{buttonView, isChecked ->
            when(buttonView.id) {
                R.id.set_quick_fling_r -> {
                    sp!!.edit().putBoolean(activity.getString(R.string.sp_fling_page), isChecked).apply()
                    GPre.flingPage = isChecked
                }
                R.id.set_quick_vscroll_r -> {
                    sp!!.edit().putBoolean(activity.getString(R.string.sp_volume_scroll), isChecked).apply()
                    GPre.volumeScroll = isChecked
                }

            }
        }
        fun_back.setOnClickListener(lis)
        fun_forw.setOnClickListener(lis)
        fun_home.setOnClickListener(lis)
        fun_num.setOnClickListener(lis)
        fun_more.setOnClickListener(lis)

        set_quick_fling.setOnClickListener(lis)
        set_quick_vscroll.setOnClickListener(lis)
        set_quick_fling_r.setOnCheckedChangeListener(cc)
        set_quick_vscroll_r.setOnCheckedChangeListener(cc)

        fun_back.setOnLongClickListener(ll)
        fun_forw.setOnLongClickListener(ll)
        fun_home.setOnLongClickListener(ll)
        fun_num.setOnLongClickListener(ll)
        fun_more.setOnLongClickListener(ll)

        set_quick_fling_r.isChecked = GPre.flingPage
        set_quick_vscroll_r.isChecked = GPre.volumeScroll

    }

    var uaPopWindow: SimplePopupWindow? = null
    var popUAModels: ArrayList<PopModel> = ArrayList()
    fun createUAPop() {
        val lis = View.OnClickListener{view ->
            changeCheck(popUAModels, view.id)
        }
        val prefix = getString(R.string.sp_qquick)
        val cc = CompoundButton.OnCheckedChangeListener{ buttonView, isChecked ->
            if (isChecked) {
                if (!TextUtils.isEmpty(currentKey)) {
                    val v = getValueByCheckBoxID(popUAModels, buttonView.id)
                    if (v != null) {
                        uaPopWindow?.dismiss()
                        resetOther(popUAModels, buttonView.id)
                        sp!!.edit().putString(prefix + currentKey, v).apply()
                        GPre.init(activity)
                        SimpleToast.makeToast(activity, "设置成功", Toast.LENGTH_LONG).show()
                    } else {
                        buttonView.isChecked = false
                    }
                }
            }
        }
        val root = View.inflate(activity, R.layout.popup_ac_quick_set, null)
        popUAModels.clear()
        popUAModels.add(PopModel("1", root.findViewById(R.id.acq_1_r), root.findViewById(R.id.acq_1)))
        popUAModels.add(PopModel("2", root.findViewById(R.id.acq_2_r), root.findViewById(R.id.acq_2)))
        popUAModels.add(PopModel("3", root.findViewById(R.id.acq_3_r), root.findViewById(R.id.acq_3)))
        popUAModels.add(PopModel("4", root.findViewById(R.id.acq_4_r), root.findViewById(R.id.acq_4)))
        popUAModels.add(PopModel("5", root.findViewById(R.id.acq_5_r), root.findViewById(R.id.acq_5)))
        popUAModels.add(PopModel("6", root.findViewById(R.id.acq_6_r), root.findViewById(R.id.acq_6)))
        popUAModels.add(PopModel("7", root.findViewById(R.id.acq_7_r), root.findViewById(R.id.acq_7)))
        popUAModels.add(PopModel("8", root.findViewById(R.id.acq_8_r), root.findViewById(R.id.acq_8)))
        popUAModels.add(PopModel("9", root.findViewById(R.id.acq_9_r), root.findViewById(R.id.acq_9)))
        popUAModels.add(PopModel("10", root.findViewById(R.id.acq_10_r), root.findViewById(R.id.acq_10)))
        popUAModels.add(PopModel("11", root.findViewById(R.id.acq_11_r), root.findViewById(R.id.acq_11)))
        popUAModels.add(PopModel("12", root.findViewById(R.id.acq_12_r), root.findViewById(R.id.acq_12)))
        popUAModels.add(PopModel("13", root.findViewById(R.id.acq_13_r), root.findViewById(R.id.acq_13)))
        popUAModels.add(PopModel("14", root.findViewById(R.id.acq_14_r), root.findViewById(R.id.acq_14)))
        popUAModels.add(PopModel("15", root.findViewById(R.id.acq_15_r), root.findViewById(R.id.acq_15)))
        popUAModels.add(PopModel("16", root.findViewById(R.id.acq_16_r), root.findViewById(R.id.acq_16)))
        popUAModels.add(PopModel("17", root.findViewById(R.id.acq_17_r), root.findViewById(R.id.acq_17)))
        popUAModels.add(PopModel("18", root.findViewById(R.id.acq_18_r), root.findViewById(R.id.acq_18)))
        for (model: PopModel in popUAModels) {
            model.checkBox.setOnCheckedChangeListener(cc)
            model.view.setOnClickListener(lis)
        }

        uaPopWindow = SimplePopupWindow(view, root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        uaPopWindow!!.animationStyle = R.style.popwin_anim_style
        val userAgent = sp!!.getString(context.getString(R.string.sp_user_agent), "3").toInt()
        popUAModels[userAgent - 1].checkBox.isChecked = true
    }

    private var currentKey = ""
    private fun showPopup(view:View, key:String, value:String) {
        currentKey = ""
        for (model in popUAModels) {
            model.checkBox.isChecked = false
        }

        changeCheckByValue(popUAModels, value)
        currentKey = key

        uaPopWindow!!.updateAnchor(view)
        uaPopWindow!!.showAtLocation(Gravity.TOP or Gravity.START)

    }
    private fun changeCheckByValue(list:ArrayList<PopModel>, value: String) {
        list
                .filter { it.value== value }
                .forEach { it.checkBox.isChecked = !it.checkBox.isChecked }
    }
    private fun getValueByCheckBoxID(list:ArrayList<PopModel>, id:Int):String? {
        for (mode in list) {
            if (mode.checkBox.id == id) {
                return mode.value
            }
        }
        return null
    }
    private fun changeCheck(list:ArrayList<PopModel>, textViewId:Int) {
        list
                .filter { it.view.id == textViewId }
                .forEach { it.checkBox.isChecked = !it.checkBox.isChecked }
    }
    private fun resetOther(list:ArrayList<PopModel>, checkBoxID:Int) {
        list
                .filter { it.checkBox.id != checkBoxID }
                .forEach { it.checkBox.isChecked = false }
    }
    class PopModel(val value:String, val checkBox: CheckBox, val view: View)

}
