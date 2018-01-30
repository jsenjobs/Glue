package app.chaosstudio.com.glue.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import app.chaosstudio.com.glue.ui.SimpleToast;
import app.chaosstudio.com.glue.unit.BrowserUnit;
import app.chaosstudio.com.glue.unit.ViewUnit;
import app.chaosstudio.com.glue.webconfig.NWebView;

/**
 * Created by jsen on 2018/1/27.
 */

@SuppressLint("StaticFieldLeak")
public class ScreenshotTask extends AsyncTask<Void, Void, Boolean> {
    private final Context context;
    private final NWebView webView;
    private int windowWidth;
    private float contentHeight;
    private String title;
    private String path;
    private Activity activity;

    public ScreenshotTask(Context context, NWebView webView) {
        this.context = context;
        this.webView = webView;
        this.windowWidth = 0;
        this.contentHeight = 0f;
        this.title = null;
        this.path = null;
    }

    @Override
    protected void onPreExecute() {

        activity = (Activity) context;
        SimpleToast.Companion.makeToast(context, "正在截图", Toast.LENGTH_LONG).show();

        try {
            windowWidth = ViewUnit.getWindowWidth(context);
            contentHeight = webView.getContentHeight() * ViewUnit.getDensity(context);

            String url = webView.getUrl();
            String domain = Uri.parse(url).getHost().replace("www.", "").trim();
            title = domain.replace(".", "_").trim();

        } catch (Exception e) {
            SimpleToast.Companion.makeToast(context, "截图失败", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Bitmap bitmap = ViewUnit.capture(webView, windowWidth, contentHeight, Bitmap.Config.ARGB_8888);
            path = BrowserUnit.screenshot(context, bitmap, title);
        } catch (Exception e) {
            path = null;
        }
        return path != null && !path.isEmpty();
    }

    @Override
    protected void onPostExecute(Boolean result) {

        if (result) {
            SimpleToast.Companion.makeToast(context, "截图成功", Toast.LENGTH_LONG).show();
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

            if (sp.getInt("screenshot",0) == 1) {

                final File pathFile = new File(sp.getString("screenshot_path", ""));

                if (pathFile.exists()) {
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("image/*");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, path);
                    Uri bmpUri = Uri.fromFile(pathFile);
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    context.startActivity(Intent.createChooser(sharingIntent, "分享"));
                    if (context instanceof Activity) {
                        ((Activity) context).overridePendingTransition(0, 0);
                    }
                }
            }

        } else {
            SimpleToast.Companion.makeToast(context, "截图失败", Toast.LENGTH_LONG).show();
        }
    }
}
