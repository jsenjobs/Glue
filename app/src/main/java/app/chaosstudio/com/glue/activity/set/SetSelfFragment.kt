package app.chaosstudio.com.glue.activity.set

import android.app.Activity
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
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.ui.SimplePopupWindow
import app.chaosstudio.com.glue.webconfig.NWebView
import kotlinx.android.synthetic.main.set_fragment_self.*
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import app.chaosstudio.com.glue.eventb.ThemeAction
import app.chaosstudio.com.glue.ui.SimpleToast
import app.chaosstudio.com.glue.unit.BrowserUnit
import app.chaosstudio.com.glue.utils.CustomTheme
import app.chaosstudio.com.glue.utils.ImageRGB
import app.chaosstudio.com.glue.webconfig.WebViewManager


/**
 * Created by jsen on 2018/1/25.
 */
class SetSelfFragment : FragmentBase() {
    init {
        title = "个性化"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.set_fragment_self, container, false)
    }

    var webView:NWebView? = null
    var sp: SharedPreferences? = null
    val RESULT_LOAD_IMAGE = 1
    val RESULT_LOAD_LOGO = 2
    var selectedImageUrl:String = ""
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sp = PreferenceManager.getDefaultSharedPreferences(context)

        webView = NWebView(activity)
        sWebViewContainer.addView(webView)

        webView?.loadUrl(BrowserUnit.getHome(activity))

        val lis = View.OnClickListener{view ->
            when(view.id) {
                R.id.set_self_back -> {
                    backPopWindow?.updateAnchor(view)
                    backPopWindow?.showAtLocation(Gravity.TOP or Gravity.START)
                }
                R.id.set_self_logo -> {
                    logoPopWindow?.updateAnchor(view)
                    logoPopWindow?.showAtLocation(Gravity.TOP or Gravity.START)
                }
                R.id.set_self_set -> SimpleToast.makeToast(activity, "暂未实现", Toast.LENGTH_LONG).show()
                R.id.self_back_edit_no -> {
                    backPopWindow?.dismiss()
                    backPopWindow?.contentView?.findViewById<CheckBox>(R.id.self_back_edit_no_r)!!.isChecked = true
                    backPopWindow?.contentView?.findViewById<CheckBox>(R.id.self_back_edit_pic_r)!!.isChecked = false
                }
                R.id.self_back_edit_pic -> {
                    backPopWindow?.dismiss()
                    val i = Intent(
                            Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(i, RESULT_LOAD_IMAGE)
                }
                R.id.self_logo_edit_no -> {
                    logoPopWindow?.dismiss()
                    logoPopWindow?.contentView?.findViewById<CheckBox>(R.id.self_logo_edit_no_r)!!.isChecked = true
                    logoPopWindow?.contentView?.findViewById<CheckBox>(R.id.self_logo_edit_local_r)!!.isChecked = false
                    logoPopWindow?.contentView?.findViewById<CheckBox>(R.id.self_logo_edit_default_r)!!.isChecked = false
                }
                R.id.self_logo_edit_default -> {
                    logoPopWindow?.dismiss()
                    logoPopWindow?.contentView?.findViewById<CheckBox>(R.id.self_logo_edit_no_r)!!.isChecked = false
                    logoPopWindow?.contentView?.findViewById<CheckBox>(R.id.self_logo_edit_local_r)!!.isChecked = false
                    logoPopWindow?.contentView?.findViewById<CheckBox>(R.id.self_logo_edit_default_r)!!.isChecked = true
                }
                R.id.self_logo_edit_local -> {
                    logoPopWindow?.dismiss()
                    val i = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(i, RESULT_LOAD_LOGO)
                }
            }
        }
        val cc = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            when(buttonView.id) {
                R.id.self_back_edit_no_r -> {
                    if (isChecked) {
                        sp!!.edit().putString(activity.getString(R.string.sp_home_custom_background), "").apply()
                        webView?.reload()
                        WebViewManager.getCurrentActive().reload()
                    }

                }
                R.id.self_back_edit_pic_r -> {
                    if (isChecked) {
                        sp!!.edit().putString(activity.getString(R.string.sp_home_custom_background), selectedImageUrl).apply()
                        webView?.reload()
                        WebViewManager.getCurrentActive().reload()
                    }
                }
                R.id.self_logo_edit_no_r -> {
                    if (isChecked) {
                        sp!!.edit().putBoolean(activity.getString(R.string.sp_home_logo_show), false).apply()
                        webView?.reload()
                        WebViewManager.getCurrentActive().reload()
                    }

                }
                R.id.self_logo_edit_default_r -> {
                    if (isChecked) {
                        sp!!.edit().putBoolean(activity.getString(R.string.sp_home_logo_show), true).apply()
                        sp!!.edit().putString(activity.getString(R.string.sp_home_logo_path), "").apply()
                        webView?.reload()
                        WebViewManager.getCurrentActive().reload()
                    }
                }
                R.id.self_logo_edit_local_r -> {
                    if (isChecked) {
                        sp!!.edit().putBoolean(activity.getString(R.string.sp_home_logo_show), true).apply()
                        sp!!.edit().putString(activity.getString(R.string.sp_home_logo_path), selectedImageUrl).apply()
                        webView?.reload()
                        WebViewManager.getCurrentActive().reload()
                    }
                }
            }

        }
        createBackPop(lis, cc)
        createLogoPop(lis, cc)
        set_self_back.setOnClickListener(lis)
        set_self_logo.setOnClickListener(lis)
        set_self_set.setOnClickListener(lis)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && null != data) {
            if (requestCode == RESULT_LOAD_IMAGE) {
                val selectedImage = data.data
                val filePathColumn: Array<String> = arrayOf(MediaStore.Images.Media.DATA)

                val cursor = activity.contentResolver.query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                val path = cursor.getString(columnIndex)
                // CustomTheme.colorPrimary = ImageRGB.colorAva(path)
                selectedImageUrl = "file://" + path
                cursor.close()
                backPopWindow?.contentView?.findViewById<CheckBox>(R.id.self_back_edit_pic_r)!!.isChecked = false
                backPopWindow?.contentView?.findViewById<CheckBox>(R.id.self_back_edit_pic_r)!!.isChecked = true
                backPopWindow?.contentView?.findViewById<CheckBox>(R.id.self_back_edit_no_r)!!.isChecked = false
            } else if (requestCode == RESULT_LOAD_LOGO) {
                val selectedImage = data.data
                val filePathColumn: Array<String> = arrayOf(MediaStore.Images.Media.DATA)

                val cursor = activity.contentResolver.query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                selectedImageUrl = "file://" + cursor.getString(columnIndex)
                cursor.close()
                logoPopWindow?.contentView?.findViewById<CheckBox>(R.id.self_logo_edit_no_r)!!.isChecked = false
                logoPopWindow?.contentView?.findViewById<CheckBox>(R.id.self_logo_edit_local_r)!!.isChecked = false
                logoPopWindow?.contentView?.findViewById<CheckBox>(R.id.self_logo_edit_local_r)!!.isChecked = true
                logoPopWindow?.contentView?.findViewById<CheckBox>(R.id.self_logo_edit_default_r)!!.isChecked = false
            }
        }
    }

    var backPopWindow: SimplePopupWindow? = null
    fun createBackPop(lis:View.OnClickListener, cc: CompoundButton.OnCheckedChangeListener) {
        val root = View.inflate(activity, R.layout.popup_self_back_edit, null)

        val back = sp!!.getString(context.getString(R.string.sp_home_custom_background), "")
        if (!TextUtils.isEmpty(back)) {
            root.findViewById<CheckBox>(R.id.self_back_edit_pic_r).isChecked = true
        } else {
            root.findViewById<CheckBox>(R.id.self_back_edit_no_r).isChecked = true
        }
        root.findViewById<View>(R.id.self_back_edit_no).setOnClickListener(lis)
        root.findViewById<CheckBox>(R.id.self_back_edit_no_r).setOnCheckedChangeListener(cc)
        root.findViewById<View>(R.id.self_back_edit_pic).setOnClickListener(lis)
        root.findViewById<CheckBox>(R.id.self_back_edit_pic_r).setOnCheckedChangeListener(cc)

        backPopWindow = SimplePopupWindow(view, root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        backPopWindow!!.animationStyle = R.style.popwin_anim_style
    }

    var logoPopWindow: SimplePopupWindow? = null
    fun createLogoPop(lis:View.OnClickListener, cc: CompoundButton.OnCheckedChangeListener) {
        val root = View.inflate(activity, R.layout.popup_self_logo_edit, null)

        val logoShow = sp!!.getBoolean(context.getString(R.string.sp_home_logo_show), true)
        if (logoShow) {
            val logoUrl = sp!!.getString(context.getString(R.string.sp_home_logo_path), "")
            if (!TextUtils.isEmpty(logoUrl)) {
                root.findViewById<CheckBox>(R.id.self_logo_edit_local_r).isChecked = true
            } else {
                root.findViewById<CheckBox>(R.id.self_logo_edit_default_r).isChecked = true
            }
        } else {
            root.findViewById<CheckBox>(R.id.self_logo_edit_no_r).isChecked = true
        }
        root.findViewById<View>(R.id.self_logo_edit_no).setOnClickListener(lis)
        root.findViewById<CheckBox>(R.id.self_logo_edit_no_r).setOnCheckedChangeListener(cc)
        root.findViewById<View>(R.id.self_logo_edit_local).setOnClickListener(lis)
        root.findViewById<CheckBox>(R.id.self_logo_edit_local_r).setOnCheckedChangeListener(cc)
        root.findViewById<View>(R.id.self_logo_edit_default).setOnClickListener(lis)
        root.findViewById<CheckBox>(R.id.self_logo_edit_default_r).setOnCheckedChangeListener(cc)

        logoPopWindow = SimplePopupWindow(view, root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        logoPopWindow!!.animationStyle = R.style.popwin_anim_style
    }
}