package app.chaosstudio.com.glue.download.parse

import android.net.Uri
import android.os.Environment
import java.io.File
import java.util.*

/**
 * Created by jsen on 2018/2/2.
 */

abstract class ParseBase:Parse {

    protected fun checkDirectory(saveDir:String):String {
        val file = File(Environment.getExternalStorageDirectory(), saveDir)
        if (!file.mkdirs()) {
            file.createNewFile()
        }
        return file.absolutePath
    }
    protected fun getFileName(url: String, trueSaveDir:String):String {
        try {
            val uri = Uri.parse(url)
            return uri.path.substring(uri.path.lastIndexOf("/") + 1)
        } catch (e:Exception) {
            e.printStackTrace()
            while (true) {
                val uuid = UUID.randomUUID().toString()
                if (!File(trueSaveDir, uuid).exists()) {
                    return uuid
                }
            }
        }
    }
    protected fun getSkip(file: File):Long {
        if (!file.exists() || !file.isFile) return 0L
        return file.length()
    }
}
