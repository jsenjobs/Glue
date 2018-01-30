package app.chaosstudio.com.glue.webconfig;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import app.chaosstudio.com.glue.eventb.WebViewAction;

public class NClickHandler extends Handler {

    public NClickHandler() {
        super();
    }

    @Override
    public void handleMessage(Message message) {
        super.handleMessage(message);
        // src url title
        String url = message.getData().getString("url");
        if (url == null) {
            url = message.getData().getString("src");
        }
        if (url == null) {
            url = "";
        }
        if (!TextUtils.isEmpty(url)) {
            WebViewAction.Companion.fire(WebViewAction.ACTION.ONLONGCLICK, url);
        }
    }
}
