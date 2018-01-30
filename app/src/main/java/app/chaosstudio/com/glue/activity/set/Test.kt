package app.chaosstudio.com.glue.activity.set

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * Created by jsen on 2018/1/22.
 */

class Test {

    fun Te() {
        val t = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {

            }
        }
    }
}
