package app.chaosstudio.com.glue

import android.net.Uri
import okhttp3.*
import org.junit.Test

import org.junit.Assert.*
import java.io.IOException
import java.net.URI

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun getUrlAA() {
        val api = "https://v.pinpaibao.com.cn/cert/site/?site="
        val domain = "www.baidu.com"
        val requestUrl = api + domain

        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url(requestUrl).build()

        val call = okHttpClient.newCall(request)
        try {
            val res = call.execute()
            System.out.println(res.body()?.string())
        } catch (e:IOException) {
            e.printStackTrace()
        }
    }

    @Test
    fun getUrlAAAsync() {
        val api = "https://v.pinpaibao.com.cn/cert/site/?site="
        val domain = "www.baidu.com"
        val requestUrl = api + domain

        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url(requestUrl).build()

        val call = okHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {
                if (response!!.body()!!.string()!!.contains("安全联盟企业信誉评级证书")) {
                    System.out.println("可信")
                } else {
                    System.out.println("不可信")
                }
            }

            override fun onFailure(call: Call?, e: IOException?) {
            }


        })

        Thread.sleep(5000)
    }

    @Test
    fun jsTest() {
        val uri = URI("jjs://docker.com/sick/se?name=jsen&date=1#fasd")
        System.out.println(uri.scheme)
        System.out.println(uri.host)
        System.out.println(uri.query)

        System.out.println(uri.authority)
        System.out.println(uri.path)

        System.out.println(uri.fragment)
    }
}
