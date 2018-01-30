package app.chaosstudio.com.glue.activity

import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import app.chaosstudio.com.glue.R
import app.chaosstudio.com.glue.activity.set.FragmentBase
import app.chaosstudio.com.glue.activity.set.SetFragment
import app.chaosstudio.com.glue.eventb.FragmentAction
import app.chaosstudio.com.glue.utils.CustomTheme
import kotlinx.android.synthetic.main.activity_set.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Created by jsen on 2018/1/21.
 */

class Set : AppCompatActivity() {
    var initFragmentName:String = SetFragment::class.java.name
    var fragment: FragmentBase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or  View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (CustomTheme.hiddenStatus) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        setContentView(R.layout.activity_set)
        setSupportActionBar(toolbar)
        acb_title.text = "设置"

        if (intent.hasExtra("fragment")) {
            initFragmentName = intent.getStringExtra("fragment")
        }

        initFragment(savedInstanceState)
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
        overridePendingTransition( R.anim.slide_left_in,R.anim.slide_right_out);
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
