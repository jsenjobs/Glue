package app.chaosstudio.com.glue.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

/**
 * Created by jsen on 2018/1/25.
 */

public class ImageRGB {
    public static int colorAva(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        int w = bitmap.getWidth();
        // int h = bitmap.getHeight();
        int aR = 0, aG = 0, aB = 0, aA = 0;
        for (int i = 0; i < w; i++) {
            int color = bitmap.getPixel(i, 0);
            aR += Color.red(color);
            aG += Color.green(color);
            aB += Color.blue(color);
            aA += Color.alpha(color);
        }
        return Color.argb(aA / w, aR / w, aG / w, aB / w);
    }
}
