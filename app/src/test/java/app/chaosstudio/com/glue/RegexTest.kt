package app.chaosstudio.com.glue

import android.util.Log
import org.junit.Test
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by jsen on 2018/2/4.
 */
class RegexTest {
    @Test fun reT() {
        val htmlStr = "<div class=\"wrap\" id=\"page_wrapper\"><div><div class=\"mod\" id=\"content_wrapper\"><header class=\"landingHead\"><h1 class=\"titleFont\"><span class=\"titleTxt\">美特种部队和土耳其猛烈交火：土军数万大军无法前进一步！</span><div class=\"titleLogo\"></div></h1><section><h3><div class=\"avatarLink\" key='1'><a href=\"https://baijiahao.baidu.com/u?app_id=1568886952696819\" data-box={data.cmd} data-box-cmd={data.cmd}><img src=\"https://timg01.bdimg.com/timg?pacompress&imgtype=0&sec=1439619614&autorotate=1&di=17a2c08809e03dd1bb0992c86a4964d2&quality=90&size=b105_105&src=http%3A%2F%2Fbos.nj.bpc.baidu.com%2Fv1%2Fmediaspot%2F0c212a58deb19d71e63d5e326b0f2190.jpeg\" /></a></div><div class=\"extraInfo\" key='2'><a href=\"https://baijiahao.baidu.com/u?app_id=1568886952696819\"><div class=\"authorName\">全球军事热评</div></a>"
        val p_image: Pattern
        val m_image: Matcher
        var img: String
        val regEx_img = "<img.+src\\s*=\\s*.*/>"
        p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE)
        m_image = p_image.matcher(htmlStr)
        while (m_image.find()) {
            img = m_image.group()
            System.out.println(img)
        }
    }
}