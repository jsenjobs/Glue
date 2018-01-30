package app.chaosstudio.com.glue.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.activity.set.FragmentBase
import app.chaosstudio.com.glue.activity.set.SetFragment
import app.chaosstudio.com.glue.eventb.ActionBarAction
import app.chaosstudio.com.glue.eventb.FragmentAction
import app.chaosstudio.com.glue.ui.SimpleAlert
import app.chaosstudio.com.glue.utils.CustomTheme
import app.chaosstudio.com.glue.webconfig.WebViewManager
import kotlinx.android.synthetic.main.activity_simple.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.EventBusException
import org.greenrobot.eventbus.Subscribe
import android.widget.Toast
import app.chaosstudio.com.glue.utils.PermissionHelp.REQUEST_CODE_ASK_PERMISSIONS_1


/**
 * Created by jsen on 2018/1/21.
 */

class SimpleContainer : AppCompatActivity() {
    var initFragmentName:String = SetFragment::class.java.name
    var fragment: FragmentBase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (CustomTheme.hiddenStatus) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        setContentView(R.layout.activity_simple)
        setSupportActionBar(toolbar)
        acb_title.text = "设置"

        if (intent.hasExtra("fragment")) {
            initFragmentName = intent.getStringExtra("fragment")
        }

        initFragment(savedInstanceState)

        try {
            EventBus.getDefault().register(fragment)
        } catch (e : EventBusException) {
        }

    }

    fun initFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            fragment = Fragment.instantiate(this, initFragmentName) as FragmentBase
        } else {
            fragment = supportFragmentManager.getFragment(savedInstanceState, initFragmentName) as FragmentBase
        }
        acb_title.text = fragment!!.title
        supportFragmentManager.beginTransaction().replace(R.id.set_fragment_container, fragment).commit()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(fragment)
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        supportFragmentManager.putFragment(outState, fragment!!.javaClass.name, fragment)
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        supportFragmentManager.putFragment(outState, fragment!!.javaClass.name, fragment)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (fragment?.showMenu!!)
            menuInflater.inflate(fragment?.defaultMenu ?: R.menu.popup_common_container,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        FragmentAction.Companion.fire(FragmentAction.ACTION.MENU_ITEM, item.itemId)
        return super.onOptionsItemSelected(item)
    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onFragmentAction(action: FragmentAction) {
        when(action.action) {
            FragmentAction.ACTION.FULL_STATUS_CHANGE -> {
                if (CustomTheme.hiddenStatus) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                }
            }
        }
    }


}