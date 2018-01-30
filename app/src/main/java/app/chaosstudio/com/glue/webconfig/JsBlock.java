package app.chaosstudio.com.glue.webconfig;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import app.chaosstudio.com.glue.App;
import app.chaosstudio.com.glue.greendb.gen.JsDomainDao;
import app.chaosstudio.com.glue.greendb.model.JsDomain;

/**
 * Created by jsen on 2018/1/22.
 */

public class JsBlock {
    private static final String FILE = "javaHosts.txt";
    private static final Set<String> hostsJS = new HashSet<>();
    private static final List<String> whitelistJS = new ArrayList<>();
    private static final Locale locale = Locale.getDefault();

    private static void loadHosts(final Context context) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                AssetManager manager = context.getAssets();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(manager.open(FILE)));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        hostsJS.add(line.toLowerCase(locale));
                    }
                } catch (IOException i) {
                    Log.w("Browser", "Error loading hosts");
                }
            }
        });
        thread.start();
    }

    private synchronized static void loadDomains(Context context) {

        JsDomainDao jsDomainDao = App.getInstances().getDaoSession().getJsDomainDao();
        List<JsDomain> list = jsDomainDao.loadAll();
        whitelistJS.clear();
        for (JsDomain domain: list) {
            whitelistJS.add(domain.getDomain());
        }
    }

    public JsBlock(Context context) {

        if (hostsJS.isEmpty()) {
            loadHosts(context);
        }
        loadDomains(context);
    }

    public boolean isWhite(String url) {
        for (String domain : whitelistJS) {
            if (url.contains(domain)) {
                return true;
            }
        }
        return false;
    }


    public synchronized void addDomain(String domain) {
        JsDomain jsDomain = new JsDomain();
        jsDomain.setDomain(domain);
        App.getInstances().getDaoSession().getJsDomainDao().insert(jsDomain);
        whitelistJS.add(domain);
    }

    public synchronized void removeDomain(String domain) {
        JsDomain jsDomain = new JsDomain();
        jsDomain.setDomain(domain);
        App.getInstances().getDaoSession().getJsDomainDao().delete(jsDomain);
        whitelistJS.remove(domain);
    }

    public synchronized void clearDomains() {
        App.getInstances().getDaoSession().getJsDomainDao().deleteAll();
        whitelistJS.clear();
    }

}
