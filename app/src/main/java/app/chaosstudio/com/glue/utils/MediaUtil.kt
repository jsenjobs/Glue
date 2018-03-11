package app.chaosstudio.com.glue.utils

import android.media.MediaMetadataRetriever
import android.text.TextUtils

/**
 * Created by jsen on 2018/2/3.
 */

class MediaUtil {
    companion object {
        fun getType(p:String):String? {
            val path = p.toLowerCase()
            return if (path.endsWith(".mp3") ||
                    path.endsWith(".wav") ||
                    path.endsWith(".midi") ||
                    path.endsWith(".cda") ||
                    path.endsWith(".flac")
            ) {
                "audio/*"
            } else if (path.endsWith(".mp4") ||
                    path.endsWith(".avi") ||
                    path.endsWith(".rmvb") ||
                    path.endsWith(".rm") ||
                    path.endsWith(".asf") ||
                    path.endsWith(".divx") ||
                    path.endsWith(".ts") ||
                    path.endsWith(".wmv") ||
                    path.endsWith(".mkv") ||
                    path.endsWith(".m3u8") ||
                    path.endsWith(".vob") ||
                    path.endsWith(".mpg")
            ) {
                "video/*"
            }  else if (path.endsWith(".mpg") ||
                    path.endsWith(".mpeg") ||
                    path.endsWith(".rmvb") ||
                    path.endsWith(".rm") ||
                    path.endsWith(".asf") ||
                    path.endsWith(".divx") ||
                    path.endsWith(".mpg")
            ) {
                "image/*"
            } else {
                null
            }
        }

        fun getMimeType(filePath: String?): String? {
            val mmr = MediaMetadataRetriever()
            var mime:String? = null
            if (!TextUtils.isEmpty(filePath)) {
                try {
                    mmr.setDataSource(filePath)
                    mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
                } catch (e: IllegalStateException) {
                    return mime
                } catch (e: IllegalArgumentException) {
                    return mime
                } catch (e: RuntimeException) {
                    return mime
                }

            }
            return mime
        }
    }
}
