package app.chaosstudio.com.glue.activity.set

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.ui.SimpleToast
import app.chaosstudio.com.glue.utils.PermissionHelp
import app.chaosstudio.com.glue.utils.PermissionHelp.REQUEST_CODE_ASK_PERMISSIONS_1
import app.chaosstudio.com.glue.webconfig.WebViewManager
import kotlinx.android.synthetic.main.set_fragment_set_private.*

/**
 * Created by jsen on 2018/1/22.
 */

class SetPrivateFragment : FragmentBase() {

    init {
        title = "隐私"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.set_fragment_set_private, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val hasAccess_FINE_LOCATION = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            if (hasAccess_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
                sp.edit().putBoolean(context.getString(R.string.sp_location), false).apply()
            }
        }

        set_private_loc_r.isChecked = sp.getBoolean(context.getString(R.string.sp_location), false)

        val lis = View.OnClickListener{ view ->
            when(view.id) {
                R.id.set_private_loc -> set_private_loc_r.isChecked = !set_private_loc_r.isChecked
            }
        }
        val cc = CompoundButton.OnCheckedChangeListener{buttonView, isChecked ->
            sp.edit().putBoolean(context.getString(R.string.sp_location), isChecked).apply()
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val hasAccess_FINE_LOCATION = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    if (hasAccess_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
                        PermissionHelp.grantPermissionsLocForce(activity, this@SetPrivateFragment, PermissionHelp.OnResult { exec ->
                            if (!exec) {
                                set_private_loc_r.isChecked = false
                            }
                        })
                    }
                }
            }
            WebViewManager.reloadPreferences()
        }
        set_private_loc.setOnClickListener(lis)
        set_private_loc_r.setOnCheckedChangeListener(cc)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS_1) {
            if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                set_private_loc_r.isChecked = false
            }
        }
    }

}