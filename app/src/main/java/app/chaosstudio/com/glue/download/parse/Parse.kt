package app.chaosstudio.com.glue.download.parse

/**
 * Created by jsen on 2018/2/1.
 */

interface Parse {
    fun parse(id:Long, url: String)

    fun stop(url:String)

    fun isTaskExist(url: String):Boolean
}
