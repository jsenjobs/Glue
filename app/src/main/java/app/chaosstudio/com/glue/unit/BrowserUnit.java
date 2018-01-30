package app.chaosstudio.com.glue.unit;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.regex.Pattern;

import app.chaosstudio.com.glue.R;
import app.chaosstudio.com.glue.ui.SimpleToast;

/**
 * Created by jsen on 2018/1/21.
 */

public class BrowserUnit {
    private static final String SUFFIX_HTML = ".html";
    public static final String SUFFIX_PNG = ".png";
    private static final String SUFFIX_TXT = ".txt";

    public static final String MIME_TYPE_IMAGE = "image/*";
    public static final String MIME_TYPE_TEXT_PLAIN = "text/plain";
    public static final String MIME_TYPE_TEXT_HTML= "text/html";
    public static final String URL_ENCODING = "UTF-8";

    public static final String UA_DESKTOP = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";

    public static void copyURL(Context context, String url) {
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText(null, url.trim());
        assert manager != null;
        manager.setPrimaryClip(data);
        SimpleToast.Companion.makeToast(context, "复制链接成功", Toast.LENGTH_LONG).show();
    }

    public static void download(final Context context, String url, String contentDisposition, String mimeType) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String filename = URLUtil.guessFileName(url, contentDisposition, mimeType); // Maybe unexpected filename.

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(filename);
        request.setMimeType(mimeType);
        String dir = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.sp_file_download_dir), Environment.DIRECTORY_DOWNLOADS);
        request.setDestinationInExternalPublicDir(dir, filename);

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        assert manager != null;

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int hasWRITE_EXTERNAL_STORAGE = context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                SimpleToast.Companion.makeToast(context, "无法读写磁盘", Toast.LENGTH_LONG).show();
            } else {
                manager.enqueue(request);
                try {
                    SimpleToast.Companion.makeToast(context, "开始下载", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    SimpleToast.Companion.makeToast(context, "开始下载", Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            manager.enqueue(request);
            try {
                SimpleToast.Companion.makeToast(context, "开始下载", Toast.LENGTH_LONG).show();
                // NinjaToast.show(context, R.string.toast_start_download);
            } catch (Exception e) {
                SimpleToast.Companion.makeToast(context, "开始下载", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static final String URL_ABOUT_BLANK = "about:blank";
    public static final String URL_SCHEME_ABOUT = "about:";
    public static final String URL_SCHEME_MAIL_TO = "mailto:";
    private static final String URL_SCHEME_FILE = "file://";
    private static final String URL_SCHEME_HTTP = "http://";
    public static final String URL_SCHEME_INTENT = "intent://";
    public static boolean isURL(String url) {
        if (url == null) {
            return false;
        }

        url = url.toLowerCase(Locale.getDefault());
        if (url.startsWith(URL_ABOUT_BLANK)
                || url.startsWith(URL_SCHEME_MAIL_TO)
                || url.startsWith(URL_SCHEME_FILE)) {
            return true;
        }

        String regex = "^((ftp|http|https|intent)?://)"                      // support scheme
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" // ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}"                            // IP形式的URL -> 199.194.52.184
                + "|"                                                        // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*"                                  // 域名 -> www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\."                    // 二级域名
                + "[a-z]{2,6})"                                              // first level domain -> .com or .museum
                + "(:[0-9]{1,4})?"                                           // 端口 -> :80
                + "((/?)|"                                                   // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(url).matches();
    }
    private static final String URL_PREFIX_GOOGLE_PLAY = "www.google.com/url?q=";
    private static final String URL_SUFFIX_GOOGLE_PLAY = "&sa";
    private static final String URL_PREFIX_GOOGLE_PLUS = "plus.url.google.com/url?q=";
    private static final String URL_SUFFIX_GOOGLE_PLUS = "&rct";

    private static final String SEARCH_ENGINE_GOOGLE = "https://www.google.com/search?q=";
    private static final String SEARCH_ENGINE_DUCKDUCKGO = "https://duckduckgo.com/?q=";
    private static final String SEARCH_ENGINE_STARTPAGE = "https://startpage.com/do/search?query=";
    private static final String SEARCH_ENGINE_BING = "http://www.bing.com/search?q=";
    public static final String SEARCH_ENGINE_BAIDU = "http://www.baidu.com/s?wd=";
    private static final String SEARCH_ENGINE_SHENMA = "http://m.sm.cn/s?q=";
    private static final String SEARCH_ENGINE_SOGOU = "https://www.sogou.com/web?query=";
    private static final String SEARCH_ENGINE_360 = "https://www.so.com/s?q=";
    public static String queryWrapper(Context context, String query) {
        // Use prefix and suffix to process some special links
        String temp = query.toLowerCase(Locale.getDefault());
        if ("home".equals(temp)) {
            return getHome(context);
        }
        if (temp.contains(URL_PREFIX_GOOGLE_PLAY) && temp.contains(URL_SUFFIX_GOOGLE_PLAY)) {
            int start = temp.indexOf(URL_PREFIX_GOOGLE_PLAY) + URL_PREFIX_GOOGLE_PLAY.length();
            int end = temp.indexOf(URL_SUFFIX_GOOGLE_PLAY);
            query = query.substring(start, end);
        } else if (temp.contains(URL_PREFIX_GOOGLE_PLUS) && temp.contains(URL_SUFFIX_GOOGLE_PLUS)) {
            int start = temp.indexOf(URL_PREFIX_GOOGLE_PLUS) + URL_PREFIX_GOOGLE_PLUS.length();
            int end = temp.indexOf(URL_SUFFIX_GOOGLE_PLUS);
            query = query.substring(start, end);
        }

        if (isURL(query)) {
            if (query.startsWith(URL_SCHEME_ABOUT) || query.startsWith(URL_SCHEME_MAIL_TO)) {
                return query;
            }

            if (!query.contains("://")) {
                query = URL_SCHEME_HTTP + query;
            }

            return query;
        }

        if (temp.startsWith("javascript:")) {
            return query;
        }
        try {
            query = URLEncoder.encode(query, URL_ENCODING);
        } catch (UnsupportedEncodingException u) {
            Log.w("Browser", "Unsupported Encoding Exception");
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String custom = sp.getString(context.getString(R.string.sp_search_engine_custom), SEARCH_ENGINE_BAIDU);
        final int i = Integer.valueOf(sp.getString(context.getString(R.string.sp_search_engine), "4"));
        switch (i) {
            case 0:
                return SEARCH_ENGINE_GOOGLE + query;
            case 1:
                return SEARCH_ENGINE_DUCKDUCKGO + query;
            case 2:
                return SEARCH_ENGINE_STARTPAGE + query;
            case 3:
                return SEARCH_ENGINE_BING + query;
            case 4:
                return SEARCH_ENGINE_BAIDU + query;
            case 5:
                return SEARCH_ENGINE_SHENMA + query;
            case 6:
                return SEARCH_ENGINE_SOGOU+ query;
            case 7:
                return SEARCH_ENGINE_360 + query;
            case 8:
                return custom + query;
            default:
                return SEARCH_ENGINE_BAIDU + query;
        }
    }

    public static String getHomeCustomer(Context context) {
        JSONObject jsonObject = new JSONObject();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        // String custom = sp.getString(context.getString(R.string.sp_search_engine_custom), SEARCH_ENGINE_BAIDU);
        final int i = Integer.valueOf(sp.getString(context.getString(R.string.sp_search_engine), "4"));
        switch (i) {
            /*
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 8:
                jsonObject.put("engine", "百度");
                break;
                */
            case 6:
                jsonObject.put("engine", "搜狗");
                break;
            case 7:
                jsonObject.put("engine", "好搜");
                break;
            default:
                jsonObject.put("engine", "百度");
                break;
        }
        final String url = sp.getString(context.getString(R.string.sp_home_custom_background), "");
        if (!TextUtils.isEmpty(url)) {
            jsonObject.put("backgroundImage", url);
        }
        final boolean logoShow = sp.getBoolean(context.getString(R.string.sp_home_logo_show), true);
        jsonObject.put("showLogo", logoShow);
        if (logoShow) {
            final String logoUrl = sp.getString(context.getString(R.string.sp_home_logo_path), "");
            if (!TextUtils.isEmpty(logoUrl)) {
                jsonObject.put("logoSrc", logoUrl);
            }
        }

        return jsonObject.toJSONString();

    }


    public static String screenshot(Context context, Bitmap bitmap, String name) {
        if (bitmap == null) {
            return null;
        }

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (name == null || name.trim().isEmpty()) {
            name = String.valueOf(System.currentTimeMillis());
        }
        name = name.trim();

        int count = 0;
        File file = new File(dir, name + SUFFIX_PNG);
        while (file.exists()) {
            count++;
            file = new File(dir, name + "_" + count + SUFFIX_PNG);
        }

        try {
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString("screenshot_path", file.getPath()).apply();

            return file.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    public final static String defaultHome = "file:///android_asset/home.html";
    private static String home = null;
    public static String getHome(Context context) {
        if (home == null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            home = sp.getString(context.getString(R.string.sp_home), defaultHome);
        }
        return home;
    }
    public static void setHome(String home) {
        BrowserUnit.home = home;
    }

}
