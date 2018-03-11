package app.chaosstudio.com.glue.webconfig

import android.net.Uri
import app.chaosstudio.com.glue.App
import app.chaosstudio.com.glue.greendb.gen.LogModeDao
import app.chaosstudio.com.glue.greendb.model.LogMode
import java.util.ArrayList

/**
 * Created by jsen on 2018/1/28.
 */

class Logs {

    val dao:LogModeDao = App.instances.daoSession.logModeDao

    fun addLog(uuid:String, url: String, loaded: Boolean) {
        val l = LogMode()
        l.timestamp = System.currentTimeMillis()
        l.url = url
        l.loaded = loaded
        l.uuid = uuid
        dao.save(l)
    }

    fun clear(uuid:String) {
        dao.queryBuilder().where(LogModeDao.Properties.Uuid.eq(uuid)).buildDelete().executeDeleteWithoutDetachingEntities()
    }

    fun getLogs(uuid:String): List<LogMode> {
        return dao.queryBuilder().where(LogModeDao.Properties.Uuid.eq(uuid)).list()
    }

    fun findResources(uuid:String):ArrayList<LogMode> {
        val result = ArrayList<LogMode>()
        for (log in dao.queryBuilder().where(LogModeDao.Properties.Uuid.eq(uuid)).list()) {
            try {
                val uri = Uri.parse(log.url)
                if (filterVideoAudioPicture(uri)) {
                    result.add(log)
                }
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    fun filterVideoAudioPicture(uri: Uri):Boolean {
        val path = uri.path.toLowerCase()
        return path.endsWith(".mp4") ||
                path.endsWith(".mp3") ||
                path.endsWith(".wav") ||
                path.endsWith(".midi") ||
                path.endsWith(".cda") ||
                path.endsWith(".wma") ||
                path.endsWith(".flac") ||
                path.endsWith(".avi") ||
                path.endsWith(".rmvb") ||
                path.endsWith(".rm") ||
                path.endsWith(".asf") ||
                path.endsWith(".divx") ||
                path.endsWith(".mpg") ||
                path.endsWith(".mpeg") ||
                path.endsWith(".wmv") ||
                path.endsWith(".mkv") ||
                path.endsWith(".vob") ||
                path.endsWith(".m3u8")
    }
}
