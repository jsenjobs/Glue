package app.chaosstudio.com.glue.webconfig;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import app.chaosstudio.com.glue.App;
import app.chaosstudio.com.glue.greendb.gen.PluginDao;
import app.chaosstudio.com.glue.greendb.model.Plugin;

/**
 * Created by jsen on 2018/1/31.
 */

public class PluginFilter {

    private static final List<Plugin> plugins = new ArrayList<>();

    public synchronized static void loadPlugins(Context context) {

        PluginDao jsDomainDao = App.getInstances().getDaoSession().getPluginDao();
        List<Plugin> data = jsDomainDao.loadAll();
        plugins.clear();
        plugins.addAll(data);
    }

    private PluginFilter(Context context) {

        if (plugins.isEmpty()) {
            loadPlugins(context);
        }
    }


    private static PluginFilter instance = null;

    public static void init(Context context) {
        if (instance == null) {
            synchronized (PluginFilter.class) {
                if (instance == null) {
                    instance = new PluginFilter(context);
                }
            }
        }
    }

    public static void filterPlugin(NWebView webView, String url) {
        for (Plugin plugin:plugins) {
            if ("*".equals(plugin.getFilter()) || url.contains(plugin.getFilter())) {
                webView.loadUrl("javascript:" + plugin.getJs());
            }
        }
    }
}
