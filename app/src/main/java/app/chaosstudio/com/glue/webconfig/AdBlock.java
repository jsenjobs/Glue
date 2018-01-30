package app.chaosstudio.com.glue.webconfig;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import app.chaosstudio.com.glue.App;
import app.chaosstudio.com.glue.greendb.gen.BlackUrlDao;
import app.chaosstudio.com.glue.greendb.gen.WhiteDomainDao;
import app.chaosstudio.com.glue.greendb.model.BlackUrl;
import app.chaosstudio.com.glue.greendb.model.WhiteDomain;

/**
 * Created by jsen on 2018/1/21.
 */

public class AdBlock {
    private static final String FILE = "hosts.txt";
    private static final Set<String> hosts = new HashSet<>();
    private static final List<String> whitelist = new ArrayList<>();
    private static final List<String> blacklist = new ArrayList<>();
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
                        hosts.add(line.toLowerCase(locale));
                    }
                } catch (IOException i) {
                    Log.w("Browser", "Error loading hosts", i);
                }
            }
        });
        thread.start();
    }

    public synchronized static void loadDomains(Context context) {

        WhiteDomainDao whiteDomainDao = App.getInstances().getDaoSession().getWhiteDomainDao();
        List<WhiteDomain> list = whiteDomainDao.loadAll();
        whitelist.clear();
        for (WhiteDomain domain: list) {
            whitelist.add(domain.getDomain());
        }
        BlackUrlDao blackUrlDao = App.getInstances().getDaoSession().getBlackUrlDao();
        List<BlackUrl> list1 = blackUrlDao.loadAll();
        blacklist.clear();
        for (BlackUrl blackUrl:list1) {
            blacklist.add(blackUrl.getDomain());
        }
    }

    private static String getDomain(String url) throws URISyntaxException {
        url = url.toLowerCase(locale);

        int index = url.indexOf('/', 8); // -> http://(7) and https://(8)
        if (index != -1) {
            url = url.substring(0, index);
        }

        URI uri = new URI(url);
        String domain = uri.getHost();
        if (domain == null) {
            return url;
        }
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }


    public AdBlock(Context context) {

        if (hosts.isEmpty()) {
            loadHosts(context);
            loadDomains(context);
        }
    }

    public boolean isWhite(String url) {
        for (String domain : whitelist) {
            if (url.contains(domain)) {
                return true;
            }
        }
        return false;
    }

    public boolean isBlack(String url) {
        for (String domain : blacklist) {
            if (url.contains(domain)) {
                return true;
            }
        }
        return false;
    }

    boolean isAd(String url) {
        String domain;
        try {
            domain = getDomain(url);
        } catch (URISyntaxException u) {
            return false;
        }
        return hosts.contains(domain.toLowerCase(locale));
    }

    public synchronized void addDomain(String domain) {
        WhiteDomain whiteDomain = new WhiteDomain();
        whiteDomain.setDomain(domain);
        App.getInstances().getDaoSession().getWhiteDomainDao().insert(whiteDomain);
        whitelist.add(domain);
    }

    public synchronized void removeDomain(String domain) {
        WhiteDomain whiteDomain = new WhiteDomain();
        whiteDomain.setDomain(domain);
        App.getInstances().getDaoSession().getWhiteDomainDao().delete(whiteDomain);
        whitelist.remove(domain);
    }

    public synchronized void clearDomains() {
        App.getInstances().getDaoSession().getWhiteDomainDao().deleteAll();
        whitelist.clear();
    }
}