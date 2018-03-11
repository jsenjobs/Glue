package app.chaosstudio.com.glue.download.parse

import app.chaosstudio.com.glue.utils.OKManager
import okhttp3.Request
import java.io.BufferedReader
import java.io.StringReader

/**
 * Created by jsen on 2018/2/1.
 */

class ParseUtils {
    companion object {
        fun getString(url:String):String? {
            val request = Request.Builder().url(url).build()
            val call = OKManager.okHttpClient.newCall(request)
            return try {
                val response = call.execute()
                if (response.isSuccessful) {
                    response.body()?.string()
                } else {
                    null
                }
            } catch (e:Exception) {
                e.printStackTrace()
                null
            }

        }

        fun getBufferedReader(url: String):BufferedReader? {
            val data = getString(url)
            if (data != null) {
                return BufferedReader(StringReader(data))
            }
            return null
        }
    }
}
