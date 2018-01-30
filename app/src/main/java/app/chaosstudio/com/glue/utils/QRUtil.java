package app.chaosstudio.com.glue.utils;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.zxing.Result;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import app.chaosstudio.com.glue.zxing.DecodeImage;

/**
 * Created by jsen on 2018/1/30.
 */

public class QRUtil {
    public static Result result = null;
    /**
     * 判断是否为二维码
     *
     * @return
     */
    public static boolean isQRImage(AssetManager assetManager, String url) {
        Bitmap bm = getBitmap(assetManager, url);
        Log.e("MARK", url);
        if (bm == null) return false;
        result = DecodeImage.handleQRCodeFormBitmap(bm);
        return result != null;

    }
    /**
     * 根据地址获取网络图片
     *
     * @param sUrl 图片地址
     * @return
     */
    private static Bitmap getBitmap(AssetManager assetManager, String sUrl) {
        try {
            URL url = new URL(sUrl);
            if ("http".equals(url.getProtocol()) || "https".equals(url.getProtocol())) {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() == 200) {
                    InputStream inputStream = conn.getInputStream();
                    return BitmapFactory.decodeStream(inputStream);
                }
            } else if (sUrl.startsWith("file:///android_asset/")){
                String u = sUrl.substring("file:///android_asset/".length(), sUrl.length());
                InputStream inputStream = assetManager.open(u);
                return BitmapFactory.decodeStream(inputStream);
            } else {
                FileInputStream fis = new FileInputStream(url.getPath());
                return BitmapFactory.decodeStream(fis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
