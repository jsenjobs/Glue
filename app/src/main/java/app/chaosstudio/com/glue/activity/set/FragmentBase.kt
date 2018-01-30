package app.chaosstudio.com.glue.activity.set

import android.support.v4.app.Fragment
import app.chaosstudio.com.glue.R

/**
 * Created by jsen on 2018/1/22.
 */

open class FragmentBase : Fragment() {
    var title = ""

    var showMenu = false

    var defaultMenu = R.menu.popup_common_container
}
